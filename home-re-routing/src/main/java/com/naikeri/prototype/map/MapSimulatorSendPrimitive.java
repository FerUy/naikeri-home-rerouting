package com.naikeri.prototype.map;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import com.google.common.util.concurrent.RateLimiter;
import com.naikeri.sgw.impl.settings.sccp.SccpSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.MAPParameterFactoryImpl;
import org.restcomm.protocols.ss7.map.MAPStackImpl;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContext;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextName;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParameterFactory;
import org.restcomm.protocols.ss7.map.api.MAPProvider;
import org.restcomm.protocols.ss7.map.api.primitives.*;
import org.restcomm.protocols.ss7.map.api.service.callhandling.MAPDialogCallHandling;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.RequestingNodeType;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ADDInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.EPSInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ExtSupportedFeatures;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.LocationArea;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.NetworkNodeDiameterAddress;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PagingArea;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SGSNCapability;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SMSRegisterRequest;
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
import org.restcomm.protocols.ss7.map.api.service.sms.*;
import org.restcomm.protocols.ss7.map.api.smstpdu.AbsoluteTimeStamp;
import org.restcomm.protocols.ss7.map.api.smstpdu.AddressField;
import org.restcomm.protocols.ss7.map.api.smstpdu.CharacterSet;
import org.restcomm.protocols.ss7.map.api.smstpdu.DataCodingScheme;
import org.restcomm.protocols.ss7.map.api.smstpdu.NumberingPlanIdentification;
import org.restcomm.protocols.ss7.map.api.smstpdu.ProtocolIdentifier;
import org.restcomm.protocols.ss7.map.api.smstpdu.SmsDeliverTpdu;
import org.restcomm.protocols.ss7.map.api.smstpdu.TypeOfNumber;
import org.restcomm.protocols.ss7.map.api.smstpdu.UserData;
import org.restcomm.protocols.ss7.map.api.smstpdu.UserDataHeader;
import org.restcomm.protocols.ss7.map.primitives.*;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.*;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SupportedCamelPhasesImpl;
import org.restcomm.protocols.ss7.map.service.sms.CorrelationIDImpl;
import org.restcomm.protocols.ss7.map.service.sms.SipUriImpl;
import org.restcomm.protocols.ss7.map.service.sms.SmsSignalInfoImpl;
import org.restcomm.protocols.ss7.map.smstpdu.AbsoluteTimeStampImpl;
import org.restcomm.protocols.ss7.map.smstpdu.AddressFieldImpl;
import org.restcomm.protocols.ss7.map.smstpdu.ApplicationPortAddressing16BitAddressImpl;
import org.restcomm.protocols.ss7.map.smstpdu.DataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.smstpdu.ProtocolIdentifierImpl;
import org.restcomm.protocols.ss7.map.smstpdu.SmsDeliverTpduImpl;
import org.restcomm.protocols.ss7.map.smstpdu.SmsSubmitTpduImpl;
import org.restcomm.protocols.ss7.map.smstpdu.UserDataHeaderImpl;
import org.restcomm.protocols.ss7.map.smstpdu.UserDataImpl;
import org.restcomm.protocols.ss7.map.smstpdu.ValidityPeriodImpl;
import org.restcomm.protocols.ss7.sccp.NetworkIdState;

public class MapSimulatorSendPrimitive {

  private RateLimiter rateLimiterObj;
  private static final Logger logger = LogManager.getLogger(MapSimulatorSendPrimitive.class.getName());

  private final SccpSettings sccpClientSettings;
  private final SccpSettings sccpServerSettings;
  private final MAPStackImpl mapClient;
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
      logger.debug("Sending Mt Forward SM for IMSI = {}", imsiString);
      this.mapClient.getMAPProvider().getMAPServiceSms().activate();

      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control: MAP load test client: networkIdState={}", networkIdState);
        Thread.sleep(3000);
      }

      this.rateLimiterObj.acquire();

      MAPApplicationContext appCnt;
      appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.shortMsgMTRelayContext, MAPApplicationContextVersion.version3);
      AddressString originReference = this.mapParameterFactory.createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "31628968300");
      AddressString destReference = this.mapParameterFactory.createAddressString(AddressNature.international_number, NumberingPlan.land_mobile, "204208300008002");

      MAPDialogSms clientDialogSms =
          this.mapClient.getMAPProvider().getMAPServiceSms().createNewDialog(appCnt,
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), originReference,
              this.sccpClientSettings.getRoutingAddresses().get(1).getSccpAddress(), destReference);
      SM_RP_DA smRPDA = this.mapParameterFactory.createSM_RP_DA(this.mapParameterFactory.createIMSI(imsiString));
      AddressString msisdn1 = this.mapParameterFactory
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "111222333");
      SM_RP_OA smRPOA = this.mapParameterFactory.createSM_RP_OA_ServiceCentreAddressOA(msisdn1);
      AddressField originatingAddress = new AddressFieldImpl(TypeOfNumber.Alphanumeric, NumberingPlanIdentification.Unknown, "447");
      ZonedDateTime now = ZonedDateTime.now();
      AbsoluteTimeStamp serviceCentreTimeStamp = getAbsoluteTimeStamp(now);
      int dcsVal = 4; // 0 = GSM7, 4 = GSM8, 8 = UCS2
      DataCodingScheme dcs = new DataCodingSchemeImpl(dcsVal);
      UserDataHeader udh = null;
      if (dcs.getCharacterSet() == CharacterSet.GSM8) {
        ApplicationPortAddressing16BitAddressImpl apa16 = new ApplicationPortAddressing16BitAddressImpl(16020, 0);
        udh = new UserDataHeaderImpl();
        udh.addInformationElement(apa16);
      }
      boolean moreMessagesToSend = false;
      boolean forwardedOrSpawned = false;
      boolean replyPathExists = false;
      boolean statusReportIndication = false;
      Charset gsm8Charset = StandardCharsets.UTF_8;
      UserData userData = new UserDataImpl("MT-SMS text", dcs, udh, gsm8Charset);
      ProtocolIdentifier pi = new ProtocolIdentifierImpl(0);

      Integer smDeliveryTimer = 60;
      boolean smsOverIPOnlyIndicator = true;
      DiameterIdentity gmscName = new DiameterIdentityImpl("msc04.mme.epc.mnc002.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
      DiameterIdentity gmscRealm = new DiameterIdentityImpl("epc.mnc002.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
      SmsDeliverTpdu tpdu = new SmsDeliverTpduImpl(moreMessagesToSend, forwardedOrSpawned, replyPathExists, statusReportIndication, originatingAddress, pi, serviceCentreTimeStamp, userData);
      SmsSignalInfo sm_RP_UI = new SmsSignalInfoImpl(tpdu, gsm8Charset);
      ZonedDateTime utcNow = now.withZoneSameInstant(ZoneOffset.UTC);
      ZonedDateTime utcDeadline = utcNow.plusDays(1);
      Time smDeliveryStartTime = new TimeImpl(utcNow.getYear(), utcNow.getMonthValue(), utcNow.getDayOfMonth(),
              utcNow.getHour(), utcNow.getMinute(), utcNow.getSecond());
      Time maximumRetransmissionTime = new TimeImpl(utcDeadline.getYear(), utcDeadline.getMonthValue(),
              utcDeadline.getDayOfMonth(), utcDeadline.getHour(), utcDeadline.getMinute(), utcDeadline.getSecond());
      ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number,
              NumberingPlan.ISDN, "59899077937");
      String uriA = msisdn.getAddress() + "@restcomm.org";
      SipUri sipUriA = new SipUriImpl(uriA.getBytes(StandardCharsets.UTF_8));
      SipUri sipUriB = new SipUriImpl("mtLoadTest@restcomm.org".getBytes(StandardCharsets.UTF_8));
      CorrelationID correlationID = new CorrelationIDImpl(new IMSIImpl(imsiString), sipUriA, sipUriB);
      ISDNAddressString smsGmscAddress = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "5989900123");
      NetworkNodeDiameterAddress smsGmscDiameterAddress = new NetworkNodeDiameterAddressImpl(gmscName, gmscRealm);

      clientDialogSms.addMtForwardShortMessageRequest(smRPDA, smRPOA, sm_RP_UI, moreMessagesToSend, null, smDeliveryTimer,
              smDeliveryStartTime, smsOverIPOnlyIndicator, correlationID, maximumRetransmissionTime, smsGmscAddress, smsGmscDiameterAddress);

      clientDialogSms.send();
      logger.debug("Message sent successfully");
    } catch (Exception e) {
      logger.error("Error occurred", e);
    }
  }

  private static AbsoluteTimeStamp getAbsoluteTimeStamp(ZonedDateTime zonedDateTime) {
    int quarterHours = zonedDateTime.getOffset().getTotalSeconds() / 900;
    return new AbsoluteTimeStampImpl(zonedDateTime.getYear() - 2000, zonedDateTime.getMonthValue(), zonedDateTime.getDayOfMonth(),
            zonedDateTime.getHour(), zonedDateTime.getMinute(), zonedDateTime.getSecond(), quarterHours);
  }

  public void sendMoForwardSm(String imstring, String imsi2String) {
    try {
      this.mapClient.getMAPProvider().getMAPServiceSms().activate();
      NetworkIdState networkIdState = this.mapClient.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control: MAP load test client: networkIdState={}", networkIdState);
        Thread.sleep(3000);
      }

      this.rateLimiterObj.acquire();
      MAPApplicationContext appCnt;

      appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.shortMsgMORelayContext, MAPApplicationContextVersion.version3);

      AddressString orgiReference = this.mapParameterFactory.createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "31628968300");
      AddressString destReference = this.mapParameterFactory.createAddressString(AddressNature.international_number, NumberingPlan.land_mobile, "204208300008002");

      MAPDialogSms clientDialogSms =
          this.mapClient.getMAPProvider().getMAPServiceSms().createNewDialog(appCnt,
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), orgiReference,
              this.sccpClientSettings.getRoutingAddresses().get(1).getSccpAddress(), destReference);
      // clientDialogSms.setExtentionContainer(MAPExtensionContainerTest.GetTestExtensionContainer())

      IMSI imsi1 = this.mapParameterFactory.createIMSI(imstring);
      SM_RP_DA smRPDA = this.mapParameterFactory.createSM_RP_DA(imsi1);
      ISDNAddressString msisdn1 = this.mapParameterFactory.createISDNAddressString(AddressNature.international_number, NumberingPlan.ISDN, "111222333");
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

      clientDialogSms.addMoForwardShortMessageRequest(smRPDA, smRPOA, smRPUI, null, imsi2, null, null);

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
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control: MAP load test client: networkIdState={}", networkIdState);
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
              this.sccpClientSettings.getRoutingAddresses().get(1).getSccpAddress(), destRef);

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
      SupportedRATTypes supportedRATTypesIndicator = new SupportedRATTypesImpl(true, true, false, false, true, false);
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

      Long invokeId = mapDialogMobility.addUpdateGprsLocationRequest(imsi, sgsnNumber, sgsnAddress,
          extensionContainer, sgsnCapability, informPreviousNetworkEntity, psLCSNotSupportedByUE,
          vGmlcAddress, addInfo, epsInfo, servingNodeTypeIndicator, skipSubscriberDataUpdate,
          usedRATType, gprsSubscriptionDataNotNeeded, nodeTypeIndicator, areaRestricted,
          ueReachableIndicator, epsSubscriptionDataNotNeeded, uesrvccCapability, ePLMNList,
          mmeNumberForMTSMS, smsRegisterRequest, smsOnly, sgsnName, sgsnRealm, lgdSupportIndicator,
          removalOfMMERegistrationForSMS, adjacentPLMNList);
      logger.info("InvokeId = {}", invokeId);
      mapDialogMobility.send(); // issue?????
    } catch (Exception e) {
      logger.error("Error while sending MAP ATI:{}", String.valueOf(e));
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
        logger.warn("Outgoing congestion control: MAP load test client: networkIdState={}", networkIdState);

        Thread.sleep(3000);

      }

      this.rateLimiterObj.acquire();
      clientMapProvider.getMAPServiceMobility().activate();
      this.mapParameterFactory = clientMapProvider.getMAPParameterFactory();

      MAPApplicationContext appCnt;

      appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.networkLocUpContext,
          MAPApplicationContextVersion.version3);

      MAPDialogMobility clientDialogMobility =
          clientMapProvider.getMAPServiceMobility().createNewDialog(appCnt,
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), null,
              this.sccpClientSettings.getRoutingAddresses().get(1).getSccpAddress(), null);

      IMSI imsi = this.mapParameterFactory.createIMSI(imsiString);
      ISDNAddressString mscNumber = this.mapParameterFactory.createISDNAddressString(
          AddressNature.international_number, NumberingPlan.ISDN, "8222333444");
      ISDNAddressString vlrNumber = this.mapParameterFactory.createISDNAddressString(
          AddressNature.network_specific_number, NumberingPlan.ISDN, "700000111");
      LMSI lmsi = this.mapParameterFactory.createLMSI(new byte[] {1, 2, 3, 4});
      IMEI imeisv = this.mapParameterFactory.createIMEI("987654321098765");
      ADDInfo addInfo = this.mapParameterFactory.createADDInfo(imeisv, false);
      PagingArea pagingArea = null;
      boolean skipSubscriberDataUpdate = false;
      boolean restorationIndicator = false;
      ArrayList<PlmnId> ePLMNList = null;
      NetworkNodeDiameterAddress mmeDiameterAddress = null;
      clientDialogMobility.addUpdateLocationRequest(imsi, mscNumber, null, vlrNumber, lmsi, null,
          null, true, false, null, addInfo, pagingArea, skipSubscriberDataUpdate,
          restorationIndicator, ePLMNList, mmeDiameterAddress);

      clientDialogMobility.send();
    } catch (Exception e) {
      logger.error("Unable to process update location request. " , e);
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
        logger.warn("Outgoing congestion control: MAP load test client: networkIdState={}", networkIdState);
        Thread.sleep(3000);
      }

      this.rateLimiterObj.acquire();
      clientMapProvider.getMAPServiceMobility().activate();
      this.mapParameterFactory = clientMapProvider.getMAPParameterFactory();

      MAPApplicationContext appCnt;

      appCnt = MAPApplicationContext.getInstance(MAPApplicationContextName.infoRetrievalContext,
          MAPApplicationContextVersion.version3);

      MAPDialogMobility clientDialogMobility =
          clientMapProvider.getMAPServiceMobility().createNewDialog(appCnt,
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), null,
              this.sccpClientSettings.getRoutingAddresses().get(1).getSccpAddress(), null);

      IMSI imsi = this.mapParameterFactory.createIMSI(imsiString);
      clientDialogMobility.addSendAuthenticationInfoRequest(imsi, 3, true, true, null, null,
          RequestingNodeType.sgsn, null, 5, false, false);
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
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control: MAP load test client: networkIdState={}", networkIdState);

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
      MAPApplicationContext appCnt;

      appCnt =
          MAPApplicationContext.getInstance(MAPApplicationContextName.roamingNumberEnquiryContext,
              MAPApplicationContextVersion.version3);

      MAPDialogCallHandling mapDialogMobility =
          clientMapProvider.getMAPServiceCallHandling().createNewDialog(appCnt,
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), origRef,
              this.sccpClientSettings.getRoutingAddresses().get(1).getSccpAddress(), destRef);
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
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control: MAP load test client: networkIdState={}", networkIdState);
        Thread.sleep(3000);

      }

      this.rateLimiterObj.acquire();
      clientMapProvider.getMAPServiceMobility().activate();
      this.mapParameterFactory = clientMapProvider.getMAPParameterFactory();

      MAPApplicationContext appCnt;

      appCnt =
          MAPApplicationContext.getInstance(MAPApplicationContextName.subscriberDataMngtContext,
              MAPApplicationContextVersion.version3);

      MAPDialogMobility clientDialogMobility =
          clientMapProvider.getMAPServiceMobility().createNewDialog(appCnt,
              this.sccpClientSettings.getRoutingAddresses().get(0).getSccpAddress(), null,
              this.sccpClientSettings.getRoutingAddresses().get(1).getSccpAddress(), null);

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
