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

import ch.qos.logback.core.util.FileSize
import com.propentus.iot.configs.ConfigReader

import java.time.OffsetDateTime
import java.time.ZoneOffset

//Get current running environment
def ENV = grails.util.Environment.current.name
def LOG_FILE = "SmartlogClient" + ENV
def ORGANISATION_NAME = new ConfigReader().organisationConfiguration.organisation.mspid

ZoneOffset o = OffsetDateTime.now().getOffset();

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "["+ORGANISATION_NAME+"] [%d{yyyy-MM-dd HH:mm:ss.SSS" + o.toString() + "}] [%level] [%logger] [%X{uuid}] - %msg%n"
    }
}

appender("ROLLING", RollingFileAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "[%d{yyyy-MM-dd hh:mm:ss}] [%level] [%logger] - %msg%n"
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "/var/log/tomcat/"+ LOG_FILE + "_%d{yyyy-MM-dd_HH-mm}.log"
        maxHistory = 7 //Deletes older log files.
        totalSizeCap = FileSize.valueOf("200MB") // Max size allowed for the log files on disk
    }
}

root(INFO, ['STDOUT'])

loggerList = ['ROLLING', 'STDOUT']

logger('grails.app.controllers', DEBUG, loggerList, false)
logger('grails.app.services', DEBUG, loggerList, false)
logger('grails.app.jobs', DEBUG, loggerList, false)
logger('grails.app.domain', DEBUG, loggerList, false)
logger('grails.app.taglibs', DEBUG, loggerList, false)
logger('grails.app.init.swagger.example.BootStrap', DEBUG, loggerList, false)
logger('swagger.example.BootStrap', DEBUG, loggerList, false)
logger('org.apache.http.headers', INFO, loggerList, false)
logger('com.propentus', DEBUG, loggerList, false)
