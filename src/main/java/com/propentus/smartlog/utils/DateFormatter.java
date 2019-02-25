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

package com.propentus.smartlog.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateFormatter {

    private DateFormatter(){

    }

    public static String formatDate(String timestamp){
        List<DateTimeFormatter> formatterList = new ArrayList<>();
        formatterList.add(DateTimeFormatter.ofPattern("[yyyy-MM-dd][yyyy/MM/dd][yyyyMMdd][yyyy:MM:dd] [HH:mm:ss] [XXX][Z]"));
        formatterList.add(DateTimeFormatter.ofPattern("[yyyy-MM-dd][yyyy/MM/dd][yyyyMMdd][yyyy:MM:dd] [HH:mm:ss][XXX][Z]"));

        DateTimeFormatter outPutFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX");
        for(DateTimeFormatter d : formatterList){
            try{
                ZoneId timeZone = ZoneId.of("UTC");
                ZonedDateTime sdate = ZonedDateTime.parse(timestamp, d).withZoneSameInstant(timeZone);
                String date = sdate.format(outPutFormatter);
                if(date.endsWith("Z")){
                    date = date.replace("Z", "+00:00");
                    return date;
                }
                return date;
            }
            catch (Exception e){

            }
        }
        return null;
    }
}
