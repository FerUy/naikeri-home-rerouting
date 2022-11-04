package com.naikeri.sgw.impl.rules;

public class CapApplicationRulesResult {

  private String ruleName;
  private ReplacedValues replacedValues;
  private CapRuleComponent capRuleComponent;

  public void setRuleName(String ruleName){
    this.ruleName = ruleName;
  }

  public String getRuleName() {
    return this.ruleName;
  }
  public ReplacedValues getReplacedValues() {
    return replacedValues;
  }

  public void setReplacedValues(ReplacedValues replacedValues) {
    this.replacedValues = replacedValues;
  }

  public CapRuleComponent getCapRuleComponent() {
    return capRuleComponent;
  }

  public void setCapRuleComponent(CapRuleComponent capRuleComponent) {
    this.capRuleComponent = capRuleComponent;
  }

}