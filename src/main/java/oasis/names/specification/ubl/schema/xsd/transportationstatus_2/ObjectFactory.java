//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.11.21 at 03:03:54 PM EET 
//


package oasis.names.specification.ubl.schema.xsd.transportationstatus_2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the oasis.names.specification.ubl.schema.xsd.transportationstatus_2 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TransportationStatus_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:TransportationStatus-2", "TransportationStatus");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: oasis.names.specification.ubl.schema.xsd.transportationstatus_2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TransportationStatus }
     * 
     */
    public TransportationStatus createTransportationStatusType() {
        return new TransportationStatus();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransportationStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:TransportationStatus-2", name = "TransportationStatus")
    public JAXBElement<TransportationStatus> createTransportationStatus(TransportationStatus value) {
        return new JAXBElement<TransportationStatus>(_TransportationStatus_QNAME, TransportationStatus.class, null, value);
    }

}