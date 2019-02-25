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

package com.propentus.smartlog.dashboard

import com.propentus.iot.BlockchainConnector
import com.propentus.iot.chaincode.TransportChainChaincode
import com.propentus.iot.configs.ConfigReader
import com.propentus.iot.configs.FabricConfigLoader
import com.propentus.iot.configs.OrganisationConfiguration
import com.propentus.smartlog.blockchain.ConnectorHolderService
import com.propentus.smartlog.installer.SmartlogInstaller
import com.propentus.smartlog.query.CouchDbQueryProcessor
import com.propentus.smartlog.service.EntityService
import com.propentus.smartlog.utils.VersionResolver
import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.DockerClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class DashboardController {
    ConnectorHolderService connectorHolderService

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class)

    def index() {

        List statusList = []
        if(!SmartlogInstaller.isInstalled()){
            redirect(controller: "installer", action: "index")
            return
        }
        // Check general connection to blockchain
        if(!connectorHolderService) {

            try {
                BlockchainConnector connector = new BlockchainConnector();
                connectorHolderService.setConnector(connector);
                statusList.add(true)
            } catch (Exception e) {
                logger.error(e.getMessage(), e)
                statusList.add(false)
            }
        } else {

            //  Connection is already established
            statusList.add(true)
        }

        try{
            // Check connection to couchdb
            CouchDbQueryProcessor processor = new CouchDbQueryProcessor(params, connectorHolderService.connector.getConfig(), session.user)
            processor.query()
            processor.getMessages()
            statusList.add(true)
        } catch (Exception e){
            logger.error(e.getMessage(), e)
            statusList.add(false)
        }

        try{
            // Check connection to orderer
            FabricConfigLoader.getConfigBlock()
            statusList.add(true)
        } catch (Exception e){
            logger.error(e.getMessage(), e)
            statusList.add(false)
        }

        try{
            // Check connection to peer
            TransportChainChaincode service = new TransportChainChaincode(connectorHolderService.connector)

            if(service.getTransportChain("testChain") == null){
                statusList.add(false)
            }
            else {
                statusList.add(true)
            }
        } catch (Exception e){
            logger.error(e.getMessage(), e)
            statusList.add(false)
            Thread.sleep(15000)
        }

        return[statusList: statusList]
    }

    def runTests(){
        redirect(action : "index")
    }
}
