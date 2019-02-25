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

package com.propentus.smartlog.query;

import com.propentus.common.exception.ConfigurationException;
import com.propentus.common.util.StringUtil;
import com.propentus.iot.chaincode.model.UBLChaincodeTO;
import com.propentus.iot.configs.ConfigReader;
import com.propentus.iot.configs.OrganisationConfiguration;
import com.propentus.smartlog.datasource.couchdb.entities.ApiUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all of the possible data source query implementations.
 *
 * Gets parameters from request and maps those to UBL-chaincode parameters.
 *
 * Implementations then handle the parameters the way it sees fit.
 */
abstract class AbstractQueryProcessor {


    protected Map<String, String> parameters = new HashMap<String, String>();
    protected List<UBLChaincodeTO> messages = new ArrayList<UBLChaincodeTO>();

    private AbstractQueryProcessor() {}

    /**
     * Wanted parameters are setup in constructor
     *
     * @param params
     */
    public AbstractQueryProcessor(Map params, ApiUser user) throws ConfigurationException {

        /**
         * Get data from the parameters
         */

        String organizationId = (String) params.get("organisationId");
        String containerId = (String) params.get("containerId");
        String carrierAssignedId = (String) params.get("carrierAssignedId");
        String createdFrom = (String) params.get("createdFrom");
        String createdTo = (String) params.get("createdTo");
        String supplyChainId = (String) params.get("supplyChainId");
        String shippingOrderId = (String) params.get("shippingOrderId");

        // Added for Qlik reporting API
        String documentId = (String) params.get("documentId");
        String senderParty = (String) params.get("senderParty");
        String RFIDTransportEquipment = (String) params.get("rfidTransportEquipment");
        String RFIDTransportHandlingUnit = (String) params.get("rfidTransportHandlingUnit");
        String statusTypeCode = (String) params.get("statusTypeCode");

        //  Added for Smartlog API
        String contentType = (String) params.get("contentType");
        String contentTypeSchemeVersion = (String) params.get("contentTypeSchemeVersion");
        String statusLocationId = (String) params.get("statusLocationId");
        String emptyFullIndicator = (String) params.get("emptyFullIndicator");

        // Added for console
        String participantId = (String) params.get("participantId");

        Boolean report = Boolean.parseBoolean((String) params.get("report"));

        //Check if the method is called from QLik reporting API
        if(!report) {

            if (StringUtil.isEmpty(organizationId) &&
                    StringUtil.isEmpty(containerId) &&
                    StringUtil.isEmpty(carrierAssignedId) &&
                    StringUtil.isEmpty(createdFrom) &&
                    StringUtil.isEmpty(createdTo) &&
                    StringUtil.isEmpty(supplyChainId) &&
                    StringUtil.isEmpty(shippingOrderId) &&
                    StringUtil.isEmpty(documentId) &&
                    StringUtil.isEmpty(RFIDTransportEquipment) &&
                    StringUtil.isEmpty(RFIDTransportHandlingUnit) &&
                    StringUtil.isEmpty(statusTypeCode) &&
                    StringUtil.isEmpty(contentType) &&
                    StringUtil.isEmpty(contentTypeSchemeVersion) &&
                    StringUtil.isEmpty(statusLocationId) &&
                    StringUtil.isEmpty(emptyFullIndicator) &&
                    StringUtil.isEmpty(participantId)) {

                ConfigReader cr = new ConfigReader();
                OrganisationConfiguration config  = cr.getOrganisationConfiguration();

                if (config.isCloudInstallation()) {
                    organizationId = user.getOrganisation();

                } else {
                    organizationId = config.organisation.getMspid();
                }
            }
        }

        /**
         * Map parameters to smart contract model parameters.
         * Parameter values must match with smart contract model.
         *
         * If you add new parameters, check from UBL-chaincode what is the correct value and if it is totally new
         * parameter, add it also to UBL-chaincode.
         */

        parameters.put("organisationID", organizationId);
        parameters.put("containerID", containerId);
        parameters.put("carrierAssignedID", carrierAssignedId);
        parameters.put("createdFrom", createdFrom);
        parameters.put("createdTo", createdTo);
        parameters.put("supplyChainID", supplyChainId);
        parameters.put("shippingOrderID", shippingOrderId);

        // Added for Qlik reporting API
        parameters.put("documentID", documentId);
        parameters.put("senderParty", senderParty);
        parameters.put("RFIDTransportEquipment", RFIDTransportEquipment);
        parameters.put("RFIDTransportHandlingUnit", RFIDTransportHandlingUnit);

        //  Added for Smartlog API
        parameters.put("statusTypeCode", statusTypeCode);
        parameters.put("contentType", contentType);
        parameters.put("contentTypeSchemeVersion", contentTypeSchemeVersion);
        parameters.put("statusLocationId", statusLocationId);
        parameters.put("emptyFullIndicator", emptyFullIndicator);

        //  Added for console
        parameters.put("participantId", participantId);
    }
    /**
     * Make query to data source
     * @return
     */
    public abstract void query();

    /**
     * Parse what was got from datasource to UBLChaincodeTO
     * @param o
     * @return
     */
    abstract void parseToUblChaincodeTO(Object o);

    /**
     * Get messages that we got from query
     */
    public List<UBLChaincodeTO> getMessages() {
        return this.messages;
    }
}
