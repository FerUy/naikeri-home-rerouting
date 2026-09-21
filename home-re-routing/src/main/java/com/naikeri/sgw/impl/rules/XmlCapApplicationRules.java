package com.naikeri.sgw.impl.rules;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.naikeri.sgw.helpers.SgwResource;
import com.naikeri.sgw.info.ServiceKeys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPMessageType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XmlCapApplicationRules
 */
public class XmlCapApplicationRules {

  private final Logger logger = LogManager.getLogger(XmlCapApplicationRules.class);
  private final String filename;
  private final Pattern pattern = Pattern.compile("^\\d+$");
  private static final String PRIMITIVE = "primitives";
  private static final String VALUE = "value";

  public XmlCapApplicationRules(String filename) {
    this.filename = filename;
  }

  private String getAttributeValue(String value, String name) {
    if (value != null && !value.isEmpty() && !pattern.matcher(value).matches()) {
      throw new IllegalStateException(String.format("Invalid Attributes (%s = '%s')", name, value));
    }
    return value;
  }

  private void validatePrimitiveTypes(String primitives, String name) {
    // validate the primitive
    if (primitives == null || primitives.isEmpty()) {
      throw new IllegalStateException(
          String.format("The Primitives/Message Type is not defined for the Rule = '%s'", name));
    }
    String primitiveforLogging = "";
    try {
      for (String primitive : primitives.replaceAll("\\s+", "").split(",")) {
        primitiveforLogging = primitive;
        CAPMessageType.valueOf(primitive);
      }
    } catch (Exception e) {
      throw new IllegalStateException(String.format(
          "The CAP Primitive '%s' is NOT supported. Rule = '%s'", primitiveforLogging, name));
    }
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

  private ApplicationRulesSetting getXmlRules(String name, Element element, boolean enableRegex,
                                              boolean isLeg2) {
    ApplicationRulesSetting.Builder builder = new ApplicationRulesSetting.Builder();
    builder.setName(name);
    builder.setIsLeg2(isLeg2);
    try {
      // read the match
      NodeList matchList = element.getElementsByTagName("Match");
      if (matchList.getLength() <= 0) {
        return null;
      }
      Element matchElement = (Element) matchList.item(0);
      String clgGtStr = matchElement.getAttribute("ClgGt");
      String cldGtStr = matchElement.getAttribute("CldGt");
      String imsiStr = matchElement.getAttribute("Imsi");

      // validate the pattern if regex is enabled
      if (enableRegex) {
        String imsiErr =
            String.format("Invalid Regex pattern for 'IMSI = \"%s\"' for Rule '%s'", imsiStr, name);
        validateReExpression(imsiStr, imsiErr);
        String clgErr = String.format("Invalid Regex pattern  for 'ClgGt = \"%s\"' for Rule '%s'",
            clgGtStr, name);
        validateReExpression(clgGtStr, clgErr);
        String cldErr = String.format("Invalid Regex pattern for 'CldGt = \"%s\"' for Rule '%s'",
            cldGtStr, name);
        validateReExpression(cldGtStr, cldErr);
      }

      String messageTypes;
      if (matchElement.hasAttribute("messagetypes")) {
        messageTypes = matchElement.getAttribute("messagetypes");
      } else {
        messageTypes = matchElement.getAttribute(PRIMITIVE);
      }

      validatePrimitiveTypes(messageTypes, name);
      // add the match rule
      builder.setCalledGt(cldGtStr).setCallingGt(clgGtStr).setImsi(imsiStr)
          .setMessageType(messageTypes).setRegexEnabled(enableRegex);

      // read the replace
      boolean isReplaceOrComponent = readReplaceTag(element, builder, name, enableRegex);
      if (isReplaceOrComponent) {
        return builder.build();
      }
    } catch (Exception e) {
      logger.error("Failed to read configuration. Error: ", e);
    }
    return null;
  }

  private boolean readReplaceTag(Element element, ApplicationRulesSetting.Builder builder,
                                 String name, boolean enableRegex) {
    boolean isReplaceOrComponent = false;
    NodeList replaceList = element.getElementsByTagName("Replace");
    for (int i = 0; i < replaceList.getLength(); i++) {
      Node nodeRepace = replaceList.item(i);
      if (nodeRepace.getNodeType() == Node.ELEMENT_NODE) {
        Element replaceElement = (Element) nodeRepace;
        if (!replaceElement.getParentNode().getNodeName().equalsIgnoreCase("Component")) {
          ApplicationReplaceRule replacerule = getReplaceTags(replaceElement, name, enableRegex);
          if (replacerule != null) {
            builder.setReplaceRule(replacerule);
            isReplaceOrComponent = true;
          }
        }
      }
    }
    // read the component tags
    NodeList componentTag = element.getElementsByTagName("Component");
    if (componentTag.getLength() > 0) {
      Element componentElement = (Element) componentTag.item(0);
      CapRuleComponent capComponent = getComponentElements(componentElement, name);
      if (capComponent != null) {
        builder.setComponent(capComponent);
        isReplaceOrComponent = true;
      }
    }
    return isReplaceOrComponent;
  }

  private CapRuleComponent getComponentElements(Element componentElement, String name) {
    try {
      CapRuleComponent ruleComponent = new CapRuleComponent();
      // get Remove Xml tag
      NodeList removeNodelist = componentElement.getElementsByTagName("Remove");
      if (removeNodelist.getLength() > 0) {
        // get only the first item in the list
        Node removeNode = removeNodelist.item(0);
        Element removeElement = (Element) removeNode;
        String primitive = removeElement.getAttribute(PRIMITIVE);
        CapRuleComponent.Remove removeComponent = new CapRuleComponent.Remove();
        if (!primitive.isEmpty()) {
          removeComponent.setPrimitives(primitive.replaceAll("\\s+", ""));
        }
        ruleComponent.setRemove(removeComponent);

      }
      // get Replace xml tag
      NodeList replaceNodeList = componentElement.getElementsByTagName("Replace");
      if (replaceNodeList.getLength() > 0) {
        Node replaceNode = replaceNodeList.item(0);
        if (replaceNode.getNodeType() == Node.ELEMENT_NODE) {
          Element replaceElement = (Element) replaceNode;
          String primitives = replaceElement.getAttribute(PRIMITIVE);
          boolean apply = false;
          if (replaceElement.hasAttribute("apply")) {
            apply = replaceElement.getAttribute("apply").equalsIgnoreCase("true");
          }
          CapRuleComponent.Replace replaceComponent = new CapRuleComponent.Replace();
          replaceComponent.setApply(apply);
          replaceComponent.setPrimitive(primitives);
          // read the Arguments tag
          NodeList argumentNodeList = replaceElement.getElementsByTagName("Arguments");
          if (argumentNodeList.getLength() > 0) {
            Node argNode = argumentNodeList.item(0);
            Element argElement = (Element) argNode;
            String argPrimitives = argElement.getAttribute(PRIMITIVE);
            String cdPNStr = argElement.getAttribute("CdPN");
            String range = argElement.getAttribute("range");
            String nai = argElement.getAttribute("nai");
            String npi = argElement.getAttribute("npi");
            String inni = argElement.getAttribute("inni");
            CapRuleComponent.Replace.ReplaceArguments replaceArguments =
                new CapRuleComponent.Replace.ReplaceArguments();
            replaceArguments.setInni(inni);
            replaceArguments.setNai(nai);
            replaceArguments.setPrimitives(argPrimitives);
            replaceArguments.setCdPN(cdPNStr);
            replaceArguments.setRange(range);
            replaceArguments.setNpi(npi);

            replaceComponent.setArguments(replaceArguments);

          }
          ruleComponent.setReplace(replaceComponent);
        }
      }
      return ruleComponent;
    } catch (Exception ex) {
      logger.error("Exception reading Component tag of for the Rule = '{}'. Error: {}", name, ex);
    }
    return null;
  }

  private String[] getImsiValue(Element element, String name, boolean enableRegex) {
    NodeList imsiTag = element.getElementsByTagName("Imsi");
    if (imsiTag.getLength() > 0) {
      Element elTag = (Element) imsiTag.item(0);
      String imsiValue = elTag.getAttribute(VALUE);
      String patternStr = elTag.getAttribute("pattern");
      try {
        if (enableRegex) {
          // validate the regular expression
          Pattern.compile(patternStr);
        } else {
          imsiValue = getAttributeValue(imsiValue, "Imsi");
        }
      } catch (Exception e) {
        throw new IllegalStateException(
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
      throw new IllegalStateException(
          String.format("Invalid Translation Type for Rule = '%s'", name));
    }
    int encodingScheme;
    if (cTag.hasAttribute("encodingScheme")) {
      encodingScheme = Integer.parseInt(cTag.getAttribute("encodingScheme"));
    } else {
      throw new IllegalStateException(
          String.format("Invalid Encoding Scheme for Rule = '%s'", name));
    }

    int numberingPlan;
    if (cTag.hasAttribute("numberingPlan")) {
      numberingPlan = Integer.parseInt(cTag.getAttribute("numberingPlan"));
    } else {
      throw new IllegalStateException(
          String.format("Invalid Numbering Plan for Rule = '%s'", name));
    }

    String natureOfAddress;
    if (cTag.hasAttribute("natureOfAddress")) {
      natureOfAddress = cTag.getAttribute("natureOfAddress");
    } else {
      throw new IllegalStateException(
          String.format("Invalid Nature Of Address for Rule = '%s'", name));

    }
    String gtValue = cTag.getAttribute(VALUE);
    String regexPattern = cTag.getAttribute("pattern");
    try {
      if (enableRegex) {
        Pattern.compile(regexPattern);
      } else {
        gtValue = getAttributeValue(gtValue, nodeName);
      }
    } catch (Exception e) {
      throw new IllegalStateException(
          String.format("%s for Replace node for Rule %s", e.getMessage(), name));
    }
    return new ApplicationRuleGlobalTitle(gtValue, encodingScheme, numberingPlan, natureOfAddress,
        translationType, regexPattern, getSccpAddressParameters(cTag));

  }

  private PatternSccpAddress getSccpAddressParameters(final Element cTag) {
    try {
      Integer subSystemNumber = cTag.hasAttribute("ssn") ? Integer.parseInt(cTag.getAttribute("ssn")) : null;
      Integer destPointCode = cTag.hasAttribute("dpc") ? Integer.parseInt(cTag.getAttribute("dpc")) : null;
      String routingIndicator = cTag.getAttribute("ri");
      return new PatternSccpAddress(destPointCode, subSystemNumber, routingIndicator);
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
      ApplicationRuleGlobalTitle callingGt = getGlobalTitleValues(element, name, "ClgGt", enableRegex);
      ApplicationRuleGlobalTitle calledGt = getGlobalTitleValues(element, name, "CldGt", enableRegex);

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
          boolean isLeg2 = eElement.hasAttribute("leg2")
              && eElement.getAttribute("leg2").equalsIgnoreCase("true");
          ApplicationRulesSetting mrule = getXmlRules(name, eElement, enableRegex, isLeg2);
          if (mrule != null) {
            apprules.add(mrule);
          }
        }
      }
      logger.info("Total '{}' Application rules found", apprules.size());

      readServiceKeyConfiguration(document);

    } catch (Exception e) {
      logger.error("Exception caught for loading application rules. Filename = '{}', Error: {}", this.filename, e);
    }
    return apprules;
  }

  private void readServiceKeyConfiguration(Document document) {
    NodeList serviceKeysList = document.getElementsByTagName("ServiceKey");
    for (int i = 0; i < serviceKeysList.getLength(); i++) {
      Node srvKeyNode = serviceKeysList.item(i);
      if (srvKeyNode.getNodeType() == Node.ELEMENT_NODE) {
        Element srvElement = (Element) srvKeyNode;
        if (srvElement.getParentNode().getNodeName().equalsIgnoreCase("ServiceKeys")) {
          String primitive = srvElement.getAttribute("primitive");
          int serviceKeyValue = -1;
          if (srvElement.hasAttribute(VALUE)) {
            serviceKeyValue = Integer.parseInt(srvElement.getAttribute(VALUE));
          }
          if (serviceKeyValue > -1) {
            ServiceKeys.getInstance().addServiceKey(primitive, serviceKeyValue);
          } else {
            logger.error("Invalid Service Key Value for {}", primitive);
          }
        }
      }
    }
  }

}
