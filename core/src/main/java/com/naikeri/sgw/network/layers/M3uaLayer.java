package com.naikeri.sgw.network.layers;

import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import com.naikeri.sgw.impl.chn.ChannelHandler;
import com.naikeri.sgw.impl.settings.m3ua.*;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.mtp.Mtp3UserPart;
import org.restcomm.protocols.ss7.ss7ext.Ss7ExtInterfaceImpl;

public class M3uaLayer implements LayerInterface {

  private M3UAManagementImpl m3ua;
  private SctpLayer sctp;
  private LayerSettingsInterface layerSetting;
  private static final Logger logger = Logger.getLogger(M3uaLayer.class);

  public Mtp3UserPart getMtp3UserPart() {
    return m3ua;
  }

  public M3UAManagementImpl getM3uaManagement() {
    return this.m3ua;
  }


  public SctpLayer getSctpLayer(){
    return this.sctp;
  }
  public M3uaLayer(M3uaSettings m3uaSettings, SctpLayer sctp) throws Exception {
    logger.debug(m3uaSettings.toString());
    this.layerSetting = m3uaSettings;
    this.sctp = sctp;
    // configuration startup
    m3ua = new M3UAManagementImpl(m3uaSettings.getName(), m3uaSettings.getProductName(),
        new Ss7ExtInterfaceImpl());
    m3ua.setTransportManagement(sctp.getSctpManagement());
    m3ua.start();
    m3ua.setHeartbeatTime(m3uaSettings.getHeartbeatTime());
    m3ua.removeAllResources();

    // application servers
    for (String applicationServerName : m3uaSettings.getApplicationServerNames()) {
      M3uaApplicationServerSettings applicationServerSettings =
          m3uaSettings.getApplicationServerSettings(applicationServerName);
      logger.debug(applicationServerSettings.toString());
      m3ua.createAs(applicationServerSettings.getName(),
          applicationServerSettings.getFunctionality(), applicationServerSettings.getExchangeType(),
          applicationServerSettings.getIpspType(), applicationServerSettings.getRoutingContext(),
          applicationServerSettings.getTrafficModeType(),
          applicationServerSettings.getMinAspActiveForLoadBalancing(),
          applicationServerSettings.getNetworkAppearance());

      for (String m3uaApplicationServerProcessName : applicationServerSettings.getProcesess()) {
        M3uaApplicationServerProcessSettings m3uaApplicationServerProcessSettings =
            applicationServerSettings.getApplicationServerProcess(m3uaApplicationServerProcessName);
        logger.debug(m3uaApplicationServerProcessSettings.toString());

        m3ua.createAspFactory(m3uaApplicationServerProcessSettings.getName(),
            m3uaApplicationServerProcessSettings.getSctpAssociationName(),
            m3uaApplicationServerProcessSettings.isHeartbeatEnabled());

        m3ua.assignAspToAs(applicationServerSettings.getName(),
            m3uaApplicationServerProcessSettings.getName());
        logger.debug(String.format("assignAspToAs: %s - %s", applicationServerSettings.getName(),
            m3uaApplicationServerProcessSettings.getName()));
        m3ua.startAsp(m3uaApplicationServerProcessSettings.getName());
      }

      logger.debug("Adding Routes");
      m3ua.addRoute(applicationServerSettings.getDestinationPointCode(),
          applicationServerSettings.getOriginPointCode(),
          applicationServerSettings.getServiceIndicator(), applicationServerSettings.getName());
    }

  }

  @Override
  public void setChannelHandler(ChannelHandler channelHandler) {
    //
  }

  @Override
  public String getName() {
    return m3ua.getName();
  }

  @Override
  public void stop() {
    try {
      this.m3ua.stop();
      logger.info("M3UA has been stopped");
    } catch (Exception e) {
      logger.error("Exception when stopping M3uaLayer ('" + getName() + "'). " + e);
    }

  }

  @Override
  public LayerSettingsInterface getSetting() {
    return this.layerSetting;
  }
}
