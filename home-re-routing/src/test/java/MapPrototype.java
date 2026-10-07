import java.math.BigInteger;
import java.util.ArrayList;

import com.google.common.util.concurrent.RateLimiter;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.impl.chn.ChannelHandler;
import com.naikeri.sgw.impl.settings.ChannelSettings;
import com.naikeri.sgw.impl.settings.m3ua.M3uaSettings;
import com.naikeri.sgw.impl.settings.sccp.SccpSettings;
import com.naikeri.sgw.impl.settings.sctp.SctpSettings;
import com.naikeri.sgw.impl.settings.tcap.TcapSettings;
import com.naikeri.sgw.network.layers.M3uaLayer;
import com.naikeri.sgw.network.layers.SccpLayer;
import com.naikeri.sgw.network.layers.SctpLayer;
import com.naikeri.sgw.network.layers.TcapLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.MAPStackImpl;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContext;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextName;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPDialogListener;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.MAPProvider;
import org.restcomm.protocols.ss7.map.api.dialog.MAPAbortProviderReason;
import org.restcomm.protocols.ss7.map.api.dialog.MAPAbortSource;
import org.restcomm.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic;
import org.restcomm.protocols.ss7.map.api.dialog.MAPRefuseReason;
import org.restcomm.protocols.ss7.map.api.dialog.MAPUserAbortChoice;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.DiameterIdentity;
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.api.service.lsm.MAPServiceLsmListener;
import org.restcomm.protocols.ss7.map.api.service.lsm.ProvideSubscriberLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.ProvideSubscriberLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.SendRoutingInfoForLCSRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.SendRoutingInfoForLCSResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPServiceMobilityListener;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ForwardCheckSSIndicationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ResetRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.imei.CheckImeiRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.imei.CheckImeiResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ADDInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.EPSInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ExtSupportedFeatures;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SGSNCapability;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SMSRegisterRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SuperChargerInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedFeatures;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedLCSCapabilitySets;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedRATTypes;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UESRVCCCapability;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeRequest_Mobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeResponse_Mobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeModificationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeModificationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeSubscriptionInterrogationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeSubscriptionInterrogationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OfferedCamel4CSIs;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SupportedCamelPhases;
import org.restcomm.protocols.ss7.map.primitives.GSNAddressImpl;
import org.restcomm.protocols.ss7.map.primitives.IMEIImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.ADDInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SGSNCapabilityImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SupportedLCSCapabilitySetsImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SupportedRATTypesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SupportedCamelPhasesImpl;
import org.restcomm.protocols.ss7.sccp.NetworkIdState;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.asn.ApplicationContextName;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

/**
 * MAP Prototype
 *
 */
public class MapPrototype extends ChannelHandler implements MAPDialogListener, MAPServiceMobilityListener, MAPServiceLsmListener {

  private SctpLayer sctpClient;
  private SctpLayer sctpServer;
  private M3uaLayer m3uaServer;
  private M3uaLayer m3uaClient;
  private SccpLayer sccpServer;
  private SccpLayer sccpClient;
  private SccpSettings sccpClientSettings;
  private SccpSettings sccpServerSettings;
  private TcapLayer tcapServer;
  private TcapLayer tcapClient;
  private MAPStackImpl mapServer;
  private MAPStackImpl mapClient;
  private MAPProvider clientMapProvider;

  public static Logger logger = LogManager.getLogger(MapPrototype.class.getName());

  public MapPrototype(ChannelSettings channelSettings) {
    super(channelSettings);
  }

  public void channelInitializeComplete() {
    // here we are completely connected to the ESG

    // SCTP
    try {
      logger.info("SCTP layer starting");
      // configuration load Server
      SctpSettings sctpServerSettings = new SctpSettings("sctpServer", true, 1000, true);
      sctpServerSettings.addServer("testsrv", "127.0.0.1:8022", false, false, 10, null);
      sctpServerSettings.addServerAssociation("testsrv", "server_assoc_0", "127.0.0.1:8011", false,
          null);
      sctpServer = new SctpLayer(sctpServerSettings);

      // Configuration load Client

      SctpSettings sctpClientSettings = new SctpSettings("sctpClient", true, 1000, true);
      sctpClientSettings.addAssociation("assoc_0", "127.0.0.1:8011", "127.0.0.1:8022", false, null);
      sctpClient = new SctpLayer(sctpClientSettings);

    } catch (Exception e) {
      logger.error("Caught exception", e);
    }

    // M3UA
    try {
      logger.info("M3UA layer starting");
      // configuration load Server
      M3uaSettings m3uaServerSettings =
          new M3uaSettings("m3uaServer", "NAIKERI_SGW", 10000, "sctpServer", true);

      m3uaServerSettings.addApplicationServer("AS1", "SGW", "SE", "SERVER", 100, "LOADSHARE", 0, 0);
      m3uaServerSettings.addApplicationServerProcess("AS1", "ASP1", "server_assoc_0", true);
      m3uaServerSettings.addApplicationServerRoute("AS1", 1001, 1000, -1);

      m3uaServer = new M3uaLayer(m3uaServerSettings, sctpServer);

      // Configure load Client
      M3uaSettings m3uaClientSettings = new M3uaSettings("m3uaClient", "NAIKERI_SGW", 10000, "sctpClient", true);
      m3uaClientSettings.addApplicationServer("AS2", "AS", "SE", "CLIENT", 100, "LOADSHARE", 0, 0);
      m3uaClientSettings.addApplicationServerProcess("AS2", "ASP2", "assoc_0", true);
      m3uaClientSettings.addApplicationServerRoute("AS2", 1000, 1001, -1);

      m3uaClient = new M3uaLayer(m3uaClientSettings, sctpClient);
    } catch (Exception e) {
      logger.error("Caught exception", e);
    }

    // SCCP
    try {
      logger.info("SCCP layer starting");
      sccpServerSettings = new SccpSettings("sccpServer", "m3ua", 1, true);
      sccpServerSettings.addRemoteSpc("1001", 1, 1001, 0, 0);
      sccpServerSettings.addRemoteSsn("8", 1, 1001, 8, 0, false);
      sccpServerSettings.addMtp3ServiceAccessPoint("SAPServer", 1, 1, 1000, 2, 0, "50373700000");
      sccpServerSettings.addMtp3Destination("SAPServer", 1, 1, 1001, 1001, 0, 255, 255);
      // sccpServerSettings.addRoutingAddress("localRoutingServer", 1,
      // "ROUTING_BASED_ON_GLOBAL_TITLE",
      // "50373700000", 1000, 6);
      // sccpServerSettings.addRoutingAddress("remoteRoutingServer", 2,
      // "ROUTING_BASED_ON_GLOBAL_TITLE",
      // "50373700001", 1001, 8);

      // sccpServerSettings.addSccpRules("remoteRuleServer", 1, "Solitary",
      // "Undefined",
      // "REMOTE", "*", "K", 1, -1,
      // null, 0, null);
      // sccpServerSettings.addSccpRules("localRuleServer", 2, "Solitary",
      // "Undefined",
      // "LOCAL", "*", "K", 2, -1,
      // null, 0, null);

      sccpServer = new SccpLayer(sccpServerSettings, m3uaServer);

      sccpClientSettings = new SccpSettings("sccpClient", "m3ua", 1, true);
      sccpClientSettings.addRemoteSpc("1000", 1, 1000, 0, 0);
      sccpClientSettings.addRemoteSsn("6", 1, 1000, 6, 0, false);
      sccpClientSettings.addMtp3ServiceAccessPoint("SAPClient", 1, 1, 1001, 2, 0, "50373700001");
      sccpClientSettings.addMtp3Destination("SAPClient", 1, 1, 1000, 1000, 0, 255, 255);
      // sccpClientSettings.addRoutingAddress("localRoutingClient", 1,
      // "ROUTING_BASED_ON_GLOBAL_TITLE",
      // "50373700001", 1001, 8);
      // sccpClientSettings.addRoutingAddress("remoteRoutingClient", 2,
      // "ROUTING_BASED_ON_GLOBAL_TITLE",
      // "50373700000", 1000, 6);

      // sccpClientSettings.addSccpRules("remoteRulClient", 1, "Solitary",
      // "Undefined",
      // "REMOTE", "*", "K", 1, -1,
      // null, 0, null);
      // sccpClientSettings.addSccpRules("localRuleClient", 2, "Solitary",
      // "Undefined",
      // "LOCAL", "*", "K", 2, -1,
      // null, 0, null);

      sccpClient = new SccpLayer(sccpClientSettings, m3uaClient);

    } catch (Exception e) {
      logger.error("Caught exception", e);
    }

    // TCAP
    try {
      logger.info("TCAP layer starting");
      TcapSettings tcapServerSettings = new TcapSettings("tcapServer", "sccpServer", 6, true);
      // tcapServerSettings.addSubSystemNumber(6);
      tcapServerSettings.setDialogIdleTimeout(60000);
      tcapServerSettings.setInvokeTimeOut(30000);
      tcapServerSettings.setMaxDialogs(5000);
      tcapServer = new TcapLayer(tcapServerSettings, sccpServer);

      TcapSettings tcapClientSettings = new TcapSettings("tcapClient", "sccpClient", 8, true);
      // tcapClientSettings.addSubSystemNumber(8);
      tcapClientSettings.setDialogIdleTimeout(60000);
      tcapClientSettings.setInvokeTimeOut(30000);
      tcapClientSettings.setMaxDialogs(5000);
      tcapClient = new TcapLayer(tcapClientSettings, sccpClient);

    } catch (Exception e) {
      logger.error("Caught exception", e);
    }

    // MAP
    try {
      logger.info("MAP layer starting");
      mapClient = new MAPStackImpl("mapClient", tcapClient.getTcapProvider());
      clientMapProvider = mapClient.getMAPProvider();
      clientMapProvider.addMAPDialogListener(this);

      clientMapProvider.getMAPServiceMobility().addMAPServiceListener(this);
      clientMapProvider.getMAPServiceMobility().activate();
      clientMapProvider.getMAPServiceLsm().addMAPServiceListener(this);
      clientMapProvider.getMAPServiceLsm().activate();

      mapClient.start();

      mapServer = new MAPStackImpl("mapServer", tcapServer.getTcapProvider());
      MAPProvider serverMapProvider = mapServer.getMAPProvider();
      serverMapProvider.addMAPDialogListener(this);

      serverMapProvider.getMAPServiceMobility().addMAPServiceListener(this);
      serverMapProvider.getMAPServiceMobility().activate();
      serverMapProvider.getMAPServiceLsm().addMAPServiceListener(this);
      serverMapProvider.getMAPServiceLsm().activate();

      mapServer.start();

    } catch (Exception e) {
      logger.error("Caught exception", e);
    }
  }

  public void initiateUpdateGprsLocation(String IMSI, String sgsn_address, String sgsn_number)
      throws MAPException {
    try {
      logger.info("Sending MAP Message");
      RateLimiter rateLimiterObj = RateLimiter.create(5000);
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null
          || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control: MAP load test client: networkIdState="
            + networkIdState);
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          logger.error("Caught exception", e);
        }
      }

      rateLimiterObj.acquire();

      // TO DO: For now i declared these variables manually but they need to be
      // some
      // how included into the SccpSettings

      AddressString origRef = this.clientMapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
      AddressString destRef = this.clientMapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");
      MAPDialogMobility mapDialogMobility =
          this.clientMapProvider.getMAPServiceMobility().createNewDialog(
              MAPApplicationContext.getInstance(MAPApplicationContextName.gprsLocationUpdateContext,
                  MAPApplicationContextVersion.version3),
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), origRef,
              this.sccpServerSettings.getRoutingAddresses().get(1).getSccpAddress(), destRef);

      ISDNAddressString sgsnNumber = new ISDNAddressStringImpl(AddressNature.international_number,
          NumberingPlan.ISDN, sgsn_number);
      // ISDNAddressString gsmSCFAddress = new
      // ISDNAddressStringImpl(AddressNature.international_number,
      // NumberingPlan.ISDN, sgsn_address);
      IMSI imsi = new IMSIImpl(IMSI);
      byte[] sgsnAddressByteArray = new BigInteger("112233445500", 16).toByteArray();
      GSNAddress sgsnAddress = new GSNAddressImpl(sgsnAddressByteArray);
      MAPExtensionContainer extensionContainer = null;
      boolean solsaSupportIndicator = false;
      SuperChargerInfo superChargerSupportedInServingNetworkEntity = null;
      boolean gprsEnhancementsSupportIndicator = false;
      SupportedCamelPhases supportedCamelPhases =
          new SupportedCamelPhasesImpl(true, true, true, false);
      OfferedCamel4CSIs offeredCamel4CSIs = null;
      boolean smsCallBarringSupportIndicator = true;
      SupportedRATTypes supportedRATTypesIndicator =
          new SupportedRATTypesImpl(true, true, false, false, true, false);
      boolean lcsCapabilitySetRelease98_99 = true;
      boolean lcsCapabilitySetRelease4 = true;
      boolean lcsCapabilitySetRelease5 = true;
      boolean lcsCapabilitySetRelease6 = true;
      boolean lcsCapabilitySetRelease7 = false;
      SupportedLCSCapabilitySets supportedLCSCapabilitySets =
          new SupportedLCSCapabilitySetsImpl(lcsCapabilitySetRelease98_99, lcsCapabilitySetRelease4,
              lcsCapabilitySetRelease5, lcsCapabilitySetRelease6, lcsCapabilitySetRelease7);
      SupportedFeatures supportedFeatures = null;
      boolean tAdsDataRetrieval = true;
      Boolean homogeneousSupportOfIMSVoiceOverPSSessions = null;
      boolean cancellationTypeInitialAttach = false;
      boolean misdnlessOperationSupported = false;
      boolean updateOfHomogeneousSupportOfIMSVoiceOverPSSessions = false;
      boolean resetIdsSupported = false;
      ExtSupportedFeatures extSupportedFeatures = null;
      SGSNCapability sgsnCapability = new SGSNCapabilityImpl(solsaSupportIndicator,
          extensionContainer, superChargerSupportedInServingNetworkEntity,
          gprsEnhancementsSupportIndicator, supportedCamelPhases, supportedLCSCapabilitySets,
          offeredCamel4CSIs, smsCallBarringSupportIndicator, supportedRATTypesIndicator,
          supportedFeatures, tAdsDataRetrieval, homogeneousSupportOfIMSVoiceOverPSSessions,
          cancellationTypeInitialAttach, misdnlessOperationSupported,
          updateOfHomogeneousSupportOfIMSVoiceOverPSSessions, resetIdsSupported, extSupportedFeatures);

      boolean informPreviousNetworkEntity = false;
      boolean psLCSNotSupportedByUE = false;
      byte[] visitedGmlcAddress = new BigInteger("112233445500", 16).toByteArray();
      GSNAddress vGmlcAddress = new GSNAddressImpl(visitedGmlcAddress);
      IMEI imeisv = new IMEIImpl("01171400466105");
      boolean skipSubscriberDataUpdate = true;
      ADDInfo addInfo = new ADDInfoImpl(imeisv, skipSubscriberDataUpdate);
      EPSInfo epsInfo = null;
      boolean servingNodeTypeIndicator = false;
      UsedRATType usedRATType = UsedRATType.utran;
      boolean gprsSubscriptionDataNotNeeded = false;
      boolean nodeTypeIndicator = false;
      boolean areaRestricted = true;
      boolean ueReachableIndicator = false;
      boolean epsSubscriptionDataNotNeeded = true;
      UESRVCCCapability uesrvccCapability = UESRVCCCapability.ueSrvccSupported;
      ArrayList<PlmnId> ePLMNList = null;
      ISDNAddressString mmeNumberForMTSMS = null;
      SMSRegisterRequest smsRegisterRequest = null;
      boolean smsOnly = false;
      DiameterIdentity sgsnName = null;
      DiameterIdentity sgsnRealm = null;
      boolean lgdSupportIndicator = false;
      boolean removalOfMMERegistrationForSMS = false;
      ArrayList<PlmnId> adjacentPLMNList = null;

      mapDialogMobility.addUpdateGprsLocationRequest(imsi, sgsnNumber, sgsnAddress,
          extensionContainer, sgsnCapability, informPreviousNetworkEntity, psLCSNotSupportedByUE,
          vGmlcAddress, addInfo, epsInfo, servingNodeTypeIndicator, skipSubscriberDataUpdate,
          usedRATType, gprsSubscriptionDataNotNeeded, nodeTypeIndicator, areaRestricted,
          ueReachableIndicator, epsSubscriptionDataNotNeeded, uesrvccCapability, ePLMNList,
          mmeNumberForMTSMS, smsRegisterRequest, smsOnly, sgsnName, sgsnRealm, lgdSupportIndicator,
          removalOfMMERegistrationForSMS, adjacentPLMNList);
      mapDialogMobility.send();
    } catch (MAPException e) {
      logger.error("Error while sending MAP ATI:{}", String.valueOf(e));
    }
  }

  public void receiveMessageRequest(ChannelMessage channelMessage) {
    // this is forced here to explicitly receive a message request to the network
    // return null;
  }

  public void processMessageResponse(ChannelMessage channelMessage) {
    // this is to get the response from the application
    /// compose new dialog to forward.
  }

  public int sendMessageResponse(ChannelMessage channelMessage) {
    // this is forced here to explicitly send a message response to the network
    return 0;
  }

  public static void main(String[] args) {
    // pass the parameters for MAP configuration
    ChannelSettings channelSettings =
        new ChannelSettings("MapChannelProto", "MapPrototype", null, "map");

    // create the instance for MAP channel to connect to ESG
    try {
      MapPrototype mapPrototype = new MapPrototype(channelSettings);
      mapPrototype.channelInitializeComplete();

      Thread.sleep(5000);
      String IMSI = "425016871012345";
      String sgsn_address = "112233445500";
      String sgsn_number = "112233445501";
      while (true) {
        mapPrototype.initiateUpdateGprsLocation(IMSI, sgsn_address, sgsn_number);
        Thread.sleep(5000);
      }

    } catch (Exception e) {
      logger.error("Caught exception", e);
    }

  }

  @Override
  public void onProvideSubscriberLocationRequest(
      ProvideSubscriberLocationRequest provideSubscriberLocationRequest) {

  }

  @Override
  public void onProvideSubscriberLocationResponse(
      ProvideSubscriberLocationResponse provideSubscriberLocationResponse) {

  }

  @Override
  public void onSubscriberLocationReportRequest(
      SubscriberLocationReportRequest subscriberLocationReportRequest) {

  }

  @Override
  public void onSubscriberLocationReportResponse(
      SubscriberLocationReportResponse subscriberLocationReportResponse) {

  }

  @Override
  public void onSendRoutingInfoForLCSRequest(
      SendRoutingInfoForLCSRequest sendRoutingInfoForLCSRequest) {

  }

  @Override
  public void onSendRoutingInfoForLCSResponse(
      SendRoutingInfoForLCSResponse sendRoutingInfoForLCSResponse) {

  }

  @Override
  public void onUpdateLocationRequest(UpdateLocationRequest updateLocationRequest) {

  }

  @Override
  public void onUpdateLocationResponse(UpdateLocationResponse updateLocationResponse) {

  }

  @Override
  public void onCancelLocationRequest(CancelLocationRequest cancelLocationRequest) {

  }

  @Override
  public void onCancelLocationResponse(CancelLocationResponse cancelLocationResponse) {

  }

  @Override
  public void onSendIdentificationRequest(SendIdentificationRequest sendIdentificationRequest) {

  }

  @Override
  public void onSendIdentificationResponse(SendIdentificationResponse sendIdentificationResponse) {

  }

  @Override
  public void onUpdateGprsLocationRequest(UpdateGprsLocationRequest updateGprsLocationRequest) {
    try {
      IMSI newIMSI;
      MAPDialogMobility ulgDialogIn = updateGprsLocationRequest.getMAPDialog();

      MAPApplicationContext appCntx = ulgDialogIn.getApplicationContext();
      SccpAddress origAddress = ulgDialogIn.getRemoteAddress();
      AddressString origReference = ulgDialogIn.getReceivedOrigReference();
      SccpAddress destAddress = ulgDialogIn.getLocalAddress();
      AddressString destReference = ulgDialogIn.getReceivedDestReference();

      // TO DO: eventually some modifications shall be made here to the above
      // values

      MAPDialogMobility ulgDialogOut = mapServer.getMAPProvider().getMAPServiceMobility()
          .createNewDialog(appCntx, destAddress, destReference, origAddress, origReference);

      IMSI imsi = updateGprsLocationRequest.getImsi();
      logger.info("Received IMSI: {}", imsi.getData());
      logger.info("From GT: {}", origAddress.getGlobalTitle().getDigits());
      if (origAddress.getGlobalTitle().getDigits().equals("50373700001")
          && imsi.getData().startsWith("42501")) {
        logger.info("Performing IMSI Replacement");
        newIMSI = new IMSIImpl(imsi.getData().replaceFirst("42501", "12345"));
        ulgDialogOut.addUpdateGprsLocationRequest(newIMSI,
            updateGprsLocationRequest.getSgsnNumber(), updateGprsLocationRequest.getSgsnAddress(),
            updateGprsLocationRequest.getExtensionContainer(),
            updateGprsLocationRequest.getSGSNCapability(),
            updateGprsLocationRequest.getInformPreviousNetworkEntity(),
            updateGprsLocationRequest.getPsLCSNotSupportedByUE(),
            updateGprsLocationRequest.getVGmlcAddress(), updateGprsLocationRequest.getADDInfo(),
            updateGprsLocationRequest.getEPSInfo(),
            updateGprsLocationRequest.getServingNodeTypeIndicator(),
            updateGprsLocationRequest.getSkipSubscriberDataUpdate(),
            updateGprsLocationRequest.getUsedRATType(),
            updateGprsLocationRequest.getGprsSubscriptionDataNotNeeded(),
            updateGprsLocationRequest.getNodeTypeIndicator(),
            updateGprsLocationRequest.getAreaRestricted(),
            updateGprsLocationRequest.getUeReachableIndicator(),
            updateGprsLocationRequest.getEpsSubscriptionDataNotNeeded(),
            updateGprsLocationRequest.getUESRVCCCapability(),
            updateGprsLocationRequest.getEPLMNList(),
            updateGprsLocationRequest.getMmeNumberForMTSMS(),
            updateGprsLocationRequest.getSMSRegisterRequest(),
            updateGprsLocationRequest.getSmsOnly(),
            updateGprsLocationRequest.getSgsnName(),
            updateGprsLocationRequest.getSgsnRealm(),
            updateGprsLocationRequest.getLgdSupportIndicator(),
            updateGprsLocationRequest.getRemovalOfMMERegistrationForSMS(),
            updateGprsLocationRequest.getAdjacentPLMNList());
        ulgDialogOut.send();
      }

    } catch (MAPException mapException) {
      logger.error("MAP Exception while processing UpdateGprsLocationRequest {}", String.valueOf(mapException));
    } catch (Exception e) {
      logger.error("Exception while processing AnyTimeInterrogationRequest{}", String.valueOf(e));
    }

  }

  @Override
  public void onUpdateGprsLocationResponse(UpdateGprsLocationResponse updateGprsLocationResponse) {

  }

  @Override
  public void onPurgeMSRequest(PurgeMSRequest purgeMSRequest) {

  }

  @Override
  public void onPurgeMSResponse(PurgeMSResponse purgeMSResponse) {

  }

  @Override
  public void onSendAuthenticationInfoRequest(SendAuthenticationInfoRequest sendAuthenticationInfoRequest) {

  }

  @Override
  public void onSendAuthenticationInfoResponse(SendAuthenticationInfoResponse sendAuthenticationInfoResponse) {

  }

  @Override
  public void onAuthenticationFailureReportRequest(AuthenticationFailureReportRequest authenticationFailureReportRequest) {

  }

  @Override
  public void onAuthenticationFailureReportResponse(AuthenticationFailureReportResponse authenticationFailureReportResponse) {

  }

  @Override
  public void onResetRequest(ResetRequest resetRequest) {

  }

  @Override
  public void onForwardCheckSSIndicationRequest(ForwardCheckSSIndicationRequest forwardCheckSSIndicationRequest) {

  }

  @Override
  public void onRestoreDataRequest(RestoreDataRequest restoreDataRequest) {

  }

  @Override
  public void onRestoreDataResponse(RestoreDataResponse restoreDataResponse) {

  }

  @Override
  public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest anyTimeInterrogationRequest) {

  }

  @Override
  public void onAnyTimeInterrogationResponse(AnyTimeInterrogationResponse anyTimeInterrogationResponse) {

  }

  @Override
  public void onAnyTimeSubscriptionInterrogationRequest(AnyTimeSubscriptionInterrogationRequest anyTimeSubscriptionInterrogationRequest) {

  }

  @Override
  public void onAnyTimeSubscriptionInterrogationResponse(AnyTimeSubscriptionInterrogationResponse anyTimeSubscriptionInterrogationResponse) {

  }

  @Override
  public void onAnyTimeModificationRequest(AnyTimeModificationRequest anyTimeModificationRequest) {

  }

  @Override
  public void onAnyTimeModificationResponse(AnyTimeModificationResponse anyTimeModificationResponse) {

  }

  @Override
  public void onProvideSubscriberInfoRequest(ProvideSubscriberInfoRequest provideSubscriberInfoRequest) {

  }

  @Override
  public void onProvideSubscriberInfoResponse(ProvideSubscriberInfoResponse provideSubscriberInfoResponse) {

  }

  @Override
  public void onInsertSubscriberDataRequest(InsertSubscriberDataRequest insertSubscriberDataRequest) {

  }

  @Override
  public void onInsertSubscriberDataResponse(InsertSubscriberDataResponse insertSubscriberDataResponse) {

  }

  @Override
  public void onDeleteSubscriberDataRequest(DeleteSubscriberDataRequest deleteSubscriberDataRequest) {

  }

  @Override
  public void onDeleteSubscriberDataResponse(DeleteSubscriberDataResponse deleteSubscriberDataResponse) {

  }

  @Override
  public void onCheckImeiRequest(CheckImeiRequest checkImeiRequest) {

  }

  @Override
  public void onCheckImeiResponse(CheckImeiResponse checkImeiResponse) {

  }

  @Override
  public void onActivateTraceModeRequest_Mobility(ActivateTraceModeRequest_Mobility activateTraceModeRequest_mobility) {

  }

  @Override
  public void onActivateTraceModeResponse_Mobility(ActivateTraceModeResponse_Mobility activateTraceModeResponse_mobility) {

  }

  @Override
  public void onErrorComponent(MAPDialog mapDialog, Long aLong, MAPErrorMessage mapErrorMessage) {

  }

  @Override
  public void onRejectComponent(MAPDialog mapDialog, Long aLong, Problem problem, boolean b) {

  }

  @Override
  public void onInvokeTimeout(MAPDialog mapDialog, Long aLong) {

  }

  @Override
  public void onMAPMessage(MAPMessage mapMessage) {

  }

  @Override
  public void onDialogDelimiter(MAPDialog mapDialog) {

  }

  @Override
  public void onDialogRequest(MAPDialog mapDialog, AddressString addressString,
                              AddressString addressString1, MAPExtensionContainer mapExtensionContainer) {

  }

  @Override
  public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString addressString,
                                      AddressString addressString1, AddressString addressString2, AddressString addressString3) {

  }

  @Override
  public void onDialogAccept(MAPDialog mapDialog, MAPExtensionContainer mapExtensionContainer) {

  }

  @Override
  public void onDialogReject(MAPDialog mapDialog, MAPRefuseReason mapRefuseReason, ApplicationContextName applicationContextName,
                             MAPExtensionContainer mapExtensionContainer) {

  }

  @Override
  public void onDialogUserAbort(MAPDialog mapDialog, MAPUserAbortChoice mapUserAbortChoice, MAPExtensionContainer mapExtensionContainer) {

  }

  @Override
  public void onDialogProviderAbort(MAPDialog mapDialog, MAPAbortProviderReason mapAbortProviderReason, MAPAbortSource mapAbortSource,
                                    MAPExtensionContainer mapExtensionContainer) {

  }

  @Override
  public void onDialogClose(MAPDialog mapDialog) {

  }

  @Override
  public void onDialogNotice(MAPDialog mapDialog, MAPNoticeProblemDiagnostic mapNoticeProblemDiagnostic) {

  }

  @Override
  public void onDialogRelease(MAPDialog mapDialog) {

  }

  @Override
  public void onDialogTimeout(MAPDialog mapDialog) {

  }



  @Override
  public LayerInterface getLayerInterface() {
    return null;
  }

  @Override
  public void channelInitialize(LayerInterface[] layerInterface) {

  }

  @Override
  public LayerInterface getLayerInterface(String serviceFunctionName) {
    return null;
  }
}
