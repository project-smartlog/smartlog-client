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

import com.propentus.iot.BlockchainConnector
import com.propentus.smartlog.api.ResponseBuilder
import com.propentus.smartlog.blockchain.ConnectorHolderService
import com.propentus.smartlog.service.EntityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

/**
 * Handles console specific authentication.
 *
 * Checks first if connection to the blockchain is initialized and if not, tries to establish the connection. If
 * connection establishment fails, returns error message to UI.
 *
 * If this client is configured as NORMAL/ENDORSEMENT authentication is not requested.
 */
class ConsoleInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleInterceptor.class)

    private static final String ERROR_INVALID_CREDENDIALS = "Invalid credendials given. No access rights.";
    private static final String ERROR_NOT_CONNECTED = "Couldn't connect to Blockchain. Requests cannot be made.";

    static scope = "request"

    ConnectorHolderService connectorHolderService
    EntityService entityService

    //  This interceptor is only used in ConsoleController
    ConsoleInterceptor() {
        match(controller: ~/(console)/)
    }

    boolean before() {

        //Check that connection to Blockchain is working, if not, try to reconnect
        if(connectorHolderService.getConnector() == null) {

            try {
                BlockchainConnector connector = new BlockchainConnector();
                connectorHolderService.setConnector(connector);
            } catch (Exception e) {
                e.printStackTrace();
                render ResponseBuilder.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_NOT_CONNECTED)
                return;
            }
        }

        //  If this peer is NORMAL or ENDORSEMENT, no authentication needed
        if (!connectorHolderService.getConnector().config.isCloudInstallation()) {
            return true
        }

        //  Check if user has authenticated correctly
        if (!session.loggedIn && actionName != "login" && actionName != "auth") {
            redirect (controller: "console", action: "login")
            return false
        }

        return true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
