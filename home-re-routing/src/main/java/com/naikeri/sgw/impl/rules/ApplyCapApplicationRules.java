package com.naikeri.sgw.impl.rules;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * ApplyCapApplicationRules
 */
public class ApplyCapApplicationRules {

  private static final Logger logger = LogManager.getLogger(ApplyCapApplicationRules.class);

  private ApplyCapApplicationRules() {
    //
  }

  // return ReplacedValues
  public static CapApplicationRulesResult apply(SccpAddress callingAddress,
      SccpAddress calledAddress, String imsi, String primitive, String transactionId) {
    return apply(callingAddress, calledAddress, imsi, primitive, transactionId, false);
  }

  public static CapApplicationRulesResult apply(SccpAddress callingAddress, SccpAddress calledAddress,
      String imsi, String primitive, String transactionId, Boolean isLeg2) {
    CapApplicationRulesResult result;
    String callingGT = callingAddress.getGlobalTitle().getDigits();
    String calledGT = calledAddress.getGlobalTitle().getDigits();
    logger.debug("CAP<{}>: Searching RULE for: ClgGt = '{}', CldGt = '{}', Imsi = '{}', {}", primitive, callingGT, calledGT, imsi, transactionId);

    Optional<ApplicationRulesSetting> rulesSettingOpt = CapProxyApplicationRules.instance()
        .findCAPApplicationRule(callingGT, calledGT, imsi, primitive, isLeg2);
    if (rulesSettingOpt.isEmpty()) {
      return null;
    }
    ApplicationRulesSetting rulesSetting = rulesSettingOpt.get();


    result = new CapApplicationRulesResult();

    result.setRuleName(rulesSetting.getName());
    logger.debug("CAP<{}>: Matching rule found. Rule Name = '{}', ClgGt = '{}', CldGt = '{}', Imsi = '{}', {}", primitive, rulesSetting.getName(), callingGT, calledGT, imsi, transactionId);

    if (rulesSetting.getReplaceRule() != null) {
      ReplacedValues replaceRule =
          rulesSetting.getReplaceRule().applyReplaceRule(imsi, callingGT, calledGT);
      if (replaceRule != null) {
        if (replaceRule.getImsi() != null && !replaceRule.getImsi().isEmpty()) {
          logger.info("<{}, {}>, Replaced Values: OldIMSI = '{}', newIMSI = '{}', {}", primitive, transactionId, imsi, replaceRule.getImsi(), transactionId);
        }
        if (replaceRule.getCalledGlobalTitle() != null) {
          logger.info("<{}, {}>, Replaced Values: Old CldGt = '{}', new CldGt = '{}', {}", primitive, transactionId, calledGT, replaceRule.getCalledGlobalTitle().getDigits(), transactionId);
        }
        if (replaceRule.getCallingGlobalTitle() != null) {
          logger.info("<{}, {}>, Replaced Values: Old ClgGt = '{}', new ClgGt = '{}', {}", primitive, transactionId, callingGT, replaceRule.getCallingGlobalTitle().getDigits(), transactionId);
        }
        result.setReplacedValues(replaceRule);
      }
    }
    // apply the component
    result.setCapRuleComponent(rulesSetting.getComponent());
    // -- replace component

    return result;
  }
}
