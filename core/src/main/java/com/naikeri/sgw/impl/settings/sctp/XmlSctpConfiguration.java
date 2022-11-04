package com.naikeri.sgw.impl.settings.sctp;

import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlSctpConfiguration {

  SctpSettings sctpSettings;
  private static final Logger logger = Logger.getLogger(XmlSctpConfiguration.class);

  public LayerSettingsInterface getSettings() {
    return sctpSettings;
  }

  public XmlSctpConfiguration(Element element) {
    boolean isEnabled = true; // default value is true
    if (element.hasAttribute("enable")) {
      isEnabled = element.getAttribute("enable").equalsIgnoreCase("true");
    }

    // get the name of the SCTP configuration.
    String sctpName = element.getAttribute("name");
    if (sctpName == null || sctpName.isEmpty()) {
      logger.warn("The SCTP setting name is required");
    }
    logger.debug(String.format("Reading SCTP Configuration for '%s' started", sctpName));
    Boolean isSingleThread = element.hasAttribute("singleThread")
                    && element.getAttribute("singleThread").equalsIgnoreCase("true");
    int connectDelay = 3000; // default value for the connectDelay
    if (element.hasAttribute("connectDelay")) {
      connectDelay = Integer.parseInt(element.getAttribute("connectDelay"));
    }
    sctpSettings = new SctpSettings(sctpName, isSingleThread, connectDelay, isEnabled);

    // read association
    NodeList assocNodes = element.getElementsByTagName("association");
    for (int x = 0; x < assocNodes.getLength(); x++) {
      Element assocElement = (Element) assocNodes.item(x);
      String serverName = assocElement.getAttribute("name");
      logger.debug(String.format("[<SCTP Config>]: Reading SCTP Association for '%s'", serverName));
      String host = assocElement.getAttribute("host");
      String peer = assocElement.getAttribute("peer");
      Boolean transport = assocElement.hasAttribute("transport")
                      && assocElement.getAttribute("transport").equalsIgnoreCase("SCTP");
      String multiHome = assocElement.getAttribute("multihome");

      sctpSettings.addAssociation(serverName, host, peer, transport, multiHome);
    }

    NodeList serverNodes = element.getElementsByTagName("server");
    for (int x = 0; x < serverNodes.getLength(); x++) {
      Element serverElement = (Element) serverNodes.item(x);
      String serverName = serverElement.getAttribute("name");
      logger.debug(String.format("[<SCTP Config>]: Reading Server configuration for '%s'", serverName));
      String host = serverElement.getAttribute("host");
      // if transport == SCTP => true otherwise false
      Boolean transport = serverElement.hasAttribute("transport")
                      && serverElement.getAttribute("transport").equalsIgnoreCase("SCTP");

      Boolean accept = serverElement.hasAttribute("acceptAnonymousConnections")
                      && serverElement.getAttribute("acceptAnonymousConnections").equalsIgnoreCase("true");
      // set the default concurrent connection to 10
      int concurrentConnections = 10;
      if (serverElement.hasAttribute("concurrentConnections")) {
        concurrentConnections =
                        Integer.parseInt(serverElement.getAttribute("concurrentConnections"));
      }
      String multiHome = serverElement.getAttribute("multihome");
      sctpSettings.addServer(serverName, host, transport, accept, concurrentConnections, multiHome);
      // get the associations
      NodeList serverAssocList = serverElement.getElementsByTagName("serverAssociation");
      for (int y = 0; y < serverAssocList.getLength(); y++) {
        Element sAssoc = (Element) serverAssocList.item(y);
        String associationName = sAssoc.getAttribute("name");
        logger.debug(String.format("[<SCTP Config>]: Reading Server Associations for '%s'",associationName));
        String assocPeeString = sAssoc.getAttribute("peer");
        Boolean assocTransBoolean = sAssoc.hasAttribute("transport")
                        && sAssoc.getAttribute("transport").equalsIgnoreCase("SCTP");
        sctpSettings.addServerAssociation(serverName, associationName, assocPeeString,
                        assocTransBoolean, multiHome);
      }
    }
    logger.debug(String.format("Reading SCTP configuration for '%s' completed.", sctpName));
  }
}
