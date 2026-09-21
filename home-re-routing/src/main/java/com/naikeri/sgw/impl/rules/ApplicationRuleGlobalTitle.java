package com.naikeri.sgw.impl.rules;

/**
 * ApplicationRuleGlobalTitle
 */
public class ApplicationRuleGlobalTitle {

  private String globalTitleValue;
  private Integer encodingScheme;
  private Integer numberingPlan;
  private String natureOfAddress;
  private Integer translationType;
  private String regexPattern;
  private PatternSccpAddress sccpAddressParam;

  public ApplicationRuleGlobalTitle(String gtValue, Integer encodingScheme, Integer numberingPlan,
      String natureOfAddress, Integer translationType, String regexPattern, PatternSccpAddress sccpAddrParam) {
    this.globalTitleValue = gtValue;
    this.encodingScheme = encodingScheme;
    this.numberingPlan = numberingPlan;
    this.natureOfAddress = natureOfAddress;
    this.translationType = translationType;
    this.regexPattern = regexPattern;
    this.sccpAddressParam = sccpAddrParam;
  }

  public PatternSccpAddress getPatternSccpAddress(){
    return this.sccpAddressParam;
  }
  
  public String getGlobalTitleValue() {
    return globalTitleValue;
  }
  
  public Integer getEncodingScheme() {
    return encodingScheme;
  }
  public Integer getNumberingPlan() {
    return numberingPlan;
  }
  public String getNatureOfAddress() {
    return natureOfAddress;
  }
  public Integer getTranslationType() {
    return translationType;
  }

  @Override
  public String toString() {
    return "ApplicationRuleGlobalTitle [encodingScheme=" + encodingScheme + ", globalTitleValue="
        + globalTitleValue + ", natureOfAddress=" + natureOfAddress + ", numberingPlan="
        + numberingPlan + ", translationType=" + translationType + "]";
  }

  public String getRegexPattern() {
    return regexPattern;
  }
}
