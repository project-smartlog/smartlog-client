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

package smartlogclient

import com.propentus.common.util.grails.GrailsUtil
import com.propentus.iot.BlockchainConnector
import com.propentus.smartlog.batchjob.BatchjobManager
import com.propentus.smartlog.blockchain.ConnectorHolderService
import com.propentus.smartlog.installer.SmartlogInstaller
import grails.util.Environment
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BootStrap {

    private static final Logger logger = LoggerFactory.getLogger(BootStrap.class)

    def init = { servletContext ->

        Boolean skipInstall = Boolean.parseBoolean(System.getProperty("smartlog.skipinstall"))

        if (!skipInstall) {

            if(new File(SmartlogInstaller.getFullPath() + "config.json").isFile()){

                SmartlogInstaller installer = new SmartlogInstaller()
                installer.install()
            }
        }

        try {
            //Start blockchain connector on application startup and save reference to singleton grails bean
            BlockchainConnector connector = new BlockchainConnector()
            ConnectorHolderService holder = GrailsUtil.getBean(ConnectorHolderService.class)
            holder.connector = connector

            //  Start BatchjobManager in its own thread
            Thread thread = new Thread(new BatchjobManager(connector))
            thread.start()

        }
        catch (Exception e) {
            logger.error("Couldn't connect to blockchain, maybe this is first install?", e)
            //throw new RuntimeException("Something went wrong when trying to start Smartlog Client")
        }
    }

    def destroy = {
    }
}
