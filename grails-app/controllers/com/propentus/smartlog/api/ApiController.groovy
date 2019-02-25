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

package com.propentus.smartlog.api

import com.propentus.common.exception.BlockchainException
import com.propentus.common.util.EntityUtil
import com.propentus.common.util.StringUtil
import com.propentus.common.util.grails.GrailsUtil
import com.propentus.config.SupportedUBLMessageTypes
import com.propentus.iot.BlockchainManager
import com.propentus.iot.chaincode.model.UBLChaincodeTO
import com.propentus.smartlog.blockchain.ConnectorHolderService
import com.propentus.smartlog.exceptions.WrongDateFormatException
import com.propentus.smartlog.query.CouchDbQueryProcessor
import com.propentus.smartlog.util.MessageDecryptor
import io.swagger.annotations.*
import oasis.names.specification.ubl.schema.xsd.billoflading_2.BillOfLading
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType
import oasis.names.specification.ubl.schema.xsd.forwardinginstructions_2.ForwardingInstructions
import oasis.names.specification.ubl.schema.xsd.packinglist_2.PackingList
import oasis.names.specification.ubl.schema.xsd.transportationstatus_2.TransportationStatus
import oasis.names.specification.ubl.schema.xsd.transportationstatusrequest_2.TransportationStatusRequest
import oasis.names.specification.ubl.schema.xsd.waybill_2.Waybill
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

/**
 * UBL API
 *
 * Contains different API's that are used to communicate different type of UBL-messages to blockchain.
 * Contains also an action that renders API documentation using Swagger.
 *
 * Authentication and authorization is handled in ApiInterceptor.
 */
@Api(value = 'api', description = 'UBL-API', tags = ["UBL-API"])
@SwaggerDefinition(
        info = @Info(
                description = "Smartlog UBL-API for UBL-message types:" ,
                version = "V1.2.0",
                contact = @Contact(
                        name = "Support",
                        email = "smartlog.user@propentus.com"
                ),
                title = "Smartlog UBL-API"

        ),
        externalDocs = @ExternalDocs(value = "Official UBL v3.1 specification for Freight Management", url = "http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#S-INTERNATIONAL-FREIGHT-MANAGEMENT")
)
class ApiController {

    //Error messages contents
    private static final String ERROR_MESSAGE_ID_MISSING = "Required field 'ID' was not found from message"
    private static final String ERROR_MESSAGE_EXTENSION_MISSING = "Supply chain was not defined in request"
    private static final String ERROR_MESSAGE_BLOCKCHAIN_ERROR = "Error happened while trying to send data to blockchain"
    private static final String ERROR_NO_MESSAGES_FOUND = "Message was not found with given criteria; No messages found"
    private static final String ERROR_INVALID_DATE_FORMAT = "Date format is invalid"

    //Valid response contents
    private static final String VALID_MESSAGE_REQUEST_OK = "Request OK";
    private static final String REQUEST_OK_ADDED_MESSAGE_TO_BUFFER = "Request OK, there was a problem in blockchain connection, so the message was put in buffer. Message gets sent to blockchain as soon as the connection is up again."

    static scope = "request"

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class)

    RequestHolderService requestHolder = GrailsUtil.getBean(RequestHolderService.class)

    def swaggerService

    ConnectorHolderService connectorHolderService

    /**
     * Action that handles showing of Swagger documentation.
     *
     * @return
     */
    def index() {

        // If request is just basic GET-request done from browser, redirect to Swagger page.
        // After the redirect, Swagger UI reads the API documentation with AJAX using this same action and
        // goes to if clause.
        if (request.getHeader('accept') && request.getHeader('accept').indexOf(MediaType.APPLICATION_JSON_VALUE) > -1) {
            String json = swaggerService.getJsonByName("Api")
            render contentType: MediaType.APPLICATION_JSON_UTF8_VALUE, text: json
        }
        else {
            redirect (uri: swaggerService.getSwaggerUiRedirectUri(request))
        }
    }

    /**
     * This API accepts UBL Forwarding Instructions message.
     *
     * Tries to validate incoming message by parsing it to ForwardingInstructions-object.
     * If parsing succeeds, we know that we are dealing with properly done ForwardingInstructions message.
     *
     * Contains couple of other validations and if any of those validation fails, error message gets sent back to
     * requester.
     *
     * @return
     */
    @ApiOperation(
            value = "Forwarding Instructions",
            nickname = "forwardingInstructions",
            produces = "application/xml",
            consumes = "application/xml",
            httpMethod = "POST",
            response = ForwardingInstructions.class,
            notes = '<a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#T-FORWARDING-INSTRUCTIONS">Message specification</a></br><a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#S-FORWARDING-INSTRUCTIONS">Message usage description</a> </br><a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/xml/UBL-ForwardingInstructions-2.0-Example-International.xml">Sample message</a>'
    )
    @ApiResponses([
            @ApiResponse(code = 400, message = "Required field 'ID' was not found from message; Supply chain was not defined in request; Unknown UBL-message type, expected 'Forwarding Instructions'"),
            @ApiResponse(code = 200, message = "Request OK"),
            @ApiResponse(code = 202, message = "Request OK, there was a problem in blockchain connection, so the message was put in buffer. Message gets sent to blockchain as soon as the connection is up again."),
            @ApiResponse(code = 401, message = "Invalid credentials given. No access rights."),
            @ApiResponse(code = 500, message = "Error happened while trying to send data to blockchain; Couldn't connect to Blockchain. Requests cannot be made.")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = "body",
                    paramType = "body",
                    required = true,
                    value = "Forwarding instructions UBL-message",
                    dataType = "oasis.names.specification.ubl.schema.xsd.forwardinginstructions_2.ForwardingInstructions")
    ])
    def forwardingInstructions() {

        String requestBody = requestHolder.getRequest()

        //Try to parse XML to right UBL-object
        ForwardingInstructions object = null
        try {
            object = EntityUtil.XMLtoObject(ForwardingInstructions.class, requestBody);
        }
        catch(Exception e) {
            logger.warn("Unknown UBL-message type, expected 'Forwarding Instructions'")
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, "Unknown UBL-message type, expected 'Forwarding Instructions'")
            return
        }

        //Fetch ID from UBL message. Check for null or empty values
        IDType id = object.getID()
        if(id == null || StringUtil.isEmpty(id.getValue())) {
            logger.warn(ERROR_MESSAGE_ID_MISSING)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_ID_MISSING)
            return
        }

        //Parse UBL-data from request
        UBLParser parser = new UBLParser(SupportedUBLMessageTypes.MessageType.ForwardingInstructions, requestBody)
        UBLChaincodeTO ublChaincodeTO = new UBLChaincodeTO()
        // Make sure that the date is in right format
        try {
            ublChaincodeTO = parser.build()
        }
        catch(WrongDateFormatException e){
            logger.warn(ERROR_INVALID_DATE_FORMAT)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_INVALID_DATE_FORMAT)
            return
        }

        //Validate request for supply chain extension
        if(ublChaincodeTO.getSupplyChainID() == null) {
            logger.warn(ERROR_MESSAGE_EXTENSION_MISSING)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_EXTENSION_MISSING)
            return
        }

        try {

            //  Initialize BlockchainManager with connections to blockchain and user that is calling this API
            //  In local environment the user is always null, but in cloud environment the user is recognized from the
            //  client certificate that is used to connect to the API.
            BlockchainManager manager = new BlockchainManager(connectorHolderService.connector, requestHolder.getUser())

            //  Encrypts the UBL-message and puts it to UBLChaincodeTO that is then sent to the blockchain.
            manager.sendUBLMessage(ublChaincodeTO, requestBody)
        }
        catch (BlockchainException be) {

            //  If BlockchainException gets thrown, it means that connection to blockchain at this moment
            //  was not working and message was put in buffer for later sending.
            logger.error(be.getMessage(), be)
            logger.error(REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.ACCEPTED, REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            return
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e)
            logger.error(ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            return
        }

        render ResponseBuilder.buildResponseAsXml(HttpStatus.OK, VALID_MESSAGE_REQUEST_OK)
    }

    @ApiOperation(
            value = "Packing list",
            nickname = "packingList",
            produces = "application/xml",
            consumes = "application/xml",
            httpMethod = "POST",
            response = PackingList.class,
            notes = '<a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#T-PACKING-LIST">Message specification</a></br><a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#S-PACKING-LIST">Message usage description</a> </br>'
    )
    @ApiResponses([
            @ApiResponse(code = 400, message = "Required field 'ID' was not found from message; Supply chain was not defined in request; Unknown UBL-message type, expected 'Packing List'"),
            @ApiResponse(code = 200, message = "Request OK"),
            @ApiResponse(code = 202, message = "Request OK, there was a problem in blockchain connection, so the message was put in buffer. Message gets sent to blockchain as soon as the connection is up again."),
            @ApiResponse(code = 401, message = "Invalid credentials given. No access rights."),
            @ApiResponse(code = 500, message = "Error happened while trying to send data to blockchain; Couldn't connect to Blockchain. Requests cannot be made.")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = "body",
                    paramType = "body",
                    required = true,
                    value = "Packing list UBL-message",
                    dataType = "oasis.names.specification.ubl.schema.xsd.packinglist_2.PackingList")
    ])
    def packingList() {

        String requestBody = requestHolder.getRequest()

        //Try to parse XML to right UBL-object
        PackingList object = null
        try {
            object = EntityUtil.XMLtoObject(PackingList.class, requestBody);
        }
        catch(Exception e) {
            logger.warn("Unknown UBL-message type, expected 'Packing List'")
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, "Unknown UBL-message type, expected 'Packing List'")
            return
        }

        //Fetch ID from UBL message. Check for null or empty values
        IDType id = object.getID()
        if(id == null || StringUtil.isEmpty(id.getValue())) {
            logger.warn(ERROR_MESSAGE_ID_MISSING)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_ID_MISSING)
            return
        }

        //Parse UBL-data from request
        UBLParser parser = new UBLParser(SupportedUBLMessageTypes.MessageType.PackingList, requestBody)
        UBLChaincodeTO ublChaincodeTO = new UBLChaincodeTO()
        try {
            ublChaincodeTO = parser.build()
        }
        catch(WrongDateFormatException e){
            logger.warn(ERROR_INVALID_DATE_FORMAT)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_INVALID_DATE_FORMAT)
            return
        }

        //Validate request for supply chain extension
        if(ublChaincodeTO.getSupplyChainID() == null) {
            logger.warn(ERROR_MESSAGE_EXTENSION_MISSING)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_EXTENSION_MISSING)
            return
        }

        try {
            BlockchainManager manager = new BlockchainManager(connectorHolderService.connector, requestHolder.getUser())
            manager.sendUBLMessage(ublChaincodeTO, requestBody)
        }
        catch (BlockchainException be) {
            logger.error(be.getMessage(), be)
            logger.error(REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.ACCEPTED, REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            return
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e)
            logger.error(ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            return
        }

        render ResponseBuilder.buildResponseAsXml(HttpStatus.OK, VALID_MESSAGE_REQUEST_OK)
    }

    @ApiOperation(
            value = "Bill Of Lading",
            nickname = "billOfLading",
            produces = "application/xml",
            consumes = "application/xml",
            httpMethod = "POST",
            response = BillOfLading.class,
            notes = '<a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#T-BILL-OF-LADING">Message specification</a></br><a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#S-BILL-OF-LADING">Message usage description</a> </br>'
    )
    @ApiResponses([
            @ApiResponse(code = 400, message = "Required field 'ID' was not found from message; Supply chain was not defined in request; Unknown UBL-message type, expected 'Bill of Lading'"),
            @ApiResponse(code = 200, message = "Request OK"),
            @ApiResponse(code = 202, message = "Request OK, there was a problem in blockchain connection, so the message was put in buffer. Message gets sent to blockchain as soon as the connection is up again."),
            @ApiResponse(code = 401, message = "Invalid credentials given. No access rights."),
            @ApiResponse(code = 500, message = "Error happened while trying to send data to blockchain; Couldn't connect to Blockchain. Requests cannot be made.")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = "body",
                    paramType = "body",
                    required = true,
                    value = "Bill of Lading UBL-message",
                    dataType = "oasis.names.specification.ubl.schema.xsd.billoflading_2.BillOfLading")
    ])
    def billOfLading() {

        String requestBody = requestHolder.getRequest()

        //Try to parse XML to right UBL-object
        BillOfLading object = null
        try {
            object = EntityUtil.XMLtoObject(BillOfLading.class, requestBody);
        }
        catch(Exception e) {
            logger.warn("Unknown UBL-message type, expected 'Bill Of Lading'")
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, "Unknown UBL-message type, expected 'Bill Of Lading'")
            return
        }

        //Fetch ID from UBL message. Check for null or empty values
        IDType id = object.getID()
        if(id == null || StringUtil.isEmpty(id.getValue())) {
            logger.warn(ERROR_MESSAGE_ID_MISSING)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_ID_MISSING)
            return
        }

        //Parse UBL-data from request
        UBLParser parser = new UBLParser(SupportedUBLMessageTypes.MessageType.BillOfLading, requestBody)
        UBLChaincodeTO ublChaincodeTO = new UBLChaincodeTO()
        try {
            ublChaincodeTO = parser.build()
        }
        catch(WrongDateFormatException e){
            logger.warn(ERROR_INVALID_DATE_FORMAT)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_INVALID_DATE_FORMAT)
            return
        }

        //Validate request for supply chain extension
        if(ublChaincodeTO.getSupplyChainID() == null) {
            logger.warn(ERROR_MESSAGE_EXTENSION_MISSING)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_EXTENSION_MISSING)
            return
        }

        try {
            BlockchainManager manager = new BlockchainManager(connectorHolderService.connector, requestHolder.getUser())
            manager.sendUBLMessage(ublChaincodeTO, requestBody)
        }
        catch (BlockchainException be) {
            logger.error(be.getMessage(), be)
            logger.error(REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.ACCEPTED, REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            return
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e)
            logger.error(ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            return
        }

        render ResponseBuilder.buildResponseAsXml(HttpStatus.OK, VALID_MESSAGE_REQUEST_OK)
    }

    @ApiOperation(
            value = "Waybill",
            nickname = "waybill",
            produces = "application/xml",
            consumes = "application/xml",
            httpMethod = "POST",
            response = Waybill.class,
            notes = '<a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#T-WAYBILL">Message specification</a></br><a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#S-WAYBILL">Message usage description</a> </br><a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/xml/UBL-Waybill-2.0-Example-International.xml">Sample message</a>'
    )
    @ApiResponses([
            @ApiResponse(code = 400, message = "Required field 'ID' was not found from message; Supply chain was not defined in request; Unknown UBL-message type, expected 'Waybill'"),
            @ApiResponse(code = 200, message = "Request OK"),
            @ApiResponse(code = 202, message = "Request OK, there was a problem in blockchain connection, so the message was put in buffer. Message gets sent to blockchain as soon as the connection is up again."),
            @ApiResponse(code = 401, message = "Invalid credentials given. No access rights."),
            @ApiResponse(code = 500, message = "Error happened while trying to send data to blockchain; Couldn't connect to Blockchain. Requests cannot be made.")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = "body",
                    paramType = "body",
                    required = true,
                    value = "Waybill UBL-message",
                    dataType = "oasis.names.specification.ubl.schema.xsd.waybill_2.Waybill")
    ])
    def waybill() {

        String requestBody = requestHolder.getRequest()

        //Try to parse XML to right UBL-object
        Waybill object = null
        try {
            object = EntityUtil.XMLtoObject(Waybill.class, requestBody);
        }
        catch(Exception e) {
            logger.warn("Unknown UBL-message type, expected 'Waybill'")
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, "Unknown UBL-message type, expected 'Waybill'")
            return
        }

        //Fetch ID from UBL message
        IDType id = object.getID()
        if(id == null || StringUtil.isEmpty(id.getValue())) {
            logger.warn(ERROR_MESSAGE_ID_MISSING)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_ID_MISSING)
            return
        }


        //Parse UBL-data from request
        UBLParser parser = new UBLParser(SupportedUBLMessageTypes.MessageType.Waybill, requestBody)
        UBLChaincodeTO ublChaincodeTO = new UBLChaincodeTO()
        try {
            ublChaincodeTO = parser.build()
        }
        catch(WrongDateFormatException e){
            logger.warn(ERROR_INVALID_DATE_FORMAT)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_INVALID_DATE_FORMAT)
            return
        }

        //Validate request for supply chain extension
        if(ublChaincodeTO.getSupplyChainID() == null) {
            logger.warn(ERROR_MESSAGE_EXTENSION_MISSING)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_EXTENSION_MISSING)
            return
        }

        try {
            BlockchainManager manager = new BlockchainManager(connectorHolderService.connector, requestHolder.getUser())
            manager.sendUBLMessage(ublChaincodeTO, requestBody)
        }
        catch (BlockchainException be) {
            logger.error(be.getMessage(), be)
            logger.error(REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.ACCEPTED, REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            return
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e)
            logger.error(ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            return
        }

        render ResponseBuilder.buildResponseAsXml(HttpStatus.OK, VALID_MESSAGE_REQUEST_OK)
    }

    @ApiOperation(
            value = "Transportation Status",
            nickname = "transportationStatus",
            produces = "application/xml",
            consumes = "application/xml",
            httpMethod = "POST",
            response = TransportationStatus.class,
            notes = '<a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#T-TRANSPORTATION-STATUS">Message specification</a></br><a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#S-FREIGHT-STATUS-REPORTING">Message usage description</a> </br><a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/xml/UBL-TransportationStatus-2.1-Example.xml">Sample message</a>'
    )
    @ApiResponses([
            @ApiResponse(code = 400, message = "Required field 'ID' was not found from message; Supply chain was not defined in request; Unknown UBL-message type, expected 'Transportation Status'"),
            @ApiResponse(code = 200, message = "Request OK"),
            @ApiResponse(code = 202, message = "Request OK, there was a problem in blockchain connection, so the message was put in buffer. Message gets sent to blockchain as soon as the connection is up again."),
            @ApiResponse(code = 401, message = "Invalid credentials given. No access rights."),
            @ApiResponse(code = 500, message = "Error happened while trying to send data to blockchain; Couldn't connect to Blockchain. Requests cannot be made.")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = "body",
                    paramType = "body",
                    required = true,
                    value = "Transportation status UBL-message",
                    dataType = "oasis.names.specification.ubl.schema.xsd.transportationstatus_2.TransportationStatus")
    ])
    def transportationStatus() {

        String requestBody = requestHolder.getRequest()

        //Try to parse XML to right UBL-object
        TransportationStatus object = null
        try {
            object = EntityUtil.XMLtoObject(TransportationStatus.class, requestBody);
        }
        catch(Exception e) {
            logger.warn("Unknown UBL-message type, expected 'Transportation Status'")
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, "Unknown UBL-message type, expected 'Transportation Status'")
            return
        }

        //Fetch ID from UBL message. Check for null or empty values
        IDType id = object.getID()
        if(id == null || StringUtil.isEmpty(id.getValue())) {
            logger.warn(ERROR_MESSAGE_ID_MISSING)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_ID_MISSING)
            return
        }

        //Parse UBL-data from request
        UBLParser parser = new UBLParser(SupportedUBLMessageTypes.MessageType.TransportationStatus, requestBody)
        UBLChaincodeTO ublChaincodeTO = new UBLChaincodeTO()
        try {
            ublChaincodeTO = parser.build()
        }
        catch(WrongDateFormatException e){
            logger.warn(ERROR_INVALID_DATE_FORMAT)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_INVALID_DATE_FORMAT)
            return
        }
        //Validate request for supply chain extension
        if(ublChaincodeTO.getSupplyChainID() == null) {
            logger.warn(ERROR_MESSAGE_EXTENSION_MISSING)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_EXTENSION_MISSING)
            return
        }

        try {
            BlockchainManager manager = new BlockchainManager(connectorHolderService.connector, requestHolder.getUser())
            manager.sendUBLMessage(ublChaincodeTO, requestBody)
        }
        catch (BlockchainException be) {
            logger.error(be.getMessage(), be)
            logger.error(REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.ACCEPTED, REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            return
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e)
            logger.error(ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            return
        }

        render ResponseBuilder.buildResponseAsXml(HttpStatus.OK, VALID_MESSAGE_REQUEST_OK)
    }

    @ApiOperation(
            value = "Transportation Status Request",
            nickname = "transportationStatusRequest",
            produces = "application/xml",
            consumes = "application/xml",
            httpMethod = "POST",
            response = TransportationStatusRequest.class,
            notes = '<a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#T-TRANSPORTATION-STATUS-REQUEST">Message specification</a></br><a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html#S-FREIGHT-STATUS-REPORTING">Message usage description</a> </br><a target="_blank" href="http://docs.oasis-open.org/ubl/os-UBL-2.1/xml/UBL-TransportationStatusRequest-2.1-Example.xml">Sample message</a>'
    )
    @ApiResponses([
            @ApiResponse(code = 400, message = "Required field 'ID' was not found from message; Supply chain was not defined in request; Unknown UBL-message type, expected 'Transportation Status Request'"),
            @ApiResponse(code = 200, message = "Request OK"),
            @ApiResponse(code = 202, message = "Request OK, there was a problem in blockchain connection, so the message was put in buffer. Message gets sent to blockchain as soon as the connection is up again."),
            @ApiResponse(code = 401, message = "Invalid credentials given. No access rights."),
            @ApiResponse(code = 500, message = "Error happened while trying to send data to blockchain; Couldn't connect to Blockchain. Requests cannot be made.")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = "body",
                    paramType = "body",
                    required = true,
                    value = "Transportation status request UBL-message",
                    dataType = "oasis.names.specification.ubl.schema.xsd.transportationstatusrequest_2.TransportationStatusRequest")
    ])
    def transportationStatusRequest() {

        String requestBody = requestHolder.getRequest()

        //Try to parse XML to right UBL-object
        TransportationStatusRequest object = null
        try {
            object = EntityUtil.XMLtoObject(TransportationStatusRequest.class, requestBody);
        }
        catch(Exception e) {
            logger.warn("Unknown UBL-message type, expected 'Transportation Status Request'")
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, "Unknown UBL-message type, expected 'Transportation Status Request'")
            return
        }

        //Fetch ID from UBL message. Check for null or empty values
        IDType id = object.getID()
        if(id == null || StringUtil.isEmpty(id.getValue())) {
            logger.warn(ERROR_MESSAGE_ID_MISSING)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_ID_MISSING)
            return
        }

        //Parse UBL-data from request
        UBLParser parser = new UBLParser(SupportedUBLMessageTypes.MessageType.TransportationStatusRequest, requestBody)
        UBLChaincodeTO ublChaincodeTO = new UBLChaincodeTO()
        try {
            ublChaincodeTO = parser.build()
        }
        catch(WrongDateFormatException e){
            logger.warn(ERROR_INVALID_DATE_FORMAT)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_INVALID_DATE_FORMAT)
            return
        }

        //Validate request for supply chain extension
        if(ublChaincodeTO.getSupplyChainID() == null) {
            logger.warn(ERROR_MESSAGE_EXTENSION_MISSING)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_EXTENSION_MISSING)
            return
        }

        try {
            BlockchainManager manager = new BlockchainManager(connectorHolderService.connector, requestHolder.getUser())
            manager.sendUBLMessage(ublChaincodeTO, requestBody)
        }
        catch (BlockchainException be) {
            logger.error(be.getMessage(), be)
            logger.error(REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.ACCEPTED, REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            return
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            return
        }

        render ResponseBuilder.buildResponseAsXml(HttpStatus.OK, VALID_MESSAGE_REQUEST_OK)
    }

    /**
     * Find messages with parameters.
     */
    @ApiOperation(
            value = "Find messages by criteria",
            nickname = "findMessages",
            produces = "application/xml",
            consumes = "application/xml",
            httpMethod = "GET",
            response = String.class,
            notes = "Response returned is wrapped in 'Messages' XML element. If no parameter is given, participantId is defaulted to this organizations MSPID."
    )
    @ApiResponses([
            @ApiResponse(code = 404, message = "Messages were not found with given criteria; No messages found"),
            @ApiResponse(code = 200, message = "Request OK"),
            @ApiResponse(code = 401, message = "Invalid credentials given. No access rights."),
            @ApiResponse(code = 500, message = "Internal server error; Error happened while trying to send data to blockchain; Couldn't connect to Blockchain. Requests cannot be made.")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = "containerId",
                    paramType = "query",
                    value = "Container id",
                    dataType = "string"),
            @ApiImplicitParam(name = "organisationId",
                    paramType = "query",
                    value = "Organisation id (MSP)",
                    dataType = "string"),
            @ApiImplicitParam(name = "carrierAssignedId",
                    paramType = "query",
                    value = "Carrier assigned id",
                    dataType = "string"),
            @ApiImplicitParam(name = "supplyChainId",
                    paramType = "query",
                    value = "Supply chain id",
                    dataType = "string"),
            @ApiImplicitParam(name = "shippingOrderId",
                    paramType = "query",
                    value = "Shipping order id",
                    dataType = "string"),
            @ApiImplicitParam(name = "createdFrom",
                    paramType = "query",
                    value = "Created timestamp from date",
                    dataType = "string"),
            @ApiImplicitParam(name = "createdTo",
                    paramType = "query",
                    value = "Created timestamp to date",
                    dataType = "string")
    ])
    def findMessages() {

        logger.debug("findMessageByCriteria params:" + params)

        try {
            CouchDbQueryProcessor processor = new CouchDbQueryProcessor(params, connectorHolderService.connector.getConfig(), requestHolder.getUser())
            processor.query()
            List<UBLChaincodeTO> messages = processor.getMessages()

            //  If no messages were found
            if (messages.isEmpty()) {
                logger.info("No messages found")
                render ResponseBuilder.buildResponseAsXml(HttpStatus.NOT_FOUND, ERROR_NO_MESSAGES_FOUND)
                return
            }

            MessageDecryptor md = new MessageDecryptor(connectorHolderService.connector)
            List<String> decryptedMessages = md.decrypt(messages, requestHolder.getUser())

            renderResponse(decryptedMessages)

        } catch(Exception e) {

            logger.error(e.getMessage(), e)
            render ResponseBuilder.buildResponseAsXml(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
        }
    }

    /**
     * Remove unnescescary <?xml>-tags from UBL-messages and render messages like:
     * <?xml version=1.0 encoding="UTF-8"?>
     * <Messages>
     *    <Message></Message>
     *    <Message></Message>
     *    .
     *    .
     *    .
     * <Messages>
     *
     * @param messages
     */
    private void renderResponse(List<String> messages) {

        logger.debug("Generating response")
        response.setContentType("application/xml")
        response.setCharacterEncoding("UTF-8")

        String resp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        resp += "<Messages>"

        for (String s : messages) {

            if (s != null) {

                resp += "<Message>"
                if (s.startsWith("<?xml") || s.startsWith("<xml")) {

                    int index = s.indexOf("?>")
                    resp += s.substring(index + 2)
                }
                resp += "</Message>"
            }
        }
        resp += "</Messages>"
        render resp
    }
}