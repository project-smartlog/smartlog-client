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

package com.propentus.smartlog.batchjob;

import com.propentus.iot.BlockchainConnector;
import com.propentus.smartlog.batchjob.impl.BufferBatchjob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BatchjobManager implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(BatchjobManager.class);
    List<Batchjob> batchjobs = new ArrayList<Batchjob>();
    BlockchainConnector connector = null;

    public BatchjobManager(BlockchainConnector bc) {
        this.connector = bc;
    }

    @Override
    public void run() {

        //  Add wanted batchjobs to the list
        batchjobs.add(new BufferBatchjob(connector));
        logger.info("Starting batchjobs: " + batchjobs);

        while(true) {

            for (Batchjob b : batchjobs) {

                logger.debug("Starting batchjob:" + b.getClass().getCanonicalName());
                b.process();
                sleep(b.getInterval());
            }
        }
    }

    /**
     * Sleep thread for the interval of the batchjob
     * @param milliseconds
     */
    private void sleep(long milliseconds) {
        logger.debug("BatchjobManager going to sleep for:" + milliseconds);
        try {
            Thread.sleep(milliseconds);
        }
        catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
