package com.naikeri.sgw.network.layers;

import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import com.naikeri.sgw.impl.chn.ChannelHandler;
import com.naikeri.sgw.impl.settings.sctp.*;
import org.apache.log4j.Logger;
import org.mobicents.protocols.sctp.netty.NettySctpManagementImpl;

public class SctpLayer implements LayerInterface {

  private SctpSettings sctpSettings;
  private NettySctpManagementImpl sctp;
  private static final Logger logger = Logger.getLogger(SctpLayer.class);
  private LayerSettingsInterface layerSetting;
  public NettySctpManagementImpl getSctpManagement() {
    return sctp;
  }

  @Override
  public LayerSettingsInterface getSetting() {
    return this.layerSetting;
  }
  public SctpLayer(SctpSettings sctpSettings) throws Exception {
    this.sctpSettings = sctpSettings;
    layerSetting = sctpSettings;
    logger.info(sctpSettings.toString());
    // configuration startup
    sctp = new NettySctpManagementImpl(this.sctpSettings.getName());
    sctp.setSingleThread(sctpSettings.isSingleThread());
    sctp.start();

    sctp.setConnectDelay(sctpSettings.getConnectDelay());
    sctp.removeAllResources();
 
    // servers and server associations
    for (SctpServerSettings sctpServerSettings : sctpSettings.getServers()) {
      logger.debug(sctpServerSettings.toString());
      sctp.addServer(sctpServerSettings.getName(), sctpServerSettings.getHostAddress(),
          sctpServerSettings.getHostPort(), sctpServerSettings.getIpChannelType(),
          sctpServerSettings.acceptsAnonymousConnections(),
          sctpServerSettings.getMaxConcurrentConnections(), sctpServerSettings.getMultHoming());

      for (String associationName : sctpServerSettings.getAssociationNames()) {
        AssociationSettings associationSettings =
            sctpServerSettings.getAssociationSettings(associationName);
        logger.debug(associationSettings.toString());
        sctp.addServerAssociation(associationSettings.getPeerAddress(),
            associationSettings.getPeerPort(), sctpServerSettings.getName(),
            associationSettings.getName(), associationSettings.getIpChannelType());
      }
      sctp.startServer(sctpServerSettings.getName());
    }

    // client associations
    for (String associationName : sctpSettings.getAssociationNames()) {
      AssociationSettings associationSettings =
          sctpSettings.getAssociationSettings(associationName);
      logger.debug(associationSettings.toString());
      sctp.addAssociation(associationSettings.getHostAddress(), associationSettings.getHostPort(),
          associationSettings.getPeerAddress(), associationSettings.getPeerPort(),
          associationSettings.getName(), associationSettings.getIpChannelType(),
          associationSettings.getMultHoming());
    }
  }

  public void update(SctpSettings sctpSettings) {

  }

  public void start() {

  }

  @Override
  public void stop() {
    try {
      this.sctp.stop();
      logger.info("SCTP Layer '" + getName() + "' has been stopped");
    } catch (Exception e) {
      logger.error("Exception when stopping SctpLayer ('" + getName() + "'). " + e);
    }
  }

  @Override
  public void setChannelHandler(ChannelHandler channelHandler) {

  }

  @Override
  public String getName() {
    return sctp.getName();
  }
}
