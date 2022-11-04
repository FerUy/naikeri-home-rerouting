package com.naikeri.sgw.impl.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Match Rule for the application
 */
public class ApplicationMatchRule {

  private String callingGt;
  private String calledGt;
  private String imsi;
  private String primitive;
  private Boolean regexEnabled;
  private List<String> messageTypes = new ArrayList<>();

  public String getCallingGt() {
    return callingGt;
  }

  public void setCallingGt(String callingGt) {
    this.callingGt = callingGt;
  }

  public String getCalledGt() {
    return calledGt;
  }

  public void setCalledGt(String calledGt) {
    this.calledGt = calledGt;
  }

  public String getImsi() {
    return imsi;
  }

  public void setImsi(String imsi) {
    this.imsi = imsi;
  }

  public String getPrimitive() {
    return primitive;
  }

  public void setMessageType(String messageType) {
    this.primitive = messageType;
    if (messageType != null) {
      Collections.addAll(this.messageTypes, messageType.split(","));
    }
  }

  public List<String> getMessageTypes() {
    return this.messageTypes;
  }

  /**
   * Compare two Match Rule and result the result as boolean
   * 
   * @param newMatchRule New MatchRule
   * @return return True if both match else false
   */
  public Boolean compareWith(ApplicationMatchRule newMatchRule) {
    // check if primitive
    Boolean result = true;
    // do not proceed to check the remaining criteria
    for (String pitive : this.primitive.split(",")) {
      result = result && pitive.equalsIgnoreCase(newMatchRule.primitive);
    }

    if (this.imsi != null && !this.imsi.isEmpty() && newMatchRule.imsi != null
        && !newMatchRule.imsi.isEmpty()) {
      result = newMatchRule.imsi.startsWith(this.imsi);
    }

    if (this.calledGt != null && !this.calledGt.isEmpty() && newMatchRule.calledGt != null
        && !newMatchRule.calledGt.isEmpty()) {
      // result = result && <compare>
      result = result && newMatchRule.calledGt.startsWith(this.calledGt);
    }
    if (this.callingGt != null && !this.callingGt.isEmpty() && newMatchRule.callingGt != null
        && !newMatchRule.callingGt.isEmpty()) {
      // result = result && <compare>
      result = result && newMatchRule.callingGt.startsWith(this.callingGt);
    }
    return result;
  }

  public void setRegexEnabled(Boolean regexEnabled) {
    this.regexEnabled = regexEnabled;
  }

  public boolean getRegexEnabled() {
    return Boolean.TRUE.equals(regexEnabled);
  }
}
