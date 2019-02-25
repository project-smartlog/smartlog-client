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

import com.propentus.iot.BlockchainConnector
import com.propentus.iot.chaincode.KeystoreChaincodeService
import com.propentus.iot.chaincode.model.OrganisationChaincodeTO
import grails.converters.JSON

class KeystoreController {

    def index() { }

    def callMethod() {
        String methodName = params.method

        BlockchainConnector connector = new BlockchainConnector()
        KeystoreChaincodeService service = new KeystoreChaincodeService(connector)

        if(methodName.equals("addOrganisation")) {

            String mspID = params.mspID
            String publicKey = params.publicKey

            OrganisationChaincodeTO organisation = new OrganisationChaincodeTO()
            organisation.setMspID(mspID)
            organisation.setPublicKey(publicKey)
            if(service.addOrganisation(organisation)) {
                render "Organisation added"
            }
            else {
                render "Error adding organisation"
            }
            return
        }
        else if (methodName.equals("getOrganisation")) {
            String mspID = params.mspID
            OrganisationChaincodeTO organisation = service.getOrganisation(mspID)
            if(organisation != null) {
                render organisation as JSON
            }
            else {
                render "Not found"
            }
            return
        }
        else if (methodName.equals("getKeys")) {
            String response = service.getKeys()
            render "Keys:" + response
            return
        }

        render "Unknown method:" + methodName
    }

}
