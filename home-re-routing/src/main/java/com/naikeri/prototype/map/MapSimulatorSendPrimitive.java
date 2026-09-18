package com.naikeri.prototype.map;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;
import com.google.common.util.concurrent.RateLimiter;
import com.naikeri.sgw.impl.settings.sccp.SccpSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restcomm.protocols.ss7.map.MAPParameterFactoryImpl;
import org.restcomm.protocols.ss7.map.MAPStackImpl;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContext;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextName;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParameterFactory;
import org.restcomm.protocols.ss7.map.api.MAPProvider;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.ExternalSignalInfo;
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.MAPPrivateExtension;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.primitives.ProtocolId;
import org.restcomm.protocols.ss7.map.api.primitives.SignalInfo;
import org.restcomm.protocols.ss7.map.api.service.callhandling.MAPDialogCallHandling;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.RequestingNodeType;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ADDInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.EPSInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.LocationArea;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SGSNCapability;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SuperChargerInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedFeatures;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedLCSCapabilitySets;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedRATTypes;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UESRVCCCapability;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.BearerServiceCodeValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.Category;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBearerServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtSSInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtTeleserviceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ODBData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OfferedCamel4CSIs;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SubscriberStatus;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SupportedCamelPhases;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TeleserviceCodeValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.VlrCamelSubscriptionInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.VoiceBroadcastData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.VoiceGroupCallData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ZoneCode;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPDialogSms;
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_DA;
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_OA;
import org.restcomm.protocols.ss7.map.api.service.sms.SmsSignalInfo;
import org.restcomm.protocols.ss7.map.api.smstpdu.NumberingPlanIdentification;
import org.restcomm.protocols.ss7.map.api.smstpdu.TypeOfNumber;
import org.restcomm.protocols.ss7.map.primitives.ExternalSignalInfoImpl;
import org.restcomm.protocols.ss7.map.primitives.GSNAddressImpl;
import org.restcomm.protocols.ss7.map.primitives.IMEIImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.LMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.SignalInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.ADDInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.LACImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.LocationAreaImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SGSNCapabilityImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SupportedLCSCapabilitySetsImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SupportedRATTypesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SupportedCamelPhasesImpl;
import org.restcomm.protocols.ss7.map.service.sms.SmsSignalInfoImpl;
import org.restcomm.protocols.ss7.map.smstpdu.AddressFieldImpl;
import org.restcomm.protocols.ss7.map.smstpdu.DataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.smstpdu.ProtocolIdentifierImpl;
import org.restcomm.protocols.ss7.map.smstpdu.SmsSubmitTpduImpl;
import org.restcomm.protocols.ss7.map.smstpdu.UserDataImpl;
import org.restcomm.protocols.ss7.map.smstpdu.ValidityPeriodImpl;
import org.restcomm.protocols.ss7.sccp.NetworkIdState;

public class MapSimulatorSendPrimitive {

  private RateLimiter rateLimiterObj = null;
  private static final Logger logger = LoggerFactory.getLogger(MapSimulatorSendPrimitive.class);

  private SccpSettings sccpClientSettings;
  private SccpSettings sccpServerSettings;
  private MAPStackImpl mapClient;
  private MAPParameterFactory mapParameterFactory; 

  public MapSimulatorSendPrimitive(MAPStackImpl map, SccpSettings sccpClientSettings,
      SccpSettings sccpServerSettings) {
    this.mapClient = map;
    this.sccpClientSettings = sccpClientSettings;
    this.sccpServerSettings = sccpServerSettings;
    this.mapParameterFactory = map.getMAPProvider().getMAPParameterFactory();
    this.rateLimiterObj = RateLimiter.create(5000);
  }

  public void sendMtForwardSM(String imsiString) {
    try {
      logger.debug("Sending Mt Forward SM for IMSI = " + imsiString);
      this.mapClient.getMAPProvider().getMAPServiceSms().activate();

      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null
          || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn(
            "Outgoing congestion control: MAP load test client: networkIdState=" + networkIdState);
        Thread.sleep(3000);
      }

      this.rateLimiterObj.acquire();

      MAPApplicationContext appCnt = null;
      appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.shortMsgMTRelayContext,
          MAPApplicationContextVersion.version3);
      AddressString orgiReference = this.mapParameterFactory.createAddressString(
          AddressNature.international_number, NumberingPlan.ISDN, "31628968300");
      AddressString destReference = this.mapParameterFactory.createAddressString(
          AddressNature.international_number, NumberingPlan.land_mobile, "204208300008002");

      MAPDialogSms clientDialogSms =
          this.mapClient.getMAPProvider().getMAPServiceSms().createNewDialog(appCnt,
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), orgiReference,
              this.sccpServerSettings.getRoutingAddresses().get(1).getSccpAddress(), destReference);
      SM_RP_DA smRPDA =
          this.mapParameterFactory.createSM_RP_DA(this.mapParameterFactory.createIMSI(imsiString));

      AddressString msisdn1 = this.mapParameterFactory
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "111222333");
      SM_RP_OA smRPOA = this.mapParameterFactory.createSM_RP_OA_ServiceCentreAddressOA(msisdn1);
      SmsSignalInfo smRPUI = new SmsSignalInfoImpl(new byte[] {21, 22, 23, 24, 25}, null);
      clientDialogSms.addMtForwardShortMessageRequest(smRPDA, smRPOA, smRPUI, true, null);

      clientDialogSms.send();
      logger.debug("Message sent successfully");
    } catch (Exception e) {
      logger.error("Error occurred", e);
    }
  }


  public void sendMoForwardSm(String imstring, String imsi2String) {
    try {
      this.mapClient.getMAPProvider().getMAPServiceSms().activate();
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null
          || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn(
            "Outgoing congestion control: MAP load test client: networkIdState=" + networkIdState);
        Thread.sleep(3000);
      }

      this.rateLimiterObj.acquire();
      MAPApplicationContext appCnt = null;

      appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.shortMsgMORelayContext,
          MAPApplicationContextVersion.version3);

      AddressString orgiReference = this.mapParameterFactory.createAddressString(
          AddressNature.international_number, NumberingPlan.ISDN, "31628968300");
      AddressString destReference = this.mapParameterFactory.createAddressString(
          AddressNature.international_number, NumberingPlan.land_mobile, "204208300008002");

      MAPDialogSms clientDialogSms =
          this.mapClient.getMAPProvider().getMAPServiceSms().createNewDialog(appCnt,
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), orgiReference,
              this.sccpServerSettings.getRoutingAddresses().get(1).getSccpAddress(), destReference);
      // clientDialogSms.setExtentionContainer(MAPExtensionContainerTest.GetTestExtensionContainer())

      IMSI imsi1 = this.mapParameterFactory.createIMSI(imstring);
      SM_RP_DA smRPDA = this.mapParameterFactory.createSM_RP_DA(imsi1);
      ISDNAddressString msisdn1 = this.mapParameterFactory.createISDNAddressString(
          AddressNature.international_number, NumberingPlan.ISDN, "111222333");
      SM_RP_OA smRPOA = this.mapParameterFactory.createSM_RP_OA_Msisdn(msisdn1);

      AddressFieldImpl da = new AddressFieldImpl(TypeOfNumber.InternationalNumber,
          NumberingPlanIdentification.ISDNTelephoneNumberingPlan, "700007");
      ProtocolIdentifierImpl pi = new ProtocolIdentifierImpl(0);
      ValidityPeriodImpl vp = new ValidityPeriodImpl(100);
      DataCodingSchemeImpl dcs = new DataCodingSchemeImpl(0);
      UserDataImpl ud = new UserDataImpl("Hello, world !!!", dcs, null, null);
      SmsSubmitTpduImpl tpdu = new SmsSubmitTpduImpl(false, true, false, 55, da, pi, vp, ud);
      SmsSignalInfo smRPUI = new SmsSignalInfoImpl(tpdu, null);

      IMSI imsi2 = this.mapParameterFactory.createIMSI(Optional.ofNullable(imsi2String).orElse("25007123456789"));

      clientDialogSms.addMoForwardShortMessageRequest(smRPDA, smRPOA, smRPUI, null, imsi2);


      clientDialogSms.send();
    } catch (Exception e) {
      logger.error(e);
    }
  }

  public void initiateUpdateGprsLocation(String imsiString, String sgsnAddressString,
      String sgsnNumberString) {
    try {
      logger.info("Sending MAP Message");
      this.rateLimiterObj = RateLimiter.create(5000);
      MAPProvider clientMapProvider = this.mapClient.getMAPProvider();
      // preparing congestion control -- code not changing
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null
          || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn(
            "Outgoing congestion control: MAP load test client: networkIdState=" + networkIdState);
        Thread.sleep(3000);
      }

      this.rateLimiterObj.acquire();
      ///
      // create origination and destination address references
      AddressString origRef = clientMapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
      AddressString destRef = clientMapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");
      // sending a message
      MAPDialogMobility mapDialogMobility =
          clientMapProvider.getMAPServiceMobility().createNewDialog(
              MAPApplicationContext.getInstance(MAPApplicationContextName.gprsLocationUpdateContext,
                  MAPApplicationContextVersion.version3),
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), origRef,
              this.sccpServerSettings.getRoutingAddresses().get(1).getSccpAddress(), destRef);

      ISDNAddressString sgsnNumber = new ISDNAddressStringImpl(AddressNature.international_number,
          NumberingPlan.ISDN, sgsnNumberString);
      IMSI imsi = new IMSIImpl(imsiString);
      byte[] sgsnAddressByteArray = new BigInteger(sgsnAddressString, 16).toByteArray();
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
      boolean lcsCapabilitySetRelease9899 = true;
      boolean lcsCapabilitySetRelease4 = true;
      boolean lcsCapabilitySetRelease5 = true;
      boolean lcsCapabilitySetRelease6 = true;
      boolean lcsCapabilitySetRelease7 = false;
      SupportedLCSCapabilitySets supportedLCSCapabilitySets =
          new SupportedLCSCapabilitySetsImpl(lcsCapabilitySetRelease9899, lcsCapabilitySetRelease4,
              lcsCapabilitySetRelease5, lcsCapabilitySetRelease6, lcsCapabilitySetRelease7);
      SupportedFeatures supportedFeatures = null;
      boolean tAdsDataRetrieval = true;
      Boolean homogeneousSupportOfIMSVoiceOverPSSessions = null;
      SGSNCapability sgsnCapability = new SGSNCapabilityImpl(solsaSupportIndicator,
          extensionContainer, superChargerSupportedInServingNetworkEntity,
          gprsEnhancementsSupportIndicator, supportedCamelPhases, supportedLCSCapabilitySets,
          offeredCamel4CSIs, smsCallBarringSupportIndicator, supportedRATTypesIndicator,
          supportedFeatures, tAdsDataRetrieval, homogeneousSupportOfIMSVoiceOverPSSessions);
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

      Long invokeId = mapDialogMobility.addUpdateGprsLocationRequest(imsi, sgsnNumber, sgsnAddress,
          extensionContainer, sgsnCapability, informPreviousNetworkEntity, psLCSNotSupportedByUE,
          vGmlcAddress, addInfo, epsInfo, servingNodeTypeIndicator, skipSubscriberDataUpdate,
          usedRATType, gprsSubscriptionDataNotNeeded, nodeTypeIndicator, areaRestricted,
          ueReachableIndicator, epsSubscriptionDataNotNeeded, uesrvccCapability);
      logger.info(String.format("InvokeId = %d", invokeId));
      mapDialogMobility.send(); // issue?????
    } catch (Exception e) {
      logger.error("Error while sending MAP ATI:" + e);
      e.printStackTrace();
    }
  }

  public void simulateUpdateLocationRequest(String imsiString) {
    try {
      logger.info("Sending MAP updateLocation Request");
      this.rateLimiterObj = RateLimiter.create(5000);
      MAPProvider clientMapProvider = this.mapClient.getMAPProvider();
      // preparing congestion control -- code not changing
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null
          || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn(
            "Outgoing congestion control: MAP load test client: networkIdState=" + networkIdState);

        Thread.sleep(3000);

      }

      this.rateLimiterObj.acquire();
      clientMapProvider.getMAPServiceMobility().activate();
      this.mapParameterFactory = clientMapProvider.getMAPParameterFactory();

      MAPApplicationContext appCnt = null;

      appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.networkLocUpContext,
          MAPApplicationContextVersion.version3);

      MAPDialogMobility clientDialogMobility =
          clientMapProvider.getMAPServiceMobility().createNewDialog(appCnt,
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), null,
              this.sccpServerSettings.getRoutingAddresses().get(1).getSccpAddress(), null);

      IMSI imsi = this.mapParameterFactory.createIMSI(imsiString);
      ISDNAddressString mscNumber = this.mapParameterFactory.createISDNAddressString(
          AddressNature.international_number, NumberingPlan.ISDN, "8222333444");
      ISDNAddressString vlrNumber = this.mapParameterFactory.createISDNAddressString(
          AddressNature.network_specific_number, NumberingPlan.ISDN, "700000111");
      LMSI lmsi = this.mapParameterFactory.createLMSI(new byte[] {1, 2, 3, 4});
      IMEI imeisv = this.mapParameterFactory.createIMEI("987654321098765");
      ADDInfo addInfo = this.mapParameterFactory.createADDInfo(imeisv, false);
      clientDialogMobility.addUpdateLocationRequest(imsi, mscNumber, null, vlrNumber, lmsi, null,
          null, true, false, null, addInfo, null, false, true);

      clientDialogMobility.send(); 
    } catch (Exception e) {
      logger.error("Unable to process update location request. " , e);
      e.printStackTrace(); 
    }
  }

  public void sendAuthenticationInfo(String imsiString) {
    try {
      logger.info("Sending MAP updateLocation Request");

      this.rateLimiterObj = RateLimiter.create(5000);
      MAPProvider clientMapProvider = this.mapClient.getMAPProvider();
      // preparing congestion control -- code not changing
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null
          || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn(
            "Outgoing congestion control: MAP load test client: networkIdState=" + networkIdState);
        Thread.sleep(3000);
      }

      this.rateLimiterObj.acquire();
      clientMapProvider.getMAPServiceMobility().activate();
      this.mapParameterFactory = clientMapProvider.getMAPParameterFactory();

      MAPApplicationContext appCnt = null;

      appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.infoRetrievalContext,
          MAPApplicationContextVersion.version3);

      MAPDialogMobility clientDialogMobility =
          clientMapProvider.getMAPServiceMobility().createNewDialog(appCnt,
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), null,
              this.sccpServerSettings.getRoutingAddresses().get(1).getSccpAddress(), null);

      IMSI imsi = this.mapParameterFactory.createIMSI(imsiString);
      clientDialogMobility.addSendAuthenticationInfoRequest(imsi, 3, true, true, null, null,
          RequestingNodeType.sgsn, null, 5, false);
      clientDialogMobility.send();

    } catch (Exception e) {
      logger.error("Unable to process update location request. ", e);
    }
  }

  public void initiateProvideRoamingNumber(String imsiString) throws MAPException {

    try {
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      MAPProvider clientMapProvider = this.mapClient.getMAPProvider();
      this.rateLimiterObj = RateLimiter.create(5000);
      if (!(networkIdState == null
          || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn(
            "Outgoing congestion control: MAP load test client: networkIdState=" + networkIdState);

        Thread.sleep(3000);

      }
      this.rateLimiterObj.acquire();
      MAPParameterFactoryImpl mapFactory = new MAPParameterFactoryImpl();
      // First create Dialog
      AddressString origRef = clientMapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
      AddressString destRef = clientMapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");


      clientMapProvider.getMAPServiceCallHandling().activate();
      MAPApplicationContext appCnt = null;

      appCnt =
          MAPApplicationContext.getInstance(MAPApplicationContextName.roamingNumberEnquiryContext,
              MAPApplicationContextVersion.version3);

      MAPDialogCallHandling mapDialogMobility =
          clientMapProvider.getMAPServiceCallHandling().createNewDialog(appCnt,
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), origRef,
              this.sccpServerSettings.getRoutingAddresses().get(1).getSccpAddress(), destRef);
      ArrayList<MAPPrivateExtension> al = new ArrayList<>();
      al.add(mapFactory.createMAPPrivateExtension(new long[] {1, 2, 3, 4},
          new byte[] {11, 12, 13, 14, 15}));
      al.add(mapFactory.createMAPPrivateExtension(new long[] {1, 2, 3, 6}, null));
      al.add(mapFactory.createMAPPrivateExtension(new long[] {1, 2, 3, 5},
          new byte[] {21, 22, 23, 24, 25, 26}));
      IMSI imsi = new IMSIImpl(imsiString);
      ISDNAddressString mscNumber = new ISDNAddressStringImpl(AddressNature.international_number,
          NumberingPlan.ISDN, "22228");
      ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number,
          NumberingPlan.ISDN, "22227");
      LMSI lmsi = new LMSIImpl(new byte[] {0, 3, 98, 39});

      MAPExtensionContainer extensionContainerForExtSigInfo =
          mapFactory.createMAPExtensionContainer(al, new byte[] {31, 32, 33});
      byte[] dataTa = new byte[] {10, 20, 30, 40};
      SignalInfo signalInfo = new SignalInfoImpl(dataTa);
      ProtocolId protocolId = ProtocolId.gsm_0806;
      ExternalSignalInfo gsmBearerCapability =
          new ExternalSignalInfoImpl(signalInfo, protocolId, extensionContainerForExtSigInfo);
      ExternalSignalInfo networkSignalInfo =
          new ExternalSignalInfoImpl(signalInfo, protocolId, extensionContainerForExtSigInfo);

      boolean suppressionOfAnnouncement = false;
      ISDNAddressString gmscAddress = new ISDNAddressStringImpl(AddressNature.international_number,
          NumberingPlan.ISDN, "22226");
      boolean orInterrogation = false;
      boolean ccbsCall = false;
      boolean orNotSupportedInGMSC = false;
      boolean prePagingSupported = false;
      boolean longFTNSupported = false;
      boolean suppressVtCsi = false;
      boolean mtRoamingRetrySupported = false;
      ArrayList<LocationArea> locationAreas = new ArrayList<>();
      LACImpl lac = new LACImpl(123);
      LocationAreaImpl la = new LocationAreaImpl(lac);
      locationAreas.add(la);
      boolean mtrfIndicator = false;

      mapDialogMobility.addProvideRoamingNumberRequest(imsi, mscNumber, msisdn, lmsi,
          gsmBearerCapability, networkSignalInfo, suppressionOfAnnouncement, gmscAddress, null,
          orInterrogation, null, null, ccbsCall, null, null, orNotSupportedInGMSC,
          prePagingSupported, longFTNSupported, suppressVtCsi, null, mtRoamingRetrySupported, null,
          null, mtrfIndicator, null);

      mapDialogMobility.send();

    } catch (Exception e) {
      logger.error("Error while sending MAP updateLocation:", e);
    }
  }

  public void insertSubscriberDataRequest(String imsiString) {
    try {
      logger.info("Sending MAP updateLocation Request");
      this.rateLimiterObj = RateLimiter.create(5000);
      MAPProvider clientMapProvider = this.mapClient.getMAPProvider();
      // preparing congestion control -- code not changing
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null
          || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn(
            "Outgoing congestion control: MAP load test client: networkIdState=" + networkIdState);
        Thread.sleep(3000);

      }

      this.rateLimiterObj.acquire();
      clientMapProvider.getMAPServiceMobility().activate();
      this.mapParameterFactory = clientMapProvider.getMAPParameterFactory();

      MAPApplicationContext appCnt = null;

      appCnt =
          MAPApplicationContext.getInstance(MAPApplicationContextName.subscriberDataMngtContext,
              MAPApplicationContextVersion.version3);

      MAPDialogMobility clientDialogMobility =
          clientMapProvider.getMAPServiceMobility().createNewDialog(appCnt,
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), null,
              this.sccpServerSettings.getRoutingAddresses().get(1).getSccpAddress(), null);

      IMSI imsi = this.mapParameterFactory.createIMSI(imsiString);
      Category category = this.mapParameterFactory.createCategory(5);
      SubscriberStatus subscriberStatus = SubscriberStatus.operatorDeterminedBarring;
      ArrayList<ExtBearerServiceCode> bearerServiceList = new ArrayList<>();
      ExtBearerServiceCode extBearerServiceCode = this.mapParameterFactory
          .createExtBearerServiceCode(BearerServiceCodeValue.padAccessCA_9600bps);
      bearerServiceList.add(extBearerServiceCode);
      ArrayList<ExtTeleserviceCode> teleserviceList = new ArrayList<>();
      ExtTeleserviceCode extTeleservice = this.mapParameterFactory
          .createExtTeleserviceCode(TeleserviceCodeValue.allSpeechTransmissionServices);

      teleserviceList.add(extTeleservice);
      boolean roamingRestrictionDueToUnsupportedFeature = true;
      ArrayList<ExtSSInfo> provisionedSS = null;
      ODBData odbData = null;
      ArrayList<ZoneCode> regionalSubscriptionData = null;
      ArrayList<VoiceBroadcastData> vbsSubscriptionData = null;
      ArrayList<VoiceGroupCallData> vgcsSubscriptionData = null;
      VlrCamelSubscriptionInfo vlrCamelSubscriptionInfo = null;
      ISDNAddressString msisdn = this.mapParameterFactory
          .createISDNAddressString(AddressNature.international_number, NumberingPlan.ISDN, "22234");
      clientDialogMobility.addInsertSubscriberDataRequest(imsi, msisdn, category, subscriberStatus,
          bearerServiceList, teleserviceList, provisionedSS, odbData,
          roamingRestrictionDueToUnsupportedFeature, regionalSubscriptionData, vbsSubscriptionData,
          vgcsSubscriptionData, vlrCamelSubscriptionInfo);
      clientDialogMobility.send();

    } catch (Exception e) {
      logger.error("Unable to process update location request. ", e);
    }
  }
}
