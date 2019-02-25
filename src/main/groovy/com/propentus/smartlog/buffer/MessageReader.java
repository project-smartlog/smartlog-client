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
import com.propentus.common.util.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles reading of the UBL-files from the buffer
 */
public class MessageReader extends MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageReader.class);

    /**
     * Default constructor
     * Constructs buffer folder from configs
     * @throws ConfigurationException
     */
    public MessageReader() throws ConfigurationException {
        super();
        logger.debug("Initializing MessageReader");
    }

    /**
     * Constructor that enables new buffer folder to be given
     * @param newBufferFolder
     */
    public MessageReader(String newBufferFolder) {
        super(newBufferFolder);
        logger.debug("Initializing MessageReader");
    }

    /**
     * Reads messages from the buffer and parses those back to UBLChaincodeTOs
     * @return
     */
    public Map<String, String> readMessages() throws IOException {

        Map<String, String> messages = new HashMap<String, String>();

        //  Read files from disk
        List<Path> filesInFolder = Files.walk(Paths.get(bufferFolder))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

        //  Read contents and parse to UBLChaincodeTOs
        for (Path p : filesInFolder) {

            String content = FileUtil.readFileAsString(p.toString());
            messages.put(p.toString(), content);
        }

        return messages;
    }

}
