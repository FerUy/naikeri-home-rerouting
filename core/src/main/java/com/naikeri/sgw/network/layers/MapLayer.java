package com.naikeri.sgw.network.layers;

import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import com.naikeri.sgw.impl.chn.ChannelHandler;
import com.naikeri.sgw.impl.settings.map.MapSettings;
import com.naikeri.sgw.network.layers.listeners.map.MapCallHandlingListener;
import com.naikeri.sgw.network.layers.listeners.map.MapDialogListener;
import com.naikeri.sgw.network.layers.listeners.map.MapOamListener;
import com.naikeri.sgw.network.layers.listeners.map.MapServiceLsmListener;
import com.naikeri.sgw.network.layers.listeners.map.MapServiceMobilityListener;
import com.naikeri.sgw.network.layers.listeners.map.MapSmsListener;
import com.naikeri.sgw.network.layers.listeners.map.MapSupplementaryServiceListener;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.MAPStackImpl;
import org.restcomm.protocols.ss7.map.api.MAPProvider;

public class MapLayer implements LayerInterface {

  private static final Logger logger = Logger.getLogger(MapLayer.class);
  private MAPStackImpl map;
  private MAPProvider provider;
  private ChannelHandler channelHandler;
  private LayerSettingsInterface layerSetting;
  public MAPProvider getMapProvider() {
    return map.getMAPProvider();
  }

  public MAPStackImpl getMapStack() {
    return this.map;
  }

  public MapLayer(MapSettings mapSettings, TcapLayer tcap) throws Exception {
    logger.info(mapSettings.toString());
    layerSetting = mapSettings;
    map = new MAPStackImpl(mapSettings.getName(), tcap.getTcapProvider());
    
    map.start();
  }

  @Override
  public LayerSettingsInterface getSetting() {
    return this.layerSetting;
  }
  @Override
  public void setChannelHandler(ChannelHandler channelHandler) {
    logger.debug("Starting the Map Listeners");
    this.channelHandler = channelHandler;
    provider = map.getMAPProvider();
    provider.addMAPDialogListener(new MapDialogListener(channelHandler));

    provider.getMAPServiceMobility().addMAPServiceListener(new MapServiceMobilityListener(channelHandler));
    provider.getMAPServiceMobility().activate();

    provider.getMAPServiceLsm().addMAPServiceListener(new MapServiceLsmListener(channelHandler));
    provider.getMAPServiceLsm().activate();

    provider.getMAPServiceCallHandling().addMAPServiceListener(new MapCallHandlingListener(channelHandler));
    provider.getMAPServiceCallHandling().activate();

    provider.getMAPServiceSms().addMAPServiceListener(new MapSmsListener(channelHandler));
    provider.getMAPServiceSms().activate();
    provider.getMAPServiceOam().addMAPServiceListener(new MapOamListener(channelHandler));
    provider.getMAPServiceOam().activate();

    provider.getMAPServiceSupplementary()
    .addMAPServiceListener(new MapSupplementaryServiceListener(channelHandler));
    provider.getMAPServiceSupplementary().activate();

  }

  @Override
  public String getName() {
    return map.getName();
  }

  public void stop() {
    try {
      logger.debug("Stopping TCAP+MAP Layer");
      this.map.stop();
      // remove the listeners
      
      provider.getMAPServiceMobility().deactivate();
      provider.getMAPServiceMobility().removeMAPServiceListener(new MapServiceMobilityListener(channelHandler));

      provider.getMAPServiceLsm().deactivate();
      provider.getMAPServiceLsm().removeMAPServiceListener(new MapServiceLsmListener(channelHandler));

      provider.getMAPServiceCallHandling().deactivate();
      provider.getMAPServiceCallHandling().removeMAPServiceListener(new MapCallHandlingListener(channelHandler));

      provider.getMAPServiceSms().deactivate();
      provider.getMAPServiceSms().removeMAPServiceListener(new MapSmsListener(channelHandler));

      provider.getMAPServiceOam().deactivate();
      provider.getMAPServiceOam().removeMAPServiceListener(new MapOamListener(channelHandler));

      provider.getMAPServiceSupplementary().deactivate();
      provider.getMAPServiceSupplementary().removeMAPServiceListener(new MapSupplementaryServiceListener(channelHandler));
      
      this.provider.removeMAPDialogListener(new MapDialogListener(channelHandler));

      logger.debug("TCAP+MAP has been stopped");
    } catch (Exception e) {
      logger.error("Exception when stopping MapLayer" + e);
    }
  }
}
