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
import com.propentus.common.util.StringUtil
import com.propentus.common.util.grails.GrailsUtil
import com.propentus.smartlog.service.EntityService
import io.swagger.annotations.*
import org.core.Order
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

/**
 * Controller for DBE Core like API
 */
@Api(value = 'dbe', description = 'DBE-Core API', tags = ["DBE-Core API"])
@SwaggerDefinition(
        info = @Info(
                description = "DBE-Core API description" ,
                version = "V1.0.0",
                contact = @Contact(
                        name = "Support",
                        email = "smartlog.user@propentus.com"
                ),
                title = "DBE-Core API"

        ),
        externalDocs = @ExternalDocs(value = "DBE-Core spesification:", url = "")
)
class DBECoreController {

    private static final Logger logger = LoggerFactory.getLogger(DBECoreController.class)

    def swaggerService
    RequestHolderService requestHolder = GrailsUtil.getBean(RequestHolderService.class)
    EntityService entityService

    def index() {
        if (request.getHeader('accept') && request.getHeader('accept').indexOf(MediaType.APPLICATION_JSON_VALUE) > -1) {
            String json = swaggerService.getJsonByName("DBECore")
            render contentType: MediaType.APPLICATION_JSON_UTF8_VALUE, text: json
        }
        else {
            redirect uri: swaggerService.getSwaggerUiRedirectUri(request)
        }
    }

    @ApiOperation(
            value = "Create Order",
            nickname = "createOrder",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "POST",
            response = String.class,
            notes = 'Used to submit a new order to supplier. DBE Core Supplier needs to implement this API operation to receive orders from DBE Core Buyer.'
    )
    def createOrder() {
        logger.info("In createOrder")

        String requestBody = requestHolder.getRequest()
        //logger.info("Request body for parsing:" + requestBody)

        //Try to parse JSON to right DBECore-object
        Order object = null
        try {
            object = EntityUtil.JsonToObject(requestBody, Order.class)
        }
        catch(Exception e) {
            e.printStackTrace()
            render ResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Unknown UBL-message type, expected 'Forwarding Instructions'")
            return
        }

        //Fetch ID from DBE-Core message
        String id = object.getID();
        if(StringUtil.isEmpty(id)) {
            render ResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST, ERROR_MESSAGE_ID_MISSING)
            return
        }

        //Get current ApiUser from request holder and set right UBL-message type
        if(!entityService.saveMessage(requestHolder.getUser(), id, requestBody)) {
            render ResponseBuilder.buildResponse(HttpStatus.CONFLICT, ERROR_MESSAGE_ID_EXISTS)
            return
        }

        render ResponseBuilder.buildResponse(HttpStatus.OK, VALID_MESSAGE_REQUEST_OK)
    }

    @ApiOperation(
            value = "Update Order",
            nickname = "updateOrder",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "POST",
            response = String.class,
            notes = 'Can be used to update an existing order by order ID'
    )
    def updateOrder() {
        render "OK"
    }



}
