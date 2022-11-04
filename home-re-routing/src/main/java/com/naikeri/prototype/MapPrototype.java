package com.naikeri.prototype;

import java.math.BigInteger;
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
// import com.naikeri.sgw.network.listeners.map.MapDialogListener;
// import com.naikeri.sgw.network.listeners.map.MapServiceLsmListener;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.MAPStackImpl;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContext;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextName;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPProvider;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ADDInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.EPSInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SGSNCapability;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SuperChargerInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedFeatures;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedLCSCapabilitySets;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedRATTypes;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UESRVCCCapability;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
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

/*
 * MAP Prototype
 *
 */
public class MapPrototype extends ChannelHandler {

    private SctpLayer sctpClient;
    private M3uaLayer m3uaServer;
    private SccpLayer sccpServer;
    private SccpSettings sccpServerSettings;
    private TcapLayer tcapServer;
    private MAPStackImpl mapServer;
    private MAPProvider mapServerProvider;

    private SctpLayer sctpServer;
    private M3uaLayer m3uaClient;
    private SccpLayer sccpClient;
    private SccpSettings sccpClientSettings;
    private TcapLayer tcapClient;
    private MAPStackImpl mapClient;
    private MAPProvider mapClientProvider;

    private NetworkIdState networkIdState;
    private RateLimiter rateLimiterObj = null;

    private static Logger logger = Logger.getLogger(MapPrototype.class);

    public MapPrototype(ChannelSettings channelSettings) {
        super(channelSettings);
    }


    public void channelInitialize() {
        // here we are completely connected to the ESG

        // SCTP
        try {
            logger.info("SCTP layer starting...");

            SctpSettings sctpServerSettings = new SctpSettings("sctpServer", true, 1000, true);
            sctpServerSettings.addServer("testsrv", "127.0.0.1:8122", false, false, 10, null);
            sctpServerSettings.addServerAssociation("testsrv", "server_assoc_0", "127.0.0.1:8111",
                    false, null);
            sctpServer = new SctpLayer(sctpServerSettings);

            SctpSettings sctpClientSettings = new SctpSettings("sctpClient", true, 1000, true);
            sctpClientSettings.addAssociation("assoc_0", "127.0.0.1:8111", "127.0.0.1:8122", false,
                    null);
            sctpClient = new SctpLayer(sctpClientSettings);
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }

        // M3UA
        try {
            logger.info("M3UA layer starting...");

            M3uaSettings m3uaServerSettings =
                    new M3uaSettings("m3uaServer", "NAIKERI_SGW", 10000, "sctpServer", true);
            m3uaServerSettings.addApplicationServer("AS1", "SGW", "SE", "SERVER", 100, "LOADSHARE",
                    0, 0);
            m3uaServerSettings.addApplicationServerProcess("AS1", "ASP1", "server_assoc_0", true);
            m3uaServerSettings.addApplicationServerRoute("AS1", 1001, 1000, -1);
            m3uaServer = new M3uaLayer(m3uaServerSettings, sctpServer);

            M3uaSettings m3uaClientSettings =
                    new M3uaSettings("m3uaClient", "NAIKERI_SGW", 10000, "sctpClient", true);
            m3uaClientSettings.addApplicationServer("AS2", "AS", "SE", "CLIENT", 100, "LOADSHARE",
                    0, 0);
            m3uaClientSettings.addApplicationServerProcess("AS2", "ASP2", "assoc_0", true);
            m3uaClientSettings.addApplicationServerRoute("AS2", 1000, 1001, -1);
            m3uaClient = new M3uaLayer(m3uaClientSettings, sctpClient);
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }

        // SCCP
        try {
            logger.info("SCCP layer starting...");

            sccpServerSettings = new SccpSettings("sccpServer", "m3uaServer", 1, true);
            sccpServerSettings.addRemoteSpc("1001", 1, 1001, 0, 0);
            sccpServerSettings.addRemoteSsn("8", 1, 1001, 8, 0, false);
            sccpServerSettings.addMtp3ServiceAccessPoint("SAPServer", 1, 1, 1000, 2, 0,
                    "50373700000");
            sccpServerSettings.addMtp3Destination("SAPServer", 1, 1, 1001, 1001, 0, 255, 255);
            // sccpServerSettings.addRoutingAddress("localRoutingServer",1,"ROUTING_BASED_ON_GLOBAL_TITLE","50373700000",1000,6);
            // sccpServerSettings.addRoutingAddress("remoteRoutingServer",2,"ROUTING_BASED_ON_GLOBAL_TITLE","50373700001",1001,8);

            // sccpServerSettings.addSccpRules("remoteRuleServer",1,"Solitary","Undefined","REMOTE","*","K",1,-1,null,0,null);
            // sccpServerSettings.addSccpRules("localRuleServer",2,"Solitary","Undefined","LOCAL","*","K",2,-1,null,0,null);

            sccpServer = new SccpLayer(sccpServerSettings, m3uaServer);


            sccpClientSettings = new SccpSettings("sccpClient", "m3uaClient", 1, true);
            sccpClientSettings.addRemoteSpc("1000", 1, 1000, 0, 0);
            sccpClientSettings.addRemoteSsn("6", 1, 1000, 6, 0, false);
            sccpClientSettings.addMtp3ServiceAccessPoint("SAPClient", 1, 1, 1001, 2, 0,
                    "50373700001");
            sccpClientSettings.addMtp3Destination("SAPClient", 1, 1, 1000, 1000, 0, 255, 255);
            // sccpClientSettings.addRoutingAddress("localRoutingClient",1,"ROUTING_BASED_ON_GLOBAL_TITLE","50373700001",1001,8);
            // sccpClientSettings.addRoutingAddress("remoteRoutingClient",2,"ROUTING_BASED_ON_GLOBAL_TITLE","50373700000",1000,6);

            // sccpClientSettings.addSccpRules("remoteRulClient",1,"Solitary","Undefined","REMOTE","*","K",1,-1,null,0,null);
            // sccpClientSettings.addSccpRules("localRuleClient",2,"Solitary","Undefined","LOCAL","*","K",2,-1,null,0,null);

            sccpClient = new SccpLayer(sccpClientSettings, m3uaClient);
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }

        // TCAP
        try {
            logger.info("TCAP layer starting...");

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
            logger.error(e.getStackTrace());
        }

        // MAP
        try {
            logger.info("MAP layer starting...");

            mapServer = new MAPStackImpl("mapServer", tcapServer.getTcapProvider());
            mapServerProvider = mapServer.getMAPProvider();
            // mapServerProvider.addMAPDialogListener(new MapDialogListener(mapServer));
            // mapServerProvider.getMAPServiceMobility().addMAPServiceListener(new
            // MapServiceMobilityListener(mapServer, this));
            mapServerProvider.getMAPServiceMobility().activate();
            // mapServerProvider.getMAPServiceLsm().addMAPServiceListener(new
            // MapServiceLsmListener(mapClient, this));
            mapServerProvider.getMAPServiceLsm().activate();
            mapServer.start();

            mapClient = new MAPStackImpl("mapClient", tcapClient.getTcapProvider());
            mapClientProvider = mapClient.getMAPProvider();
            // mapClientProvider.addMAPDialogListener(new MapDialogListener(mapClient));
            // mapClientProvider.getMAPServiceMobility().addMAPServiceListener(new
            // MapServiceMobilityListener(mapClient, this));
            mapClientProvider.getMAPServiceMobility().activate();
            // mapClientProvider.getMAPServiceLsm().addMAPServiceListener(new
            // MapServiceLsmListener(mapClient, this));
            mapClientProvider.getMAPServiceLsm().activate();
            mapClient.start();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
    }

    public void initiateUpdateGprsLocation(String IMSI, String sgsn_address, String sgsn_number)
            throws MAPException {
        try {
            logger.info("Sending MAP message...");
            this.rateLimiterObj = RateLimiter.create(5000);
            this.networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
            if (!(this.networkIdState == null || this.networkIdState.isAvailable()
                    && this.networkIdState.getCongLevel() == 0)) {
                // congestion or unavailable
                logger.warn("Outgoing congestion control: MAP load test client: networkIdState="
                        + this.networkIdState);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }

            this.rateLimiterObj.acquire();


            AddressString origRef =
                    this.mapClientProvider.getMAPParameterFactory().createAddressString(
                            AddressNature.international_number, NumberingPlan.ISDN, "12345");
            AddressString destRef =
                    this.mapClientProvider.getMAPParameterFactory().createAddressString(
                            AddressNature.international_number, NumberingPlan.ISDN, "67890");
            MAPDialogMobility mapDialogMobility =
                    this.mapClientProvider.getMAPServiceMobility().createNewDialog(
                            MAPApplicationContext.getInstance(
                                    MAPApplicationContextName.gprsLocationUpdateContext,
                                    MAPApplicationContextVersion.version3),
                            this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(),
                            origRef,
                            this.sccpServerSettings.getRoutingAddresses().get(1).getSccpAddress(),
                            destRef);

            ISDNAddressString sgsnNumber = new ISDNAddressStringImpl(
                    AddressNature.international_number, NumberingPlan.ISDN, sgsn_number);
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
                    new SupportedRATTypesImpl(true, true, false, false, true);
            boolean lcsCapabilitySetRelease98_99 = true;
            boolean lcsCapabilitySetRelease4 = true;
            boolean lcsCapabilitySetRelease5 = true;
            boolean lcsCapabilitySetRelease6 = true;
            boolean lcsCapabilitySetRelease7 = false;
            SupportedLCSCapabilitySets supportedLCSCapabilitySets =
                    new SupportedLCSCapabilitySetsImpl(lcsCapabilitySetRelease98_99,
                            lcsCapabilitySetRelease4, lcsCapabilitySetRelease5,
                            lcsCapabilitySetRelease6, lcsCapabilitySetRelease7);
            SupportedFeatures supportedFeatures = null;
            boolean tAdsDataRetrieval = true;
            Boolean homogeneousSupportOfIMSVoiceOverPSSessions = null;
            SGSNCapability sgsnCapability = new SGSNCapabilityImpl(solsaSupportIndicator,
                    extensionContainer, superChargerSupportedInServingNetworkEntity,
                    gprsEnhancementsSupportIndicator, supportedCamelPhases,
                    supportedLCSCapabilitySets, offeredCamel4CSIs, smsCallBarringSupportIndicator,
                    supportedRATTypesIndicator, supportedFeatures, tAdsDataRetrieval,
                    homogeneousSupportOfIMSVoiceOverPSSessions);
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

            mapDialogMobility.addUpdateGprsLocationRequest(imsi, sgsnNumber, sgsnAddress,
                    extensionContainer, sgsnCapability, informPreviousNetworkEntity,
                    psLCSNotSupportedByUE, vGmlcAddress, addInfo, epsInfo, servingNodeTypeIndicator,
                    skipSubscriberDataUpdate, usedRATType, gprsSubscriptionDataNotNeeded,
                    nodeTypeIndicator, areaRestricted, ueReachableIndicator,
                    epsSubscriptionDataNotNeeded, uesrvccCapability);
            mapDialogMobility.send();
        } catch (MAPException e) {
            logger.error("Error while sending MAP ATI:" + e);
        }
    }

    public void receiveMessageRequest(ChannelMessage channelMessage) {
        // this is forced here to explicitly receive a message request to the network
        // return null;
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
            mapPrototype.channelInitialize();

            String IMSI = "425016871012345";
            String sgsn_address = "112233445500";
            String sgsn_number = "112233445501";

            while (true) {
                Thread.sleep(5000);
                mapPrototype.initiateUpdateGprsLocation(IMSI, sgsn_address, sgsn_number);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

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
