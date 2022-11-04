package com.naikeri.sgw.network.layers;

import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import com.naikeri.sgw.impl.chn.ChannelHandler;
import com.naikeri.sgw.impl.settings.sccp.*;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.sccp.SccpProvider;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;
import org.restcomm.protocols.ss7.sccpext.impl.SccpExtModuleImpl;
import org.restcomm.protocols.ss7.ss7ext.Ss7ExtInterface;
import org.restcomm.protocols.ss7.ss7ext.Ss7ExtInterfaceImpl;

public class SccpLayer implements LayerInterface {

  private SccpStackImpl sccp;
  private static final Logger logger = Logger.getLogger(SccpLayer.class);
  SccpExtModuleImpl sccpExtModule;
  private LayerSettingsInterface layerSetting;

  public SccpProvider getSccpProvider() {
    return sccp.getSccpProvider();
  }

  public SccpLayer(SccpSettings sccpSettings, M3uaLayer m3ua) throws Exception {
    logger.info(sccpSettings.toString());
    layerSetting = sccpSettings;
    Ss7ExtInterface ss7ExtInterface = new Ss7ExtInterfaceImpl();
    sccpExtModule = new SccpExtModuleImpl();
    ss7ExtInterface.setSs7ExtSccpInterface(sccpExtModule);

    sccp = new SccpStackImpl(sccpSettings.getName(), ss7ExtInterface);
    // this.sccp.setPersistDir(persistDir)
    // SAP ID can't be 0
    sccp.setMtp3UserPart(sccpSettings.getId(), m3ua.getMtp3UserPart());

    sccp.start();
    sccp.removeAllResources();

    // sccp.setSccpProtocolVersion(sccpProtocolVersion)
    // add the service access Point
    sccpSettings.getServiceAccessPoint().forEach(sap -> {
      try {
        logger.info(sap.toString());
        sccp.getRouter().addMtp3ServiceAccessPoint(sap.getID(), sap.getMtp3Id(), sap.getOPC(),
            sap.getNetworkIndicator(), sap.getNetworkId(), sap.getLocalGtDigits());
        // add the Mtp3Destinations
        for (SccpMtp3Destination mtp3 : sap.getMtp3Destination()) {
          logger.debug("Adding MTP3 Destination. SAP ID = " + mtp3.getSapId() + ", Dest ID = "
              + mtp3.getDestinationId());
          sccp.getRouter().addMtp3Destination(mtp3.getSapId(), mtp3.getDestinationId(),
              mtp3.getFirstDpc(), mtp3.getLastDpc(), mtp3.getFirstSls(), mtp3.getLastSls(),
              mtp3.getSlsMask());
        }
      } catch (Exception ex) {
        logger.error(ex.getMessage());
        throw new RuntimeException(ex);
      }
    });

    // add long message rule
    for (SccpLongMessageRule slmr : sccpSettings.getLongMessageRules()) {
      logger.debug("Adding Long Message Rule" + slmr.toString());
      sccp.getRouter().addLongMessageRule(slmr.getId(), slmr.getFirstSpc(), slmr.getLastSpc(),
          slmr.getRuleType());
    }
    // add the remote spc
    sccpSettings.getRemoteSpc().forEach(spc -> {
      try {
        logger.debug(spc.toString());
        sccp.getSccpResource().addRemoteSpc(spc.getRemoteSpcId(), spc.getRemoteSpc(),
            spc.getRemoteSpcFlag(), spc.getMask());
      } catch (Exception ex) {
        logger.error(ex.getMessage());
        throw new RuntimeException(ex);
      }
    });

    // add the Sub System Number
    sccpSettings.getRemoteSsn().forEach(ssn -> {
      try {
        logger.debug(ssn.toString());
        sccp.getSccpResource().addRemoteSsn(ssn.getRemoteSsnId(), ssn.getRemoteSpc(),
            ssn.getRemoteSsn(), ssn.getRemoteSsnFlag(), ssn.getProhibitWhenSpcResuming());
      } catch (Exception ex) {
        logger.error(ex.getMessage());
        throw new RuntimeException(ex);
      }
    });

    // add the routing rules
    for (SccpRoutingAddress routeAddr : sccpSettings.getRoutingAddresses()) {
      logger.debug(routeAddr.toString());
      sccpExtModule.getRouterExt().addRoutingAddress(routeAddr.getId(), routeAddr.getSccpAddress());
    }

    // add the rules
    for (SccpRules rules : sccpSettings.getRules()) {
      logger.debug("Adding SCCP Rules:" + rules.toString());
      sccpExtModule.getRouterExt().addRule(rules.getID(), rules.getRuleType(),
          rules.getLoadSharingAlgorithm(), rules.getOriginationType(),
          rules.getSccpPatternAddress(), rules.getMask(), rules.getPAddressId(),
          rules.getSAddressId(), rules.getCallingPartyAddressAddressId(), rules.getNetworkId(),
          rules.getPatternCallingAddress());
    }
  }

  @Override
  public void setChannelHandler(ChannelHandler channelHandler) {
    //
  }

  @Override
  public LayerSettingsInterface getSetting() {
    return this.layerSetting;
  }

  @Override
  public String getName() {
    return sccp.getName();
  }

  @Override
  public void stop() {
    try {
      this.sccp.removeAllResources();
      this.sccp.stop();
      logger.debug("SCCP Layer '" + getName() + "' has been stopped");
    } catch (Exception e) {
      logger.error("Exception when stopping SccpLayer ('" + getName() + "'). " + e);
    }
  }

}
