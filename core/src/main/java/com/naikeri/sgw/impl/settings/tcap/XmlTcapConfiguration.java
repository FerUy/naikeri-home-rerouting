package com.naikeri.sgw.impl.settings.tcap;

import java.util.ArrayList;
import java.util.List;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

public class XmlTcapConfiguration {

  TcapSettings tcap;
  private static final Logger logger = Logger.getLogger(XmlTcapConfiguration.class);

  public LayerSettingsInterface getSettings() {
    return tcap;
  }

  public XmlTcapConfiguration(Element element) {
    boolean isEnabled  = true; // default value is enable
    if (element.hasAttribute("enable")) {
      isEnabled = element.getAttribute("enable").equalsIgnoreCase("true");
    }

    String tcapName = element.getAttribute("name");
    if (tcapName == null || tcapName.isEmpty()) {
      logger.warn("The SCCP name is required");
    }
    String sccpName = element.getAttribute("sccp");
    if (sccpName == null || sccpName.isEmpty()) {
      logger.warn("The SCCP layer for the TCAP cannot be found in the configuration file");
    }
    logger.debug(String.format("Reading TCAP configuration started. TCAP Name = %s, SCCP = %s", tcapName, sccpName));

    Integer ssn = null;
    if (element.hasAttribute("ssn")){
      ssn = Integer.parseInt(element.getAttribute("ssn"));
    }

    List<Integer> extraSsns = new ArrayList<Integer>();
    if (element.hasAttribute("extraSsn")){
      for(String extraSsn: element.getAttribute("extraSsn").split(",",0)){
        try {
          extraSsns.add(Integer.parseInt(extraSsn));
        } catch (Exception e) {
          logger.error("Unable to convert extra ssn value ('"+ssn+"') to integer. error " + e);
        }
      }
    }
    int dialogidletimeout = Integer.parseInt(element.getAttribute("dialogidletimeout"));
    int invoketimeout = Integer.parseInt(element.getAttribute("invoketimeout"));
    int maxdialogs = Integer.parseInt(element.getAttribute("maxdialogs"));
    tcap = new TcapSettings(tcapName, sccpName, ssn, isEnabled);
    for(Integer extraSsn: extraSsns){
      tcap.addExtraSubSystemNumber(extraSsn);
    }
    tcap.setDialogIdleTimeout(dialogidletimeout);
    tcap.setInvokeTimeOut(invoketimeout);
    tcap.setMaxDialogs(maxdialogs);
    logger.debug(String.format("Reading TCAP configuration for '%s' completed.", tcapName));
  }
}
