package com.naikeri.sgw.impl.rules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDOddEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.DefaultEncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;

/**
 * The replacement for the match rule
 */
public class ApplicationReplaceRule {

  public static class Builder {
    private ApplicationRuleGlobalTitle callingGt;
    private ApplicationRuleGlobalTitle calledGt;
    private String imsi;
    private Boolean regexEnabled;

    private String regexImsi;



    public Builder setRegexImsiPattern(String imsi) {
      this.regexImsi = imsi;
      return this;
    }

    public Builder setRegexEnabled(Boolean regexEnabled) {
      this.regexEnabled = regexEnabled;
      return this;
    }

    public Builder setCallingGt(ApplicationRuleGlobalTitle callingGt) {
      this.callingGt = callingGt;
      return this;
    }

    public Builder setCalledGt(ApplicationRuleGlobalTitle calledGt) {
      this.calledGt = calledGt;
      return this;
    }

    public Builder setIMSI(String imsi) {
      this.imsi = imsi;
      return this;
    }

    public ApplicationReplaceRule build() {
      ApplicationReplaceRule applicationReplaceRule = new ApplicationReplaceRule();
      applicationReplaceRule.setCalledGt(calledGt);
      applicationReplaceRule.setCallingGt(callingGt);
      applicationReplaceRule.setIMSI(imsi);
      applicationReplaceRule.setRegexEnabled(regexEnabled);

      applicationReplaceRule.setRegexImsiPattern(this.regexImsi);
      return applicationReplaceRule;
    }
  }

  private final Logger logger = LogManager.getLogger(ApplicationReplaceRule.class);

  private ApplicationRuleGlobalTitle callingGt;
  private ApplicationRuleGlobalTitle calledGt;
  private String imsi;
  private Boolean regexEnabled;
  private String regexImsiPattern;
  public void setRegexImsiPattern(String imsiPattern) {
    this.regexImsiPattern = imsiPattern;
  }

  private ApplicationReplaceRule() {
  }

  public void setCallingGt(ApplicationRuleGlobalTitle callingGt) {
    this.callingGt = callingGt;
  }

  public void setCalledGt(ApplicationRuleGlobalTitle calledGt) {
    this.calledGt = calledGt;
  }

  public void setIMSI(String imsi) {
    this.imsi = imsi;
  }

  public void setRegexEnabled(Boolean regexEnabled) {
    this.regexEnabled = regexEnabled;
  }

  private String replaceImsiByRegex(String newImsi) {
    if (this.regexImsiPattern == null || this.regexImsiPattern.isEmpty())
      return newImsi;
    if (this.imsi == null || this.imsi.isEmpty())
      return newImsi;
    return newImsi.replaceAll(this.regexImsiPattern, this.imsi);
  }

  private String replaceImsi(String newImsi) {
    if (this.imsi == null || this.imsi.isEmpty()) {
      return newImsi;
    }
    int length = this.imsi.length();
    if (length >= newImsi.length()) {
      return this.imsi;
    }
    return this.imsi.concat(newImsi.substring(length));
  }

  // isCalledGt == TRUE => CalledGT
  // isCalledGt == FALSE => CallingGt
  private GlobalTitle replaceGlobalTitle(String gt, Boolean isCalledGt) {
    ApplicationRuleGlobalTitle tempRuleGt;
    if (Boolean.TRUE.equals(isCalledGt)) {
      tempRuleGt = this.calledGt;
    } else {
      tempRuleGt = this.callingGt;
    }
    // check if the tempRule is null
    if (tempRuleGt == null) {
      return null;
    }
    // change the GT Value
    String gtValue = tempRuleGt.getGlobalTitleValue();

    String newGtValue;
    int length = gtValue.length();
    if (length >= gt.length()) {
      newGtValue = gtValue;
    } else {
      newGtValue = gtValue.concat(gt.substring(length));
    }
    try {
      ParameterFactoryImpl factory = new ParameterFactoryImpl();
      EncodingScheme ec;
      switch (tempRuleGt.getEncodingScheme()) { // digits.length() % 2 == 0
        case 1:
          ec = new BCDOddEncodingScheme();
          break;
        case 2:
          ec = new BCDEvenEncodingScheme();
          break;
        default:
          ec = new DefaultEncodingScheme();
          break;
      }
      NumberingPlan np = NumberingPlan.valueOf(tempRuleGt.getNumberingPlan());
      // NatureOfAddress
      NatureOfAddress noa = NatureOfAddress.valueOf(tempRuleGt.getNatureOfAddress());

      return factory.createGlobalTitle(newGtValue, tempRuleGt.getTranslationType(), np, ec, noa);
    } catch (Exception e) {
      logger.error("Exception caught: {}", String.valueOf(e));
    }
    return null;
  }

  // isCalledGt == TRUE => CalledGT
  // isCalledGt == FALSE => CallingGt
  private GlobalTitle replaceGlobalTitleUsingRegex(String gt, boolean isCalledGt) {
    ApplicationRuleGlobalTitle tempRuleGt;
    if (isCalledGt) {
      tempRuleGt = this.calledGt;
    } else {
      tempRuleGt = this.callingGt;
    }
    // check if the tempRule is null
    if (tempRuleGt == null) {
      return null;
    }
    String pattern = tempRuleGt.getRegexPattern();

    if (pattern == null || pattern.isEmpty())
      return null;
    if (gt == null || gt.isEmpty())
      return null;

    // change the GT Value
    String gtValue = tempRuleGt.getGlobalTitleValue();
    String newGtValue = gt.replaceAll(pattern, gtValue);

    try {
      ParameterFactoryImpl factory = new ParameterFactoryImpl();
      EncodingScheme ec;
      switch (tempRuleGt.getEncodingScheme()) { // digits.length() % 2 == 0
        case 1:
          ec = new BCDOddEncodingScheme();
          break;
        case 2:
          ec = new BCDEvenEncodingScheme();
          break;
        default:
          ec = new DefaultEncodingScheme();
          break;
      }
      NumberingPlan np = NumberingPlan.valueOf(tempRuleGt.getNumberingPlan());
      // NatureOfAddress
      NatureOfAddress noa = NatureOfAddress.valueOf(tempRuleGt.getNatureOfAddress());

      return factory.createGlobalTitle(newGtValue, tempRuleGt.getTranslationType(), np, ec, noa);
    } catch (Exception e) {
      logger.error("Exception caught: {}", String.valueOf(e));
    }
    return null;
  }


  public ReplacedValues applyReplaceRule(String imsiString, String callingGt, String calledGt) {
    ReplacedValues retValues = new ReplacedValues();
    if (this.calledGt != null){
      retValues.setCalledSccpParameter(this.calledGt.getPatternSccpAddress());
    }
    if (this.callingGt != null){
      retValues.setCallingSccpParameter(this.callingGt.getPatternSccpAddress());
    }
    if (Boolean.TRUE.equals(this.regexEnabled)) {
      // use regex for the replacement
      // 1. IMSI
      retValues.setImsi(replaceImsiByRegex(imsiString));
      // 2. CALLEDGT
      retValues.setCalledGlobalTitle(replaceGlobalTitleUsingRegex(calledGt, true));
      // 3. CALLINGGT
      retValues.setCallingGlobalTitle(replaceGlobalTitleUsingRegex(callingGt, false));
    } else {
      // replace the values here
      // 1. IMSI
      retValues.setImsi(replaceImsi(imsiString));
      // 2. CALLEDGT
      retValues.setCalledGlobalTitle(replaceGlobalTitle(calledGt, true));
      // 3. CALLINGGT
      retValues.setCallingGlobalTitle(replaceGlobalTitle(callingGt, false));
    }
    return retValues;
  }
}
