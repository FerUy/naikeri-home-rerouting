package com.naikeri.prototype.map;

import java.util.ArrayList;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.MAPParameterFactoryImpl;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.MAPParameterFactory;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextName;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPServiceMobilityListener;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.AuthenticationQuintuplet;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.AuthenticationSetList;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.EpcAv;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.EpsAuthenticationSetList;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.QuintupletList;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.UEUsageType;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ForwardCheckSSIndicationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ResetRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.imei.CheckImeiRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.imei.CheckImeiResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.ExtSupportedFeatures;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedFeatures;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationResponse;
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
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.BearerServiceCodeValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.Category;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBearerServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtSSInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtTeleserviceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ODBData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ODBGeneralData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OfferedCamel4CSIs;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.RegionalSubscriptionResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SubscriberStatus;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SupportedCamelPhases;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TeleserviceCodeValue;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.VlrCamelSubscriptionInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.VoiceBroadcastData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.VoiceGroupCallData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ZoneCode;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SupplementaryCodeValue;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.service.mobility.authentication.AuthenticationQuintupletImpl;
import org.restcomm.protocols.ss7.map.service.mobility.authentication.AuthenticationSetListImpl;
import org.restcomm.protocols.ss7.map.service.mobility.authentication.EpcAvImpl;
import org.restcomm.protocols.ss7.map.service.mobility.authentication.EpsAuthenticationSetListImpl;
import org.restcomm.protocols.ss7.map.service.mobility.authentication.QuintupletListImpl;
import org.restcomm.protocols.ss7.map.service.mobility.authentication.UEUsageTypeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.ExtSupportedFeaturesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SupportedFeaturesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtTeleserviceCodeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.InsertSubscriberDataRequestImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ODBGeneralDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.OfferedCamel4CSIsImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SupportedCamelPhasesImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.SSCodeImpl;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

public class MapPrototypeMobility implements MAPServiceMobilityListener {

  private static final Logger logger = LogManager.getLogger(MapPrototypeMobility.class);

  private final MAPParameterFactory mapParameterFactory;

  public MapPrototypeMobility(MAPParameterFactory mapParameterFactory){
    this.mapParameterFactory = mapParameterFactory;
  }

  @Override
  public void onErrorComponent(MAPDialog mapDialog, Long invokeId, MAPErrorMessage mapErrorMessage) {
    // Auto-generated method stub

  }

  @Override
  public void onRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem, boolean isLocalOriginated) {
    // Auto-generated method stub

  }

  @Override
  public void onInvokeTimeout(MAPDialog mapDialog, Long invokeId) {
    // Auto-generated method stub

  }

  @Override
  public void onMAPMessage(MAPMessage mapMessage) {
    // Auto-generated method stub

  }

  @Override
  public void onUpdateLocationRequest(UpdateLocationRequest updateLocationReq) {
    // Playing the HLR: Update-Location is answered with one Insert-Subscriber-Data on the same
    // dialogue, for the IMSI in the request. The Update-Location result follows once the ISD is
    // acknowledged, in onInsertSubscriberDataResponse.
    MAPDialogMobility mapDialogMobility = updateLocationReq.getMAPDialog();
    if (logger.isInfoEnabled()) {
      logger.info("UpdateLocationRequest for DialogId={}", mapDialogMobility.getLocalDialogId());
    }
    try {
      // remembered so the Update-Location result answers the right invoke
      mapDialogMobility.setUserObject(updateLocationReq.getInvokeId());

      IMSI imsi = updateLocationReq.getImsi();
      Category category = this.mapParameterFactory.createCategory(5);
      SubscriberStatus subscriberStatus = SubscriberStatus.operatorDeterminedBarring;
      ArrayList<ExtBearerServiceCode> bearerServiceList = new ArrayList<>();
      bearerServiceList.add(this.mapParameterFactory.createExtBearerServiceCode(BearerServiceCodeValue.padAccessCA_9600bps));
      ArrayList<ExtTeleserviceCode> teleserviceList = new ArrayList<>();
      teleserviceList.add(this.mapParameterFactory.createExtTeleserviceCode(TeleserviceCodeValue.allSpeechTransmissionServices));
      boolean roamingRestrictionDueToUnsupportedFeature = true;
      ArrayList<ExtSSInfo> provisionedSS = null;
      ODBData odbData = null;
      ArrayList<ZoneCode> regionalSubscriptionData = null;
      ArrayList<VoiceBroadcastData> vbsSubscriptionData = null;
      ArrayList<VoiceGroupCallData> vgcsSubscriptionData = null;
      VlrCamelSubscriptionInfo vlrCamelSubscriptionInfo = null;
      ISDNAddressString msisdn = this.mapParameterFactory.createISDNAddressString(AddressNature.international_number, NumberingPlan.ISDN, "22234");
      mapDialogMobility.addInsertSubscriberDataRequest(imsi, msisdn, category, subscriberStatus,
          bearerServiceList, teleserviceList, provisionedSS, odbData,
          roamingRestrictionDueToUnsupportedFeature, regionalSubscriptionData,
          vbsSubscriptionData, vgcsSubscriptionData, vlrCamelSubscriptionInfo);
      mapDialogMobility.send();
    } catch (Exception e) {
      logger.error("Unable to process the Update-Location request", e);
    }
  }

  @Override
  public void onUpdateLocationResponse(UpdateLocationResponse ind) {
    // Auto-generated method stub

  }

  @Override
  public void onCancelLocationRequest(CancelLocationRequest request) {
    // Auto-generated method stub

  }

  @Override
  public void onCancelLocationResponse(CancelLocationResponse response) {
    // Auto-generated method stub

  }

  @Override
  public void onSendIdentificationRequest(SendIdentificationRequest request) {
    // Auto-generated method stub

  }

  @Override
  public void onSendIdentificationResponse(SendIdentificationResponse response) {
    // Auto-generated method stub

  }

  @Override
  public void onUpdateGprsLocationRequest(UpdateGprsLocationRequest request) {
    // Auto-generated method stub
    if (logger.isInfoEnabled()) {
      logger.info("UpdateGprsLocationRequest for DialogId={}", request.getMAPDialog().getLocalDialogId());
    }

    try {
      MAPDialogMobility mapDialogMobility = request.getMAPDialog();
      long invokeId = request.getInvokeId();
      mapDialogMobility.setUserObject(invokeId);

      ISDNAddressString hlrNumber = new ISDNAddressStringImpl(AddressNature.international_number,
          NumberingPlan.ISDN, "5982123000");
      MAPExtensionContainer extensionContainer = null;
      boolean addCapability = true;
      boolean sgsnMmeSeparationSupported = true;

      mapDialogMobility.addUpdateGprsLocationResponse(invokeId, hlrNumber, extensionContainer,
          addCapability, sgsnMmeSeparationSupported, false);

      // This will initiate the TC-BEGIN with INVOKE component
      mapDialogMobility.close(false);

    } catch (MAPException mapException) {
      logger.error("MAP Exception while processing onUpdateGprsLocationRequest ", mapException);
    } catch (Exception e) {
      logger.error("Exception while processing onUpdateGprsLocationRequest ", e);
    }

  }

  @Override
  public void onUpdateGprsLocationResponse(UpdateGprsLocationResponse response) {
    // Auto-generated method stub

  }

  @Override
  public void onPurgeMSRequest(PurgeMSRequest request) {
    // Auto-generated method stub

  }

  @Override
  public void onPurgeMSResponse(PurgeMSResponse response) {
    // Auto-generated method stub

  }

  @Override
  public void onSendAuthenticationInfoRequest(SendAuthenticationInfoRequest request) {
    // Auto-generated method stub
    if (logger.isInfoEnabled()) {
      logger.info("onUpdateGprsLocationRequest for DialogId={}", request.getMAPDialog().getLocalDialogId());
    }

    try {
      MAPDialogMobility mapDialogMobility = request.getMAPDialog();
      long invokeId = request.getInvokeId();
      mapDialogMobility.setUserObject(invokeId);

      byte[] rand;
      rand = new byte[] {(byte) 0xba, 0x73, 0x31, 0x2e, (byte) 0x8b, (byte) 0xa1, 0x19, 0x75, (byte) 0xe0,
          (byte) 0xe7, (byte) 0xae, 0x2b, (byte) 0xd1, 0x44, (byte) 0xa7, 0x75};
      byte[] xres = new byte[] {(byte) 0xe4, (byte) 0xb9, (byte) 0xca, 0x0c, 0x2b, 0x12, 0x37, (byte) 0xb6};
      byte[] ck = new byte[] {(byte) 0x81, (byte) 0xe8, 0x64, (byte) 0xf0, (byte) 0xc5, 0x0a, 0x53, 0x64, (byte) 0xda,
          (byte) 0xed, 0x49, 0x76, 0x03, (byte) 0xc9, (byte) 0xbf, 0x5d};
      byte[] ik = new byte[] {(byte) 0x92, 0x59, 0x62, 0x13, (byte) 0xf2, 0x43, 0x75, 0x69, (byte) 0x96, (byte) 0x9d, 0x26,
          0x0d, (byte) 0xac, 0x60, (byte) 0xbf, 0x6b};
      byte[] autn = new byte[] {0x5a, (byte) 0xcd, 0x63, 0x56, (byte) 0x83, (byte) 0xfe, (byte) 0x80, 0x00, (byte) 0xba,
          (byte) 0x95, (byte) 0xba, (byte) 0xae, 0x08, 0x0a, 0x30, 0x73};
      AuthenticationQuintuplet authenticationQuintuplet = new AuthenticationQuintupletImpl(rand, xres, ck, ik, autn);
      ArrayList<AuthenticationQuintuplet> authenticationQuintupletList = new ArrayList<>();
      authenticationQuintupletList.add(authenticationQuintuplet);
      QuintupletList quintupletList = new QuintupletListImpl(authenticationQuintupletList);
      AuthenticationSetList authenticationSetList = new AuthenticationSetListImpl(quintupletList);
      byte[] epsRand = new byte[] {(byte) 0xf6, (byte) 0xe2, (byte) 0xc3, (byte) 0xdc, (byte) 0xa4, (byte) 0xca,
          (byte) 0xae, (byte) 0x9e, 0x4c, (byte) 0xba, 0x0f, (byte) 0xd3, 0x42, 0x72, (byte) 0xee, 0x46};
      byte[] epsXres = new byte[] {0x1e, 0x42, (byte) 0xe6, 0x58, (byte) 0xce, (byte) 0x99, 0x33, (byte) 0xb6};
      byte[] epsAutn = new byte[] {(byte) 0xe9, 0x15, (byte) 0x97, (byte) 0x88, (byte) 0xbc, (byte) 0xeb, (byte) 0x80, 0x00,
          (byte) 0x81, 0x3f, (byte) 0xc0, 0x40, (byte) 0xff, 0x53, (byte) 0xd5, (byte) 0xfa};
      byte[] epsKasme = new byte[] {0x70, (byte) 0xad, (byte) 0x8c, (byte) 0xd7, (byte) 0x89, 0x28, (byte) 0xc2, (byte) 0xde,
          (byte) 0x97, (byte) 0xcf, (byte) 0xe7, (byte) 0xb8, (byte) 0xbf, 0x10, 0x40, (byte) 0xe9, (byte) 0xa4, (byte) 0xdd,
          0x79, (byte) 0x80, 0x5b, 0x54, 0x61, (byte) 0x95, (byte) 0xc2, (byte) 0xb6, 0x0c, (byte) 0xb4, (byte) 0xcc, 0x41, (byte) 0xbe, 0x47};
      EpcAv epcAuthVector = new EpcAvImpl(epsRand, epsXres, epsAutn, epsKasme, null);
      ArrayList<EpcAv> epcAvList = new ArrayList<>();
      epcAvList.add(epcAuthVector);
      EpsAuthenticationSetList epsAuthenticationSetList = new EpsAuthenticationSetListImpl(epcAvList);
      UEUsageType ueUsageType = new UEUsageTypeImpl(new byte[] {0, 0, 0, (byte) 0x80});

      logger.info("Sending authentication  request info");
      mapDialogMobility.addSendAuthenticationInfoResponse(invokeId, authenticationSetList, null, epsAuthenticationSetList, ueUsageType);
      // This will initiate the TC-BEGIN with INVOKE component
      mapDialogMobility.close(false);

    } catch (MAPException mapException) {
      logger.error("MAP Exception while processing onUpdateGprsLocationRequest ", mapException);
    } catch (Exception e) {
      logger.error("Exception while processing onUpdateGprsLocationRequest ", e);
    }

  }

  @Override
  public void onSendAuthenticationInfoResponse(SendAuthenticationInfoResponse ind) {
    // Auto-generated method stub

  }

  @Override
  public void onAuthenticationFailureReportRequest(AuthenticationFailureReportRequest ind) {
    // Auto-generated method stub

  }

  @Override
  public void onAuthenticationFailureReportResponse(AuthenticationFailureReportResponse ind) {
    // Auto-generated method stub

  }

  @Override
  public void onResetRequest(ResetRequest ind) {
    // Auto-generated method stub

  }

  @Override
  public void onForwardCheckSSIndicationRequest(ForwardCheckSSIndicationRequest ind) {
    // Auto-generated method stub

  }

  @Override
  public void onRestoreDataRequest(RestoreDataRequest ind) {
    // Auto-generated method stub

  }

  @Override
  public void onRestoreDataResponse(RestoreDataResponse ind) {
    // Auto-generated method stub

  }

  @Override
  public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest request) {
    // Auto-generated method stub

  }

  @Override
  public void onAnyTimeInterrogationResponse(AnyTimeInterrogationResponse response) {
    // Auto-generated method stub

  }

  @Override
  public void onAnyTimeSubscriptionInterrogationRequest(
      AnyTimeSubscriptionInterrogationRequest request) {
    // Auto-generated method stub

  }

  @Override
  public void onAnyTimeSubscriptionInterrogationResponse(
      AnyTimeSubscriptionInterrogationResponse response) {
    // Auto-generated method stub

  }

  @Override
  public void onAnyTimeModificationRequest(AnyTimeModificationRequest anyTimeModificationRequest) {

  }

  @Override
  public void onAnyTimeModificationResponse(AnyTimeModificationResponse anyTimeModificationResponse) {

  }

  @Override
  public void onProvideSubscriberInfoRequest(ProvideSubscriberInfoRequest request) {
    // Auto-generated method stub

  }

  @Override
  public void onProvideSubscriberInfoResponse(ProvideSubscriberInfoResponse response) {
    // Auto-generated method stub

  }

  @Override
  public void onInsertSubscriberDataRequest(InsertSubscriberDataRequest request) {
    // Auto-generated method stub
    if (logger.isInfoEnabled()) {
      logger.info("onInsertSubscriberDataRequest  for DialogId={}", request.getMAPDialog().getLocalDialogId());
    }
    try {
      MAPDialogMobility d = request.getMAPDialog();
      InsertSubscriberDataRequestImpl ind = (InsertSubscriberDataRequestImpl) request;
      ArrayList<ExtBearerServiceCode> bearerServiceList = ind.getBearerServiceList();
      ArrayList<ExtTeleserviceCode> teleserviceList = ind.getTeleserviceList();
      if (teleserviceList == null) {
        teleserviceList = new ArrayList<>();
      }
      ExtTeleserviceCode shortMessageMT_PP = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.shortMessageMT_PP);
      ExtTeleserviceCode shortMessageMO_PP = new ExtTeleserviceCodeImpl(TeleserviceCodeValue.shortMessageMO_PP);
      teleserviceList.add(shortMessageMT_PP);
      teleserviceList.add(shortMessageMO_PP);
      MAPExtensionContainer extensionContainer = ind.getExtensionContainer();
      ArrayList<SSCode> ssList = new ArrayList<>();
      SupplementaryCodeValue supplementaryCodeValue = getSupplementaryCodeValue();
      SSCode ssCode = new SSCodeImpl(supplementaryCodeValue);
      ssList.add(ssCode);
      ODBGeneralData odbGeneralData = getOdbGeneralData();
      RegionalSubscriptionResponse regionalSubscriptionResponse = RegionalSubscriptionResponse.networkNodeAreaRestricted;
      SupportedCamelPhases supportedCamelPhases = new SupportedCamelPhasesImpl(true, true, true, true);
      boolean oCsi = false;
      boolean dCsi = false;
      boolean vtCsi = false;
      boolean tCsi = false;
      boolean mtSMSCsi = true;
      boolean mgCsi = true;
      boolean psiEnhancements = true;
      OfferedCamel4CSIs offeredCamel4CSIs = new OfferedCamel4CSIsImpl(oCsi,dCsi,vtCsi,tCsi, mtSMSCsi, mgCsi, psiEnhancements);
      SupportedFeatures supportedFeatures = getSupportedFeatures();
      boolean unlicensedSpectrumAsSecondaryRAT = true;
      ExtSupportedFeatures extSupportedFeatures = new ExtSupportedFeaturesImpl(unlicensedSpectrumAsSecondaryRAT);
      d.addInsertSubscriberDataResponse(request.getInvokeId(), teleserviceList, bearerServiceList,
          ssList, odbGeneralData, regionalSubscriptionResponse, supportedCamelPhases,
          extensionContainer, offeredCamel4CSIs, supportedFeatures, extSupportedFeatures);
      // Inside an Update-Location dialogue the HLR still owes the Update-Location result, so the
      // dialogue stays open; a standalone Insert-Subscriber-Data dialogue ends with this result.
      if (d.getApplicationContext().getApplicationContextName() == MAPApplicationContextName.networkLocUpContext) {
        d.send();
      } else {
        d.close(false);
      }
    } catch (Exception e) {
      logger.error("Error while adding InsertSubscriberDataResponse", e);
    }
  }

  @Override
  public void onInsertSubscriberDataResponse(InsertSubscriberDataResponse response) {
    // Playing the HLR: an Insert-Subscriber-Data acknowledged inside an Update-Location dialogue
    // completes the location update, so the Update-Location result goes back and the dialogue ends.
    // A standalone Insert-Subscriber-Data dialogue was already ended by the VLR's result.
    MAPDialogMobility mapDialogMobility = response.getMAPDialog();
    if (logger.isInfoEnabled()) {
      logger.info("InsertSubscriberDataResponse for DialogId={}", mapDialogMobility.getLocalDialogId());
    }
    if (mapDialogMobility.getApplicationContext().getApplicationContextName() != MAPApplicationContextName.networkLocUpContext) {
      return;
    }
    Object updateLocationInvokeId = mapDialogMobility.getUserObject();
    if (!(updateLocationInvokeId instanceof Long)) {
      logger.warn("No Update-Location invoke to answer on DialogId={}", mapDialogMobility.getLocalDialogId());
      return;
    }
    try {
      ISDNAddressString hlrNumber = this.mapParameterFactory.createISDNAddressString(AddressNature.international_number, NumberingPlan.ISDN, "97254070001");
      mapDialogMobility.addUpdateLocationResponse((Long) updateLocationInvokeId, hlrNumber, null, false, false);
      mapDialogMobility.close(false);
    } catch (Exception e) {
      logger.error("Unable to send the Update-Location result", e);
    }
  }

  @Override
  public void onDeleteSubscriberDataRequest(DeleteSubscriberDataRequest request) {
    // Auto-generated method stub
  }

  @Override
  public void onDeleteSubscriberDataResponse(DeleteSubscriberDataResponse request) {
    // Auto-generated method stub
  }

  @Override
  public void onCheckImeiRequest(CheckImeiRequest request) {
    // Auto-generated method stub
  }

  @Override
  public void onCheckImeiResponse(CheckImeiResponse response) {
    // Auto-generated method stub
  }

  @Override
  public void onActivateTraceModeRequest_Mobility(ActivateTraceModeRequest_Mobility ind) {
    // Auto-generated method stub
  }

  @Override
  public void onActivateTraceModeResponse_Mobility(ActivateTraceModeResponse_Mobility ind) {
    // Auto-generated method stub
  }

  private static SupplementaryCodeValue getSupplementaryCodeValue() {
    SupplementaryCodeValue supplementaryCodeValue;
    Random rand = new Random();
    switch (rand.nextInt(72 + 1)) {
      case 1:
        supplementaryCodeValue = SupplementaryCodeValue.allLineIdentificationSS;
        break;
      case 2:
        supplementaryCodeValue = SupplementaryCodeValue.clip;
        break;
      case 3:
        supplementaryCodeValue = SupplementaryCodeValue.clir;
        break;
      case 4:
        supplementaryCodeValue = SupplementaryCodeValue.colp;
        break;
      case 5:
        supplementaryCodeValue = SupplementaryCodeValue.colr;
        break;
      case 6:
        supplementaryCodeValue = SupplementaryCodeValue.mci;
        break;
      case 7:
        supplementaryCodeValue = SupplementaryCodeValue.allNameIdentificationSS;
        break;
      case 8:
        supplementaryCodeValue = SupplementaryCodeValue.cnap;
        break;
      case 9:
        supplementaryCodeValue = SupplementaryCodeValue.allForwardingSS;
        break;
      case 10:
        supplementaryCodeValue = SupplementaryCodeValue.cfu;
        break;
      case 11:
        supplementaryCodeValue = SupplementaryCodeValue.allCondForwardingSS;
        break;
      case 12:
        supplementaryCodeValue = SupplementaryCodeValue.cfb;
        break;
      case 13:
        supplementaryCodeValue = SupplementaryCodeValue.cfnry;
        break;
      case 14:
        supplementaryCodeValue = SupplementaryCodeValue.cfnrc;
        break;
      case 15:
        supplementaryCodeValue = SupplementaryCodeValue.cd;
        break;
      case 16:
        supplementaryCodeValue = SupplementaryCodeValue.allCallOfferingSS;
        break;
      case 17:
        supplementaryCodeValue = SupplementaryCodeValue.ect;
        break;
      case 18:
        supplementaryCodeValue = SupplementaryCodeValue.mah;
        break;
      case 19:
        supplementaryCodeValue = SupplementaryCodeValue.allCallCompletionSS;
        break;
      case 20:
        supplementaryCodeValue = SupplementaryCodeValue.cw;
        break;
      case 21:
        supplementaryCodeValue = SupplementaryCodeValue.hold;
        break;
      case 22:
        supplementaryCodeValue = SupplementaryCodeValue.ccbs_A;
        break;
      case 23:
        supplementaryCodeValue = SupplementaryCodeValue.ccbs_B;
        break;
      case 24:
        supplementaryCodeValue = SupplementaryCodeValue.mc;
        break;
      case 25:
        supplementaryCodeValue = SupplementaryCodeValue.allMultiPartySS;
        break;
      case 26:
        supplementaryCodeValue = SupplementaryCodeValue.multiPTY;
        break;
      case 27:
        supplementaryCodeValue = SupplementaryCodeValue.allCommunityOfInterestSS;
        break;
      case 28:
        supplementaryCodeValue = SupplementaryCodeValue.cug;
        break;
      case 29:
        supplementaryCodeValue = SupplementaryCodeValue.allChargingSS;
        break;
      case 30:
        supplementaryCodeValue = SupplementaryCodeValue.aoci;
        break;
      case 31:
        supplementaryCodeValue = SupplementaryCodeValue.aocc;
        break;
      case 32:
        supplementaryCodeValue = SupplementaryCodeValue.allAdditionalInfoTransferSS;
        break;
      case 33:
        supplementaryCodeValue = SupplementaryCodeValue.uus1;
        break;
      case 34:
        supplementaryCodeValue = SupplementaryCodeValue.uus2;
        break;
      case 35:
        supplementaryCodeValue = SupplementaryCodeValue.uus3;
        break;
      case 36:
        supplementaryCodeValue = SupplementaryCodeValue.allCallRestrictionSS;
        break;
      case 37:
        supplementaryCodeValue = SupplementaryCodeValue.barringOfOutgoingCalls;
        break;
      case 38:
        supplementaryCodeValue = SupplementaryCodeValue.baoc;
        break;
      case 39:
        supplementaryCodeValue = SupplementaryCodeValue.boic;
        break;
      case 40:
        supplementaryCodeValue = SupplementaryCodeValue.boicExHC;
        break;
      case 41:
        supplementaryCodeValue = SupplementaryCodeValue.barringOfIncomingCalls;
        break;
      case 42:
        supplementaryCodeValue = SupplementaryCodeValue.baic;
        break;
      case 43:
        supplementaryCodeValue = SupplementaryCodeValue.bicRoam;
        break;
      case 44:
        supplementaryCodeValue = SupplementaryCodeValue.allPLMN_specificSS;
        break;
      case 45:
        supplementaryCodeValue = SupplementaryCodeValue.allCallPrioritySS;
        break;
      case 46:
        supplementaryCodeValue = SupplementaryCodeValue.emlpp;
        break;
      case 47:
        supplementaryCodeValue = SupplementaryCodeValue.allLCSPrivacyException;
        break;
      case 48:
        supplementaryCodeValue = SupplementaryCodeValue.universal;
        break;
      case 49:
        supplementaryCodeValue = SupplementaryCodeValue.callrelated;
        break;
      case 50:
        supplementaryCodeValue = SupplementaryCodeValue.callunrelated;
        break;
      case 51:
        supplementaryCodeValue = SupplementaryCodeValue.plmnoperator;
        break;
      case 52:
        supplementaryCodeValue = SupplementaryCodeValue.serviceType;
        break;
      case 53:
        supplementaryCodeValue = SupplementaryCodeValue.allMOLR_SS;
        break;
      case 54:
        supplementaryCodeValue = SupplementaryCodeValue.basicSelfLocation;
        break;
      case 55:
        supplementaryCodeValue = SupplementaryCodeValue.autonomousSelfLocation;
        break;
      case 56:
        supplementaryCodeValue = SupplementaryCodeValue.transferToThirdParty;
        break;
      default:
        supplementaryCodeValue = SupplementaryCodeValue.allSS;
        break;
    }
    return supplementaryCodeValue;
  }

  private static ODBGeneralData getOdbGeneralData() {
    boolean allOGCallsBarred= true;
    boolean internationalOGCallsBarred = true;
    boolean internationalOGCallsNotToHPLMNCountryBarred= true;
    boolean premiumRateInformationOGCallsBarred = false;
    boolean premiumRateEntertainmentOGCallsBarred= true;
    boolean ssAccessBarred= true;
    boolean interzonalOGCallsBarred = true;
    boolean interzonalOGCallsNotToHPLMNCountryBarred= true;
    boolean interzonalOGCallsAndInternationalOGCallsNotToHPLMNCountryBarred = true;
    boolean allECTBarred= true;
    boolean chargeableECTBarred= true;
    boolean internationalECTBarred = true;
    boolean interzonalECTBarred= true;
    boolean doublyChargeableECTBarred= true;
    boolean multipleECTBarred = true;
    boolean allPacketOrientedServicesBarred= true;
    boolean roamerAccessToHPLMNAPBarred= false;
    boolean roamerAccessToVPLMNAPBarred = false;
    boolean roamingOutsidePLMNOGCallsBarred= false;
    boolean allICCallsBarred= true;
    boolean roamingOutsidePLMNICCallsBarred = true;
    boolean roamingOutsidePLMNICountryICCallsBarred= true;
    boolean roamingOutsidePLMNBarred = false;
    boolean roamingOutsidePLMNCountryBarred= false;
    boolean registrationAllCFBarred= true;
    boolean registrationCFNotToHPLMNBarred = true;
    boolean registrationInterzonalCFBarred= true;
    boolean registrationInterzonalCFNotToHPLMNBarred = false;
    boolean registrationInternationalCFBarred = true;
    return new ODBGeneralDataImpl(allOGCallsBarred, internationalOGCallsBarred,
        internationalOGCallsNotToHPLMNCountryBarred, premiumRateInformationOGCallsBarred, premiumRateEntertainmentOGCallsBarred,
        ssAccessBarred, interzonalOGCallsBarred, interzonalOGCallsNotToHPLMNCountryBarred,
        interzonalOGCallsAndInternationalOGCallsNotToHPLMNCountryBarred, allECTBarred, chargeableECTBarred,
        internationalECTBarred, interzonalECTBarred, doublyChargeableECTBarred, multipleECTBarred,
        allPacketOrientedServicesBarred, roamerAccessToHPLMNAPBarred, roamerAccessToVPLMNAPBarred,
        roamingOutsidePLMNOGCallsBarred, allICCallsBarred, roamingOutsidePLMNICCallsBarred,
        roamingOutsidePLMNICountryICCallsBarred, roamingOutsidePLMNBarred,
        roamingOutsidePLMNCountryBarred, registrationAllCFBarred, registrationCFNotToHPLMNBarred,
        registrationInterzonalCFBarred, registrationInterzonalCFNotToHPLMNBarred, registrationInternationalCFBarred);
  }

  private static SupportedFeatures getSupportedFeatures() {
    boolean odbAllApn = false;
    boolean odbHPLMNApn = false;
    boolean odbVPLMNApn = false;
    boolean odbAllOg = false;
    boolean odbAllInternationalOg = false;
    boolean odbAllIntOgNotToHPLMNCountry = false;
    boolean odbAllInterzonalOg = false;
    boolean odbAllInterzonalOgNotToHPLMNCountry = false;
    boolean odbAllInterzonalOgandInternatOgNotToHPLMNCountry = false;
    boolean regSub = false;
    boolean trace = false;
    boolean lcsAllPrivExcep = true;
    boolean lcsUniversal = true;
    boolean lcsCallSessionRelated = true;
    boolean lcsCallSessionUnrelated = true;
    boolean lcsPLMNOperator = true;
    boolean lcsServiceType = true;
    boolean lcsAllMOLRSS = true;
    boolean lcsBasicSelfLocation = true;
    boolean lcsAutonomousSelfLocation = true;
    boolean lcsTransferToThirdParty = true;
    boolean smMoPp = true;
    boolean barringOutgoingCalls = true;
    boolean baoc = true;
    boolean boic = true;
    boolean boicExHC = true;
    boolean localTimeZoneRetrieval = true;
    boolean additionalMsisdn = true;
    boolean smsInMME = true;
    boolean smsInSGSN = true;
    boolean ueReachabilityNotification = true;
    boolean stateLocationInformationRetrieval = true;
    boolean partialPurge = true;
    boolean gddInSGSN = true;
    boolean sgsnCAMELCapability = true;
    boolean pcscfRestoration = true;
    boolean dedicatedCoreNetworks = true;
    boolean nonIPPDNTypeAPNs = true;
    boolean nonIPPDPTypeAPNs = true;
    boolean nrAsSecondaryRAT = true;
    return new SupportedFeaturesImpl(odbAllApn, odbHPLMNApn, odbVPLMNApn, odbAllOg, odbAllInternationalOg,
        odbAllIntOgNotToHPLMNCountry, odbAllInterzonalOg, odbAllInterzonalOgNotToHPLMNCountry,
        odbAllInterzonalOgandInternatOgNotToHPLMNCountry, regSub, trace, lcsAllPrivExcep, lcsUniversal,
        lcsCallSessionRelated, lcsCallSessionUnrelated, lcsPLMNOperator, lcsServiceType, lcsAllMOLRSS,
        lcsBasicSelfLocation, lcsAutonomousSelfLocation, lcsTransferToThirdParty, smMoPp, barringOutgoingCalls, baoc,
        boic, boicExHC, localTimeZoneRetrieval, additionalMsisdn, smsInMME, smsInSGSN, ueReachabilityNotification,
        stateLocationInformationRetrieval, partialPurge, gddInSGSN, sgsnCAMELCapability,
        pcscfRestoration, dedicatedCoreNetworks, nonIPPDNTypeAPNs, nonIPPDPTypeAPNs,
        nrAsSecondaryRAT);
  }
}
