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

package com.propentus.smartlog.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.propentus.common.exception.ConfigurationException;
import com.propentus.common.util.EntityUtil;
import com.propentus.iot.chaincode.model.UBLChaincodeTO;
import com.propentus.iot.configs.OrganisationConfiguration;
import com.propentus.smartlog.datasource.couchdb.entities.ApiUser;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Processor to handle parameters given and generating query from those.
 *
 * POSTs query to CouchDB and receives messages.
 *
 */
public class CouchDbQueryProcessor extends AbstractQueryProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CouchDbQueryProcessor.class);

    private static final String COUCH_DB_API = "{url}/{channel}_$u$b$l/_find";
    private String fullQuery = "{\"selector\": {[QUERY]}, \"limit\": 10000}";
    private URL url;

    private OrganisationConfiguration configuration;
    private String requestUrl;

    public CouchDbQueryProcessor(Map params, OrganisationConfiguration configuration, ApiUser user) throws ConfigurationException {
        super(params, user);
        this.configuration = configuration;
        requestUrl = createUrl();
    }

    /**
     * Create URL where the requests are send
     * @return
     */
    private String createUrl() {
        String requestUrl = COUCH_DB_API.replace("{url}", configuration.couchDbUrl);
        requestUrl = requestUrl.replace("{channel}", configuration.channel);

        try {
            this.url = new URL(configuration.couchDbUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        logger.debug("CouchDB url: " + requestUrl);
        return requestUrl;
    }

    /**
     * Generated query is sent to CouchDB using POST and then the response is parsed to List<UBLChaincodeTO>
     * @return
     */
    @Override
    public void query() {

        generateQuery();

        logger.debug("Querying data from CouchDB with query: " + fullQuery);

        HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());

        //  Create Basic Authentication credentials
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(configuration.couchDbUsername, configuration.couchDbPassword);
        provider.setCredentials(AuthScope.ANY, credentials);

        //  Create authentication cache and context to use preemptive basic authentication
        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(provider);
        context.setAuthCache(authCache);

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(requestUrl);

        try {
            httppost.setEntity(new StringEntity(fullQuery));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("StringEntity not properly encoded");
        }

        HttpResponse response = null;
        String responseAsString = null;
        try {
            httppost.setHeader("Content-Type","application/json");
            response = httpclient.execute(httppost, context);
            responseAsString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Couldn't execute request to CouchDB");
        }

        logger.debug("Got response from CouchDB: " + responseAsString);

        //  If we got response try to parse it
        if (responseAsString != null) {
            parseToUblChaincodeTO(responseAsString);
        }
    }

    /**
     * Response from CouchDB is got as a String (JSON) so GSON is used to parse the response to UBLChaincodeTOs
     *
     * @param o
     * @return
     */
    @Override
    void parseToUblChaincodeTO(Object o) {

        logger.debug("Parsing data to UBLChaincodeTO's");

        String response = (String) o;

        //	Parse configuration and new organisation JSON as a JsonObject
        JsonParser parser = new JsonParser();
        JsonObject element = parser.parse(response).getAsJsonObject();

        //	Add new organisation to configuration JSON
        JsonArray array = element.getAsJsonArray("docs");
        Iterator i = array.iterator();

        while (i.hasNext()) {
            JsonObject json = (JsonObject) i.next();

            if (json != null) {
                String ublJson = json.toString();
                UBLChaincodeTO ublChaincodeTO = EntityUtil.JsonToObject(ublJson, UBLChaincodeTO.class);
                messages.add(ublChaincodeTO);
            }
        }
    }

    /**
     * Generate query from parameters
     */
    private void generateQuery() {

        logger.debug("Generating query with params: " + parameters);

        String query = "";
        String dateQuery = "";

        for (Map.Entry<String, String> param : parameters.entrySet()) {

            String key = param.getKey();
            String value = param.getValue();

            if (value != null && !value.equals("")) {

                //  Handle date/time fields
                if (key.equals("createdFrom") || key.equals("createdTo")) {

                    //  If key is createdTo, use less than or even comparator
                    String comparator = "$lte";

                    //  If key is createdFrom, use greater than or even comparator
                    if (key.equals("createdFrom")) {
                        comparator = "$gte";
                    }

                    //  One of the dates is already added
                    if (!dateQuery.equals("")) {
                        dateQuery += ",";
                    }

                    dateQuery += "\"" + comparator + "\":" + "\"" + value + "\"";

                } else if (key.equals("participantId")){
                    //  If this isn't the first parameter
                    if (!query.equals("")) {
                        query += ",";
                    }
                    query += "\"participants\":{\"$elemMatch\": {\"MSPID\": \"" + value + "\"}}";
                } else {
                    //  If this isn't the first parameter
                    if (!query.equals("")) {
                        query += ",";
                    }
                    query += "\"" + key + "\":" + "\"" + value + "\"";
                }
            }
        }

        // Remove last comma
        if (query.endsWith(",")) {
            query = query.substring(0, query.length() - 1);
        }

        //  If dates were used in parameters, create timestamp parameter with dateQuery as an object
        if (!dateQuery.equals("")) {

            if (!query.equals("")) {
                query += ",";
            }
            query += "\"timestamp\":" + "{" + dateQuery + "}";
        }

        // Replace template from fullQuery
        fullQuery = fullQuery.replace("[QUERY]", query);
    }
}
