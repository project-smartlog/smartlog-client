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

package com.propentus.smartlog.buffer;

import com.propentus.common.exception.ConfigurationException;
import com.propentus.common.util.EntityUtil;
import com.propentus.common.util.file.FileUtil;
import com.propentus.common.util.grails.GrailsUtil;
import com.propentus.iot.chaincode.model.UBLChaincodeTO;
import com.propentus.smartlog.api.RequestHolderService;
import com.propentus.smartlog.datasource.couchdb.entities.ApiUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Handles writing the UBL-messages to the buffer
 */
public class MessageWriter extends MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageWriter.class);

    /**
     * Default constructor
     * Constructs buffer folder from configs
     *
     * @throws ConfigurationException
     */
    public MessageWriter() throws ConfigurationException {
        super();
        logger.debug("Initializing MessageWriter");
    }

    /**
     * Constructor that enables new buffer folder to be given
     *
     * @param newBufferFolder
     */
    public MessageWriter(String newBufferFolder) {
        super(newBufferFolder);
        logger.debug("Initializing MessageWriter");
    }

    /**
     * Parses UBLChaincodeTO to JSON and saves it to bufferFolder
     *
     * @param message
     * @throws IOException
     */
    public void writeMessage(UBLChaincodeTO message) throws IOException {

        logger.info("Writing message to disk");
        String msg = EntityUtil.ObjectToJson(message);
        FileUtil.writeFile(bufferFolder, generateFilename(), msg, false);
    }

    /**
     * If blockchain connection fails in chain phase
     * we need to save the plain UBL
     */
    public void writePlainUBL(String message) throws IOException {

        logger.info("Writing plain UBL to disk");
        FileUtil.writeFile(bufferFolder, generateFilename(), message, false);
    }

    /**
     * Generates unique name for the file.
     *
     * Uses datetime and random hash.
     * Makes it easier to handle debugging of errors.
     *
     * @return
     */
    private String generateFilename() {
        logger.info("Generating filename for message");

        RequestHolderService holder = GrailsUtil.getBean(RequestHolderService.class);
        ApiUser user = holder.getUser();
        String token = "local";

        if (user != null) {
            token = user.getAuthToken();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
        String dateTime = LocalDateTime.now().format(formatter).toString();
        String uuid = UUID.randomUUID().toString();
        return token + "_" + uuid + "_" + dateTime;
    }
}
