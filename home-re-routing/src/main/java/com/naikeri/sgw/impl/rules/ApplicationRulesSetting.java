package com.naikeri.sgw.impl.rules;

import java.util.Optional;

/**
 * ApplicationRulesSetting
 */
public final class ApplicationRulesSetting {
  /**
   * The Builder Class
   */
  public static class Builder {
    private String name;
    private String callingGt;
    private String calledGt;
    private String imsi;
    private String primitive;
    private Boolean regexEnabled = false;
    private ApplicationReplaceRule replaceRule;
    private CapRuleComponent component;
    private Boolean isLeg2 = false;

    public Builder setIsLeg2(Boolean isLeg2){
      this.isLeg2 = isLeg2;
      return this;
    }
    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setCallingGt(String callingGt) {
      this.callingGt = callingGt;
      return this;
    }

    public Builder setCalledGt(String calledGt) {
      this.calledGt = calledGt;
      return this;
    }

    public Builder setImsi(String imsi) {
      this.imsi = imsi;
      return this;
    }

    public Builder setMessageType(String messageType) {
      this.primitive = messageType;
      
      return this;
    }

    public Builder setReplaceRule(ApplicationReplaceRule replacerule) {
      this.replaceRule = replacerule;
      return this;
    }

    public Builder setRegexEnabled(Boolean regexEnabled){
      this.regexEnabled = regexEnabled;
      return this;
    }

    public Builder setComponent(CapRuleComponent component){
      this.component = component;
      return this;
    }

    public ApplicationRulesSetting build() {
      ApplicationRulesSetting appRuleSettings = new ApplicationRulesSetting(this.name);

      ApplicationMatchRule matchRule = new ApplicationMatchRule();
      matchRule.setCalledGt(this.calledGt);
      matchRule.setCallingGt(this.callingGt);
      matchRule.setImsi(this.imsi);
      matchRule.setMessageType(this.primitive);
      matchRule.setRegexEnabled(this.regexEnabled);
      appRuleSettings.setMatchRule(matchRule);
      appRuleSettings.setReplaceRule(replaceRule);
      appRuleSettings.setRegexEnabled(regexEnabled);
      appRuleSettings.isLeg2(this.isLeg2);
      appRuleSettings.setComponent(this.component);
      return appRuleSettings;
    }
  }

  private String name;
  private ApplicationMatchRule matchRule;
  private ApplicationReplaceRule replaceRule;
  private CapRuleComponent component;
  private boolean regexEnabled;
  private boolean isLeg2;

  private ApplicationRulesSetting(String name) {
    this.name = name;
  }
  public boolean getIsLeg2(){
    return this.isLeg2;
  }
  public void isLeg2(boolean isLeg2) {
    this.isLeg2 = isLeg2;
  }
  public String getName() {
    return name;
  }
  public ApplicationMatchRule getMatchRule() {
    return matchRule;
  }
  public Optional<ApplicationMatchRule> getMatchRuleOpt(){
    return Optional.ofNullable(this.matchRule);
  }
  public void setMatchRule(ApplicationMatchRule matchRule) {
    this.matchRule = matchRule;
  }
  public ApplicationReplaceRule getReplaceRule() {
    return replaceRule;
  }

  public void setReplaceRule(ApplicationReplaceRule replaceRule) {
    this.replaceRule = replaceRule;
  }

  public void setRegexEnabled(Boolean regexEnabled){
    this.regexEnabled = regexEnabled;
  }

  public Boolean isRegexEnabled(){
    return this.regexEnabled;
  }
  public void setComponent(CapRuleComponent component){
    this.component = component;
  }
  public CapRuleComponent getComponent() {
    return this.component;
  }
}
