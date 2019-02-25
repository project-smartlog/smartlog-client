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

package com.propentus.smartlog.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC


/**
 * Logs stuff related to requests, like what controller and action was called and how long the request took.
 */
class RequestLoggingInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class)
    int order = 199
    long start = 0

    public RequestLoggingInterceptor() {

        //  Define in which controllers and actions the RequestLoggingInterceptor is used
        match(controller: "smartlogapi").excludes(action: "doc")
        match(controller: "api").excludes(action: "index")
    }

    boolean before() {

        //  Create random UUID for every request, so we can map things that happened in same request
        String uuid = UUID.randomUUID().toString()

        //  Put uuid to MDC so logger can use it
        MDC.put("uuid", uuid)

        logger.info("Requesting API: " + controllerName + "/" + actionName)

        //  Set start time, so we can count the elapsed time in request in after()-method
        start = System.currentTimeMillis();

        //  true so execution can continue
        true
    }

    boolean after() {

        // Count how many seconds request/response took
        long elapsedTimeMillis = System.currentTimeMillis() - start
        Double elapsedTimeSec = elapsedTimeMillis/1000F

        logger.info("Request took " + elapsedTimeSec + " seconds.")
        logger.info("Request finished, response code: " + response.getStatus())

        //  Remove uuid from MDC
        MDC.remove("uuid")
        true
    }

    void afterView() {
        // no-op
    }
}
