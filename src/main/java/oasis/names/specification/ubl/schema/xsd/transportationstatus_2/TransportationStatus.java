//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.11.21 at 03:03:54 PM EET 
//


package oasis.names.specification.ubl.schema.xsd.transportationstatus_2;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionsType;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for TransportationStatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransportationStatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2}UBLExtensions" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}UBLVersionID" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}CustomizationID" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}ProfileID" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}ProfileExecutionID" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}ID"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}CarrierAssignedID" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}UUID" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}IssueDate" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}IssueTime" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}Name" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}Description" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}Note" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}ShippingOrderID" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}OtherInstruction" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}TransportationStatusTypeCode" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}TransportExecutionStatusCode" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}Consignment" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}TransportEvent" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}DocumentReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}Signature" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}SenderParty" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}ReceiverParty" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}TransportationStatusRequestDocumentReference" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}TransportExecutionPlanDocumentReference" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}UpdatedPickupTransportEvent" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}UpdatedDeliveryTransportEvent" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}StatusLocation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}StatusPeriod" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "TransportationStatus")
@XmlType(name = "TransportationStatus", propOrder = {
    "ublExtensions",
    "ublVersionID",
    "customizationID",
    "profileID",
    "profileExecutionID",
    "id",
    "carrierAssignedID",
    "uuid",
    "issueDate",
    "issueTime",
    "name",
    "description",
    "note",
    "shippingOrderID",
    "otherInstruction",
    "transportationStatusTypeCode",
    "transportExecutionStatusCode",
    "consignment",
    "transportEvent",
    "documentReference",
    "signature",
    "senderParty",
    "receiverParty",
    "transportationStatusRequestDocumentReference",
    "transportExecutionPlanDocumentReference",
    "updatedPickupTransportEvent",
    "updatedDeliveryTransportEvent",
    "statusLocation",
    "statusPeriod"
})
public class TransportationStatus {

    @XmlElement(name = "UBLExtensions", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2")
    protected UBLExtensionsType ublExtensions;
    @XmlElement(name = "UBLVersionID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected UBLVersionIDType ublVersionID;
    @XmlElement(name = "CustomizationID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected CustomizationIDType customizationID;
    @XmlElement(name = "ProfileID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected ProfileIDType profileID;
    @XmlElement(name = "ProfileExecutionID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected ProfileExecutionIDType profileExecutionID;
    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    protected IDType id;
    @XmlElement(name = "CarrierAssignedID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected CarrierAssignedIDType carrierAssignedID;
    @XmlElement(name = "UUID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected UUIDType uuid;
    @XmlElement(name = "IssueDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected IssueDateType issueDate;
    @XmlElement(name = "IssueTime", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected IssueTimeType issueTime;
    @XmlElement(name = "Name", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected NameType name;
    @XmlElement(name = "Description", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected List<DescriptionType> description;
    @XmlElement(name = "Note", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected List<NoteType> note;
    @XmlElement(name = "ShippingOrderID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected ShippingOrderIDType shippingOrderID;
    @XmlElement(name = "OtherInstruction", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected OtherInstructionType otherInstruction;
    @XmlElement(name = "TransportationStatusTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected TransportationStatusTypeCodeType transportationStatusTypeCode;
    @XmlElement(name = "TransportExecutionStatusCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected TransportExecutionStatusCodeType transportExecutionStatusCode;
    @XmlElement(name = "Consignment", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected List<ConsignmentType> consignment;
    @XmlElement(name = "TransportEvent", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected List<TransportEventType> transportEvent;
    @XmlElement(name = "DocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected List<DocumentReferenceType> documentReference;
    @XmlElement(name = "Signature", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected List<SignatureType> signature;
    @XmlElement(name = "SenderParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected PartyType senderParty;
    @XmlElement(name = "ReceiverParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected PartyType receiverParty;
    @XmlElement(name = "TransportationStatusRequestDocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected DocumentReferenceType transportationStatusRequestDocumentReference;
    @XmlElement(name = "TransportExecutionPlanDocumentReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected DocumentReferenceType transportExecutionPlanDocumentReference;
    @XmlElement(name = "UpdatedPickupTransportEvent", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected TransportEventType updatedPickupTransportEvent;
    @XmlElement(name = "UpdatedDeliveryTransportEvent", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected TransportEventType updatedDeliveryTransportEvent;
    @XmlElement(name = "StatusLocation", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected List<oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.LocationType> statusLocation;
    @XmlElement(name = "StatusPeriod", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected List<PeriodType> statusPeriod;

    /**
     * Gets the value of the ublExtensions property.
     * 
     * @return
     *     possible object is
     *     {@link UBLExtensionsType }
     *     
     */
    public UBLExtensionsType getUBLExtensions() {
        return ublExtensions;
    }

    /**
     * Sets the value of the ublExtensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBLExtensionsType }
     *     
     */
    public void setUBLExtensions(UBLExtensionsType value) {
        this.ublExtensions = value;
    }

    /**
     * Gets the value of the ublVersionID property.
     * 
     * @return
     *     possible object is
     *     {@link UBLVersionIDType }
     *     
     */
    public UBLVersionIDType getUBLVersionID() {
        return ublVersionID;
    }

    /**
     * Sets the value of the ublVersionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link UBLVersionIDType }
     *     
     */
    public void setUBLVersionID(UBLVersionIDType value) {
        this.ublVersionID = value;
    }

    /**
     * Gets the value of the customizationID property.
     * 
     * @return
     *     possible object is
     *     {@link CustomizationIDType }
     *     
     */
    public CustomizationIDType getCustomizationID() {
        return customizationID;
    }

    /**
     * Sets the value of the customizationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomizationIDType }
     *     
     */
    public void setCustomizationID(CustomizationIDType value) {
        this.customizationID = value;
    }

    /**
     * Gets the value of the profileID property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileIDType }
     *     
     */
    public ProfileIDType getProfileID() {
        return profileID;
    }

    /**
     * Sets the value of the profileID property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileIDType }
     *     
     */
    public void setProfileID(ProfileIDType value) {
        this.profileID = value;
    }

    /**
     * Gets the value of the profileExecutionID property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileExecutionIDType }
     *     
     */
    public ProfileExecutionIDType getProfileExecutionID() {
        return profileExecutionID;
    }

    /**
     * Sets the value of the profileExecutionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileExecutionIDType }
     *     
     */
    public void setProfileExecutionID(ProfileExecutionIDType value) {
        this.profileExecutionID = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link IDType }
     *     
     */
    public IDType getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link IDType }
     *     
     */
    public void setID(IDType value) {
        this.id = value;
    }

    /**
     * Gets the value of the carrierAssignedID property.
     * 
     * @return
     *     possible object is
     *     {@link CarrierAssignedIDType }
     *     
     */
    public CarrierAssignedIDType getCarrierAssignedID() {
        return carrierAssignedID;
    }

    /**
     * Sets the value of the carrierAssignedID property.
     * 
     * @param value
     *     allowed object is
     *     {@link CarrierAssignedIDType }
     *     
     */
    public void setCarrierAssignedID(CarrierAssignedIDType value) {
        this.carrierAssignedID = value;
    }

    /**
     * Gets the value of the uuid property.
     * 
     * @return
     *     possible object is
     *     {@link UUIDType }
     *     
     */
    public UUIDType getUUID() {
        return uuid;
    }

    /**
     * Sets the value of the uuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link UUIDType }
     *     
     */
    public void setUUID(UUIDType value) {
        this.uuid = value;
    }

    /**
     * Gets the value of the issueDate property.
     * 
     * @return
     *     possible object is
     *     {@link IssueDateType }
     *     
     */
    public IssueDateType getIssueDate() {
        return issueDate;
    }

    /**
     * Sets the value of the issueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link IssueDateType }
     *     
     */
    public void setIssueDate(IssueDateType value) {
        this.issueDate = value;
    }

    /**
     * Gets the value of the issueTime property.
     * 
     * @return
     *     possible object is
     *     {@link IssueTimeType }
     *     
     */
    public IssueTimeType getIssueTime() {
        return issueTime;
    }

    /**
     * Sets the value of the issueTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link IssueTimeType }
     *     
     */
    public void setIssueTime(IssueTimeType value) {
        this.issueTime = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link NameType }
     *     
     */
    public NameType getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameType }
     *     
     */
    public void setName(NameType value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DescriptionType }
     * 
     * 
     */
    public List<DescriptionType> getDescription() {
        if (description == null) {
            description = new ArrayList<DescriptionType>();
        }
        return this.description;
    }

    /**
     * Gets the value of the note property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the note property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNote().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NoteType }
     * 
     * 
     */
    public List<NoteType> getNote() {
        if (note == null) {
            note = new ArrayList<NoteType>();
        }
        return this.note;
    }

    /**
     * Gets the value of the shippingOrderID property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingOrderIDType }
     *     
     */
    public ShippingOrderIDType getShippingOrderID() {
        return shippingOrderID;
    }

    /**
     * Sets the value of the shippingOrderID property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingOrderIDType }
     *     
     */
    public void setShippingOrderID(ShippingOrderIDType value) {
        this.shippingOrderID = value;
    }

    /**
     * Gets the value of the otherInstruction property.
     * 
     * @return
     *     possible object is
     *     {@link OtherInstructionType }
     *     
     */
    public OtherInstructionType getOtherInstruction() {
        return otherInstruction;
    }

    /**
     * Sets the value of the otherInstruction property.
     * 
     * @param value
     *     allowed object is
     *     {@link OtherInstructionType }
     *     
     */
    public void setOtherInstruction(OtherInstructionType value) {
        this.otherInstruction = value;
    }

    /**
     * Gets the value of the transportationStatusTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link TransportationStatusTypeCodeType }
     *     
     */
    public TransportationStatusTypeCodeType getTransportationStatusTypeCode() {
        return transportationStatusTypeCode;
    }

    /**
     * Sets the value of the transportationStatusTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportationStatusTypeCodeType }
     *     
     */
    public void setTransportationStatusTypeCode(TransportationStatusTypeCodeType value) {
        this.transportationStatusTypeCode = value;
    }

    /**
     * Gets the value of the transportExecutionStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link TransportExecutionStatusCodeType }
     *     
     */
    public TransportExecutionStatusCodeType getTransportExecutionStatusCode() {
        return transportExecutionStatusCode;
    }

    /**
     * Sets the value of the transportExecutionStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportExecutionStatusCodeType }
     *     
     */
    public void setTransportExecutionStatusCode(TransportExecutionStatusCodeType value) {
        this.transportExecutionStatusCode = value;
    }

    /**
     * Gets the value of the consignment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the consignment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConsignment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConsignmentType }
     * 
     * 
     */
    public List<ConsignmentType> getConsignment() {
        if (consignment == null) {
            consignment = new ArrayList<ConsignmentType>();
        }
        return this.consignment;
    }

    /**
     * Gets the value of the transportEvent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transportEvent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransportEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransportEventType }
     * 
     * 
     */
    public List<TransportEventType> getTransportEvent() {
        if (transportEvent == null) {
            transportEvent = new ArrayList<TransportEventType>();
        }
        return this.transportEvent;
    }

    /**
     * Gets the value of the documentReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the documentReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocumentReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocumentReferenceType }
     * 
     * 
     */
    public List<DocumentReferenceType> getDocumentReference() {
        if (documentReference == null) {
            documentReference = new ArrayList<DocumentReferenceType>();
        }
        return this.documentReference;
    }

    /**
     * Gets the value of the signature property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the signature property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSignature().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SignatureType }
     * 
     * 
     */
    public List<SignatureType> getSignature() {
        if (signature == null) {
            signature = new ArrayList<SignatureType>();
        }
        return this.signature;
    }

    /**
     * Gets the value of the senderParty property.
     * 
     * @return
     *     possible object is
     *     {@link PartyType }
     *     
     */
    public PartyType getSenderParty() {
        return senderParty;
    }

    /**
     * Sets the value of the senderParty property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyType }
     *     
     */
    public void setSenderParty(PartyType value) {
        this.senderParty = value;
    }

    /**
     * Gets the value of the receiverParty property.
     * 
     * @return
     *     possible object is
     *     {@link PartyType }
     *     
     */
    public PartyType getReceiverParty() {
        return receiverParty;
    }

    /**
     * Sets the value of the receiverParty property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyType }
     *     
     */
    public void setReceiverParty(PartyType value) {
        this.receiverParty = value;
    }

    /**
     * Gets the value of the transportationStatusRequestDocumentReference property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentReferenceType }
     *     
     */
    public DocumentReferenceType getTransportationStatusRequestDocumentReference() {
        return transportationStatusRequestDocumentReference;
    }

    /**
     * Sets the value of the transportationStatusRequestDocumentReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentReferenceType }
     *     
     */
    public void setTransportationStatusRequestDocumentReference(DocumentReferenceType value) {
        this.transportationStatusRequestDocumentReference = value;
    }

    /**
     * Gets the value of the transportExecutionPlanDocumentReference property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentReferenceType }
     *     
     */
    public DocumentReferenceType getTransportExecutionPlanDocumentReference() {
        return transportExecutionPlanDocumentReference;
    }

    /**
     * Sets the value of the transportExecutionPlanDocumentReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentReferenceType }
     *     
     */
    public void setTransportExecutionPlanDocumentReference(DocumentReferenceType value) {
        this.transportExecutionPlanDocumentReference = value;
    }

    /**
     * Gets the value of the updatedPickupTransportEvent property.
     * 
     * @return
     *     possible object is
     *     {@link TransportEventType }
     *     
     */
    public TransportEventType getUpdatedPickupTransportEvent() {
        return updatedPickupTransportEvent;
    }

    /**
     * Sets the value of the updatedPickupTransportEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportEventType }
     *     
     */
    public void setUpdatedPickupTransportEvent(TransportEventType value) {
        this.updatedPickupTransportEvent = value;
    }

    /**
     * Gets the value of the updatedDeliveryTransportEvent property.
     * 
     * @return
     *     possible object is
     *     {@link TransportEventType }
     *     
     */
    public TransportEventType getUpdatedDeliveryTransportEvent() {
        return updatedDeliveryTransportEvent;
    }

    /**
     * Sets the value of the updatedDeliveryTransportEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportEventType }
     *     
     */
    public void setUpdatedDeliveryTransportEvent(TransportEventType value) {
        this.updatedDeliveryTransportEvent = value;
    }

    /**
     * Gets the value of the statusLocation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the statusLocation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatusLocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.LocationType }
     * 
     * 
     */
    public List<oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.LocationType> getStatusLocation() {
        if (statusLocation == null) {
            statusLocation = new ArrayList<oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.LocationType>();
        }
        return this.statusLocation;
    }

    /**
     * Gets the value of the statusPeriod property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the statusPeriod property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatusPeriod().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PeriodType }
     * 
     * 
     */
    public List<PeriodType> getStatusPeriod() {
        if (statusPeriod == null) {
            statusPeriod = new ArrayList<PeriodType>();
        }
        return this.statusPeriod;
    }

}
