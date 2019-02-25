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

package com.propentus.config;

import com.propentus.common.util.EntityUtil;
import oasis.names.specification.ubl.schema.xsd.api.UBLMessageWrapper;
import oasis.names.specification.ubl.schema.xsd.billoflading_2.BillOfLading;
import oasis.names.specification.ubl.schema.xsd.forwardinginstructions_2.ForwardingInstructions;
import oasis.names.specification.ubl.schema.xsd.packinglist_2.PackingList;
import oasis.names.specification.ubl.schema.xsd.transportationstatus_2.TransportationStatus;
import oasis.names.specification.ubl.schema.xsd.transportationstatusrequest_2.TransportationStatusRequest;
import oasis.names.specification.ubl.schema.xsd.waybill_2.Waybill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.util.HashMap;

/**
 * Class containing configurations about supported UBL-messages types
 * If messages types are added remember to add them to UBLMessageWrapper class @XMLSeeAlso annotation
 */
public class SupportedUBLMessageTypes {

    private static Logger logger = LoggerFactory.getLogger(SupportedUBLMessageTypes.class);


    public static HashMap<MessageType, Class<?>> classMap = new HashMap<>();
    static {
            classMap.put(MessageType.ForwardingInstructions, ForwardingInstructions.class);
            classMap.put(MessageType.BillOfLading, BillOfLading.class);
            classMap.put(MessageType.PackingList, PackingList.class);
            classMap.put(MessageType.TransportationStatus, TransportationStatus.class);
            classMap.put(MessageType.TransportationStatusRequest, TransportationStatusRequest.class);
            classMap.put(MessageType.Waybill, Waybill.class);

            logger.debug("Supported UBL-message types: " + classMap);
    }



    public enum MessageType {
        ForwardingInstructions,
        Waybill,
        PackingList,
        BillOfLading,
        TransportationStatusRequest,
        TransportationStatus;
    }


    public static final Class[] SUPPORTED_UBL_MESSAGE =
        {
                UBLMessageWrapper.class,
                ForwardingInstructions.class,
                Waybill.class,
                PackingList.class,
                BillOfLading.class,
                TransportationStatusRequest.class,
                TransportationStatus.class
        };

    /**
     * Convert UBL-message name and XML to JAXB object
     * @param ublTypeName
     * @param xml
     * @return
     */
    public static Object convertUBLMessageToObject(String ublTypeName, String xml) {
        //Check that given UBL type is actually supported
        MessageType messageType = MessageType.valueOf(ublTypeName);
        if(messageType == null) {
            logger.warn("Not supported UBL-message type '{0}'", ublTypeName);
            logger.warn("Check class SupportedUBLMessageTypes, for supported types!");
            return null;
        }

        //Convert XML String to actual UBL-message object
        Class<?> clazz = classMap.get(messageType);
        try {
            Object obj = EntityUtil.XMLtoObject(clazz, xml);
            return obj;
        } catch (JAXBException e) {
            logger.warn(e.getMessage(), e);
        }

        return null;
    }

    /**
     * Convert UBL-message name and XML to JAXB object
     * @param ublTypeName
     * @param xml
     * @return
     */
    public static Object convertUBLMessageToObject(MessageType ublTypeName, String xml) {

        //Convert XML String to actual UBL-message object
        Class<?> clazz = classMap.get(ublTypeName);
        try {
            Object obj = EntityUtil.XMLtoObject(clazz, xml);
            return obj;
        } catch (JAXBException e) {
            logger.warn(e.getMessage(), e);
        }

        return null;
    }

 }
