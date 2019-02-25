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
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * Attachement
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-02-05T15:39:37.700+02:00")
public class Attachement {
  @SerializedName("uri")
  private String uri = null;

  public Attachement uri(String uri) {
    this.uri = uri;
    return this;
  }

   /**
   * Web link
   * @return uri
  **/
  @ApiModelProperty(value = "Web link")
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Attachement attachement = (Attachement) o;
    return Objects.equals(this.uri, attachement.uri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Attachement {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
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

