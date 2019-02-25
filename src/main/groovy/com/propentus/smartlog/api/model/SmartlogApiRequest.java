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

package com.propentus.smartlog.api.model;

import com.propentus.iot.chaincode.model.UBLChaincodeTO;
import com.propentus.iot.chaincode.model.UBLChaincodeTO.FullEmptyIndicator;

import java.util.ArrayList;
import java.util.List;

public class SmartlogApiRequest {

    public String content;
    public String contentType;
    public String contentTypeSchemeVersion;
    public String documentId;
    public String supplyChainId;
    public String containerId;
    public String senderParty;
    public String rfidTransportEquipment;
    public String rfidTranportHandlingUnit;
    public String statusTypeCode;
    public String timestamp;
    public String carrierAssignedId;
    public String shippingOrderId;
    public String statusLocationId;
    public FullEmptyIndicator emptyFullIndicator;

    public SmartlogApiRequest() {}

    public UBLChaincodeTO parseToChaincodeTO() {

        UBLChaincodeTO ublChaincodeTO = new UBLChaincodeTO();
        ublChaincodeTO.setTimestamp(this.timestamp);
        ublChaincodeTO.setSupplyChainID(this.supplyChainId);
        ublChaincodeTO.setStatusTypeCode(this.statusTypeCode);
        ublChaincodeTO.setShippingOrderID(this.shippingOrderId);
        ublChaincodeTO.setSenderParty(this.senderParty);
        ublChaincodeTO.setRFIDTransportHandlingUnit(this.rfidTranportHandlingUnit);
        ublChaincodeTO.setRFIDTransportEquipment(this.rfidTransportEquipment);
        ublChaincodeTO.setEmptyFullIndicator(this.emptyFullIndicator);
        ublChaincodeTO.setDocumentID(this.documentId);
        ublChaincodeTO.setContainerID(this.containerId);
        ublChaincodeTO.setCarrierAssignedID(this.carrierAssignedId);
        ublChaincodeTO.setContentType(this.contentType);
        ublChaincodeTO.setContentTypeSchemeVersion(this.contentTypeSchemeVersion);
        ublChaincodeTO.setStatusLocationId(this.statusLocationId);

        return ublChaincodeTO;
    }

    public static List<SmartlogApiRequest> parseFromUblChaincodeToList(List<UBLChaincodeTO> messages) {

        List<SmartlogApiRequest> smartlogApiMessages = new ArrayList<SmartlogApiRequest>();

        for (UBLChaincodeTO message : messages) {

            SmartlogApiRequest smartlogApiMessage = new SmartlogApiRequest();

            smartlogApiMessage.carrierAssignedId = message.getCarrierAssignedID();
            smartlogApiMessage.containerId = message.getContainerID();
            smartlogApiMessage.content = message.getDecryptedMessage();
            smartlogApiMessage.contentType = message.getContentType();
            smartlogApiMessage.contentTypeSchemeVersion = message.getContentTypeSchemeVersion();
            smartlogApiMessage.documentId = message.getDocumentID();
            smartlogApiMessage.supplyChainId = message.getSupplyChainID();
            smartlogApiMessage.emptyFullIndicator = message.getEmptyFullIndicator();
            smartlogApiMessage.rfidTranportHandlingUnit = message.getRFIDTransportHandlingUnit();
            smartlogApiMessage.rfidTransportEquipment = message.getRFIDTransportEquipment();
            smartlogApiMessage.senderParty = message.getSenderParty();
            smartlogApiMessage.shippingOrderId = message.getShippingOrderID();
            smartlogApiMessage.statusLocationId = message.getStatusLocationId();
            smartlogApiMessage.statusTypeCode = message.getStatusTypeCode();
            smartlogApiMessage.timestamp = message.getTimestamp();

            smartlogApiMessages.add(smartlogApiMessage);
        }

        return smartlogApiMessages;
    }
}
