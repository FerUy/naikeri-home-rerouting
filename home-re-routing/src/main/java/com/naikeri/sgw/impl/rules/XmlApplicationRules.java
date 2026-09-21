package com.naikeri.sgw.impl.rules;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.naikeri.sgw.helpers.SgwResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPMessageType;

/**
 * ApplicationRules
 */
public class XmlApplicationRules {

  private final Logger logger = LogManager.getLogger(XmlApplicationRules.class);
  private final String filename;
  private final Pattern pattern = Pattern.compile("^\\d+$");

  public XmlApplicationRules(String filename) {
    this.filename = filename;
  }

  private boolean isNumeric(String strNum) {
    if (strNum == null || strNum.isEmpty()) {
      return false;
    }
    return pattern.matcher(strNum).matches();
  }

  private void validateMessageType(String primitives, String name) {
    // validate the primitive
    if (primitives == null || primitives.isEmpty()) {
      throw new IllegalStateException(
          String.format("The Primitive/Message Type is not defined for the Rule = '%s'", name));
    }
    String primitiveforLogging = "";
    try {
      for (String primitive : primitives.replaceAll("\\s+", "").split(",")) {
        primitiveforLogging = primitive;
        MAPMessageType.valueOf(primitive);
      }
    } catch (Exception e) {
      throw new IllegalStateException(String.format(
          "The Message Type '%s' is NOT supported. Rule = '%s'", primitiveforLogging, name));
    }
  }

  private String getAttributeValue(String ele, String name) {
    if (!isNumeric(ele)) {
      throw new IllegalArgumentException(
          String.format("Invalid Attributes (%s = '%s')", name, ele));
    }
    return ele;
  }

  private void validateReExpression(String str, String errorMsg) {
    if (str == null || str.isEmpty())
      return;
    try {
      Pattern.compile(str);
    } catch (PatternSyntaxException e) {
      throw new IllegalStateException(errorMsg);
    }
  }

  private ApplicationRulesSetting getXmlRules(String name, Element element, boolean enableRegex) {
    ApplicationRulesSetting.Builder builder = new ApplicationRulesSetting.Builder();
    builder.setName(name);
    // read the match
    NodeList matchList = element.getElementsByTagName("Match");
    if (matchList.getLength() > 0) {
      Element matchElement = (Element) matchList.item(0);
      String clgGtStr = matchElement.getAttribute("ClgGt");
      String cldGtStr = matchElement.getAttribute("CldGt");
      String imsiStr = matchElement.getAttribute("Imsi");

      // validate the pattern if regex is enabled
      if (enableRegex) {
        String errorMsg =
            String.format("Invalid Regex: 'IMSI = \"%s\"'. Rule: '%s'", imsiStr, name);
        validateReExpression(imsiStr, errorMsg);
        String clgError =
            String.format("Invalid Regex: 'ClgGt = \"%s\"'. Rule: '%s'", clgGtStr, name);
        validateReExpression(clgGtStr, clgError);
        String cldErr =
            String.format("Invalid Regex: 'CldGt = \"%s\"'. Rule: '%s'", cldGtStr, name);
        validateReExpression(cldGtStr, cldErr);
      }

      String messageTypes;
      if (matchElement.hasAttribute("messagetypes")) {
        messageTypes = matchElement.getAttribute("messagetypes");
      } else {
        messageTypes = matchElement.getAttribute("primitives");
      }

      validateMessageType(messageTypes, name);

      // add the match rule
      builder.setCalledGt(cldGtStr).setCallingGt(clgGtStr).setImsi(imsiStr)
          .setMessageType(messageTypes).setRegexEnabled(enableRegex);
    }

    // read the replace
    NodeList replaceList = element.getElementsByTagName("Replace");
    if (replaceList.getLength() > 0) {
      Element replaceElement = (Element) replaceList.item(0);
      ApplicationReplaceRule replacerule = getReplaceTags(replaceElement, name, enableRegex);
      if (replacerule != null) {
        builder.setReplaceRule(replacerule);
        return builder.build();
      }
    } else {
      logger.info("No Replace tag found for rule '{}'", name);
    }
    return null;
  }

  private String[] getImsiValue(Element element, String name, boolean enableRegex) {
    NodeList imsiTag = element.getElementsByTagName("Imsi");
    if (imsiTag.getLength() > 0) {
      Element elTag = (Element) imsiTag.item(0);
      String imsiValue = elTag.getAttribute("value");
      String patternStr = elTag.getAttribute("pattern");
      try {
        if (enableRegex) {
          // validate the regular expression
          Pattern.compile(patternStr);
        } else {
          imsiValue = getAttributeValue(imsiValue, "Imsi");
        }
      } catch (Exception e) {
        throw new IllegalArgumentException(
            String.format("%s for Replace node for Rule %s", e.getMessage(), name));
      }
      return new String[] {imsiValue, patternStr};
    }
    return new String[0];
  }

  private ApplicationRuleGlobalTitle getGlobalTitleValues(Element element, String name,
                                                          String nodeName, boolean enableRegex) {
    NodeList callingNodeList = element.getElementsByTagName(nodeName);
    if (callingNodeList.getLength() <= 0) {
      return null;
    }
    // get only the first item in the list
    Element cTag = (Element) callingNodeList.item(0);
    int translationType;
    if (cTag.hasAttribute("translationType")) {
      translationType = Integer.parseInt(cTag.getAttribute("translationType"));
    } else {
      throw new IllegalArgumentException(
          String.format("Invalid Translation Type for Rule = '%s'", name));
    }
    int encodingScheme;
    if (cTag.hasAttribute("encodingScheme")) {
      encodingScheme = Integer.parseInt(cTag.getAttribute("encodingScheme"));
    } else {
      throw new IllegalArgumentException(
          String.format("Invalid Encoding Scheme for Rule = '%s'", name));
    }

    int numberingPlan;
    if (cTag.hasAttribute("numberingPlan")) {
      numberingPlan = Integer.parseInt(cTag.getAttribute("numberingPlan"));
    } else {
      throw new IllegalArgumentException(
          String.format("Invalid Numbering Plan for Rule = '%s'", name));
    }

    String natureOfAddress;
    if (cTag.hasAttribute("natureOfAddress")) {
      natureOfAddress = cTag.getAttribute("natureOfAddress");
    } else {
      throw new IllegalArgumentException(
          String.format("Invalid Nature Of Address for Rule = '%s'", name));

    }
    String gtValue = cTag.getAttribute("value");
    String regexPattern = cTag.getAttribute("pattern");
    try {
      if (enableRegex) {
        Pattern.compile(regexPattern);
      } else {
        gtValue = getAttributeValue(gtValue, nodeName);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("%s for Replace node for Rule %s", e.getMessage(), name));
    }

    return new ApplicationRuleGlobalTitle(gtValue, encodingScheme, numberingPlan, natureOfAddress,
        translationType, regexPattern, getSccpAddressParameters(cTag));
  }

  private PatternSccpAddress getSccpAddressParameters(final Element cTag) {
    try {
      Integer subSsytemNumber =
          cTag.hasAttribute("ssn") ? Integer.parseInt(cTag.getAttribute("ssn")) : null;
      Integer destPointCode =
          cTag.hasAttribute("ssn") ? Integer.parseInt(cTag.getAttribute("ssn")) : null;
      String routingIndicator = cTag.getAttribute("ri");
      return new PatternSccpAddress(destPointCode, subSsytemNumber, routingIndicator);
    } catch (Exception e) {
      return null;
    }
  }

  private ApplicationReplaceRule getReplaceTags(Element element, String name, Boolean enableRegex) {
    // Steps:
    // 1. Read and validate IMSI
    // 2. Read and validate Calling GT
    // 3. Read and validate the Called GT
    String imsiStr = null;
    String imsiPattern = null;
    try {
      String[] imsiTagValues = getImsiValue(element, name, enableRegex);
      if (imsiTagValues.length > 0) {
        imsiStr = imsiTagValues[0];
        imsiPattern = imsiTagValues[1];
      }
      ApplicationRuleGlobalTitle callingGt =
          getGlobalTitleValues(element, name, "ClgGt", enableRegex);
      ApplicationRuleGlobalTitle calledGt =
          getGlobalTitleValues(element, name, "CldGt", enableRegex);

      if ((imsiStr == null && callingGt == null && calledGt == null)) {
        logger.error("The 'Replace node' is not defined properly for '{}'", name);
        return null;
      }
      return new ApplicationReplaceRule.Builder().setIMSI(imsiStr).setCallingGt(callingGt)
          .setCalledGt(calledGt).setRegexEnabled(enableRegex).setRegexImsiPattern(imsiPattern)
          .build();
    } catch (Exception e) {
      logger.error("Rules Exception caught: Error: {}Rule Name: {}", e, name);
      return null;
    }
  }

  public List<ApplicationRulesSetting> getApplicationRules() {
    List<ApplicationRulesSetting> apprules = new ArrayList<>();
    if (this.filename == null || this.filename.isEmpty()) {
      logger.error("Invalid filename '{}' for the Application rules", this.filename);
      return apprules;
    }
    logger.debug("Reading Application Rules. Filename: {}", this.filename);
    try {
      InputStream is = new SgwResource(this.filename).getAsStream();
      // Get document builder
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      // Build document
      Document document = builder.parse(is);
      // Normalize the XML Structure
      document.getDocumentElement().normalize();
      // read the configured applications
      NodeList ruleList = document.getElementsByTagName("Rule");
      for (int temp = 0; temp < ruleList.getLength(); temp++) {
        Node nNode = ruleList.item(temp);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element eElement = (Element) nNode;
          String name = eElement.getAttribute("name");
          boolean enableRegex = eElement.hasAttribute("regex")
              && eElement.getAttribute("regex").equalsIgnoreCase("true");
          readApplicationXmlRules(apprules, eElement, name, enableRegex);
        }
      }
      logger.info("Total '{}' Application rules found", apprules.size());

    } catch (Exception e) {
      logger.error("Exception caught for loading application rules. Filename = '{}', Error: {}", this.filename, e);
    }
    return apprules;
  }

  private void readApplicationXmlRules(List<ApplicationRulesSetting> apprules, Element eElement,
                                       String name, boolean enableRegex) {
    try {
      ApplicationRulesSetting mrule = getXmlRules(name, eElement, enableRegex);
      if (mrule != null) {
        apprules.add(mrule);
      }
    } catch (Exception ex) {
      logger.error(ex.getMessage());
    }
  }
}
