package com.naikeri.sgw.impl.rules;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * ApplyCapApplicationRules
 */
public class ApplyCapApplicationRules {

  private static final Logger logger = LoggerFactory.getLogger(ApplyCapApplicationRules.class);

  private ApplyCapApplicationRules() {
    //
  }

  // return ReplacedValues
  public static CapApplicationRulesResult apply(SccpAddress callingAddress,
      SccpAddress calledAddress, String imsi, String primitive, String transactionId) {
    return apply(callingAddress, calledAddress, imsi, primitive, transactionId, false);
  }

  public static CapApplicationRulesResult apply(SccpAddress callingAddress,
      SccpAddress calledAddress, String imsi, String primitive, String transactionId,
      Boolean isLeg2) {
    CapApplicationRulesResult result = null;
    String callingGT = callingAddress.getGlobalTitle().getDigits();
    String calledGT = calledAddress.getGlobalTitle().getDigits();
    logger.debug(
        String.format("CAP<%s>: Searching RULE for: ClgGt = '%s', CldGt = '%s', Imsi = '%s', %s",
            primitive, callingGT, calledGT, imsi, transactionId));


    Optional<ApplicationRulesSetting> rulesSettingOpt = CapProxyApplicationRules.instance()
        .findCAPApplicationRule(callingGT, calledGT, imsi, primitive, isLeg2);
    if (!rulesSettingOpt.isPresent()) {
      return null;
    }
    ApplicationRulesSetting rulesSetting = rulesSettingOpt.get();


    result = new CapApplicationRulesResult();
    
    result.setRuleName(rulesSetting.getName());
    logger.debug(String.format(
        "CAP<%s>: Matching rule found. Rule Name = '%s', ClgGt = '%s', CldGt = '%s', Imsi = '%s', %s",
        primitive, rulesSetting.getName(), callingGT, calledGT, imsi, transactionId));

    if (rulesSetting.getReplaceRule() != null) {
      ReplacedValues replaceRule =
          rulesSetting.getReplaceRule().applyReplaceRule(imsi, callingGT, calledGT);
      if (replaceRule != null) {
        if (replaceRule.getImsi() != null && !replaceRule.getImsi().isEmpty()) {
          logger.info(String.format("<%s, %s>, Replaced Values: OldIMSI = '%s', newIMSI = '%s', %s",
              primitive, transactionId, imsi, replaceRule.getImsi(), transactionId));
        }
        if (replaceRule.getCalledGlobalTitle() != null) {
          logger.info(
              String.format("<%s, %s>, Replaced Values: Old CldGt = '%s', new CldGt = '%s', %s",
                  primitive, transactionId, calledGT,
                  replaceRule.getCalledGlobalTitle().getDigits(), transactionId));
        }
        if (replaceRule.getCallingGlobalTitle() != null) {
          logger.info(
              String.format("<%s, %s>, Replaced Values: Old ClgGt = '%s', new ClgGt = '%s', %s",
                  primitive, transactionId, callingGT,
                  replaceRule.getCallingGlobalTitle().getDigits(), transactionId));
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
