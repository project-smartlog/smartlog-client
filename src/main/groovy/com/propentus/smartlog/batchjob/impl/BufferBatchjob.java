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

package com.propentus.smartlog.batchjob.impl;

import com.propentus.common.exception.ConfigurationException;
import com.propentus.common.util.EntityUtil;
import com.propentus.common.util.file.FileUtil;
import com.propentus.common.util.grails.GrailsUtil;
import com.propentus.iot.BlockchainConnector;
import com.propentus.iot.BlockchainManager;
import com.propentus.iot.chaincode.UBLChaincodeService;
import com.propentus.iot.chaincode.model.UBLChaincodeTO;
import com.propentus.iot.configs.OrganisationConfiguration;
import com.propentus.smartlog.api.UBLParser;
import com.propentus.smartlog.api.model.SmartlogApiRequest;
import com.propentus.smartlog.batchjob.Batchjob;
import com.propentus.smartlog.buffer.MessageReader;
import com.propentus.smartlog.datasource.couchdb.entities.ApiUser;
import com.propentus.smartlog.exceptions.WrongDateFormatException;
import com.propentus.smartlog.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Batchjob for the buffer
 */
public class BufferBatchjob implements Batchjob{

    private static final Logger logger = LoggerFactory.getLogger(BufferBatchjob.class);
    private BlockchainConnector connector = null;

    /**
     * Gets BlockchainConnector from ConnectorHolderService
     */
    public BufferBatchjob(BlockchainConnector bc) {
        this.connector = bc;
    }

    /**
     * Read messages from the disk, try to send those to Blockchain and if sending was succesful,
     * remove message from disk.
     */
    @Override
    public void process() {

        OrganisationConfiguration configuration = connector.getConfig();
        EntityService entityService = GrailsUtil.getBean(EntityService.class);

        try {
            MessageReader reader = new MessageReader();
            Map<String, String> messages = reader.readMessages();

            //  If no messages was found, just end the processing
            if (messages.size() == 0) {
                logger.debug("No messages to process.");
                return;
            }

            for (Map.Entry<String, String> entry : messages.entrySet()) {

                String path = entry.getKey();
                String message = entry.getValue();

                if (isUblMessage(message)) {
                    handleUblMessage(message, path);
                    break;
                }

                if (isUblChaincodeMessage(message)) {
                    handleUblChaincodeMessage(message, path);
                    break;
                }

                if (isSmartlogApiRequest(message)) {
                    handleSmartlogApiRequest(message, path);
                    break;
                }
            }

        } catch (ConfigurationException e) {
            logger.error("Couldn't read configuration file from disk");
        } catch (IOException e) {
            logger.error("Couldn't read files from the buffer.");
        } catch (WrongDateFormatException e) {
            e.printStackTrace();
        }
    }



    @Override
    public long getInterval() {
        return 1800000L;
    }

    /**
     * Gets authToken from path
     *
     * Splits path using default file separator and gets the last one of the parts
     * Then splits the filename using the "_"-separator and returns first one of the split parts.
     *
     * @param path
     * @return
     */
    private String getAuthTokenFromPath(String path) {

        String pattern = Pattern.quote(FileSystems.getDefault().getSeparator());
        String[] splittedFileName = path.split(pattern);

        String filename = splittedFileName[splittedFileName.length - 1];
        String authToken = filename.split("_")[0];

        return authToken;
    }

    /**
     * Tries to find ApiUser with authentication token from filename
     * @param path
     * @return
     */
    private ApiUser getApiUserFromPath(String path) {

        OrganisationConfiguration configuration = connector.getConfig();
        EntityService entityService = GrailsUtil.getBean(EntityService.class);

        //  Check if cloud installation
        if (configuration.isCloudInstallation()) {

            //  Get ApiUser from filename using token
            String authToken = getAuthTokenFromPath(path);
            ApiUser user = entityService.findApiUser(authToken);

            return user;
        }

        return null;
    }

    /**
     * Check if message can be parsed from UBL to UBLChaincodeTO
     * @param message
     * @return
     * @throws WrongDateFormatException
     */
    private boolean isUblMessage(String message) throws WrongDateFormatException {
        try {

            UBLParser parser = new UBLParser(message);
            UBLChaincodeTO ublMessage = parser.build();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Handles pure UBL-message and tries to send it to blockchain
     *
     * If client is cloud version, get user from request and use that user data to get correct cryptography
     * to encrypt the message.
     *
     * Otherwise use local cryptography to encrypt the message.
     *
     * If putting data to blockchain fails, message doesn't get added to buffer again
     *
     * If everything went ok, deletes the message from buffer.
     *
     * @param message
     * @param path
     */
    private void handleUblMessage(String message, String path) {

        //  Try to send
        try {

            ApiUser user = getApiUserFromPath(path);

            UBLParser parser = new UBLParser(message);
            UBLChaincodeTO ublMessage = parser.build();
            BlockchainManager manager = new BlockchainManager(connector, user);
            manager.sendUsingBuffer(ublMessage, message);

            //  Try to remove message from disk
            FileUtil.deleteFile(path);

        } catch (WrongDateFormatException wdfe) {
            logger.error(wdfe.getMessage(), wdfe);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("Couldn't get chain or encrypt message.");
        }

    }

    /**
     * Checks if message can be parsed straight to UBLChaincodeTO
     *
     * If connection to blockchain has failed on the last step, then messages get put to buffer as
     * UBLChaincodeTO and this check gets those.
     *
     * @param message
     * @return
     */
    private boolean isUblChaincodeMessage(String message) {
        try {
            UBLChaincodeTO ublMessage = EntityUtil.JsonToObject(message, UBLChaincodeTO.class);
            return ublMessage != null && !ublMessage.getDocumentID().isEmpty();

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses message to UBLChaincodeTO and tries to send it to blockchain using UBLChaincodeService
     * because the message has already been parsed and encrypted.
     *
     * If everything went ok, message gets removed from buffer.
     *
     * @param message
     * @param path
     */
    private void handleUblChaincodeMessage(String message, String path) {
        UBLChaincodeTO ublMessage = EntityUtil.JsonToObject(message, UBLChaincodeTO.class);

        UBLChaincodeService service = new UBLChaincodeService(connector);

        try {
            service.addMessage(ublMessage);

            //  Try to remove message from disk
            FileUtil.deleteFile(path);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("Couldn't add message to blockchain.");
        }

    }

    /**
     * Checks if message can be parsed to SmartlogApiRequest
     *
     * @param message
     * @return true if parse was successful and false otherwise
     */
    private boolean isSmartlogApiRequest(String message) {
        try {
            SmartlogApiRequest req = EntityUtil.JsonToObject(message, SmartlogApiRequest.class);
            return req != null && !req.documentId.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * If client is cloud version, get user from request and use that user data to get correct cryptography.
     *
     * Otherwise use local cryptography to encrypt the message.
     *
     * If putting data to blockchain fails, message doesn't get added to buffer again
     *
     * If everything went ok, deletes the message from buffer.
     *
     * Encrypts data using
     * @param message
     * @param path
     */
    private void handleSmartlogApiRequest(String message, String path) {
        SmartlogApiRequest req = EntityUtil.JsonToObject(message, SmartlogApiRequest.class);

        OrganisationConfiguration configuration = connector.getConfig();
        EntityService entityService = GrailsUtil.getBean(EntityService.class);

        ApiUser user = getApiUserFromPath(path);

        BlockchainManager manager = new BlockchainManager(connector, user);
        try {
            manager.sendUsingBuffer(req.parseToChaincodeTO(), req.content);

            //  Try to remove message from disk
            FileUtil.deleteFile(path);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("Couldn't add message to blockchain.");
        }
    }
}
