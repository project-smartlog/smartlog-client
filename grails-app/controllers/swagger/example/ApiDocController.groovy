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

package swagger

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class ApiDocController {
    static responseFormats = ['json']
    static namespace = 'v1'
    static allowedMethods = [getDocuments: "GET"]

    SwaggerService swaggerService = new SwaggerService()
    def swaggerCacheService

    @Value("classpath*:**/webjars/swagger-ui/**/index.html")
    Resource[] swaggerUiResources

    def getDocuments() {
        if (request.getHeader('accept') && request.getHeader('accept').indexOf(MediaType.APPLICATION_JSON_VALUE) > -1) {
            try {

                String swaggerJson = swaggerCacheService.getJson()

                if (!swaggerJson) {

                    //	swaggerJson is wrong at first, load it twice and then cache it, FIXME
                    String dummyJson = swaggerService.generateSwaggerDocument()

                    swaggerJson = swaggerService.generateSwaggerDocument()

                    swaggerCacheService.setJson(swaggerJson)
                }

                render contentType: MediaType.APPLICATION_JSON_UTF8_VALUE, text: swaggerJson

            } catch (Exception e) {
                logger.error("Error loading Swagger JSON", e)
                render status: HttpStatus.INTERNAL_SERVER_ERROR,
                        text: 'Some small :) error occurred'
            }
        } else {
            String uri = "/webjars/swagger-ui" + getSwaggerUiFile() + "?url=" +request.getRequestURI()
            redirect uri: uri
        }
    }

    protected String getSwaggerUiFile() {
        try {
            (swaggerUiResources.getAt(0) as Resource).getURI().toString().split("/webjars/swagger-ui")[1]
        } catch (Exception e) {
            throw new Exception("Unable to find swagger ui.. Please make sure that you have added swagger ui dependency eg:-\n compile 'org.webjars:swagger-ui:2.2.8' \nin your build.gradle file", e)
        }
    }
}
