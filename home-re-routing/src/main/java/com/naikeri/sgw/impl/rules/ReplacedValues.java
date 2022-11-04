package com.naikeri.sgw.impl.rules;

import java.util.Optional;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;

/**
 * ReplacedValues
 */
public class ReplacedValues {
  
  private GlobalTitle callingGlobalTitle;
  private GlobalTitle calledGlobalTitle;
  private String imsi;
  private String ruleName;
  private PatternSccpAddress callingSccpParameter;
  private PatternSccpAddress calledSccpParamter;

  public GlobalTitle getCallingGlobalTitle() {
    return callingGlobalTitle;
  }

  public void setCallingGlobalTitle(GlobalTitle callingGlobalTitle) {
    this.callingGlobalTitle = callingGlobalTitle;
  }

  public GlobalTitle getCalledGlobalTitle() {
    return calledGlobalTitle;
  }

  public void setCalledGlobalTitle(GlobalTitle calledGlobalTitle) {
    this.calledGlobalTitle = calledGlobalTitle;
  }

  public String getImsi() {
    return this.imsi;
  }

  public void setImsi(String imsi) {
    this.imsi = imsi;
  }

  public void setCalledSccpParameter(PatternSccpAddress calledSccpOptional){
    this.calledSccpParamter = calledSccpOptional;
  }
  public Optional<PatternSccpAddress> getCalledSccpAddressParam(){
    return Optional.ofNullable(this.calledSccpParamter);
  }

  public void setCallingSccpParameter(PatternSccpAddress callingSccpOptional) {
    this.callingSccpParameter = callingSccpOptional;
  }

  public Optional<PatternSccpAddress> getCallingSccpAddressParam() {
    return Optional.ofNullable(this.callingSccpParameter);
  }
  @Override
  public String toString() {
    return "ReplacedValues [Imsi=" + this.imsi + ", calledGt =" + calledGlobalTitle
        + ", callingGT=" + callingGlobalTitle + "]";
  }

  public String getRuleName() {
    return ruleName;
  }

  public void setRuleName(String ruleName) {
    this.ruleName = ruleName;
  }
}