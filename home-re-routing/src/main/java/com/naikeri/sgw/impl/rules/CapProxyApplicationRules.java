package com.naikeri.sgw.impl.rules;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * CapProxyApplicationRules
 */
public class CapProxyApplicationRules {

  private static final Logger logger = LogManager.getLogger(CapProxyApplicationRules.class);
  private static CapProxyApplicationRules sInstance = null;
  private final CopyOnWriteArrayList<ApplicationRulesSetting> capApplicationRules;

  public CapProxyApplicationRules() {
    capApplicationRules = new CopyOnWriteArrayList<>();
  }

  public static CapProxyApplicationRules instance() {
    if (sInstance == null) {
      sInstance = new CapProxyApplicationRules();
    }
    return sInstance;
  }

  public List<ApplicationRulesSetting> getCapApplicationRules() {
    return this.capApplicationRules;
  }

  public void addCapApplicationRules(String filename) {
    try {
      XmlCapApplicationRules xmlCapAppRules = new XmlCapApplicationRules(filename);
      capApplicationRules.addAll(xmlCapAppRules.getApplicationRules());
    } catch (Exception e) {
      logger.error("Exception caught reading CAP Application Rule xml file. Exception: {}", String.valueOf(e));
    }
  }

  public Optional<ApplicationRulesSetting> findCAPApplicationRule(String callingGT, String calledGT,
                                                                  String imsi, String primitive, Boolean isLeg2) {

    return this.capApplicationRules.stream().filter(currentRuleSetting -> searchApplicationRules(currentRuleSetting, callingGT,
        calledGT, imsi, primitive, isLeg2)).findFirst();
  }

  private boolean searchApplicationRules(ApplicationRulesSetting ruleSetting, String callingGT,
                                         String calledGT, String imsi, String primitive, boolean isLeg2) {
    // checking the legs rules
    if (ruleSetting.getIsLeg2() != isLeg2) {
      return false;
    }
    ApplicationMatchRule matchRule = ruleSetting.getMatchRule();
    boolean result =
        matchRule.getMessageTypes().stream().anyMatch(p -> p.equalsIgnoreCase(primitive));

    if (!result) {
      // return - no message type found
      return false;
    }

    // check if the regex is enabled
    if (matchRule.getRegexEnabled()) {
      // check for IMSI pattern
      result = isPatternMatch(matchRule.getImsi(), imsi, result);
      // called GT
      result = isPatternMatch(matchRule.getCalledGt(), calledGT, result);
      // calling GT
      result = isPatternMatch(matchRule.getCallingGt(), callingGT, result);
    } else {
      // check for IMSI pattern
      result = isStartWith(imsi, matchRule.getImsi(), result);
      // called GT
      result = isStartWith(calledGT, matchRule.getCalledGt(), result);
      // calling GT
      result = isStartWith(callingGT, matchRule.getCallingGt(), result);
    }
    return result;
  }

  private boolean isPatternMatch(String regexStr, String str2, boolean result) {
    if (regexStr == null || regexStr.isEmpty() || str2 == null) {
      return result;
    }
    return result && Pattern.matches(regexStr, str2);
  }

  private boolean isStartWith(String paramStr1, String paramStr2, boolean defaultValue) {
    if (paramStr1 == null || paramStr1.isEmpty() || paramStr2 == null) {
      return defaultValue;
    }
    return defaultValue && paramStr1.startsWith(paramStr2);
  }
}
