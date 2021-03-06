/*
 * DBE Core Complete API
 * Complete DBE Core API with all the defined API operations. Different API operations are also provided based on DBE Core roles as separate APIs.
 *
 * OpenAPI spec version: 1.0.0
 * Contact: kari.korpela@lut.fi
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package org.core;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Response to order query
 */
@ApiModel(description = "Response to order query")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-02-05T15:39:37.700+02:00")
public class OrderResponse {
  @SerializedName("CustomizationID")
  private String customizationID = null;

  @SerializedName("ID")
  private String ID = null;

  @SerializedName("IssueDate")
  private LocalDate issueDate = null;

  @SerializedName("IssueTime")
  private OffsetDateTime issueTime = null;

  @SerializedName("NoteType")
  private String noteType = null;

  @SerializedName("BuyerCustomerParty")
  private BuyerCustomerParty buyerCustomerParty = null;

  @SerializedName("SellerSupplierParty")
  private SellerSupplierParty sellerSupplierParty = null;

  @SerializedName("Delivery")
  private Delivery delivery = null;

  @SerializedName("OrderLine")
  private OrderLine orderLine = null;

  @SerializedName("SalesOrderID")
  private String salesOrderID = null;

  @SerializedName("OrderReference")
  private String orderReference = null;

  public OrderResponse customizationID(String customizationID) {
    this.customizationID = customizationID;
    return this;
  }

   /**
   * Get customizationID
   * @return customizationID
  **/
  @ApiModelProperty(value = "")
  public String getCustomizationID() {
    return customizationID;
  }

  public void setCustomizationID(String customizationID) {
    this.customizationID = customizationID;
  }

  public OrderResponse ID(String ID) {
    this.ID = ID;
    return this;
  }

   /**
   * Get ID
   * @return ID
  **/
  @ApiModelProperty(value = "")
  public String getID() {
    return ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }

  public OrderResponse issueDate(LocalDate issueDate) {
    this.issueDate = issueDate;
    return this;
  }

   /**
   * Get issueDate
   * @return issueDate
  **/
  @ApiModelProperty(value = "")
  public LocalDate getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(LocalDate issueDate) {
    this.issueDate = issueDate;
  }

  public OrderResponse issueTime(OffsetDateTime issueTime) {
    this.issueTime = issueTime;
    return this;
  }

   /**
   * Get issueTime
   * @return issueTime
  **/
  @ApiModelProperty(value = "")
  public OffsetDateTime getIssueTime() {
    return issueTime;
  }

  public void setIssueTime(OffsetDateTime issueTime) {
    this.issueTime = issueTime;
  }

  public OrderResponse noteType(String noteType) {
    this.noteType = noteType;
    return this;
  }

   /**
   * Get noteType
   * @return noteType
  **/
  @ApiModelProperty(value = "")
  public String getNoteType() {
    return noteType;
  }

  public void setNoteType(String noteType) {
    this.noteType = noteType;
  }

  public OrderResponse buyerCustomerParty(BuyerCustomerParty buyerCustomerParty) {
    this.buyerCustomerParty = buyerCustomerParty;
    return this;
  }

   /**
   * Get buyerCustomerParty
   * @return buyerCustomerParty
  **/
  @ApiModelProperty(value = "")
  public BuyerCustomerParty getBuyerCustomerParty() {
    return buyerCustomerParty;
  }

  public void setBuyerCustomerParty(BuyerCustomerParty buyerCustomerParty) {
    this.buyerCustomerParty = buyerCustomerParty;
  }

  public OrderResponse sellerSupplierParty(SellerSupplierParty sellerSupplierParty) {
    this.sellerSupplierParty = sellerSupplierParty;
    return this;
  }

   /**
   * Get sellerSupplierParty
   * @return sellerSupplierParty
  **/
  @ApiModelProperty(value = "")
  public SellerSupplierParty getSellerSupplierParty() {
    return sellerSupplierParty;
  }

  public void setSellerSupplierParty(SellerSupplierParty sellerSupplierParty) {
    this.sellerSupplierParty = sellerSupplierParty;
  }

  public OrderResponse delivery(Delivery delivery) {
    this.delivery = delivery;
    return this;
  }

   /**
   * Get delivery
   * @return delivery
  **/
  @ApiModelProperty(value = "")
  public Delivery getDelivery() {
    return delivery;
  }

  public void setDelivery(Delivery delivery) {
    this.delivery = delivery;
  }

  public OrderResponse orderLine(OrderLine orderLine) {
    this.orderLine = orderLine;
    return this;
  }

  public OrderResponse addOrderLineItem(LineItem orderLineItem) {
    if (this.orderLine == null) {
       this.orderLine = new OrderLine();
    }
    this.orderLine.add(orderLineItem);
    return this;
  }

   /**
   * Get orderLine
   * @return orderLine
  **/
  @ApiModelProperty(value = "")
  public OrderLine getOrderLine() {
    return orderLine;
  }

  public void setOrderLine(OrderLine orderLine) {
    this.orderLine = orderLine;
  }

  public OrderResponse salesOrderID(String salesOrderID) {
    this.salesOrderID = salesOrderID;
    return this;
  }

   /**
   * Get salesOrderID
   * @return salesOrderID
  **/
  @ApiModelProperty(value = "")
  public String getSalesOrderID() {
    return salesOrderID;
  }

  public void setSalesOrderID(String salesOrderID) {
    this.salesOrderID = salesOrderID;
  }

  public OrderResponse orderReference(String orderReference) {
    this.orderReference = orderReference;
    return this;
  }

   /**
   * Get orderReference
   * @return orderReference
  **/
  @ApiModelProperty(value = "")
  public String getOrderReference() {
    return orderReference;
  }

  public void setOrderReference(String orderReference) {
    this.orderReference = orderReference;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderResponse orderResponse = (OrderResponse) o;
    return Objects.equals(this.customizationID, orderResponse.customizationID) &&
        Objects.equals(this.ID, orderResponse.ID) &&
        Objects.equals(this.issueDate, orderResponse.issueDate) &&
        Objects.equals(this.issueTime, orderResponse.issueTime) &&
        Objects.equals(this.noteType, orderResponse.noteType) &&
        Objects.equals(this.buyerCustomerParty, orderResponse.buyerCustomerParty) &&
        Objects.equals(this.sellerSupplierParty, orderResponse.sellerSupplierParty) &&
        Objects.equals(this.delivery, orderResponse.delivery) &&
        Objects.equals(this.orderLine, orderResponse.orderLine) &&
        Objects.equals(this.salesOrderID, orderResponse.salesOrderID) &&
        Objects.equals(this.orderReference, orderResponse.orderReference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customizationID, ID, issueDate, issueTime, noteType, buyerCustomerParty, sellerSupplierParty, delivery, orderLine, salesOrderID, orderReference);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrderResponse {\n");
    
    sb.append("    customizationID: ").append(toIndentedString(customizationID)).append("\n");
    sb.append("    ID: ").append(toIndentedString(ID)).append("\n");
    sb.append("    issueDate: ").append(toIndentedString(issueDate)).append("\n");
    sb.append("    issueTime: ").append(toIndentedString(issueTime)).append("\n");
    sb.append("    noteType: ").append(toIndentedString(noteType)).append("\n");
    sb.append("    buyerCustomerParty: ").append(toIndentedString(buyerCustomerParty)).append("\n");
    sb.append("    sellerSupplierParty: ").append(toIndentedString(sellerSupplierParty)).append("\n");
    sb.append("    delivery: ").append(toIndentedString(delivery)).append("\n");
    sb.append("    orderLine: ").append(toIndentedString(orderLine)).append("\n");
    sb.append("    salesOrderID: ").append(toIndentedString(salesOrderID)).append("\n");
    sb.append("    orderReference: ").append(toIndentedString(orderReference)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

