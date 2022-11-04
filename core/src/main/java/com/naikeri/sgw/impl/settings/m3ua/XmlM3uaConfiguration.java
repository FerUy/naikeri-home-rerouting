package com.naikeri.sgw.impl.settings.m3ua;

import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlM3uaConfiguration {

  M3uaSettings m3ua;
  private static final Logger logger = Logger.getLogger(XmlM3uaConfiguration.class);

  public LayerSettingsInterface getSettings() {
    return m3ua;
  }

  /**
   * Read the settings from the xml Element into the M3uaSetting object
   *
   * @param element
   * @return M3uaSettings
   */
  public XmlM3uaConfiguration(Element element) {

    Boolean isEnabled = true; // default value
    if (element.hasAttribute("enable")) {
      isEnabled = element.getAttribute("enable").equalsIgnoreCase("true");
    }
    String m3uaName = element.getAttribute("name");
    if (m3uaName == null || m3uaName.isEmpty()) {
      logger.warn("The name for the M3UA node is missing");
    }
    String productName = element.getAttribute("productName");
    String sctpName = element.getAttribute("sctp");
    if (sctpName == null || sctpName.isEmpty()) {
      logger.warn("The SCTP Layer name is required");
    }
    logger.debug(String.format("Reading M3UA configuration for '%s', SCTP = '%s' - started.", m3uaName, sctpName));

    Integer heartbeatTime = 10000; // default value from prototype
    if (element.hasAttribute("heartbeatTime")) {
      heartbeatTime = Integer.parseInt(element.getAttribute("heartbeatTime"));
    }
    m3ua = new M3uaSettings(m3uaName, productName, heartbeatTime, sctpName, isEnabled);
    // load the application server
    NodeList asNodelist = element.getElementsByTagName("as");
    for (int i = 0; i < asNodelist.getLength(); i++) {
      Element asItem = (Element) asNodelist.item(i);
      // application server name
      String aSName = asItem.getAttribute("name");
      logger.debug(String.format("[<M3UA Config>]: Reading Application Server configuration for '%s'", aSName));
      // functionality
      String functionality = asItem.getAttribute("functionality");
      // exchangetype
      String exchangetype = asItem.getAttribute("exchangetype");
      // ipsptype
      String ipsptype = asItem.getAttribute("ipsptype");
      // routing context
      int routingcontext = 100;
      if (asItem.hasAttribute("routingcontext")) {
        routingcontext = Integer.parseInt(asItem.getAttribute("routingcontext"));
      }
      // traffic mode
      String trafficmode = asItem.getAttribute("trafficmode");
      int minaspforloadbalance = 0;
      if (asItem.hasAttribute("minaspforloadbalance")) {
        minaspforloadbalance = Integer.parseInt(asItem.getAttribute("minaspforloadbalance"));
      }
      long networkappearance = 0L;
      if (asItem.hasAttribute("networkappearance")) {
        networkappearance = Integer.parseInt(asItem.getAttribute("networkappearance"));
      }
      m3ua.addApplicationServer(aSName, functionality, exchangetype, ipsptype, routingcontext, trafficmode, minaspforloadbalance, networkappearance);

      NodeList aspList = asItem.getElementsByTagName("asp");
      for (int x = 0; x < aspList.getLength(); x++) {
        Element aspItem = (Element) aspList.item(x);
        String aspName = aspItem.getAttribute("name");
        logger.debug(String.format("[<M3UA Config>]: Reading Application Server Process configuration for '%s'", aspName));
        String association = aspItem.getAttribute("sctpAssociationName");
        Boolean heartbeatenabled = aspItem.hasAttribute("heartbeatenabled")
                        && aspItem.getAttribute("heartbeatenabled").equalsIgnoreCase("true");
        m3ua.addApplicationServerProcess(aSName, aspName, association, heartbeatenabled);
      }

      // read application server routes
      NodeList routeList = asItem.getElementsByTagName("route");
      for (int x = 0; x < routeList.getLength(); x++) {
        Element routeItem = (Element) routeList.item(x);
        int destinationPointCode = Integer.parseInt(routeItem.getAttribute("destinationPointCode"));
        int originatingPointCode = Integer.parseInt(routeItem.getAttribute("originatingPointCode"));
        int serviceIndicator = Integer.parseInt(routeItem.getAttribute("serviceIndicator"));
        logger.debug(String.format("[<M3UA Config>]: Reading Route configuration. DPC = %d, OPC = %d, SI = %d",
                destinationPointCode, originatingPointCode, serviceIndicator));
        m3ua.addApplicationServerRoute(aSName, destinationPointCode, originatingPointCode, serviceIndicator);
      }
    }
    logger.debug(String.format("Reading M3UA configuration for '%s' - completed.", m3uaName));
  }
}
