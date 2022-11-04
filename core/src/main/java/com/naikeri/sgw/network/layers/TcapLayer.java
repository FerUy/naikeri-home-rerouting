package com.naikeri.sgw.network.layers;

import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import com.naikeri.sgw.impl.chn.ChannelHandler;
import com.naikeri.sgw.impl.settings.tcap.TcapSettings;
import com.naikeri.sgw.network.layers.listeners.tcap.TcapTcListener;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.tcap.TCAPStackImpl;
import org.restcomm.protocols.ss7.tcap.api.TCAPProvider;

public class TcapLayer implements LayerInterface {

  private TCAPStackImpl tcap;
  private static final Logger logger = Logger.getLogger(TcapLayer.class);
  private ChannelHandler channelHandler;
  private LayerSettingsInterface layerSettings;
  public TCAPProvider getTcapProvider() {
    return tcap.getProvider();
  }

  public TcapLayer(TcapSettings tcapSettings, SccpLayer sccp) throws Exception {
    logger.info(tcapSettings.toString());
    layerSettings = tcapSettings;
    tcap = new TCAPStackImpl(tcapSettings.getName(), sccp.getSccpProvider(), tcapSettings.getSubSystemNumber());
    if (!tcapSettings.getExtraSubsystemNumbers().isEmpty()) {
        tcap.setExtraSsns(tcapSettings.getExtraSubsystemNumbers());
    }
    tcap.start();
    tcap.setDialogIdleTimeout(tcapSettings.getDialogIdleTimeout());
    tcap.setInvokeTimeout(tcapSettings.getInvokeTimeOut());
    tcap.setMaxDialogs(tcapSettings.getMaxDialogs());
  }

  @Override
  public void setChannelHandler(ChannelHandler channelHandler) {
    this.channelHandler = channelHandler;
    tcap.getProvider().addTCListener(new TcapTcListener(channelHandler));
  }

  @Override
  public String getName() {
    return tcap.getName();
  }

  @Override
  public LayerSettingsInterface getSetting() {
    return this.layerSettings;
  }
  public void stop() {
    this.tcap.stop();
    this.tcap.getProvider().removeTCListener(new TcapTcListener(this.channelHandler));
  }

}
