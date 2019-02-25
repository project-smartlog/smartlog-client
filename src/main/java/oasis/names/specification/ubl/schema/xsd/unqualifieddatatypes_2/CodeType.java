//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.11.17 at 03:30:58 PM EET 
//


package oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *         
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:UniqueID xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns:ccts-cct="urn:un:unece:uncefact:data:specification:CoreComponentTypeSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;UBLUDT000007&lt;/ccts:UniqueID&gt;
 * </pre>
 * 
 *         
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:CategoryCode xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns:ccts-cct="urn:un:unece:uncefact:data:specification:CoreComponentTypeSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;UDT&lt;/ccts:CategoryCode&gt;
 * </pre>
 * 
 *         
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:DictionaryEntryName xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns:ccts-cct="urn:un:unece:uncefact:data:specification:CoreComponentTypeSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Code. Type&lt;/ccts:DictionaryEntryName&gt;
 * </pre>
 * 
 *         
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:VersionID xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns:ccts-cct="urn:un:unece:uncefact:data:specification:CoreComponentTypeSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;1.0&lt;/ccts:VersionID&gt;
 * </pre>
 * 
 *         
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Definition xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns:ccts-cct="urn:un:unece:uncefact:data:specification:CoreComponentTypeSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;A character string (letters, figures, or symbols) that for brevity and/or language independence may be used to represent or replace a definitive value or text of an attribute, together with relevant supplementary information.&lt;/ccts:Definition&gt;
 * </pre>
 * 
 *         
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:RepresentationTermName xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns:ccts-cct="urn:un:unece:uncefact:data:specification:CoreComponentTypeSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Code&lt;/ccts:RepresentationTermName&gt;
 * </pre>
 * 
 *         
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:PrimitiveType xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns:ccts-cct="urn:un:unece:uncefact:data:specification:CoreComponentTypeSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;string&lt;/ccts:PrimitiveType&gt;
 * </pre>
 * 
 *         
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:UsageRule xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns:ccts-cct="urn:un:unece:uncefact:data:specification:CoreComponentTypeSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;Other supplementary components in the CCT are captured as part of the token and name for the schema module containing the code list and thus, are not declared as attributes. &lt;/ccts:UsageRule&gt;
 * </pre>
 * 
 *       
 * 
 * <p>Java class for CodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CodeType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:un:unece:uncefact:data:specification:CoreComponentTypeSchemaModule:2>CodeType">
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CodeType")
@XmlSeeAlso({
    /*CurrencyCodeType.class,
    PreferenceCriterionCodeType.class,
    TransportModeCodeType.class,
    CalculationExpressionCodeType.class,
    FeatureTacticTypeCodeType.class,
    RoleCodeType.class,
    ReferenceEventCodeType.class,
    ServiceInformationPreferenceCodeType.class,
    TaxLevelCodeType.class,
    TransportEquipmentTypeCodeType.class,
    ActivityTypeCodeType.class,
    MandateTypeCodeType.class,
    ConsumerIncentiveTacticTypeCodeType.class,
    DocumentCurrencyCodeType.class,
    StatusCodeType.class,
    ComparisonDataSourceCodeType.class,
    PaymentChannelCodeType.class,
    DescriptionCodeType.class,
    InhalationToxicityZoneCodeType.class,
    FormatCodeType.class,
    MimeCodeType.class,
    StatementTypeCodeType.class,
    RequestedInvoiceCurrencyCodeType.class,
    TariffClassCodeType.class,
    DocumentTypeCodeType.class,
    TaxCurrencyCodeType.class,
    ConditionCodeType.class,
    ConsumersEnergyLevelCodeType.class,
    CorporateRegistrationTypeCodeType.class,
    HeatingTypeCodeType.class,
    PaymentPurposeCodeType.class,
    SourceCurrencyCodeType.class,
    PartPresentationCodeType.class,
    SealIssuerTypeCodeType.class,
    PositionCodeType.class,
    ContractTypeCodeType.class,
    HazardousRegulationCodeType.class,
    SubscriberTypeCodeType.class,
    HandlingCodeType.class,
    PackageLevelCodeType.class,
    DocumentStatusCodeType.class,
    TrackingDeviceCodeType.class,
    MiscellaneousEventTypeCodeType.class,
    ExpressionCodeType.class,
    LatitudeDirectionCodeType.class,
    TenderEnvelopeTypeCodeType.class,
    TransportationStatusTypeCodeType.class,
    OneTimeChargeTypeCodeType.class,
    CompanyLiquidationStatusCodeType.class,
    TradeItemPackingLabelingTypeCodeType.class,
    LifeCycleStatusCodeType.class,
    CollaborationPriorityCodeType.class,
    CalculationMethodCodeType.class,
    EvaluationCriterionTypeCodeType.class,
    ResolutionCodeType.class,
    TargetCurrencyCodeType.class,
    DirectionCodeType.class,
    SealStatusCodeType.class,
    ProcurementSubTypeCodeType.class,
    AllowanceChargeReasonCodeType.class,
    AdjustmentReasonCodeType.class,
    OrderTypeCodeType.class,
    TenderTypeCodeType.class,
    PackLevelCodeType.class,
    DataSourceCodeType.class,
    PriceTypeCodeType.class,
    ValidationResultCodeType.class,
    MeterReadingTypeCodeType.class,
    TaxTypeCodeType.class,
    CardTypeCodeType.class,
    ExecutionRequirementCodeType.class,
    CapabilityTypeCodeType.class,
    ConsumptionLevelCodeType.class,
    TransportHandlingUnitTypeCodeType.class,
    WeekDayCodeType.class,
    ResidenceTypeCodeType.class,
    NameCodeType.class,
    WorkPhaseCodeType.class,
    WeightingAlgorithmCodeType.class,
    FinancingInstrumentCodeType.class,
    PromotionalEventTypeCodeType.class,
    CertificateTypeCodeType.class,
    TradeServiceCodeType.class,
    ForecastPurposeCodeType.class,
    CardChipCodeType.class,
    ExceptionStatusCodeType.class,
    TransportEventTypeCodeType.class,
    FundingProgramCodeType.class,
    PaymentFrequencyCodeType.class,
    LocaleCodeType.class,
    ParentDocumentTypeCodeType.class,
    CorrectionTypeCodeType.class,
    MedicalFirstAidGuideCodeType.class,
    DespatchAdviceTypeCodeType.class,
    PaymentCurrencyCodeType.class,
    TendererRoleCodeType.class,
    LineStatusCodeType.class,
    CoordinateSystemCodeType.class,
    TransportExecutionStatusCodeType.class,
    TariffCodeType.class,
    TypeCodeType.class,
    TransportMeansTypeCodeType.class,
    AccountingCostCodeType.class,
    TransportEmergencyCardCodeType.class,
    TimingComplaintCodeType.class,
    SubstitutionStatusCodeType.class,
    FullnessIndicationCodeType.class,
    ProcessReasonCodeType.class,
    TransportAuthorizationCodeType.class,
    RejectActionCodeType.class,
    ActionCodeType.class,
    TransitDirectionCodeType.class,
    ProfileStatusCodeType.class,
    ServiceTypeCodeType.class,
    CreditNoteTypeCodeType.class,
    RejectReasonCodeType.class,
    ProviderTypeCodeType.class,
    NatureCodeType.class,
    AddressFormatCodeType.class,
    ComparisonDataCodeType.class,
    AwardingCriterionTypeCodeType.class,
    UtilityStatementTypeCodeType.class,
    OwnerTypeCodeType.class,
    ShippingPriorityLevelCodeType.class,
    TaxExemptionReasonCodeType.class,
    ReminderTypeCodeType.class,
    AwardingMethodTypeCodeType.class,
    ExpenseCodeType.class,
    NotificationTypeCodeType.class,
    DocumentStatusReasonCodeType.class,
    ExemptionReasonCodeType.class,
    TelecommunicationsSupplyTypeCodeType.class,
    EncodingCodeType.class,
    SupplyChainActivityTypeCodeType.class,
    AddressTypeCodeType.class,
    DisplayTacticTypeCodeType.class,
    PackingCriteriaCodeType.class,
    ProcedureCodeType.class,
    TransportServiceCodeType.class,
    CommodityCodeType.class,
    RevisionStatusCodeType.class,
    PreviousMeterReadingMethodCodeType.class,
    CountrySubentityCodeType.class,
    PrivacyCodeType.class,
    TelecommunicationsServiceCallCodeType.class,
    ConsumptionTypeCodeType.class,
    TelecommunicationsServiceCategoryCodeType.class,
    CurrentChargeTypeCodeType.class,
    PackagingTypeCodeType.class,
    PaymentAlternativeCurrencyCodeType.class,
    PreviousCancellationReasonCodeType.class,
    ContractingSystemCodeType.class,
    ForecastTypeCodeType.class,
    EmergencyProceduresCodeType.class,
    ReceiptAdviceTypeCodeType.class,
    MeterConstantCodeType.class,
    InspectionMethodCodeType.class,
    GuaranteeTypeCodeType.class,
    UrgencyCodeType.class,
    TendererRequirementTypeCodeType.class,
    TenderResultCodeType.class,
    FreightRateClassCodeType.class,
    CustomsStatusCodeType.class,
    PerformanceMetricTypeCodeType.class,
    SizeTypeCodeType.class,
    QuantityDiscrepancyCodeType.class,
    AdmissionCodeType.class,
    ResponseCodeType.class,
    IdentificationCodeType.class,
    DeclarationTypeCodeType.class,
    InvoiceTypeCodeType.class,
    CompanyLegalFormCodeType.class,
    ShortageActionCodeType.class,
    PriceEvaluationCodeType.class,
    IndustryClassificationCodeType.class,
    QualityControlCodeType.class,
    GenderCodeType.class,
    CharacterSetCodeType.class,
    RetailEventStatusCodeType.class,
    ChannelCodeType.class,
    ConstitutionCodeType.class,
    ImportanceCodeType.class,
    PaymentMeansCodeType.class,
    StatusReasonCodeType.class,
    EvidenceTypeCodeType.class,
    SpecificationTypeCodeType.class,
    SubcontractingConditionsCodeType.class,
    ProcurementTypeCodeType.class,
    AccountFormatCodeType.class,
    LatestMeterReadingMethodCodeType.class,
    UNDGCodeType.class,
    PartyTypeCodeType.class,
    MathematicOperatorCodeType.class,
    ExceptionResolutionCodeType.class,
    CargoTypeCodeType.class,
    AvailabilityStatusCodeType.class,
    DispositionCodeType.class,
    ApplicationStatusCodeType.class,
    EnvironmentalEmissionTypeCodeType.class,
    ItemClassificationCodeType.class,
    OrderResponseCodeType.class,
    LongitudeDirectionCodeType.class,
    HazardousCategoryCodeType.class,
    SecurityClassificationCodeType.class,
    ThresholdValueComparisonCodeType.class,
    PricingCurrencyCodeType.class,
    TimeFrequencyCodeType.class,
    AccountTypeCodeType.class,
    SubmissionMethodCodeType.class,
    DutyCodeType.class,
    PurposeCodeType.class,
    LocationTypeCodeType.class,
    LossRiskResponsibilityCodeType.class,
    ExtensionReasonCodeType.class*/
})
public class CodeType
    extends un.unece.uncefact.data.specification.corecomponenttypeschemamodule._2.CodeType
{


}
