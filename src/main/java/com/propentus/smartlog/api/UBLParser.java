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

package com.propentus.smartlog.api;

import com.propentus.config.SupportedUBLMessageTypes;
import com.propentus.iot.chaincode.model.UBLChaincodeTO;
import com.propentus.smartlog.exceptions.WrongDateFormatException;
import com.propentus.smartlog.utils.DateFormatter;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.ExtensionContentType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionsType;
import oasis.names.specification.ubl.schema.xsd.transportationstatus_2.TransportationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.StringReader;

/**
 * XML-parser for UBL-messages. Tries to parse all information to UBL-chaincode object and return it.
 */
public class UBLParser {

    private static Logger logger = LoggerFactory.getLogger(UBLParser.class);

    private static final String UBL_EXTENSION_SUPPLY_CHAIN = "supplyChainID";


    private XPath xPath;
    private Document doc;

    private Class<?> messageClass;

    /**
     * Validate xml and resolve its UBL type automatically from the XML
     * @param xml
     */
    public UBLParser(String xml) {

        validate(xml);

        //  Resolve right MessageType automatically from xml
        String documentName = doc.getDocumentElement().getTagName();
        SupportedUBLMessageTypes.MessageType messageType = SupportedUBLMessageTypes.MessageType.valueOf(documentName);
        this.messageClass = SupportedUBLMessageTypes.classMap.get(messageType);
    }

    public UBLParser(SupportedUBLMessageTypes.MessageType messageType, String xml) {
        this.messageClass = SupportedUBLMessageTypes.classMap.get(messageType);
        validate(xml);
    }

    /**
     * Set supply chain ID to UBL-message
     */
    public static UBLExtensionsType createSupplyChainExtension(UBLExtensionsType extensionsType, String supplyChainValue) {
        //Create extension
        NameType nameType = new NameType();
        nameType.setValue(UBL_EXTENSION_SUPPLY_CHAIN);
        UBLExtensionType extensionType = new UBLExtensionType();
        extensionType.setName(nameType);
        ExtensionContentType extensionContentType = new ExtensionContentType();
        extensionContentType.setAny(supplyChainValue);
        extensionType.setExtensionContent(extensionContentType);

        extensionsType.getUBLExtension().add(extensionType);
        return extensionsType;
    }

    /**
     * Try to parse sender party ID.
     * SenderParty.PartyIdentification.ID
     *(used with Transportation Status and Transportation Status Request)
     * @return
     * @throws XPathExpressionException
     */
    public String parseSenderParty() throws XPathExpressionException {
        XPathExpression exp = xPath.compile("//SenderParty//PartyIdentification//ID");
        Node node = (Node)exp.evaluate(doc, XPathConstants.NODE);
        if(node != null) {
            try {
                return node.getFirstChild().getNodeValue();
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
    }

    /*
    * Parse timestamp. Timestamp is created from issueDate and issueTime elements.
    * Used with all message types
    */
    public String parseTimestamp() throws XPathExpressionException, WrongDateFormatException {

        //Parse issueDate
        String issueDate = "";
        XPathExpression exp = xPath.compile("/*//IssueDate");
        Node node = (Node)exp.evaluate(doc, XPathConstants.NODE);
        if(node != null) {
            issueDate = node.getFirstChild().getNodeValue();
        }

        //Parse issueTime
        String issueTime = "";
        XPathExpression exp2 = xPath.compile("/*//IssueTime");
        Node node2 = (Node)exp2.evaluate(doc, XPathConstants.NODE);
        if(node2 != null) {
            issueTime = node2.getFirstChild().getNodeValue();
        }

        String timestamp = issueDate + " " + issueTime;
        String validDate = DateFormatter.formatDate(timestamp);
        if(validDate == null){
            throw new WrongDateFormatException("Invalid date format");
        }
        return validDate;
    }



    /**
     * Try to parse UBLExtension:supplyChainID from UBL-message.
     * Used in all UBL-messages.
     * @return
     */
    public String parseSupplyChainID() throws XPathExpressionException {

        //NodeList nodes = this.doc.getElementsByTagName("UBLExtension");

        //XPathExpression exp = xPath.compile("//UBLExtension//ExtensionContent");
        XPathExpression exp = xPath.compile("/*//UBLExtensions//UBLExtension[Name = 'supplyChainID']");

        Node node = (Node)exp.evaluate(doc, XPathConstants.NODE);
        if(node != null) {
           NodeList nodes = node.getChildNodes();
           for(int i = 0; i < nodes.getLength(); i++) {
               Node child = nodes.item(i);
               String childName = child.getNodeName();
               if(childName.contains(":")) {
                   String[] arr = childName.split(":");
                   if(arr.length != 0) {
                       childName = arr[1];
                       if (childName.equals("ExtensionContent")) {
                           return child.getFirstChild().getNodeValue();
                       }
                   }
               }
           }
        }
        return null;
    }


    /**
     * Try to parse container number from UBL-message.
     * Shipment.TransportHandlingUnit.TransportEquipment.ID
      (used with Forwarding Instructions, Packing List, Bill Of Lading, and Waybill)


      TransportEvent.ReportedShipment.TransportHandlingUnit.TransportEquipment.ID
      (used with Transportation Status)
     * @return
     */
    public String parseContainerNumber() throws XPathExpressionException {

        XPathExpression exp;
        if(messageClass == TransportationStatus.class) {
            exp = xPath.compile("//TransportEvent//ReportedShipment//TransportHandlingUnit//TransportEquipment//ID");
        }
        else {
            exp = xPath.compile("//Shipment//TransportHandlingUnit//TransportEquipment//ID");
        }
        Node node = (Node)exp.evaluate(doc, XPathConstants.NODE);
        if(node != null) {
            try {
                return node.getFirstChild().getNodeValue();
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
    }

    /**
     * Try to parse carried assigned ID from UBL-message.
     * Used in all UBL-messages except Packing List
     * @return
     */
    public String parseCarriedAssignedID() throws XPathExpressionException {

        XPathExpression exp = xPath.compile("/*//CarrierAssignedID");
        Node node = (Node)exp.evaluate(doc, XPathConstants.NODE);
        if(node != null) {
            try {
                return node.getFirstChild().getNodeValue();
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
    }

    /**
     * Try to parse carried assigned ID from UBL-message.
     * Used in all UBL-messages except Packing List
     * @return
     */
    public String parseID() throws XPathExpressionException {

        XPathExpression exp = xPath.compile("/*//ID");
        Node node = (Node)exp.evaluate(doc, XPathConstants.NODE);
        if(node != null) {
            try {
                return node.getFirstChild().getNodeValue();
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
    }

    /**
     * Try to parse container ID in GIAI format from UBL-message.
     *
     * Shipment.TransportHandlingUnit.TransportEquipment.TraceID
     * (used with Forwarding Instructions, Packing List, Bill Of Lading, and Waybill)
     *
     * TransportEvent.ReportedShipment.TransportHandlingUnit.TransportEquipment.TraceID
     *(used with Transportation Status)
     * @return
     */
    public String parseRFIDTransportEquipment() throws XPathExpressionException {

        XPathExpression exp;
        if(messageClass == TransportationStatus.class) {
            exp = xPath.compile("//TransportEvent//ReportedShipment//TransportHandlingUnit//TransportEquipment//TraceID");
        }
        else {
            exp = xPath.compile("//Shipment//TransportHandlingUnit//TransportEquipment//TraceID");
        }

        Node node = (Node)exp.evaluate(doc, XPathConstants.NODE);
        if(node != null) {
            try {
                return node.getFirstChild().getNodeValue();
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
    }

    /**
     * Try to parse a railroad car ID in GIAI format from UBL-message.
     * Shipment.TransportHandlingUnit.TraceID
     * (used with Forwarding Instructions, Packing List, Bill Of Lading, and Waybill)
     *
     * TransportEvent.ReportedShipment.TransportHandlingUnit.TraceID
     * (used with Transportation Status)
     * @return
     */
    public String parseRFIDTransportHandlingUnit() throws XPathExpressionException {

        XPathExpression exp;
        if(messageClass == TransportationStatus.class) {
            exp = xPath.compile("//TransportEvent//ReportedShipment//TransportHandlingUnit//TraceID");
        }
        else {
            exp = xPath.compile("//Shipment//TransportHandlingUnit//TraceID");
        }

        Node node = (Node)exp.evaluate(doc, XPathConstants.NODE);
        if(node != null) {
            try {
                return node.getFirstChild().getNodeValue();
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
    }

    /**
     * Try to parse shipping order ID from UBL-message.
     * Used in all UBL-messages except Packing List
     * @return
     */
    public String parseShippingOrderID() throws XPathExpressionException {

        XPathExpression exp = xPath.compile("/*//ShippingOrderID");
        Node node = (Node)exp.evaluate(doc, XPathConstants.NODE);
        if(node != null) {
            try {
                return node.getFirstChild().getNodeValue();
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
    }

    /**
     * Try to parse TransportationStatusTypeCode from UBL-message.
     * Used in Transportation Status and Transportation Status Request
     * @return
     */
    public String parseTransportationStatus() throws XPathExpressionException {

        XPathExpression exp = xPath.compile("/*//TransportationStatusTypeCode");
        Node node = (Node)exp.evaluate(doc, XPathConstants.NODE);
        if(node != null) {
            try {
                return node.getFirstChild().getNodeValue();
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
    }

    /**
     * Try to parse FullnessIndicationCode from UBL-message.
     * Shipment.TransportHandlingUnit.TransportEquipment.FullnessIndicationCode
     *(used in Forwarding Instructions, Packing List, Bill Of Lading, and Waybill)
     *
     * TransportEvent.ReportedShipment.TransportHandlingUnit.TransportEquipment.FullnessIndicationCode
     * (used with Transportation Status)
     *
     * Allowed values are either "EMPTY" or "FULL"
     * @return
     */
    public UBLChaincodeTO.FullEmptyIndicator parseFullnessIndication() throws XPathExpressionException {

        XPathExpression exp;
        if(messageClass == TransportationStatus.class) {
            exp = xPath.compile("//TransportEvent//ReportedShipment//TransportEquipment//FullnessIndicationCode");
        }
        else {
            exp = xPath.compile("//Shipment//TransportHandlingUnit//TransportEquipment//FullnessIndicationCode");
        }

        Node node = (Node)exp.evaluate(doc, XPathConstants.NODE);
        if(node != null) {
            String value = node.getFirstChild().getNodeValue();
            return UBLChaincodeTO.FullEmptyIndicator.valueOf(value);
        }
        return null;
    }


    private void validate(String xml) {
        try {

            InputSource source = new InputSource(new StringReader(xml));
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
            this.doc = doc;

            XPath xPath = XPathFactory.newInstance().newXPath();
            this.xPath = xPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse all information from UBL-message to chaincode object.
     * @return
     */
    public UBLChaincodeTO build() throws WrongDateFormatException {
        UBLChaincodeTO ublTO = new UBLChaincodeTO();
        try {
            ublTO.setTimestamp(parseTimestamp());
            ublTO.setContainerID(parseContainerNumber());
            ublTO.setSenderParty(parseSenderParty());
            ublTO.setCarrierAssignedID(parseCarriedAssignedID());
            ublTO.setDocumentID(parseID());
            ublTO.setRFIDTransportEquipment(parseRFIDTransportEquipment());
            ublTO.setRFIDTransportHandlingUnit(parseRFIDTransportHandlingUnit());
            ublTO.setShippingOrderID(parseShippingOrderID());
            ublTO.setStatusTypeCode(parseTransportationStatus());
            ublTO.setEmptyFullIndicator(parseFullnessIndication());
            ublTO.setSupplyChainID(parseSupplyChainID());
            ublTO.setContentType("UBL");

        } catch (XPathExpressionException e) {
            logger.error(e.getMessage(), e);
        }
        return ublTO;
    }


}
