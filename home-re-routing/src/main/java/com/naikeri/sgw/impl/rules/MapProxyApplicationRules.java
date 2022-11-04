package com.naikeri.sgw.impl.rules;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.*;
import org.apache.log4j.Logger;

/**
 * MapApplicationRules
 */
public class MapProxyApplicationRules {
  private static final Logger logger = Logger.getLogger(MapProxyApplicationRules.class);
  private CopyOnWriteArrayList<ApplicationRulesSetting> mapApplicationRules;
  static MapProxyApplicationRules sInstance = null;

  public static MapProxyApplicationRules getInstance() {
    if (sInstance == null)
      sInstance = new MapProxyApplicationRules();
    return sInstance;
  }

  private MapProxyApplicationRules() {
    mapApplicationRules = new CopyOnWriteArrayList<>();
  }

  public List<ApplicationRulesSetting> getApplicationRules() {
    return mapApplicationRules;
  }

  public void addMapApplicationRules(String filename) {
    try {
      XmlApplicationRules xmlApplicationRules = new XmlApplicationRules(filename);
      mapApplicationRules.addAll(xmlApplicationRules.getApplicationRules());
    } catch (Exception e) {
      logger.error("Exception caught:" + e);
    }
  }

  public void addCapApplicationRules(String filename) {
    try {
      XmlCapApplicationRules xmlCapAppRules = new XmlCapApplicationRules(filename);
      mapApplicationRules.addAll(xmlCapAppRules.getApplicationRules());
    } catch (Exception e) {
      logger.error("Exception caught reading CAP Application Rule xml file. Exception: " + e);
    }
  }

  public ApplicationRulesSetting findMAPApplicationRule(String callingGT, String calledGT,
      String imsiStr, String messageType) {
    Optional<ApplicationRulesSetting> appRulesSetting =
        this.mapApplicationRules.stream().filter(ruleSetting -> searchApplicationRules(ruleSetting,
            callingGT, calledGT, imsiStr, messageType)).findFirst();
    if (appRulesSetting.isPresent()) {
      return appRulesSetting.get();
    }
    return null;
  }

  private boolean searchApplicationRules(ApplicationRulesSetting ruleSetting, String callingGT,
      String calledGT, String imsiStr, String messageType) {
    // ensure there is match rules
    ApplicationMatchRule matchRule = ruleSetting.getMatchRule();
    if (matchRule == null) {
      return false;
    }
    boolean result =
        matchRule.getMessageTypes().stream().anyMatch(p -> p.equalsIgnoreCase(messageType));
    if (!result) {
      // return - no message type found
      return false;
    }
    if (matchRule.getRegexEnabled()) {
      // check for IMSI pattern
      String regexImsi = ruleSetting.getMatchRule().getImsi();
      result = isPatternMatch(regexImsi, imsiStr, result);
      // called GT
      String regexCldGT = ruleSetting.getMatchRule().getCalledGt();
      result = isPatternMatch(regexCldGT, calledGT, result);
      // calling GT
      String regexClgGT = ruleSetting.getMatchRule().getCallingGt();
      result = isPatternMatch(regexClgGT, callingGT, result);
    } else {
      // check for IMSI pattern
      String ruleImsi = ruleSetting.getMatchRule().getImsi();
      result = isStartWith(imsiStr, ruleImsi, result);
      // called GT
      String ruleCldGT = ruleSetting.getMatchRule().getCalledGt();
      result = isStartWith(calledGT, ruleCldGT, result);
      // calling GT
      String ruleClgGT = ruleSetting.getMatchRule().getCallingGt();
      result = isStartWith(callingGT, ruleClgGT, result);
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
