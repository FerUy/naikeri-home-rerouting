package com.naikeri.sgw.impl.settings;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.naikeri.sgw.api.network.LayerType;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import com.naikeri.sgw.impl.settings.cap.XmlCapConfiguration;
import com.naikeri.sgw.impl.settings.cdr.CdrSettings;
import com.naikeri.sgw.impl.settings.m3ua.XmlM3uaConfiguration;
import com.naikeri.sgw.impl.settings.map.XmlMapConfiguration;
import com.naikeri.sgw.impl.settings.sccp.XmlSccpConfiguration;
import com.naikeri.sgw.impl.settings.sctp.XmlSctpConfiguration;
import com.naikeri.sgw.impl.settings.tcap.XmlTcapConfiguration;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlConfiguration {

  private List<ApplicationSettings> applicationList = new ArrayList<>();
  private List<ChannelSettings> channelList = new ArrayList<>();
  private List<LayerSettingsInterface> layerList = new ArrayList<>();
  private Logger logger = Logger.getLogger(XmlConfiguration.class);
  private List<MonitoringSettings> monitoringSettingsList = new ArrayList<>();
  private List<CdrSettings> cdrSettings = new ArrayList<>();
  private static final String ENABLED = "enabled";

  public XmlConfiguration(InputStream xmlInputStream) throws Exception {
    // Get document builder
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    // Build document
    Document document = builder.parse(xmlInputStream);
    // Normalize the XML Structure
    document.getDocumentElement().normalize();

    if (!document.getDocumentElement().getTagName().equalsIgnoreCase("SignalingGateway")) {
      logger.error("Invalid Xml configuration found. Exiting application....");
      System.exit(1);
    }
    // read the configured applications
    readApplicationConfiguration(document);
    // read the channel configuration
    readChannelConfiguration(document);
    // read the layers configuration
    readLayerConfiguration(document);
    // read the monitoring configuration
    readMonitoringConfiguration(document);
    // read the cdr configuration
    readCDRConfiguration(document);
  }

  private void readApplicationConfiguration(Document document){
    logger.debug("Reading Application configuration....");
    NodeList applicationNodeList =
        document.getElementsByTagName("Applications").item(0).getChildNodes();
    for (int temp = 0; temp < applicationNodeList.getLength(); temp++) {
      Node node = applicationNodeList.item(temp);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) node;

        boolean isApplicationEnabled =
            element.hasAttribute(ENABLED) && element.getAttribute(ENABLED).equalsIgnoreCase("true");

        if (isApplicationEnabled) {
          String applicationName = element.getAttribute("name");
          String workers = element.hasAttribute("workers") ? element.getAttribute("workers") : "10";
          String channelName = element.getAttribute("channel");
          String cdrName = element.getAttribute("cdr");
          // get the application rules filename
          String ruleFilename = getApplicationRuleFilename(element);
          ApplicationSettings applicationSettings =
              new ApplicationSettings(applicationName, Integer.parseInt(workers), channelName, cdrName);
          applicationSettings.setApplicationRuleFilename(ruleFilename);
          // add service function
          addServiceFunction(applicationSettings, element);
          applicationList.add(applicationSettings);
        }
      }
    }
  }
  private void readChannelConfiguration(Document document) {
    logger.debug("Reading Channels configuration");
    // read the configured channels
    NodeList channelNodeList = document.getElementsByTagName("Channels").item(0).getChildNodes();
    for (int temp = 0; temp < channelNodeList.getLength(); temp++) {
      Node node = channelNodeList.item(temp);
      if (node.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }
      Element element = (Element) node;
      boolean isChannelEnabled =
          element.hasAttribute(ENABLED) && element.getAttribute(ENABLED).equalsIgnoreCase("true");

      String channelName = element.hasAttribute("name") ? element.getAttribute("name")
          : UUID.randomUUID().toString();
      String channelHandler = element.hasAttribute("handler") ? element.getAttribute("handler")
          : channelName + "Channel";
      String channelQueue =
          element.hasAttribute("queue") ? element.getAttribute("queue") : channelName;
      String transportLayer = element.getAttribute("layer");
      ChannelSettings channelSetting =
          new ChannelSettings(channelName, channelHandler, transportLayer, channelQueue);
      // add the primitives
      for (String primitive : getPrimitives(element)) {
        channelSetting.addPrimitive(primitive);
      }
      if (isChannelEnabled) {
        channelList.add(channelSetting);
      }
    }
  }

  private void readLayerConfiguration(Document document) {
    logger.debug("Reading Layers configuration");
    // read layer configuration
    NodeList layerNodeList = document.getElementsByTagName("Layers").item(0).getChildNodes();
    for (int temp = 0; temp < layerNodeList.getLength(); temp++) {
      Node node = layerNodeList.item(temp);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        LayerSettingsInterface settings = null;

        if (node.getNodeName().equals("sctp")) {
          settings = new XmlSctpConfiguration((Element) node).getSettings();
        } else if (node.getNodeName().equals("m3ua")) {
          settings = new XmlM3uaConfiguration((Element) node).getSettings();
        } else if (node.getNodeName().equals("sccp")) {
          settings = new XmlSccpConfiguration((Element) node).getSettings();
        } else if (node.getNodeName().equals("tcap")) {
          settings = new XmlTcapConfiguration((Element) node).getSettings();
        } else if (node.getNodeName().equals("map")) {
          settings = new XmlMapConfiguration((Element) node).getSettings();
        } else if (node.getNodeName().equals("cap")) {
          settings = new XmlCapConfiguration((Element) node).getSettings();
        } else {
          logger.warn("Unknown configuration. Name = " + node.getNodeName());
        }

        if (settings != null) {
          layerList.add(settings);
        }
      }
    }
  }

  private void readMonitoringConfiguration(Document document) {
    logger.debug("Reading Monitoring configuration");
    NodeList monitoringNodeList = document.getElementsByTagName("Monitoring");
    if (monitoringNodeList == null || monitoringNodeList.getLength() <= 0) {
      return;
    }
    Element monitElement = (Element) monitoringNodeList.item(0);
    boolean isEnabled = false;
    if (monitElement.hasAttribute("enable")) {
      isEnabled = monitElement.getAttribute("enable").equalsIgnoreCase("true");
    }
    String folderPath = monitElement.getAttribute("folderPath");
    int refreshInterval = 5000;
    if (monitElement.hasAttribute("refreshInterval")) {
      refreshInterval = Integer.parseInt(monitElement.getAttribute("refreshInterval"));
    }
    if (isEnabled) {
      for (LayerSettingsInterface layer : layerList) {
        // get only m3ua
        if (layer.getType() == LayerType.M3ua && layer.isEnabled()) {
          MonitoringSettings monitorSetting = new MonitoringSettings();
          if (folderPath == null) {
            folderPath = System.getProperty("mainConfig.path");
          }
          String fileName =
              new File(folderPath, layer.getName() + "_monitor.log").getAbsolutePath();
          monitorSetting.setM3uaName(layer.getName());
          monitorSetting.setIsEnabled(isEnabled);
          monitorSetting.setFileName(fileName);
          monitorSetting.setRefreshInterval(refreshInterval);
          monitoringSettingsList.add(monitorSetting);
        }
      }
    }
  }

  private void readCDRConfiguration(Document document) {
    logger.debug("Reading CDR configuration");
    // read CDR configuration
    // check if CDR exist
    NodeList cdrNodes = document.getElementsByTagName("cdr");
    if (cdrNodes == null || cdrNodes.getLength() <= 0) {
      return;
    }
    NodeList cdrNodeList = document.getElementsByTagName("cdr").item(0).getChildNodes();
    for (int temp = 0; temp < cdrNodeList.getLength(); temp++) {
      Node node = cdrNodeList.item(temp);

      if (node.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }
      Element element = (Element) node;
      CdrSettings cdrSettingLocal = new CdrSettings();

      if (element.hasAttribute("name")) {
        cdrSettingLocal.setName(element.getAttribute("name"));
      }
      if (element.hasAttribute("appender")) {
        cdrSettingLocal.setLogger(element.getAttribute("appender"));
      }
      if (element.hasAttribute("logger")){
        cdrSettingLocal.setLogger(element.getAttribute("logger"));
      }
      if (element.hasAttribute("display-names")) {
        cdrSettingLocal
            .setDisplayName(element.getAttribute("display-names").equalsIgnoreCase("true"));
      }
      if (element.hasAttribute("fields")) {
        cdrSettingLocal.setFields(Arrays.asList(element.getAttribute("fields").split(",")));
      }
      if (element.hasAttribute("separator")) {
        cdrSettingLocal.setSeparator(element.getAttribute("separator"));
      }
      cdrSettings.add(cdrSettingLocal);
    }
  }

  private void addServiceFunction(ApplicationSettings applicationSetting, Element element) {
    try {
      NodeList serviceFunctionNodeList = element.getElementsByTagName("Function");
      for (int i = 0; i < serviceFunctionNodeList.getLength(); i++) {
        Node node = serviceFunctionNodeList.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element funcElement = (Element) node;
          String name = funcElement.getAttribute("name");
          String layerName = funcElement.getAttribute("layer");
          String serviceType =
              funcElement.getAttribute("serviceType").equalsIgnoreCase("scf") ? "scf" : "ssf";
          ServiceFunctionSetting srvFunction = new ServiceFunctionSetting();
          srvFunction.setLayerName(layerName);
          srvFunction.setName(name);
          srvFunction.setServiceType(serviceType);
          applicationSetting.addServiceFunction(srvFunction);
        }
      }
    } catch (Exception e) {
      logger.error("Error Reading Service Function. Error: ", e);
    }
  }

  private List<String> getPrimitives(Element element) {
    NodeList xmlPrimitives = element.getElementsByTagName("Primitive");
    List<String> retValue = new ArrayList<>();
    for (int i = 0; i < xmlPrimitives.getLength(); i++) {
      Node node = xmlPrimitives.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element primElement = (Element) node;
        retValue.add(primElement.getAttribute("name"));
      }
    }
    return retValue;
  }

  private String getApplicationRuleFilename(Element element) {
    String ruleFileName = "";
    try {
      NodeList xmlNodeList = element.getElementsByTagName("Rules");
      for (int i = 0; i < xmlNodeList.getLength(); i++) {
        Node node = xmlNodeList.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element ruleElement = (Element) node;
          ruleFileName = ruleElement.getAttribute("filename");
          break;
        }
      }
    } catch (Exception e) {
      logger.error("Failed to read Applications Rules for: " + e);
    }
    return ruleFileName;
  }

  public List<MonitoringSettings> getMonitoringSettings() {
    return this.monitoringSettingsList;
  }

  public List<ApplicationSettings> getApplicationList() {
    return this.applicationList;
  }

  public List<ChannelSettings> getChannelList() {
    return this.channelList;
  }

  public List<CdrSettings> getCdrSettings() {
    return cdrSettings;
  }

  public ChannelSettings getChannelSettings(String name) {
    for (ChannelSettings channelSettings : channelList)
      if (channelSettings.getName().equals(name))
        return channelSettings;
    return null;

  }

  public List<LayerSettingsInterface> getLayersByType(LayerType layerType) {
    List<LayerSettingsInterface> result = new ArrayList<>();

    for (LayerSettingsInterface layerSettings : layerList) {
      if (layerSettings.getType().equals(layerType))
        result.add(layerSettings);
    }

    return result;
  }


  public LayerSettingsInterface getLayerSettings(String name) {
    for (LayerSettingsInterface layerSettings : layerList)
      if (layerSettings.getName().equals(name))
        return layerSettings;
    return null;
  }
}
