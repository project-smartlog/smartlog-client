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

package com.propentus.smartlog.installer

import org.slf4j.Logger
import org.slf4j.LoggerFactory


class InstallerController {

    private static final Logger logger = LoggerFactory.getLogger(InstallerController.class)

    def index() {
        return
    }

    def install() {
        try{
            SmartlogInstaller smartlogInstaller = new SmartlogInstaller(params)
            smartlogInstaller.install()
            logger.info("Installation successful")
            Thread.sleep(5000)
        }
        catch(Exception e){
            logger.error("Failed to install", e)
        }

        redirect(controller: "dashboard", action: "index")
    }

    def toDashboard(){
        redirect(controller: "dashboard", action: "index")
    }

}
