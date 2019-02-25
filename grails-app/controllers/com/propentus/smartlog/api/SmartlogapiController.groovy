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
import com.propentus.iot.BlockchainManager
import com.propentus.iot.chaincode.model.UBLChaincodeTO
import com.propentus.smartlog.api.model.SmartlogApiRequest
import com.propentus.smartlog.blockchain.ConnectorHolderService
import com.propentus.smartlog.query.CouchDbQueryProcessor
import com.propentus.smartlog.util.MessageDecryptor
import com.propentus.smartlog.utils.DateFormatter
import io.swagger.annotations.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@Api(value = 'smartlogapi', description = 'Smartlog-API', tags = ["Smartlog-API"])
@SwaggerDefinition(
        info = @Info(
                description = "Smartlog API:" ,
                version = "V1.0.0",
                contact = @Contact(
                        name = "Support",
                        email = "smartlog.user@propentus.com"
                ),
                title = "Smartlog API"

        ))
class SmartlogapiController {

    //Error messages contants
    private static final String ERROR_MESSAGE_PARSING = "Required field 'ID' was not found from message; Supply chain was not defined in request; Couldn't parse request.";
    private static final String ERROR_MESSAGE_EXTENSION_MISSING = "Supply chain was not defined in request";
    private static final String ERROR_MESSAGE_BLOCKCHAIN_ERROR = "Error happened while trying to send data to blockchain";
    private static final String ERROR_NO_MESSAGES_FOUND = "Message was not found with given criteria; No messages found"
    private static final String ERROR_INVALID_DATE_FORMAT = "Date format is invalid"

    //Valid response contants
    private static final String VALID_MESSAGE_REQUEST_OK = "Request OK";
    private static final String REQUEST_OK_ADDED_MESSAGE_TO_BUFFER = "Request OK, there was a problem in blockchain connection, so the message was put in buffer. Message gets sent to blockchain as soon as the connection is up again.";

    private static final Logger logger = LoggerFactory.getLogger(SmartlogapiController.class)

    def swaggerService

    ConnectorHolderService connectorHolderService

    RequestHolderService requestHolder = GrailsUtil.getBean(RequestHolderService.class)

    static scope = "request"

    /**
     * Shows the Swagger documentation of the API
     *
     * First when user opens this action, he gets redirected to the Swagger UI (else)
     *
     * Then the Swagger UI loads the documentation JSON from this action using AJAX.
     *
     * @return
     */
    def doc() {
        if (request.getHeader('accept') && request.getHeader('accept').indexOf(MediaType.APPLICATION_JSON_VALUE) > -1) {
            String json = swaggerService.getJsonByName("Smartlogapi")
            render contentType: MediaType.APPLICATION_JSON_UTF8_VALUE, text: json
        }
        else {
            redirect (uri: swaggerService.getSwaggerUiRedirectUri(request))
        }
    }

    @ApiOperation(
            value = "Add message",
            nickname = "addMessage",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "POST",
            response = String.class,
            notes = ''
    )
    @ApiResponses([
            @ApiResponse(code = 400, message = "Required field 'ID' was not found from message; Supply chain was not defined in request; Couldn't parse request."),
            @ApiResponse(code = 200, message = "Request OK"),
            @ApiResponse(code = 202, message = "Request OK, there was a problem in blockchain connection, so the message was put in buffer. Message gets sent to blockchain as soon as the connection is up again."),
            @ApiResponse(code = 401, message = "Invalid credentials given. No access rights."),
            @ApiResponse(code = 500, message = "Error happened while trying to send data to blockchain; Couldn't connect to Blockchain. Requests cannot be made.")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = "body",
                    paramType = "body",
                    required = true,
                    value = "Smartlog message",
                    dataType = "com.propentus.smartlog.api.model.SmartlogApiRequest")
    ])
    def addMessage() {

        //  Read JSON from request body
        String requestBody = requestHolder.getRequest()
        SmartlogApiRequest req;

        logger.trace("Request body for parsing:" + requestBody)

        //  Parse request from JSON to object
        try {

            req = EntityUtil.JsonToObject(requestBody, SmartlogApiRequest.class)

        } catch (Exception e) {

            e.printStackTrace();
            logger.warn(ERROR_MESSAGE_PARSING)
            render ResponseBuilder.buildResponseAsJson(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_PARSING)
            return
        }

        //  If request is empty or couldn't be parsed, show error
        if (!req) {
            logger.warn(ERROR_MESSAGE_PARSING)
            render ResponseBuilder.buildResponseAsJson(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_PARSING)
            return
        }

        //  If no supply chain was given, show error
        if (StringUtil.isEmpty(req.supplyChainId)) {
            logger.warn(ERROR_MESSAGE_EXTENSION_MISSING)
            render ResponseBuilder.buildResponseAsJson(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_EXTENSION_MISSING)
            return
        }

        // Check that date format is valid
        String date = DateFormatter.formatDate(req.timestamp)
        if(date == null){
            logger.warn(ERROR_INVALID_DATE_FORMAT)
            render ResponseBuilder.buildResponseAsJson(HttpStatus.BAD_REQUEST, ERROR_INVALID_DATE_FORMAT)
            return
        }
        req.timestamp = date

        //  Try to send data to blockchain
        try {

            BlockchainManager manager = new BlockchainManager(connectorHolderService.connector, requestHolder.getUser())
            manager.sendUBLMessage(req.parseToChaincodeTO(), req.content)

        } catch (BlockchainException be) {
            logger.error(be.getMessage(), be)
            render ResponseBuilder.buildResponseAsJson(HttpStatus.ACCEPTED, REQUEST_OK_ADDED_MESSAGE_TO_BUFFER)
            return

        } catch (Exception e) {

            logger.error(e.getMessage(), e)
            render ResponseBuilder.buildResponseAsJson(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE_BLOCKCHAIN_ERROR)
            return
        }

        render ResponseBuilder.buildResponseAsJson(HttpStatus.OK, VALID_MESSAGE_REQUEST_OK)
    }

    @ApiOperation(
            value = "Find messages by criteria",
            nickname = "findMessages",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = String.class,
            notes = "Response returned is messages in JSON array. If no parameters are given, returns all messages for your organization."
    )
    @ApiResponses([
            @ApiResponse(code = 404, message = "Message was not found with given criteria; No messages found"),
            @ApiResponse(code = 200, message = "Request OK"),
            @ApiResponse(code = 401, message = "Invalid credentials given. No access rights."),
            @ApiResponse(code = 500, message = "Internal server error; Error happened while trying to send data to blockchain; Couldn't connect to Blockchain. Requests cannot be made.")
    ])
    @ApiImplicitParams([
            @ApiImplicitParam(name = "documentId",
                    paramType = "query",
                    value = "ID of the document",
                    dataType = "string"),
            @ApiImplicitParam(name = "containerId",
                    paramType = "query",
                    value = "Container id",
                    dataType = "string"),
            @ApiImplicitParam(name = "participantId",
                    paramType = "query",
                    value = "Participant id (MSP)",
                    dataType = "string"),
            @ApiImplicitParam(name = "carrierAssignedId",
                    paramType = "query",
                    value = "Reference number assigned by a carrier to identify a specific shipment, such as a booking reference number.",
                    dataType = "string"),
            @ApiImplicitParam(name = "supplyChainId",
                    paramType = "query",
                    value = "Supply chain id",
                    dataType = "string"),
            @ApiImplicitParam(name = "shippingOrderId",
                    paramType = "query",
                    value = "Reference number to identify a Shipping Order.",
                    dataType = "string"),
            @ApiImplicitParam(name = "contentType",
                    paramType = "query",
                    value = "Content Type",
                    dataType = "string"),
            @ApiImplicitParam(name = "contentTypeSchemeVersion",
                    paramType = "query",
                    value = "Version of the content type",
                    dataType = "string"),
            @ApiImplicitParam(name = "statusLocationId",
                    paramType = "query",
                    value = "ID of the location",
                    dataType = "string"),
            @ApiImplicitParam(name = "emptyFullIndicator",
                    paramType = "query",
                    value = "Information about the container status “EMPTY” or “FULL”.",
                    dataType = "string"),
            @ApiImplicitParam(name = "statusTypeCode",
                    paramType = "query",
                    value = "A code/text signifying the type of this transport event.",
                    dataType = "string"),
            @ApiImplicitParam(name = "rfidTransportEquipment",
                    paramType = "query",
                    value = "An identifier for use in tracing this piece of transport equipment (for example an intermodal shipping container), such as the EPC number used in RFID",
                    dataType = "string"),
            @ApiImplicitParam(name = "rfidTransportHandlingUnit",
                    paramType = "query",
                    value = "An identifier for use in tracing this transport handling unit (for example a train or a wagon), such as the EPC number used in RFID.",
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

            //  Get messages from couchdb
            CouchDbQueryProcessor processor = new CouchDbQueryProcessor(params, connectorHolderService.connector.getConfig(), requestHolder.getUser())
            processor.query()
            List<UBLChaincodeTO> messages = processor.getMessages()

            //  If no messages were found
            if (messages.isEmpty()) {
                logger.info("No messages found")
                render ResponseBuilder.buildResponseAsJson(HttpStatus.NOT_FOUND, ERROR_NO_MESSAGES_FOUND)
                return
            }

            //  Decrypt messages and get those as SmartlogApiRequests
            MessageDecryptor md = new MessageDecryptor(connectorHolderService.connector)
            List<UBLChaincodeTO> decryptedMessages = md.decryptAndGetAsUBLChaincodeTOList(messages, requestHolder.getUser())
            List<SmartlogApiRequest> resp = SmartlogApiRequest.parseFromUblChaincodeToList(decryptedMessages)

            //  Set contentType as JSON, so receiver can automatically detect correct type
            response.setContentType("application/json")

            render EntityUtil.ObjectToJson(resp)

        } catch(Exception e) {

            logger.error(e.getMessage(), e)
            render ResponseBuilder.buildResponseAsJson(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
        }
    }
}
