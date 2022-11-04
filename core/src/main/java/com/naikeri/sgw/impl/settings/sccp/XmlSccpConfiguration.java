package com.naikeri.sgw.impl.settings.sccp;

import java.util.UUID;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlSccpConfiguration {

  SccpSettings sccp;
  private static final Logger logger = Logger.getLogger(XmlSccpConfiguration.class);

  public LayerSettingsInterface getSettings() {
    return sccp;
  }

  /**
   * Read the SCCP setting from the XML element
   * NOTE: the M3UA settings need to be read first before the SCCP
   *
   * @param element
   * @return SccpSettings
   *
   */
  public XmlSccpConfiguration(Element element) {
    boolean isEnabled = true;
    if (element.hasAttribute("enable")) {
      isEnabled = element.getAttribute("enable").equalsIgnoreCase("true");
    }
    String sccpName = element.getAttribute("name");
    if (sccpName == null || sccpName.isEmpty()) {
      logger.warn("The SCCP name is required");
    }
    String m3uaName = element.getAttribute("m3ua");
    if (m3uaName == null || m3uaName.isEmpty()) {
      logger.warn("The M3UA layer for the SCCP cannot be found in the configuration file");
    }
    logger.debug(String.format("Reading SCCP Configuration for '%s', M3UA = '%s' started", sccpName, m3uaName));

    int sccpid = 1; // defualt value
    // sccpid = Integer.parseInt(element.getAttribute("id"))
    sccp = new SccpSettings(sccpName, m3uaName, sccpid, isEnabled);
    // read the spc
    NodeList spcList = element.getElementsByTagName("spc");
    for (int i = 0; i < spcList.getLength(); i++) {
      Element spcElement = (Element) spcList.item(i);
      String spcName = spcElement.getAttribute("name");
      logger.debug(String.format("[<SCCP Config>] Reading SPC configuration for '%s'", spcName));
      int remoteSpcId = Integer.parseInt(spcElement.getAttribute("remoteSpcId"));
      int remoteSpc = Integer.parseInt(spcElement.getAttribute("remoteSpc"));
      int remoteSpcFlag = Integer.parseInt(spcElement.getAttribute("remoteSpcFlag"));
      int mask = Integer.parseInt(spcElement.getAttribute("mask"));
      String newspcName = spcName.concat(UUID.randomUUID().toString());
      sccp.addRemoteSpc(newspcName, remoteSpcId, remoteSpc, remoteSpcFlag, mask);
    }
    // read the ssn
    NodeList ssnList = element.getElementsByTagName("ssn");
    for (int i = 0; i < ssnList.getLength(); i++) {
      Element ssnElement = (Element) ssnList.item(i);
      String ssnName = ssnElement.getAttribute("name");
      logger.debug(String.format("[<SCCP Config>] Reading SSN configuration for '%s'",ssnName));
      int remoteSsnid = Integer.parseInt(ssnElement.getAttribute("remoteSsnid"));
      int remoteSpc = Integer.parseInt(ssnElement.getAttribute("remoteSpc"));
      int remoteSsn = Integer.parseInt(ssnElement.getAttribute("remoteSsn"));
      int remoteSsnFlag = Integer.parseInt(ssnElement.getAttribute("remoteSsnFlag"));
      boolean spcResuming = ssnElement.hasAttribute("markProhibitedWhenSpcResuming")
                      && ssnElement.getAttribute("markProhibitedWhenSpcResuming").equalsIgnoreCase("true");
      String ssnNewName = ssnName + UUID.randomUUID().toString();
      sccp.addRemoteSsn(ssnNewName, remoteSsnid, remoteSpc, remoteSsn, remoteSsnFlag, spcResuming);
    }

    // read the long Message Rules.
    NodeList longMessageList = element.getElementsByTagName("longMessageRule");
    for (int i = 0; i < longMessageList.getLength(); i++) {
      // check if there are attributes for the longMessageRule
      Element longElem = (Element) longMessageList.item(i);
      if (longElem.getAttributes().getLength() > 0) {
        String lgName = longElem.getAttribute("name");
        logger.debug(String.format("[<SCCP Config>] Reading Long Message Rule configuration for '%s'",lgName));
        int lgId = Integer.parseInt(longElem.getAttribute("id"));
        int lgFirstSpc = Integer.parseInt(longElem.getAttribute("firstSpc"));
        int lgLastSpc = Integer.parseInt(longElem.getAttribute("lastSpc"));
        String lgRuleType = longElem.getAttribute("ruleType");
        sccp.addLongMessageRule(lgName, lgId, lgFirstSpc, lgLastSpc, lgRuleType);
      }
    }

    // Service Access Point
    NodeList sapList = element.getElementsByTagName("sap");
    for (int i = 0; i < sapList.getLength(); i++) {
      Element sap = (Element) sapList.item(i);
      String sapName = sap.getAttribute("name");
      logger.debug(String.format("[<SCCP Config>] Reading Service Access Point (SAP) configuration for '%s'",sapName));
      int sapId = Integer.parseInt(sap.getAttribute("id"));
      int mtp3Id = Integer.parseInt(sap.getAttribute("mtp3Id"));
      int opc = Integer.parseInt(sap.getAttribute("opc"));
      int ni = Integer.parseInt(sap.getAttribute("ni"));
      int networkId = Integer.parseInt(sap.getAttribute("networkId"));
      String localGtDigits = sap.getAttribute("localGtDigits");
      // get the destinations for the service access point
      SccpServiceAccessPoint sapObj = new SccpServiceAccessPoint(sapId, mtp3Id, opc, ni, networkId, localGtDigits);
      String sapNewName = sapName.concat(UUID.randomUUID().toString());
      NodeList destination = sap.getElementsByTagName("destination");
      for (int j = 0; j < destination.getLength(); j++) {
        Element dest = (Element) destination.item(j);
        String dName = dest.getAttribute("name");
        logger.debug(String.format("[<SCCP Config>] Reading Service Access Point (SAP) - Destination configuration for '%s'", dName));
        int destId = Integer.parseInt(dest.getAttribute("destid"));
        int firstDpc = Integer.parseInt(dest.getAttribute("firstDpc"));
        int lastDpc = Integer.parseInt(dest.getAttribute("lastDpc"));
        int firstSls = Integer.parseInt(dest.getAttribute("firstSls"));
        int lastSls = Integer.parseInt(dest.getAttribute("lastSls"));
        int slsMask = Integer.parseInt(dest.getAttribute("slsMask"));
        String destNewName = dName.concat(UUID.randomUUID().toString());
        sapObj.addMtp3Destination(destNewName, sapId, destId, firstDpc, lastDpc, firstSls, lastSls, slsMask);
      }
      sccp.addMtp3ServiceAccessPoint(sapNewName, sapObj);
    }

    // Routing Address
    NodeList routingAddressList = element.getElementsByTagName("routingAddress");
    for (int i = 0; i < routingAddressList.getLength(); i++) {
      // read the attributes
      Element routingAddr = (Element) routingAddressList.item(i);
      String routingName = routingAddr.getAttribute("name");
      logger.debug(String.format("[<SCCP Config>] Reading Routing Addresses configuration for '%s'",routingName));
      int idd = Integer.parseInt(routingAddr.getAttribute("id"));
      String routingIndicator = routingAddr.getAttribute("routingIndicator");
      String digits = routingAddr.getAttribute("digits");

      SccpRoutingAddress sra = new SccpRoutingAddress(routingName, idd, routingIndicator, digits);
      // read the sccp address
      NodeList sccpaddresses = routingAddr.getElementsByTagName("sccpAddress");
      for (int j = 0; j < sccpaddresses.getLength(); j++) {
        // read the attributes
        Element sccpAddr = (Element) sccpaddresses.item(j);
        // read the sccp address
        sra.addSccpAddress(readSccpAddress(sccpAddr, routingIndicator));
      }
      String routeNewName = routingName.concat(UUID.randomUUID().toString());
      sccp.addRoutingAddress(routeNewName, sra);
    }

    // Rules
    NodeList sccpRules = element.getElementsByTagName("rule");
    for (int i = 0; i < sccpRules.getLength(); i++) {
      Element ruleElem = (Element) sccpRules.item(i);
      String ruleName = ruleElem.getAttribute("name");
      logger.debug(String.format("[<SCCP Config>] Reading Rules configuration for '%s'", ruleName));
      String ruleNewName = ruleName.concat(UUID.randomUUID().toString());
      int id = Integer.parseInt(ruleElem.getAttribute("id"));
      String ruleType = ruleElem.getAttribute("ruleType");
      String loadSharingAlgo = ruleElem.getAttribute("loadSharingAlgo");
      String originationType = ruleElem.getAttribute("originationType");
      // String pattern = ruleElem.getAttribute("pattern")
      String mask = ruleElem.getAttribute("mask");
      int pAddressId = Integer.parseInt(ruleElem.getAttribute("pAddressId"));
      int sAddressId = Integer.parseInt(ruleElem.getAttribute("sAddressId"));
      int networkId = Integer.parseInt(ruleElem.getAttribute("networkId"));
      Integer newCallingPartyAddressId = null;
      if (ruleElem.hasAttribute("newCallingPartyAddressId")) {
        newCallingPartyAddressId = Integer.parseInt(ruleElem.getAttribute("newCallingPartyAddressId"));
      }
      // patternSccpAddress
      SccpAddress patternSccpAddress = null;
      NodeList sccpaddresses = ruleElem.getElementsByTagName("patternSccpAddress");
      if (sccpaddresses != null && sccpaddresses.getLength() > 0) {
        Element sccpAddr = (Element) sccpaddresses.item(0);
        patternSccpAddress = readSccpAddress(sccpAddr, null);
      }
      SccpAddress patternCallingSccpAddress = null;
      // read the pattern Calling sccp address
      NodeList callingSccpingAddress = ruleElem.getElementsByTagName("patternCallingSccpAddress");
      if (callingSccpingAddress != null && callingSccpingAddress.getLength() > 0) {
        Element patternCallingsccpAddr = (Element) callingSccpingAddress.item(0);
        patternCallingSccpAddress = readSccpAddress(patternCallingsccpAddr, null);
      }
      // add the rules to the sccp settings
      SccpRules setSccpRules = new SccpRules(id, ruleType, loadSharingAlgo, originationType, patternSccpAddress, mask, pAddressId, sAddressId,
              newCallingPartyAddressId, networkId, patternCallingSccpAddress);
      sccp.addSccpRules(ruleNewName, setSccpRules);
    }
    logger.debug(String.format("Reading SCCP Configuration for '%s' - completed", sccpName));
  }

  private SccpAddress readSccpAddress(final Element sccpAddr, String routingIndicator) {
    // read the attributes
    logger.debug("[<SCCP Config>] Reading SCCPAddress configuration. Routing Indicator = " + routingIndicator);
    int dpc = Integer.parseInt(sccpAddr.getAttribute("dpc"));
    int ssn = Integer.parseInt(sccpAddr.getAttribute("ssn"));
    String ri = sccpAddr.getAttribute("routingIndicator");
    if (ri == null || ri.isEmpty()) {
      ri = routingIndicator;
    }
    // read the gt address
    NodeList gtList = sccpAddr.getElementsByTagName("gt");
    // process only the first child. Ignore the rest
    if (gtList != null && gtList.getLength() > 0) {
      Element gtElement = (Element) gtList.item(0);
      String type = gtElement.getAttribute("type");
      int tt = Integer.parseInt(gtElement.getAttribute("translationType"));
      int es = Integer.parseInt(gtElement.getAttribute("encodingScheme"));
      int np = Integer.parseInt(gtElement.getAttribute("numberingPlan"));
      String nao = gtElement.getAttribute("natureOfAddress");
      String gtDigits = gtElement.getAttribute("digits");
      return SccpRoutingAddress.convertToSccpAddress(ri, dpc, ssn, type, tt, es, np, nao, gtDigits);
    }
    return null;
  }
}
