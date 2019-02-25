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

import com.propentus.common.util.grails.GrailsUtil
import com.propentus.common.util.http.BasicAuthenticationParser
import com.propentus.iot.BlockchainConnector
import com.propentus.iot.configs.ConfigReader
import com.propentus.iot.configs.OrganisationConfiguration
import com.propentus.smartlog.blockchain.ConnectorHolderService
import com.propentus.smartlog.datasource.couchdb.entities.ApiUser
import com.propentus.smartlog.service.EntityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletRequest

/**
 * Handles authentication and authorization to reporting API.
 */
class ReportingApiInterceptor {

	int order = 200

	private static final String ERROR_INVALID_CREDENDIALS = "Invalid credentials given. No access rights.";
	private static final String ERROR_NOT_CONNECTED = "Couldn't connect to Blockchain. Requests cannot be made.";

	private static final Logger logger = LoggerFactory.getLogger(ReportingApiInterceptor.class)

	static scope = "request"

	ConnectorHolderService connectorHolderService
	EntityService entityService

    ReportingApiInterceptor() {
		match(controller: ~/(reportingApi)/)
	}

    boolean before() {

		//Check that connection to Blockchain is working, if not, try to reconnect
		if(connectorHolderService.getConnector() == null) {
			try {
				BlockchainConnector connector = new BlockchainConnector()
				connectorHolderService.setConnector(connector)
			}
			catch (Exception e) {
				e.printStackTrace()
				render ResponseBuilder.buildResponseAsJson(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_NOT_CONNECTED)
				return;
			}
		}

        ConfigReader reader = new ConfigReader()
        OrganisationConfiguration configuration = reader.getOrganisationConfiguration()
        ApiUser user

        if(actionName.equals("doc") || actionName == null) {
            return true
        }

		RequestHolderService holder = GrailsUtil.getBean(RequestHolderService.class)

		user = checkAuthn(request, configuration)
		if (user == null) {
			return false
		}

		holder.setUser(user)

		//Validate request
		String requestBody = request.reader.text // Get XML content from body
		logger.info("Received request:" + requestBody)
		
		holder.setRequest(requestBody)

		//Ignore GET request body validation, because no such thing exists
		String method = request.getMethod()
		if(method.equals("POST")) {
			//Calling UBL-API methods, validate XML in request body
			def xml = null
			try {
				xml = new XmlSlurper().parseText(requestBody)
			} catch (Exception x) {
				render ResponseBuilder.buildResponseAsJson(HttpStatus.BAD_REQUEST, "Could not parse request as XML")
				return false
			}
		}

		return true
	}

    boolean after() { true }

    void afterView() {
        // no-op
    }

	/**
	 * Check for authorization
	 * @param request
	 * @param configuration
	 * @return
	 */
	private ApiUser checkAuthn(HttpServletRequest request, OrganisationConfiguration configuration) {

		BasicAuthenticationParser parser = new BasicAuthenticationParser(request)
		String username = request.getParameter("username")
		String password = request.getParameter("password")

		String authToken = parser.createAuthnToken(username, password)

        if (!authToken) {
            parser.setAuthRequiredResponse(response)
            return null
        }

		logger.debug("Checking for authorization token: '{}'", authToken)
		ApiUser user = entityService.findApiUser(authToken)
		if(user == null) {
			parser.setAuthRequiredResponse(response)
			render ResponseBuilder.buildResponseAsJson(HttpStatus.UNAUTHORIZED, ERROR_INVALID_CREDENDIALS)
			logger.debug("User not found.")
			return null
		}
		logger.debug("Found ApiUser with MSPID: '{}'", user.organisation)
		return user
	}
	
}
