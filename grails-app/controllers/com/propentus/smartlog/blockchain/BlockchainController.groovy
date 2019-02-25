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

package com.propentus.smartlog.blockchain

import com.propentus.common.util.grails.GrailsUtil
import com.propentus.iot.BlockchainConnector
import com.propentus.iot.chaincode.UBLChaincodeService
import org.hyperledger.fabric.sdk.Channel

class BlockchainController {

    def connectorHolderService

    def index() {

        boolean alive = false
        String channelName = null

        //Check that connection is established on startup and return channel name
        BlockchainConnector connector = connectorHolderService.connector
        if(connector != null) {
            alive = true;
            Channel channel = connectorHolderService.connector.getChannel()
            if(channel != null) {
                channelName = channel.getName()
            }
        }
        return[alive: alive, channelName: channelName]
    }

    def channelInfo() {
        Channel channel = connectorHolderService.connector.getChannel()
        if(channel == null) {
            render "Not joined channel"
        }
        else {
            render "joined channel: " + channel.getName()
            render (template: "index")
        }
    }

    def callMethod() {
        String methodName = params.method

        BlockchainConnector connector = new BlockchainConnector()
        UBLChaincodeService service = new UBLChaincodeService(connector)

        if(methodName.equals("addMessage")) {
s
            render "Message added"
            return
        }
        else if (methodName.equals("getMessage")) {
            String key = params.key
            String message = service.getMessage(key)
            render "Message:" + message
            return
        }
        else if (methodName.equals("getKeys")) {
            String response = service.getKeys()
            render "Keys:" + response
            return
        }

        render "Unknown method:" + methodName

    }

    def connect() {

        try {
            //Start blockchain connector on application startup and save reference to singleton grails bean
            BlockchainConnector connector = new BlockchainConnector()
            ConnectorHolderService holder = GrailsUtil.getBean(ConnectorHolderService.class)
            holder.connector = connector
        }
        catch (Exception e) {
            e.printStackTrace()
            render "Error happened:" + e.getMessage();
            return
        }

        render "Connection created!"
    }

}
