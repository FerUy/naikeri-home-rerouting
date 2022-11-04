package com.naikeri.sgw.network.layers;

import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import com.naikeri.sgw.impl.chn.ChannelHandler;
import com.naikeri.sgw.impl.settings.cap.CapSettings;
import com.naikeri.sgw.network.layers.listeners.cap.CapCircuitSwitchedCallListener;
import com.naikeri.sgw.network.layers.listeners.cap.CapDialogListener;
import com.naikeri.sgw.network.layers.listeners.cap.CapGprsListener;
import com.naikeri.sgw.network.layers.listeners.cap.CapSmsListener;
import org.restcomm.protocols.ss7.cap.CAPStackImpl;
import org.restcomm.protocols.ss7.cap.api.CAPProvider;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPStack;

public class CapLayer implements LayerInterface {

  private static final Logger logger = Logger.getLogger(CapLayer.class);

  private CAPStack cap;
  private CAPProvider capProvider;
  private ChannelHandler channelHandler;
  private String layerName;
  private LayerSettingsInterface layerSetting;

  public CAPProvider getCapProvider() {
    return cap.getCAPProvider();
  }

  public CAPStack getCapStack() {
    return cap;
  }

  public CapLayer(CapSettings capSettings, TcapLayer tcap) throws Exception {
    this.layerName = capSettings.getName();
    this.layerSetting = capSettings;
    logger.info(capSettings.toString());
    cap = new CAPStackImpl(capSettings.getName(), tcap.getTcapProvider());
    cap.start();
  }

  Boolean isPrimitiveEnabled() {
    return true;
  }

  public void setChannelHandler(ChannelHandler channelHandler) {
    logger.info("Starting the CAP Listeners");
    this.channelHandler = channelHandler;

    capProvider = cap.getCAPProvider();
    capProvider.addCAPDialogListener(new CapDialogListener(channelHandler, this.layerName));

    capProvider.getCAPServiceCircuitSwitchedCall()
        .addCAPServiceListener(new CapCircuitSwitchedCallListener(channelHandler, this.layerName));
    capProvider.getCAPServiceCircuitSwitchedCall().activate();

    capProvider.getCAPServiceGprs()
        .addCAPServiceListener(new CapGprsListener(channelHandler, this.layerName));
    capProvider.getCAPServiceGprs().activate();

    capProvider.getCAPServiceSms()
        .addCAPServiceListener(new CapSmsListener(channelHandler, this.layerName));
    capProvider.getCAPServiceSms().activate();
  }

  @Override
  public LayerSettingsInterface getSetting() {
    return this.layerSetting;
  }

  @Override
  public String getName() {
    return cap.getName();
  }

  @Override
  public void stop() {
    try {
      this.cap.stop();
      // remove the listeners
      capProvider.getCAPServiceCircuitSwitchedCall().deactivate();
      capProvider.getCAPServiceCircuitSwitchedCall().removeCAPServiceListener(
          new CapCircuitSwitchedCallListener(channelHandler, this.layerName));

      capProvider.getCAPServiceGprs().deactivate();
      capProvider.getCAPServiceGprs()
          .removeCAPServiceListener(new CapGprsListener(channelHandler, this.layerName));

      capProvider.getCAPServiceSms().deactivate();
      capProvider.getCAPServiceSms()
          .removeCAPServiceListener(new CapSmsListener(channelHandler, this.layerName));

      logger.info("TCAP + CAP has been stopped");
    } catch (Exception e) {
      logger.error("Exception when stopping CapLayer" + e);
    }
  }
}
