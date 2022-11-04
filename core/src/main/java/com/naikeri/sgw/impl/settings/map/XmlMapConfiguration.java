package com.naikeri.sgw.impl.settings.map;

import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

public class XmlMapConfiguration {

  MapSettings map;
  private static final Logger logger = Logger.getLogger(XmlMapConfiguration.class);

  public LayerSettingsInterface getSettings() {
    return map;
  }

  public XmlMapConfiguration(Element element) {
    boolean isEnabled = true; // default value
    if (element.hasAttribute("enable")) {
      isEnabled = element.getAttribute("enable").equalsIgnoreCase("true");
    }
    String mapName = element.getAttribute("name");
    if (mapName == null || mapName.isEmpty()) {
      logger.warn("The MAP name not found");
    }
    String tcapName = element.getAttribute("tcap");
    if (tcapName == null || tcapName.isEmpty()) {
      logger.warn("The TCAP name is not found");
    }
    logger.debug(String.format("Reading MAP Configuration for '%s', TCAP = '%s' - started", mapName, tcapName));
    map = new MapSettings(mapName, tcapName, isEnabled);
    logger.debug("Reading MAP Configuration - completed");
  }
}
