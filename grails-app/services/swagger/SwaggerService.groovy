
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

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector
import com.propentus.common.util.annotation.AnnotationUtil
import grails.web.mapping.LinkGenerator
import io.swagger.annotations.Api
import io.swagger.models.Model
import io.swagger.models.ModelImpl

//import com.fasterxml.jackson.dataformat.xml.jaxb.XmlJaxbAnnotationIntrospector

import io.swagger.models.Swagger
import io.swagger.models.Xml
import io.swagger.models.properties.ArrayProperty
import io.swagger.models.properties.ObjectProperty
import io.swagger.models.properties.Property
import io.swagger.models.properties.RefProperty
import io.swagger.servlet.Reader
import io.swagger.util.Json
import oasis.names.specification.ubl.schema.xsd.billoflading_2.BillOfLading
import oasis.names.specification.ubl.schema.xsd.forwardinginstructions_2.ForwardingInstructions
import oasis.names.specification.ubl.schema.xsd.packinglist_2.PackingList
import oasis.names.specification.ubl.schema.xsd.transportationstatus_2.TransportationStatus
import oasis.names.specification.ubl.schema.xsd.transportationstatusrequest_2.TransportationStatusRequest
import oasis.names.specification.ubl.schema.xsd.waybill_2.Waybill
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.io.Resource

import javax.servlet.http.HttpServletRequest
import javax.xml.bind.annotation.XmlElement
import java.lang.reflect.Field

class SwaggerService implements ApplicationContextAware {

	static scope = "singleton"

	private static final Logger logger = LoggerFactory.getLogger(SwaggerService.class)
	
    Swagger swagger = new Swagger()

    ApplicationContext applicationContext
	
	private static Map<String, String> packageNamesAndDefaultNamespace = new HashMap<String, String>();
	private static List<Class<?>> mainClasses = new ArrayList<Class<?>>()

	private static final String PACKAGE = "com.propentus.smartlog.api.";
	private static final String CONTROLLER = "Controller";

	def swaggerCacheService

	@Value("classpath*:**/webjars/swagger-ui/**/index.html")
	Resource[] swaggerUiResources

    String generateSwaggerDocument() {
		logger.debug("Generating Swagger document")
		//Add package names
		packageNamesAndDefaultNamespace.put("oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
		packageNamesAndDefaultNamespace.put("oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.", "oasis.names.specification.ubl.schema.xsd.CommonAggregateComponents-2")
		packageNamesAndDefaultNamespace.put("oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2")
		packageNamesAndDefaultNamespace.put("oasis.names.specification.ubl.schema.xsd.forwardinginstructions_2.", "urn:oasis:names:specification:ubl:schema:xsd:ForwardingInstructions-2")
		packageNamesAndDefaultNamespace.put("oasis.names.specification.ubl.schema.xsd.waybill_2.", "urn:oasis:names:specification:ubl:schema:xsd:Waybill-2")
		packageNamesAndDefaultNamespace.put("oasis.names.specification.ubl.schema.xsd.packinglist_2.", "urn:oasis:names:specification:ubl:schema:xsd:PackingList-2")
		packageNamesAndDefaultNamespace.put("oasis.names.specification.ubl.schema.xsd.billoflading_2.", "urn:oasis:names:specification:ubl:schema:xsd:BillOfLading-2")
		packageNamesAndDefaultNamespace.put("oasis.names.specification.ubl.schema.xsd.transportationstatus_2.", "urn:oasis:names:specification:ubl:schema:xsd:TransportationStatus-2")
		packageNamesAndDefaultNamespace.put("oasis.names.specification.ubl.schema.xsd.transportationstatusrequest_2.", "urn:oasis:names:specification:ubl:schema:xsd:TransportationStatusRequest-2")
		
		//	Add all of the main classes to list so those are easier to handle
		mainClasses.add(ForwardingInstructions.class)
		mainClasses.add(PackingList.class)
		mainClasses.add(BillOfLading.class)
		mainClasses.add(Waybill.class)
		mainClasses.add(TransportationStatus.class)
		mainClasses.add(TransportationStatusRequest.class)
		
		return getJsonDocumentWithJaxb(scanSwaggerResources())
	}

	private String createFullname(String controller) {
		return PACKAGE + controller + CONTROLLER;
	}

	public String getJsonByName(String name) {

		//Own logic for UBL-API
		if(name.equals("Api")) {

			logger.info("Loading UBL-API JSON...")

			String ublJson = swaggerCacheService.getJson()

			if (!ublJson) {

				logger.info("UBL-API JSON not found from cache, so load it")

				//	Don't know why, but when loading first time, swaggerJson is wrong, so we load it twice and then cache it
				String dummyJson = generateSwaggerDocument()

				ublJson = generateSwaggerDocument()

				swaggerCacheService.setJson(ublJson)
			}
			return ublJson
		}

		//Create new object to clear cache
		this.swagger = new Swagger()
		Swagger definition = scanResourceByName(name)
		if(definition != null) {
			return getJsonDocument(definition)
		}
		return null
	}

	Swagger scanResourceByName(String name) {
		logger.info("Generating Swagger document by name:" + name)

		String fullName = createFullname(name)
		logger.info("Full name:" + fullName)

		// Below code is written to support multi-module project.
		LinkGenerator linkGenerator = applicationContext.getBean(LinkGenerator.class)
		String host = linkGenerator.getServerBaseURL()
		host = host.replace($/http:///$, StringUtils.EMPTY)
		host = host.replace($/https:///$, StringUtils.EMPTY)
		swagger.setHost(host)
		Map<String, Object> swaggerResourcesAsMap = applicationContext.getBeansWithAnnotation(Api.class)
		List<Class> swaggerResources = swaggerResourcesAsMap.collect { it.value?.class}
		swaggerResources.removeAll {!it.name.equals(fullName)}

		if (swaggerResources) {
			logger.info("Found Swagger resource:" + swaggerResources)
			Reader.read(swagger, new HashSet<Class<?>>(swaggerResources))
			return swagger
		}

		return null
	}

    Swagger scanSwaggerResources() {
		// Below code is written to support multi-module project.
        LinkGenerator linkGenerator = applicationContext.getBean(LinkGenerator.class)
        String host = linkGenerator.getServerBaseURL()
        host = host.replace($/http:///$, StringUtils.EMPTY)
        host = host.replace($/https:///$, StringUtils.EMPTY)
		swagger.setHost(host)
        Map<String, Object> swaggerResourcesAsMap = applicationContext.getBeansWithAnnotation(Api.class)
        List<Class> swaggerResources = swaggerResourcesAsMap.collect { it.value?.class }
		swaggerResources.removeAll {!it.name.equals(createFullname("Api"))}
        if (swaggerResources) {
            Reader.read(swagger, new HashSet<Class<?>>(swaggerResources))
        }
		filter(swagger)
		manipulateJson(swagger)
        return swagger
    }

    private String getJsonDocument(Swagger swagger) {
		logger.info("Get JSON document")
		String swaggerJson = null
        if (swagger != null) {
            try {
				swaggerJson = Json.mapper().writeValueAsString(swagger)
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e)
            }
        }
		logger.info("Swagger JSON:" + swaggerJson)
        return swaggerJson
    }

	private String getJsonDocumentWithJaxb(Swagger swagger) {
		logger.info("Get JSON document")
		String swaggerJson = null
		if (swagger != null) {
			try {
				AnnotationIntrospector ai = new JaxbAnnotationIntrospector()

				//AnnotationIntrospectorPair pair = new AnnotationIntrospectorPair(new JacksonAnnotationIntrospector(),
				//new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));

				swaggerJson = Json.mapper().setAnnotationIntrospector(ai).writeValueAsString(swagger)
			} catch (JsonProcessingException e) {
				logger.error(e.getMessage(), e)
			}
		}
		logger.info("Swagger JSON:" + swaggerJson)
		return swaggerJson
	}
	
	private static filter(Swagger swagger) {
		Map<String, Model> definitions = swagger.getDefinitions()
		Iterator it = definitions.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
			List<String> filteredClasses = getFilteredClasses(mainClasses)
			
			if(!filteredClasses.contains(pair.getKey())) {
				it.remove();
			}
		
		}
	}
	
	private static List<String> getFilteredClasses(List<Class<?>> clazzes) {
		
		List<String> allowedClasses = new ArrayList<String>();
		
		for (Class<?> cls : clazzes) {
			List<Field> fields = Arrays.asList(cls.getDeclaredFields());
			
			//Add main type first!
			allowedClasses.add(cls.getSimpleName())
			
			for(Field field : fields) {
				String typeName = field.getGenericType().getTypeName();
				//Parse type name without package
				String[] temp = typeName.split("\\.");
				typeName = temp[temp.length - 1];
				
				//Lists are returned as class name + ">", for example "NoteType>"
				if(typeName.contains(">")) {
					typeName = typeName.replace(">", "");
				}
				
				allowedClasses.add(typeName);
			}
		}
		
		return allowedClasses;
	}
	
	private static void manipulateJson(Swagger swagger) {
		Map<String, Model> definitions = swagger.getDefinitions()
		Iterator it = definitions.entrySet().iterator();
		
		
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			ModelImpl model = pair.getValue()
			//println "Model properties for '" + pair.getKey() + "': " + model.getProperties()

			Map<String, Property> properties = model.getProperties()
			if(properties == null)
				continue
				
			trimBaseTypes(properties)
			capitalize(properties)
		}

		limitDepthness(swagger)		
		addNamespaces(swagger.getDefinitions())
	}
	
	private static void capitalize(Map<String, Property> properties) {
		
		Map<String, Property> fixedMap = new HashMap<String, Property>()
		
		Iterator it = properties.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			String key = pair.getKey()
			//Check if key is uppercase
			if(!Character.isUpperCase(key.codePointAt(0))) {
				String newKey = StringUtils.capitalise(key);
				//println "Capitalazing:" + newKey
				fixedMap.put(newKey, pair.getValue())
				it.remove()
			}
		}
		//Add fixed values to property set
		properties.putAll(fixedMap)
	}
	
	/**
	 * Remove unnecessary <value> elements from base types like IdentifierType.
	 */
	private static void trimBaseTypes(Map<String, Property> properties) {
		Iterator it = properties.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			String key = pair.getKey()
			if(key.equals("value")) {
				it.remove()
			}
		}
	}
	
	private static void addNamespaces(Map<String, Model> definitions) {
		//logger.info("Processing namespaces!!!")
		
		Map<String, String> fieldNamesAndNamespaces = getFieldNamesAndNamespaces(mainClasses)
		
		//logger.info("names: " + fieldNamesAndNamespaces)
		
		Iterator it = definitions.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			String clazzName = pair.getKey()
			Model value = pair.getValue()
			
			Iterator fieldIt = fieldNamesAndNamespaces.entrySet().iterator()
			
			while(fieldIt.hasNext()) {
				
				Map.Entry fieldEntry = (Map.Entry)fieldIt.next()
				String fieldName = fieldEntry.getKey()
				String namespace = fieldEntry.getValue()
				
				if (clazzName.toLowerCase().equals(fieldName.toLowerCase())) {
					
					//logger.info("MATCH FOUND: " + clazzName + " | " + fieldName + " | NAMESPACE: " + namespace)
					
					Xml xml2 = new Xml()
					xml2.setNamespace(namespace)
					value.setXml(xml2)
				}
			}
			
			String defaultNamespace = null
			Object object = null
			
			//	Normal List<String> packages was replaced with packageNamesAndDefaultNamespace
			//	So we can get default namespace easier.
			Iterator packageIterator = packageNamesAndDefaultNamespace.entrySet().iterator()
			
			while (packageIterator.hasNext()) {
				Map.Entry packagePair = (Map.Entry)packageIterator.next()
				String packageName = packagePair.getKey()
				String packageDefaultNamespace = packagePair.getValue()
				
				object = createInstance(packageName + clazzName)
				if(object) {
					//logger.info("Class found:" + clazzName + " PackageName: " + packageName)
					
					//	Set default namespace for outer iterator
					defaultNamespace = packageDefaultNamespace
					break;
				}
				
			}
			
			//logger.info("Looping fields for class:" + clazzName);
			Map<String, String> namespaceFields = new HashMap<String, String>()
			List<Field> fields = Arrays.asList(object.getClass().getDeclaredFields());
			
			for(Field field : fields) {
				field.setAccessible(true);
				//logger.info("Field name:" + field.getName());
				String namespace = getNamespaceValue(field)
				
				//logger.info("field.getName(): " + field.getName() + " | NAMESPACE: " + namespace)
				
				//	If "##default" namespace is returned, replace it with the real default namespace 
				if (namespace.equals("##default") || namespace == "") {
					namespace = defaultNamespace
					//logger.info("Setting default namespace for: " + field.getName() + " | " + namespace)
				}
				
				if(namespace != null) {
					//logger.info("Namespace:" + namespace);
					namespaceFields.put(field.getName().toLowerCase(), namespace)
				}
				
				//logger.info("Field and namespace: " + field.getName() + " | " + namespace)
			}
			
			//Add namespaces to right fields
			Map<String, Property> properties = value.getProperties()
			if(properties == null)
				continue;
				
			Iterator it2 = properties.entrySet().iterator();
			while(it2.hasNext()) {
				Map.Entry propertyPair = (Map.Entry)it2.next();
				String propertyKey = propertyPair.getKey()
				Property propertyValue = propertyPair.getValue()
				
				if(namespaceFields.containsKey(propertyKey.toLowerCase())) {
					String namespace = namespaceFields.get(propertyKey.toLowerCase())
					//logger.info("Setting namespace:" + namespace + " to property:" + propertyKey)
					Xml xml = propertyValue.getXml()
					if(xml == null) {
						propertyValue.setXml(new Xml())
					}
					propertyValue.getXml().setNamespace(namespace)
					
					if(propertyValue instanceof ArrayProperty) {
						xml = propertyValue.getItems().getXml()
						if(xml == null) {
							xml = new Xml();
							propertyValue.getItems().setXml(xml)
						}
						xml.setNamespace(namespace)
					}
					
				}
			}
		}
	}
	
	private static String getNamespaceValue(Field field) {
		String value = null;
		XmlElement elem = field.getAnnotation(XmlElement.class);
		if(elem != null) {
			return elem.namespace();
		}
		return value;
	}
	
	/**
	 * Limit depthness of the Swagger model by replacing ref types and array ref types with object types.
	 */
	private static void limitDepthness(Swagger swagger) {
		Map<String, Model> definitions = swagger.getDefinitions()
		Iterator it = definitions.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry definitionsPair = (Map.Entry)it.next();
			String modelKey = definitionsPair.getKey()
			Model model = definitionsPair.getValue()
			//If key equals base type, don't process
			if(modelKey.equals("ForwardingInstructions") || modelKey.equals("Waybill") || modelKey.equals("UBLMessageWrapper")) {
				continue;
			}
			
			Map<String, Property> fixedMap = new HashMap<String, Property>()
			Map<String, Property> properties = model.getProperties()
			if(properties == null) 
				continue;
			
			Iterator it2 = properties.entrySet().iterator();
			while(it2.hasNext()) { 
				Map.Entry propertyPair = (Map.Entry)it2.next();
				Property property = propertyPair.getValue()
				if(property instanceof RefProperty) {
					//println "Limiting depthness for ref property:" + modelKey
					//println "Ref value:" + property.get$ref()
					//println "Ref name:" + property.getName()
					
					ObjectProperty newProperty = new ObjectProperty()
					newProperty.setName(property.getName())
					newProperty.setXml(property.getXml())
					newProperty.setRequired(property.getRequired())
					//Add new to map and remove old
					fixedMap.put(propertyPair.getKey(), newProperty)
					it2.remove()
				}
				else if (property instanceof ArrayProperty) {
					//println "Limiting depthness for array property:" + modelKey
					//println "Ref value:" + property.getItems()

					RefProperty ref = property.getItems()
					//property.setItems(null)
					ObjectProperty newProperty = new ObjectProperty()
					newProperty.setName(ref.getName())
					newProperty.setXml(ref.getXml())
					newProperty.setRequired(ref.getRequired())
					property.setItems(newProperty)

				}
			}
			//println "Fixed map:" + fixedMap
			properties.putAll(fixedMap)
		}
	}
	
	
	private static Object createInstance(String clazzName)  {
		Object object = null
		try {
			Class<?> clazz = Class.forName(clazzName);
			object = clazz.newInstance()
		}
		catch(Exception e) {
			//e.printStackTrace()
		}
		return object;
	}
	/**
	 * Loops thru all of the fields from cls and returns types and namespaces
	 */
	private static Map<String, String> getFieldNamesAndNamespaces(List<Class<?>> classes) {
		
		Map<String, String> fieldNamesAndNamespaces = new HashMap<String, String>()
		
		for (Class<?> cls : classes) {
			
			Field[] fields = cls.getDeclaredFields()
			
			for (Field f : fields) {

				if(AnnotationUtil.fieldHasAnnotation(f, XmlElement.class)) {
					String type = f.getType().toString()
					String typeName = type.substring(type.lastIndexOf('.') + 1)
					String namespace = f.getAnnotation(XmlElement.class).namespace()

					//	If field is List of somekind, set typeName to fields name
					if (typeName.toLowerCase().equals("list")) {
						typeName = f.getName() + "Type"
					}
					fieldNamesAndNamespaces.put(typeName, namespace)
				}
				

			}
			
		}

		return fieldNamesAndNamespaces
	}

	public String getSwaggerUiRedirectUri(HttpServletRequest request) {
		String uri = "/webjars/swagger-ui" + getSwaggerUiFile() + "?url=" + request.getRequestURI()
		logger.info("Redirect uri:" + uri)
		return uri
	}

	private String getSwaggerUiFile() {
		try {
			(swaggerUiResources.getAt(0) as Resource).getURI().toString().split("/webjars/swagger-ui")[1]
		} catch (Exception e) {
			throw new Exception("Unable to find swagger ui.. Please make sure that you have added swagger ui dependency eg:-\n compile 'org.webjars:swagger-ui:2.2.8' \nin your build.gradle file", e)
		}
	}

}


