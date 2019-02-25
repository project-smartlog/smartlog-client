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

package com.propentus.iot;

import com.propentus.common.exception.BlockchainException;
import com.propentus.common.util.EntityUtil;
import com.propentus.iot.chaincode.KeystoreChaincodeService;
import com.propentus.iot.chaincode.TransportChainChaincode;
import com.propentus.iot.chaincode.UBLChaincodeService;
import com.propentus.iot.chaincode.model.OrganisationChaincodeTO;
import com.propentus.iot.chaincode.model.TransportChaincodeTO;
import com.propentus.iot.chaincode.model.UBLChaincodeTO;
import com.propentus.iot.configs.OrganisationConfiguration;
import com.propentus.smartlog.api.model.SmartlogApiRequest;
import com.propentus.smartlog.buffer.MessageWriter;
import com.propentus.smartlog.datasource.couchdb.entities.ApiUser;
import com.propentus.smartlog.security.AesCryptoHandler;
import com.propentus.smartlog.security.CryptoUtil;
import com.propentus.smartlog.security.RsaCryptoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager class with handles all operations to blockchain when user sends data to blockchain or fetches data.
 */
public class BlockchainManager {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainManager.class);

    private static final String PROPENTUS_MSPID = "PropentusMSP";
    //Error constants
    private static String ERROR_TRANSPORTCHAIN_NOT_FOUND = "Couldn't find transport chain with ID: '{}'";
    private static String ERROR_TRANSPORTCHAIN_SENDER_NOT_MEMBER = "Couldn't find this organisation in transport chain, cannot send message.";

    private BlockchainConnector connector;

    //Chaincode services
    private TransportChainChaincode transportChainChaincode;
    private KeystoreChaincodeService keystoreChaincodeService;
    private UBLChaincodeService ublChaincodeService;

    //Cloud configuration fields
    private ApiUser cloudUser;

    public BlockchainManager(BlockchainConnector connector) {
        this.connector = connector;

        transportChainChaincode = new TransportChainChaincode(connector);
        keystoreChaincodeService = new KeystoreChaincodeService(connector);
        ublChaincodeService = new UBLChaincodeService(connector);
    }

    public BlockchainManager(BlockchainConnector connector, ApiUser cloudUser) {
        this.connector = connector;
        this.cloudUser = cloudUser;

        transportChainChaincode = new TransportChainChaincode(connector);
        keystoreChaincodeService = new KeystoreChaincodeService(connector);
        ublChaincodeService = new UBLChaincodeService(connector);

    }

    /**
     * Send UBL-message to blockchain
     *
     * If message sending fails, add message to buffer
     *
     * @param message
     */
    public void sendUBLMessage(UBLChaincodeTO ublMessage, String message) throws Exception {
        send(ublMessage, message, true);
    }

    /**
     * Sends UBL-message to blockchain using buffer
     *
     * If message sending fails, do not write message again to buffer
     *
     * @param ublMessage
     * @param message
     */
    public void sendUsingBuffer(UBLChaincodeTO ublMessage, String message) throws Exception {
       send(ublMessage, message, false);
    }

    /**
     * Main method for sending UBL-messages to blockchain
     *
     * Encrypts data and sends it to blockchain
     *
     * If sending to blockchain fails, message gets put into the buffer
     *
     * @param ublMessage
     * @param message
     * @param useBuffer
     */
    public void send(UBLChaincodeTO ublMessage, String message, boolean useBuffer) throws Exception {

        String messageToEncrypt = message;
        SmartlogApiRequest req = parseToSmartlogApiRequest(message);

        if (req != null && !req.documentId.isEmpty()) {
            messageToEncrypt = req.content;
        }

        try {

            String encryptedMessage = encrypt(ublMessage, messageToEncrypt);
            ublMessage.setEncryptedMessage(encryptedMessage);
            ublMessage.setOrganisationID(this.getSelfMspID());
            //Send message to Blockchain
            ublChaincodeService.addMessage(ublMessage);

        } catch (BlockchainException be) {

            //  If useBuffer is true, write message to buffer
            //  If the process fails in this step, write the plain UBL-message to buffer
            if (useBuffer) {

                MessageWriter writer = new MessageWriter();
                writer.writePlainUBL(message);
            }

            throw be;
        }
    }

    /**
     * Just for development purposes, sign message only for self.
     * @return
     */
    private String encryptWithoutChain(String message) {
        try {
            AesCryptoHandler aes = new AesCryptoHandler(message);
            String encryptedMsg = aes.encrypt();
            logger.debug("Encrypted message: '{}'", encryptedMsg);
            String aesKey = aes.getKey();

            return encryptedMsg;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Encrypt given message content to transport chain members. After this UBLChaincodeTO can be send to Blockchain
     * @param message
     * @return
     */
    private String encrypt(UBLChaincodeTO ublMessage, String message) throws BlockchainException, IOException, ClassNotFoundException {

        AesCryptoHandler aes = new AesCryptoHandler(message);
        String encryptedMsg = aes.encrypt();
        logger.debug("Encrypted message: '{}'", encryptedMsg);
        String aesKey = aes.getKey();

        //Try to find supply chain first.
        logger.debug("Trying to find transport chain with ID: '{}'", ublMessage.getSupplyChainID());
        TransportChaincodeTO[] transportChain = transportChainChaincode.getTransportChain(ublMessage.getSupplyChainID());

        if (transportChain == null) {
            logger.warn(ERROR_TRANSPORTCHAIN_NOT_FOUND, ublMessage.getSupplyChainID());
            throw new BlockchainException("Couldn't get transportChain from blockchain.");
        }

        if(!this.chainContainsSelf(transportChain[0])) {
            logger.warn(ERROR_TRANSPORTCHAIN_SENDER_NOT_MEMBER);
            throw new BlockchainException(ERROR_TRANSPORTCHAIN_SENDER_NOT_MEMBER);
        }

        //Filter participant list and loop organisation MSPID's in chain and find their public keys from ledger and encrypt AES key with each organisations public key
        List<String> participants = this.filterReceivingParticipants(transportChain[0]);
        //Add Propentus as a participant to all messages send to ledger
        participants.add(this.getPropentusMSPID());

        for (String MSPID : participants) {
            OrganisationChaincodeTO organisation = keystoreChaincodeService.getOrganisation(MSPID);
            if(organisation == null) {
                logger.error("Couldn't find certificates for organisation: '{}'", MSPID);
                continue;
            }

            //Add participant to UBL-message
            UBLChaincodeTO.Participant participant = new UBLChaincodeTO.Participant();
            String encryptedKey = encryptForMember(organisation, aesKey);
            participant.setEncryptedKey(encryptedKey);
            participant.setMSPID(MSPID);

            ublMessage.getParticipants().add(participant);
        }
        return encryptedMsg;
    }

    /**
     * Create encryption key for specific organisation
     * @param organisation
     * @param aesKey
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private String encryptForMember(OrganisationChaincodeTO organisation, String aesKey) throws IOException, ClassNotFoundException {

        String publicKeyEncoded = organisation.getPublicKey();
        //Convert base64 encoded public key to PublicKey object
        PublicKey publicKey = CryptoUtil.base64ToPublicKey(publicKeyEncoded);
        //Encode AES-key with public key
        logger.debug("Creating encryption key for org: '{}'", organisation.getMspID());
        KeyPair keyPair = new KeyPair(publicKey, null);
        String encryptedKey = RsaCryptoHandler.encrypt(aesKey, keyPair);
        logger.debug("Created encrypted key: '{}')", encryptedKey);
        return encryptedKey;
    }

    private String getSelfMspID() {
        OrganisationConfiguration configuration = this.connector.getConfig();
        //In cloud environment, MSPID is found in ApiUser
        if(configuration.isCloudInstallation()) {
            return cloudUser.getOrganisation();
        }
        return configuration.organisation.getMspid();
    }

    /**
     * Check if given transport chain contains this sender
     * @return
     */
    private boolean chainContainsSelf(TransportChaincodeTO transportChain) {

        for(String mspID : transportChain.getParticipants()) {
            if(mspID.equals(this.getSelfMspID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Filter receiving participants from transport chain. Receiving participants are selected by following rules:
     * 1. First member of the chain always get's the message
     * 2. Sender always get's the message
     * 3. Participants who are after sender in the chain, get's the message
     * @return
     */
    private List<String> filterReceivingParticipants(TransportChaincodeTO transportChain) {
        List<String> filtered = new ArrayList<String>();
        List<String> original = transportChain.getParticipants();

        //Add first member
        String first = original.get(0);
        filtered.add(first);

        String selfId = getSelfMspID();

        //  If my own organisation id is not in the list, add my own org
        //  Prevents having duplicate organisations if my own organisation is the first org in chain.
        if (!filtered.contains(selfId)) {
            filtered.add(selfId);
        }

        //Add participants after self
        int selfIndex = original.indexOf(selfId) + 1;
        int last = original.size();
        List<String> sublist = original.subList(selfIndex, last);
        filtered.addAll(sublist);

        return filtered;
    }

    /**
     * Get Propentus MSPID. Hardcoded value for now. Remove when unneeded.
     */
    private String getPropentusMSPID() {
        return PROPENTUS_MSPID;
    }

    /**
     * Check if message can be parsed to SmartlogApiRequest and the given object contains data
     * @param message
     * @return
     */
    private SmartlogApiRequest parseToSmartlogApiRequest(String message) {
        try {
            SmartlogApiRequest req = EntityUtil.JsonToObject(message, SmartlogApiRequest.class);
            return req;
        } catch (Exception e) {
            logger.debug("Message wasn't SmartlogApiRequest");
        }

        return null;
    }

}
