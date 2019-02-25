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

package com.propentus.smartlog.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.propentus.common.util.session.SessionUtil;
import com.propentus.config.SupportedUBLMessageTypes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;

/**
 * Helper class for building HTTP-responses *
 */
public class ResponseBuilder {

    /**
     * Different types of response types.
     */
    public enum ResponseType {
        JSON,
        XML
    }

	private static Logger logger = LoggerFactory.getLogger(ResponseBuilder.class);

    public static String buildResponseAsJson(HttpStatus status, String message) {
        return buildResponse(status, message, ResponseType.JSON);
    }

    public static String buildResponseAsXml(HttpStatus status, String message) {
        return buildResponse(status, message, ResponseType.XML);
    }
	/**
	 * Build HTTP-response in JSON format for client. Also returns matching HttpStatus as a header in HTTP-response.
	 * @param status org.springframework.http.HttpStatus enum value, returned as String in JSON
	 * @param message message returned in JSON
	 * @return
	 */
	public static String buildResponse(HttpStatus status, String message, ResponseType responseType) {

		HttpServletResponse resp = SessionUtil.getCurrentResponse();

		if(resp == null) {

			logger.error("Current response cannot be null when building response!");
			throw new RuntimeException("Couldn't build HTTP-response. Status:" + status.toString() + ", Message:" + message);
		}

		resp.setStatus(status.value());

		if (responseType.equals(ResponseType.JSON)) {

		    resp.setContentType("application/json");
		    resp.setCharacterEncoding("UTF-8");
		    return new ResponseBuilder.HttpResponseMessage(status.toString(), message).toJson();

        } else {

            resp.setContentType("application/xml");
            resp.setCharacterEncoding("UTF-8");
            return new ResponseBuilder.HttpResponseMessage(status.toString(), message).toXml();
        }
	}

	/**
	 * Build HTTP-response in XML or JSON format for client. Also returns matching HttpStatus as a header in HTTP-response. This doesn't wrap
	 * error or OK messages to, so when sending back actually response this should be used
	 * @param
	 * @return Valid response with content
	 */
	public static String buildResponse(String renderedContent) {

		final HttpStatus OK_STATUS = HttpStatus.OK;
		HttpServletResponse resp = SessionUtil.getCurrentResponse();
		if(resp == null) {
			logger.error("Current response cannot be null when building response!");
			throw new RuntimeException("Couldn't build HTTP-response. Renderer content:" + renderedContent);
		}
		resp.setStatus(HttpStatus.OK.value());

		//	TODO: get wanted response type from request type
		resp.setContentType("application/xml");
		resp.setCharacterEncoding("UTF-8");
		return renderedContent;
	}
	
	/*
	 * Response message
	 */
	@XmlRootElement(name = "response")
	protected static class HttpResponseMessage {
	
		@XmlElement
		public String status;
		
		@XmlElement
		public String message;
		
		public HttpResponseMessage() {}
		
		public HttpResponseMessage(String status, String message) {
			this.status = status;
			this.message = message;
		}
		
		public String toJson() {
			//Convert to JSON with pretty printing enabled
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			return gson.toJson(this);
		}
		
		public String toXml() {
			
			StringWriter sw = new StringWriter();
			String xml = "";
			
			try {
				
				JAXBContext jaxbContext = JAXBContext.newInstance(HttpResponseMessage.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

				// output pretty printed
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				
				jaxbMarshaller.marshal(this, sw);
				
				xml = sw.toString();
				
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
			
			return xml;
		}
	}
	
}
