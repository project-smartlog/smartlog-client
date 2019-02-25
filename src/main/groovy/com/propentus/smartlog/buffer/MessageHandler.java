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
import com.propentus.iot.configs.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    protected String bufferFolder = "";

    /**
     * Default constructor
     * Constructs buffer folder from configs
     * @throws ConfigurationException
     */
    public MessageHandler() throws ConfigurationException {

        ConfigReader cr = new ConfigReader();
        bufferFolder = cr.getOrganisationConfiguration().fabricEnvPath + "buffer/";
        createBufferFolderIfNotExists();
        logger.debug("Initializing MessageHandler with folder: " + bufferFolder);
    }

    /**
     * Constructor that enables new buffer folder to be given
     * @param newBufferFolder
     */
    public MessageHandler(String newBufferFolder) {
        this.bufferFolder = newBufferFolder;
        createBufferFolderIfNotExists();
        logger.debug("Initializing MessageHandler with folder: " + bufferFolder);
    }

    /**
     * Create folder for buffer if not exists
     *
     * When MessageHandler is initialized first time, folder gets created
     */
    private void createBufferFolderIfNotExists() {

        File bufferFolder = new File(this.bufferFolder);

        if (!bufferFolder.exists()) {
            logger.info("Creating folder for the buffer.");
            bufferFolder.mkdirs();
        }
    }
}
