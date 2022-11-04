package com.naikeri.sgw.impl.settings.cap;

import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

public class XmlCapConfiguration {

    CapSettings cap;
    private static final Logger logger = Logger.getLogger(XmlCapConfiguration.class);

    public LayerSettingsInterface getSettings() {
        return cap;
    }

    public XmlCapConfiguration(Element element) {
        boolean isEnabled = true; // default value
        if (element.hasAttribute("enable")) {
            isEnabled = element.getAttribute("enable").equalsIgnoreCase("true");
        }
        String capName = element.getAttribute("name");
        if (capName == null || capName.isEmpty()) {
            logger.warn("The MAP name not found");
        }
        String tcapName = element.getAttribute("tcap");
        if (tcapName == null || tcapName.isEmpty()) {
            logger.warn("The TCAP name not found");
        }
        logger.debug(String.format("Reading CAP Configuration for '%s', TCAP = '%s' - started",
                capName, tcapName));
        cap = new CapSettings(capName, tcapName, isEnabled);
        logger.debug("Reading CAP Configuration - completed");
    }
}
