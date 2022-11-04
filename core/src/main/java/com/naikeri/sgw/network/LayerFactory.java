package com.naikeri.sgw.network;

import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.api.network.LayerType;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import com.naikeri.sgw.impl.settings.cap.CapSettings;
import com.naikeri.sgw.impl.settings.m3ua.M3uaSettings;
import com.naikeri.sgw.impl.settings.map.MapSettings;
import com.naikeri.sgw.impl.settings.sccp.SccpSettings;
import com.naikeri.sgw.impl.settings.sctp.SctpSettings;
import com.naikeri.sgw.impl.settings.tcap.TcapSettings;
import com.naikeri.sgw.network.layers.CapLayer;
import com.naikeri.sgw.network.layers.M3uaLayer;
import com.naikeri.sgw.network.layers.MapLayer;
import com.naikeri.sgw.network.layers.SccpLayer;
import com.naikeri.sgw.network.layers.SctpLayer;
import com.naikeri.sgw.network.layers.TcapLayer;
import org.apache.log4j.Logger;

public class LayerFactory {

  private static final Logger logger = Logger.getLogger(LayerFactory.class);

  public static LayerInterface createLayerInstance(LayerType layerType,
      LayerSettingsInterface layerSettings, LayerInterface transportLayerName) {
    LayerInterface layerInterface = null;

    try {
      switch (layerType) {
        case M3ua:
          layerInterface =
              new M3uaLayer((M3uaSettings) layerSettings, (SctpLayer) transportLayerName);
          break;
        case Sccp:
          layerInterface =
              new SccpLayer((SccpSettings) layerSettings, (M3uaLayer) transportLayerName);
          break;
        case Tcap:
          layerInterface =
              new TcapLayer((TcapSettings) layerSettings, (SccpLayer) transportLayerName);
          break;
        case Map:
          layerInterface =
              new MapLayer((MapSettings) layerSettings, (TcapLayer) transportLayerName);
          break;
        case Cap:
          layerInterface =
              new CapLayer((CapSettings) layerSettings, (TcapLayer) transportLayerName);
          break;
          default:
          break;
      }
    } catch (Exception e) {
      logger.error("Caught exception while initializing layer '" + layerSettings.getName() + "'",
          e);
    }

    return layerInterface;
  }

  public static LayerInterface createLayerInstance(LayerType layerType,
      LayerSettingsInterface layerSettings) {
    LayerInterface layerInterface = null;

    try {
      switch (layerType) {
        case Sctp:
          layerInterface = new SctpLayer((SctpSettings) layerSettings);
          break;
        default:
          break;
      }
    } catch (Exception e) {
      logger.error("Caught exception while initializing layer '" + layerSettings.getName() + "'",
          e);
    }

    return layerInterface;
  }
}
