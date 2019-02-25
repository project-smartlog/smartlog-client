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


import com.propentus.common.util.EntityUtil
import com.propentus.common.util.grails.GrailsUtil
import com.propentus.iot.chaincode.model.UBLChaincodeTO
import com.propentus.smartlog.blockchain.ConnectorHolderService
import com.propentus.smartlog.query.CouchDbQueryProcessor
import io.swagger.annotations.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

/**
 * Reporting API can be used to get all messages with only public attributes.
 *
 * Gets messages from CouchDB and removes encryption keys and real messages before rendering the messages
 * back to the requester.
 *
 */
@Api(value = 'reportingApi', description = 'Smartlog reporting API', tags = ["reporting API"])
@SwaggerDefinition(
	info = @Info(
			description = "Smartlog :" ,
			version = "V1.2.0",
			contact = @Contact(
				name = "Support",
				email = "smartlog.user@propentus.com"
			 ),
			title = "Smartlog reporting API"
	)
)
class ReportingApiController {

	//Error messages contents
    private static final String ERROR_NO_MESSAGES_FOUND = "Messages were not found with given criteria; No messages found"

	static scope = "request"
	
	private static final Logger logger = LoggerFactory.getLogger(ReportingApiController.class)

	def swaggerService
	RequestHolderService requestHolder = GrailsUtil.getBean(RequestHolderService.class)
	ConnectorHolderService connectorHolderService

    /**
     * Show Swagger UI to reporting API.
     *
     * @return
     */
	def doc() {
		if (request.getHeader('accept') && request.getHeader('accept').indexOf(MediaType.APPLICATION_JSON_VALUE) > -1) {
			String json = swaggerService.getJsonByName("ReportingApi")
			render contentType: MediaType.APPLICATION_JSON_UTF8_VALUE, text: json
		}
		else {
			redirect (uri: swaggerService.getSwaggerUiRedirectUri(request))
		}
	}

	@ApiOperation(
			value = "Find messages by criteria",
			nickname = "findMessages",
			produces = "application/json",
			httpMethod = "GET",
			response = String.class,
			notes = "Response returned as an array of documents. If no parameters are given, all messages will be fetched."
	)
	@ApiResponses([
			@ApiResponse(code = 404, message = "Messages were not found with given criteria; No messages found"),
			@ApiResponse(code = 200, message = "Request OK"),
            @ApiResponse(code = 500, message = "Error happened while trying to send data to blockchain; Couldn't connect to Blockchain. Requests cannot be made.")
	])
	@ApiImplicitParams([
			@ApiImplicitParam(name = "documentId",
					paramType = "query",
					value = "Document id",
					dataType = "string"),
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
					dataType = "string"),
			@ApiImplicitParam(name = "senderParty",
					paramType = "query",
					value = "Sender party",
					dataType = "string"),
			@ApiImplicitParam(name = "rfidTransportEquipment",
					paramType = "query",
					value = "Transport equipment RFID",
					dataType = "string"),
			@ApiImplicitParam(name = "rfidTransportHandlingUnit",
					paramType = "query",
					value = "Transport handling unit RFID",
					dataType = "string"),
			@ApiImplicitParam(name = "statusTypeCode",
					paramType = "query",
					value = "Status type code",
					dataType = "string"),
			@ApiImplicitParam(name = "timestamp",
					paramType = "query",
					value = "Timestamp",
					dataType = "string")
	])
	/**
	 * Find messages with parameters.
	 */
	def findMessages() {

		logger.info("findMessageByCriteria params:" + params)
        params.report = "true"

        try {
            CouchDbQueryProcessor processor = new CouchDbQueryProcessor(params, connectorHolderService.connector.getConfig(), requestHolder.getUser())
            processor.query()
            List<UBLChaincodeTO> messages = processor.getMessages()

            // If no messages were found
            if (messages.isEmpty()) {
				logger.info("No messages found with given criterias")
                render ResponseBuilder.buildResponseAsJson(HttpStatus.NOT_FOUND, ERROR_NO_MESSAGES_FOUND)
                return
            }
            // Remove encrypted keys and messages from every message
            for(UBLChaincodeTO s : messages){
                s.encryptedMessage = null
                for(UBLChaincodeTO.Participant p : s.participants){
                    p.encryptedKey = null
                }
            }

            render EntityUtil.ObjectToJson(messages)

        } catch(Exception e) {

            logger.error("Internal server error", e)
            render ResponseBuilder.buildResponseAsJson(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
        }
	}
}
