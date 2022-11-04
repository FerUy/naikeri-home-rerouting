package com.naikeri.sgw.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.LogManager;
import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.api.network.LayerType;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import com.naikeri.sgw.helpers.SignalingResource;
import com.naikeri.sgw.impl.app.Application;
import com.naikeri.sgw.impl.cdr.CdrImpl;
import com.naikeri.sgw.impl.chn.ChannelHandler;
import com.naikeri.sgw.impl.settings.ApplicationSettings;
import com.naikeri.sgw.impl.settings.ChannelSettings;
import com.naikeri.sgw.impl.settings.MonitoringSettings;
import com.naikeri.sgw.impl.settings.XmlConfiguration;
import com.naikeri.sgw.network.LayerFactory;
import com.naikeri.sgw.network.layers.M3uaLayer;
import com.naikeri.sgw.network.monitoring.MonitorLayers;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class SignalingGateway extends Thread {

  private static final Logger logger = Logger.getLogger(SignalingGateway.class);
  private final AtomicBoolean running = new AtomicBoolean(false);
  private static SignalingGateway instance = null;

  private static SignalingGateway getInstance() {
    if (instance == null) {
      instance = new SignalingGateway();
    }
    return instance;
  }

  private final Map<String, Application> applications = new HashMap<String, Application>();
  private final Map<String, ChannelHandler> channels = new HashMap<String, ChannelHandler>();
  private final Map<String, LayerInterface> layers = new HashMap<String, LayerInterface>();
  private final Map<String, MonitorLayers> monitorLayers = new HashMap<String, MonitorLayers>();

  public static SignalingGateway initialize(final String[] args) {
    logger.info("Instance is initializing... " + System.getProperty("mainConfig.path"));
    SignalingGateway.getInstance();
    final InputStream log4jConfiguration = new SignalingResource("log4j.xml").getAsStream();
    LogManager.getLogManager().reset();
    PropertyConfigurator.configure(log4jConfiguration);
    return instance;
  }

  private void setUpLayers(final XmlConfiguration xmlConfiguration) {
    // set up the layers
    for (final LayerType layerType : LayerType.values()) {
      logger.info("Initializing '" + layerType.toString().toUpperCase() + "' layers...");
      final List<LayerSettingsInterface> layerSettingsList = xmlConfiguration.getLayersByType(layerType);
      for (final LayerSettingsInterface layerSettings : layerSettingsList) {
        final String transportLayerName = layerSettings.getTransportName();
        if (!layerSettings.isEnabled())
          continue; // skip the initialization
        LayerInterface layerInterface = null;
        if (transportLayerName == null) {
          logger.info("Layer: " + layerSettings.getName() + ", type '" + layerSettings.getType()
              + "' is initializing....");
          layerInterface = LayerFactory.createLayerInstance(layerType, layerSettings);
        } else {
          final LayerInterface layerTransportInterface = layers.get(transportLayerName);
          if (layerTransportInterface == null) {
            logger.error("Layer '" + layerSettings.getName()
                + "', initialization failure! Underlying transport '" + transportLayerName
                + "' not initialized.");
          } else {
            logger.info("Layer '" + layerSettings.getName() + "', type '" + layerSettings.getType()
                + "', transport '" + layerTransportInterface.getName() + "' is initializing...");
            layerInterface =
                LayerFactory.createLayerInstance(layerType, layerSettings, layerTransportInterface);
          }
        }
        if (layerInterface != null)
          layers.put(layerSettings.getName(), layerInterface);
      }
    }
  }

  private void setUpChannels(final XmlConfiguration xmlConfiguration) {
    for (final ChannelSettings channelSettings : xmlConfiguration.getChannelList()) {
      try {
        if (channels.containsKey(channelSettings.getName())) {
          logger.warn("Channel `" + channelSettings.getName() + "` already instantiated!");
          continue;
        }
        logger.info("Channel '" + channelSettings.getName() + "' is initializing...");
        final String channelClassName = "com.naikeri.sgw.impl.chn." + channelSettings.getHandler();
        final ChannelHandler channelHandler = (ChannelHandler) Class.forName(channelClassName)
            .getDeclaredConstructor(ChannelSettings.class).newInstance(channelSettings);
        channels.put(channelSettings.getName(), channelHandler);
        final String[] channelLayers = channelSettings.getLayerName().replaceAll("\\s+", "").split(",");
        // add the previous layer stack and set up the handlers (e.g. TCAP...)
        final LayerInterface[] layerServices = new LayerInterface[channelLayers.length];
        for (int i = 0; i < layerServices.length; i++) {
          final LayerInterface layer = layers.get(channelLayers[i]);
          if (layer != null) {
            layer.setChannelHandler(channelHandler);
            layerServices[i] = layer;
            // set up the tcap handlers
            String transportName = layer.getSetting().getTransportName();
            if (transportName != null && !transportName.isEmpty()){
              LayerInterface transLayer = layers.get(transportName);
              if (transLayer != null){
                transLayer.setChannelHandler(channelHandler);
              }
            }
          }
        }
        if (layerServices.length > 0) {
          channelHandler.channelInitialize(layerServices);
        } else {
          logger.error("Channel Layer is not initialized");
        }
      } catch (final Exception e) {
        logger.error("Channel '" + channelSettings.getName() + "' not started. Exception caught:",
            e);
      }
    }
  }

  private void setUpApplications(final XmlConfiguration xmlConfiguration) {
    for (final ApplicationSettings applicationSettings : xmlConfiguration.getApplicationList()) {
      try {
        if (applications.containsKey(applicationSettings.getName())) {
          logger.warn("Application <" + applicationSettings.getName() + "> already instantiated!");
        } else {
          logger.info("Application <" + applicationSettings.getName() + "> is initializing...");
          final String applicationClassName = "com.naikeri.sgw.impl.app." + applicationSettings.getName();
          final Application application = (Application) Class.forName(applicationClassName)
              .getDeclaredConstructor(ApplicationSettings.class).newInstance(applicationSettings);
          application.setChannelHandler(channels.get(applicationSettings.getChannelName()));
          logger.info("Application '" + applicationSettings.getName() + "' is starting...");
          applications.put(applicationSettings.getName(), application);
          application.start();
        }
      } catch (final Exception e) {
        logger.error("Application '" + applicationSettings.getName()
            + "' not started. Exception caught '" + e + "'");
      }
    }
  }

  private void setUpMonitoring(final XmlConfiguration xmlConfiguration) {
    // setup the monitoring
    for (final MonitoringSettings monitorSetting : xmlConfiguration.getMonitoringSettings()) {
      try {
        if (layers.get(monitorSetting.getM3uaName()) != null) {
          final M3uaLayer m3uaLayer = (M3uaLayer) layers.get(monitorSetting.getM3uaName());
          final MonitorLayers monitor =
              new MonitorLayers(m3uaLayer, m3uaLayer.getSctpLayer(), monitorSetting);
          monitor.start();
          monitorLayers.put(monitorSetting.getM3uaName(), monitor);
        }
      } catch (final Exception e) {
        logger.error("Monitoring '" + monitorSetting.getM3uaName() + "' not started. Error: ", e);
      }
    }
  }

  private void setUpCDRConfiguration(final XmlConfiguration xmlConfiguration) {
    final CdrImpl cdrImpl = CdrImpl.getInstance();
    cdrImpl.setCdrSettings(xmlConfiguration.getCdrSettings());
  }

  @Override
  public void run() {
    // read configuration file for app and chn
    try {
      final InputStream inputStream =
          new SignalingResource("naikeri-signaling-gateway.xml").getAsStream();
      final XmlConfiguration xmlConfiguration = new XmlConfiguration(inputStream);
      setUpLayers(xmlConfiguration);
      // setup the channels
      setUpChannels(xmlConfiguration);
      // setup the applications
      setUpApplications(xmlConfiguration);
      // set up the monitoring
      setUpMonitoring(xmlConfiguration);
      // Setup the CDR Settings
      setUpCDRConfiguration(xmlConfiguration);
    } catch (

    final Exception e) {
      logger.error("Failed to start application instance! Exiting the application. Error: ", e);
      System.exit(1);
    }

    running.set(true);
    // TO DO: this code will raise notification status to HomeReRouting instance
    while (running.get()) {
      try {
        Thread.sleep(1000L);
      } catch (final InterruptedException e) {
        logger.warn("Interrupted! ", e);
        Thread.currentThread().interrupt();
      } 
    }

    logger.info("Instance shutdown.");
    // stop the monitoring
    stopMonitoringLayers();
    // shutting down all the layers instances
    shutDownAllLayers();
  }

  private void stopMonitoringLayers() {
    String mLayerName = null;
    try {
      for (final Map.Entry<String, MonitorLayers> monLayers : this.monitorLayers.entrySet()) {
        mLayerName = monLayers.getKey();
        monLayers.getValue().stop();
      }
    } catch (final Exception e) {
      logger.error("Failed to monitoring for '" + mLayerName + "'");
    }
  }

  private void shutDownAllLayers() {
    LayerInterface layerToStop = null;
    try {
      for (final Map.Entry<String, LayerInterface> layer : layers.entrySet()) {
        layerToStop = layer.getValue();
        logger.info(String.format("Stopping %s Layer, name = %s", layer.getValue().getName(),
            layer.getKey()));
        layer.getValue().stop();
      }
    } catch (final Exception e) {
      if (layerToStop != null) {
        logger.error("Failed to stop '" + layerToStop.getName() + "' Layer " + e);
      } else {
        logger.error(e);
      }
    }
  }

  public static void haltSignalingGateway() {
    logger.info("Shutting down Naikeri Signaling Gateway");
    SignalingGateway.getInstance().running.set(false);
  }
}
