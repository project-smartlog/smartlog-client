/*
 * Copyright 2016-2019
 *
 * Interreg Central Baltic 2014-2020 funded project
 * Smart Logistics and Freight Villages Initiative, CB426
 *
 * Kouvola Innovation Oy, FINLAND
 * Region Örebro County, SWEDEN
 * Tallinn University of Technology, ESTONIA
 * Foundation Valga County Development Agency, ESTONIA
 * Transport and Telecommunication Institute, LATVIA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.propentus.smartlog.console

import com.propentus.common.exception.BlockchainException
import com.propentus.common.util.grails.GrailsUtil
import com.propentus.config.SupportedUBLMessageTypes
import com.propentus.iot.BlockchainManager
import com.propentus.iot.chaincode.TransportChainChaincode
import com.propentus.iot.chaincode.model.TransportChaincodeTO
import com.propentus.iot.chaincode.model.UBLChaincodeTO
import com.propentus.iot.configs.OrganisationConfiguration
import com.propentus.smartlog.api.RequestHolderService
import com.propentus.smartlog.api.ResponseBuilder
import com.propentus.smartlog.api.UBLParser
import com.propentus.smartlog.blockchain.ConnectorHolderService
import com.propentus.smartlog.datasource.couchdb.entities.ApiUser
import com.propentus.smartlog.exceptions.WrongDateFormatException
import com.propentus.smartlog.query.CouchDbQueryProcessor
import com.propentus.smartlog.service.EntityService
import com.propentus.smartlog.util.MessageDecryptor
import com.propentus.smartlog.utils.ResourceUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

import java.text.SimpleDateFormat

/**
 * Client console specific actions.
 *
 * Index that shows all the messages for logged organization.
 *
 * Authentication actions login, logout and auth.
 *
 * Uses ConsoleInterceptor to check authentication before actions.
 *
 */
class ConsoleController {

    //Error messages contants
    private static final String ERROR_MESSAGE_ID_MISSING = "Required field 'ID' was not found from message";
    private static final String ERROR_MESSAGE_EXTENSION_MISSING = "Supply chain was not defined in request";
    private static final String ERROR_MESSAGE_BLOCKCHAIN_ERROR = "Error happened while trying to send data to blockchain";
    private static final String ERROR_NO_MESSAGES_FOUND = "Message was not found with given criteria; No messages found"
    private static final String ERROR_INVALID_DATE_FORMAT = "Date format is invalid"

    //Valid response contants
    private static final String VALID_MESSAGE_REQUEST_OK = "Request OK";
    private static final String REQUEST_OK_ADDED_MESSAGE_TO_BUFFER = "Request OK, there was a problem in blockchain connection, so the message was put in buffer. Message gets sent to blockchain as soon as the connection is up again.";

    final static Logger logger = LoggerFactory.getLogger(ConsoleController.class)
    ConnectorHolderService connectorHolderService
    RequestHolderService requestHolder = GrailsUtil.getBean(RequestHolderService.class)
    EntityService entityService
    static scope = "request"

    /**
     * Get UBL messages for current organization
     *
     * In local client installation organization is always the one that is configured to client,
     * but in cloud installation organization is got from the authentication information. (username / password)
     *
     */
    def index() {
        //  Set current org depending of what version this peer is CLOUD vs NORMAL
        //  Cloud gets user from authentication
        //  But local doesn't need authentication
        OrganisationConfiguration config = connectorHolderService.connector.config
        String currentOrg = config.organisation.mspid

        if (config.isCloudInstallation()) {
            currentOrg = session.user.getOrganisation()
        }

        //  If organization is logged in, use its mspid, else defaults to mspid in config
        params.setProperty("participantId", currentOrg)

        // Get all transport chains that the organization is part of
        TransportChainChaincode service = new TransportChainChaincode(connectorHolderService.connector)
        List<TransportChaincodeTO> chains = service.getTransportChain("")
        List<TransportChaincodeTO> orgChains = new ArrayList<>()
        for (TransportChaincodeTO chain in chains){
            for(String part in chain.participants){
                if(currentOrg == part){
                    orgChains.add(chain)
                    break
                }
            }
        }

        //  Get all messages from the CouchDB
        CouchDbQueryProcessor processor = new CouchDbQueryProcessor(params, connectorHolderService.connector.getConfig(), session.user)
        processor.query()

        List<UBLChaincodeTO> messages = processor.getMessages();

        //  If no messages were found return null messages, so we know to render empty table text in view
        if (messages.isEmpty()) {
            return [messages: null, currentOrg: currentOrg, isCloud: config.isCloudInstallation(), orgChains: orgChains]
        }

        MessageDecryptor md = new MessageDecryptor(connectorHolderService.connector)
        List<UBLChaincodeTO> decryptedMessages = md.decryptAndGetAsUBLChaincodeTOList(messages, session.user)

        return [messages: decryptedMessages, currentOrg: currentOrg, isCloud: config.isCloudInstallation(), orgChains: orgChains]
    }

    /**
     * Logs user out from the session
     * @return
     */
    def logout() {

        session.loggedIn = false
        session.user = null

        redirect (controller: "console", action: "login")
    }

    /**
     * Shows login screen for user.
     *
     * If user is already logged in, he gets redirected to console.
     *
     * @return
     */
    def login() {

        if (session.loggedIn) {
            redirect (controller: "console", action: "index")
        }

        return
    }

    /**
     * Logs user in if API user exists in CouchDB.
     *
     * If user is found, add loggedIn and user to session so we can identify that user is authenticated.
     * Else redirect back to login screen with error message.
     *
     * @return
     */
    def auth() {

        String username = params.username
        String password = params.password

        String creds = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(creds.getBytes("UTF-8"));
        String token = "Basic " + encoded

        ApiUser user = entityService.findApiUser(token)

        //  No user found show login screen
        if (user == null) {
            flash.error = "Wrong username or password"
            logger.warn("Wrong username or password:", username)
            redirect (controller: "console", action: "login")
            return
        } else {
            session.loggedIn = true
            logger.info("Logged in as user:", username)
            session.user = user
        }

        redirect (controller: "console", action: "index")
    }

    /**
     * Tries to add a new message to blockchain.
     *
     * Message is formed based on fields filled by user in console UI
     *
     * Message filled in UBL template to show it in raw UBL message field in message modal
     *
     * @return
     */
    def addMessage() {
        ResourceUtil resourceUtil = new ResourceUtil()
        String template = resourceUtil.readFileAsString("templates/ubl-template.xml")
        println template
        logger.info("In console's addMessage")


        template = template.replace("{chain}", (String) params.supplyChain)
        template = template.replace("{containerId}", (String) params.containerId)
        template = template.replace("{location}", (String) params.statusLocationId)
        template = template.replace("{containerRfid}", (String) params.rfidTransportEquipment)
        template = template.replace("{statusType}", (String) params.statusTypeCode)
        template = template.replace("{carrierId}", (String) params.carrierAssignedId)
        template = template.replace("{shippingOrderId}", (String) params.shippingOrderId)


        //Add sender
        OrganisationConfiguration config = connectorHolderService.connector.config
        String currentOrg = config.organisation.mspid

        if (config.isCloudInstallation()) {
            currentOrg = session.user.getOrganisation()
        }

        template = template.replace("{senderParty}", currentOrg)

        // Add empty or full indicator
        if(params.emptyFullIndicator.equals("Full")) {
            template = template.replace("{fullnessIndicator}", "FULL")
        }
        if(params.emptyFullIndicator.equals("Empty")) {
            template = template.replace("{fullnessIndicator}", "EMPTY")
        }

        // Add date
        String form = "yyyy-MM-dd HH:mm:ssXXX"
        SimpleDateFormat dateFormat = new SimpleDateFormat(form)
        String date = dateFormat.format(new Date())
        template = template.replace("{date}", date.substring(0,10))
        template = template.replace("{time}", date.substring(11))


        // Create shipment id
        UUID id = UUID.randomUUID()
        template = template.replace("{id}", currentOrg.substring(0, (currentOrg.length() - 3) ) + id)

        logger.info("Sending message with params:" + params)
        logger.debug("Trying to send message:", template)


        //Parse UBL-data from request
        UBLParser parser = new UBLParser(SupportedUBLMessageTypes.MessageType.TransportationStatus, template)
        UBLChaincodeTO ublChaincodeTO = new UBLChaincodeTO()
        try {
            ublChaincodeTO = parser.build()
        }
        catch(WrongDateFormatException e){
            logger.warn("Date format is invalid")
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_INVALID_DATE_FORMAT)
            return
        }

        //Validate request for supply chain extension
        if(ublChaincodeTO.getSupplyChainID() == null) {
            logger.warn("Supply chain was not defined in request")
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_EXTENSION_MISSING)
            return
        }

        try {
            BlockchainManager manager = new BlockchainManager(connectorHolderService.connector, session.user)
            manager.sendUBLMessage(ublChaincodeTO, template)
        }
        catch (BlockchainException be) {
            logger.error(be.getMessage(), be)
            logger.error("Request OK, there was a problem in blockchain connection, so the message was put in buffer. Message gets sent to blockchain as soon as the connection is up again.")
            render ResponseBuilder.buildResponseAsXml(HttpStatus.ACCEPTED, REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            return
        }
        catch (Exception e) {
            logger.error("Internal server error", e)
            logger.error("Error happened while trying to send data to blockchain")
            render ResponseBuilder.buildResponseAsXml(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            return
        }

        Thread.sleep(3000)

        render "OK"
    }
}
