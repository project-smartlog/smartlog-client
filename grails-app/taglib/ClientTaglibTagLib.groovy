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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ClientTaglibTagLib {
    static defaultEncodeAs = [taglib:'none']
    static namespace = "smartlog"
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    /**
     * Vertical spacer to help create space vertically between elements
     *
     * @attr	height	REQUIRED	Space between elements
     *
     */
    def verticalSpacer = { attrs ->
        out << "<div style=\"clear: both; height: ${ attrs.height }px\"></div>\n"
    }

    def dateTime = { attrs ->

        String value = attrs.value

        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("d.M.yyyy H.mm");
            LocalDateTime dateTime = LocalDateTime.parse(value, inputFormatter);
            String date = dateTime.format(outputFormatter)
            out << date

        } catch (Exception e) {
            out << value
        }
    }

}
