package com.naikeri.prototype.map;

import java.util.ArrayList;
import java.util.Random;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.MAPParameterFactoryImpl;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.MAPParameterFactory;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
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
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationResponse;
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
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.InsertSubscriberDataRequestImpl;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

public class MapPrototypeMobility implements MAPServiceMobilityListener {
  private static final Logger logger = Logger.getLogger(MapPrototypeMobility.class);

 
  private MAPParameterFactory mapParameterFactory;
  private Integer numberOfTest = 0;
  private Random rand = new Random();

  public MapPrototypeMobility(MAPParameterFactory mapParameterFactory){
    this.mapParameterFactory = mapParameterFactory;
  }
  
  @Override
  public void onErrorComponent(MAPDialog mapDialog, Long invokeId,
      MAPErrorMessage mapErrorMessage) {
    // Auto-generated method stub

  }

  @Override
  public void onRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem,
      boolean isLocalOriginated) {
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
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("UpdateLocationRequest for DialogId=%d",
          updateLocationReq.getMAPDialog().getLocalDialogId()));
    }
    if (logger.isInfoEnabled()) {
      logger.info(String.format("UpdateLocationRequest for DialogId=%d",
          updateLocationReq.getMAPDialog().getLocalDialogId()));
    }
    // initiate a new call with the ISD
    try {
      MAPDialogMobility mapDialogMobility = updateLocationReq.getMAPDialog();
      long invokeId = updateLocationReq.getInvokeId();
      mapDialogMobility.setUserObject(invokeId);


      /////////
      IMSI imsi = this.mapParameterFactory.createIMSI("425016871012345");
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
      ////////
      for (int i = 0; i < 3; i++) {
        mapDialogMobility.addInsertSubscriberDataRequest(imsi, msisdn, category, subscriberStatus,
            bearerServiceList, teleserviceList, provisionedSS, odbData,
            roamingRestrictionDueToUnsupportedFeature, regionalSubscriptionData,
            vbsSubscriptionData, vgcsSubscriptionData, vlrCamelSubscriptionInfo);
        mapDialogMobility.send();
        Thread.sleep(10000);
      }
    } catch (Exception e) {
      logger.error("Unable to process update location request. " + e.getMessage());
      e.printStackTrace();
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
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("UpdateGprsLocationRequest for DialogId=%d",
          request.getMAPDialog().getLocalDialogId()));
    }
    if (logger.isInfoEnabled()) {
      logger.info(String.format("UpdateGprsLocationRequest for DialogId=%d",
          request.getMAPDialog().getLocalDialogId()));
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
          addCapability, sgsnMmeSeparationSupported);

      // This will initiate the TC-BEGIN with INVOKE component
      mapDialogMobility.send();

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
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("onUpdateGprsLocationRequest for DialogId=%d",
          request.getMAPDialog().getLocalDialogId()));
    }
    if (logger.isInfoEnabled()) {
      logger.info(String.format("onUpdateGprsLocationRequest for DialogId=%d",
          request.getMAPDialog().getLocalDialogId()));
    }

    try {
      MAPDialogMobility mapDialogMobility = request.getMAPDialog();
      long invokeId = request.getInvokeId();
      mapDialogMobility.setUserObject(invokeId);

      MAPExtensionContainer extensionContainer = null;
      logger.info("Sending authenticationrequestinfo. ;");
      mapDialogMobility.addSendAuthenticationInfoResponse(invokeId, null, extensionContainer, null);
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
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("onInsertSubscriberDataRequest  for DialogId=%d",
          request.getMAPDialog().getLocalDialogId()));
    } else {
      logger.info(String.format("onInsertSubscriberDataRequest  for DialogId=%d",
          request.getMAPDialog().getLocalDialogId()));
    }
    try {
      MAPDialogMobility d = request.getMAPDialog();
      InsertSubscriberDataRequestImpl ind = (InsertSubscriberDataRequestImpl) request;
      ArrayList<ExtBearerServiceCode> bearerServiceList = ind.getBearerServiceList();
      ArrayList<ExtTeleserviceCode> teleserviceList = ind.getTeleserviceList();
      MAPExtensionContainer extensionContainer = ind.getExtensionContainer();
      ArrayList<SSCode> ssList = null;
      ODBGeneralData odbGeneralData = null;
      RegionalSubscriptionResponse regionalSubscriptionResponse = null;
      SupportedCamelPhases supportedCamelPhases = null;
      OfferedCamel4CSIs offeredCamel4CSIs = null;
      SupportedFeatures supportedFeatures = null;
      numberOfTest = 0;
      d.addInsertSubscriberDataResponse(ind.getInvokeId(), teleserviceList, bearerServiceList,
          ssList, odbGeneralData, regionalSubscriptionResponse, supportedCamelPhases,
          extensionContainer, offeredCamel4CSIs, supportedFeatures);
      // a random close or send
      if (rand.nextBoolean()) {
        if (rand.nextBoolean()) {
          d.close(false);
        } else {
          d.close(true);
        }
      } else {
        d.send();
      }
    } catch (Exception e) {
      logger.error("Error while adding InsertSubscriberDataResponse", e);
    }
  }

  @Override
  public void onInsertSubscriberDataResponse(InsertSubscriberDataResponse request) {
    // Auto-generated method stub
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("InsertSubscriberDataResponse for DialogId=%d",
          request.getMAPDialog().getLocalDialogId()));
    }
    if (logger.isInfoEnabled()) {
      logger.info(String.format("InsertSubscriberDataResponse for DialogId=%d",
          request.getMAPDialog().getLocalDialogId()));
    }
    try {
      MAPDialogMobility mapDialogMobility = request.getMAPDialog();
      long invokeId = request.getInvokeId();
      if (logger.isDebugEnabled()) {
        logger.debug(String.format("onInsertSubscriberDataResponse for DialogId=%d",
            request.getMAPDialog().getLocalDialogId()));
      }
      if (logger.isInfoEnabled()) {
        logger.info(String.format("onInsertSubscriberDataResponse for DialogId=%d",
            request.getMAPDialog().getLocalDialogId()));
      }

      try {
        mapDialogMobility.setUserObject(invokeId);
        MAPParameterFactoryImpl mapFactory = new MAPParameterFactoryImpl();
        ISDNAddressString msisdn = mapFactory.createISDNAddressString(
            AddressNature.international_number, NumberingPlan.ISDN, "91795244816080");
        IMSI imsi = this.mapParameterFactory.createIMSI("425016871012345");
        Category category = mapFactory.createCategory(5);
        SubscriberStatus subscriberStatus = SubscriberStatus.operatorDeterminedBarring;
        ArrayList<ExtBearerServiceCode> bearerserviceList = new ArrayList<>();
        ExtBearerServiceCode extBearerServiceCode =
            mapFactory.createExtBearerServiceCode(BearerServiceCodeValue.padAccessCA_9600bps);
        bearerserviceList.add(extBearerServiceCode);
        ArrayList<ExtTeleserviceCode> teleserviceList = new ArrayList<>();
        ExtTeleserviceCode extTeleservice =
            mapFactory.createExtTeleserviceCode(TeleserviceCodeValue.allSpeechTransmissionServices);
        teleserviceList.add(extTeleservice);
        ArrayList<ExtSSInfo> provisionedSS = null;
        ODBData odbData = null;
        ArrayList<ZoneCode> regionalSubscriptionData = null;
        ArrayList<VoiceBroadcastData> vbsSubscriptionData = null;
        ArrayList<VoiceGroupCallData> vgcsSubscriptionData = null;
        VlrCamelSubscriptionInfo vlrCamelSubscriptionInfo = null;

        if (request.getSupportedCamelPhases() != null
            && request.getSupportedCamelPhases().getPhase4Supported()) {
          logger.info("supported pahse " + request.getSupportedCamelPhases().getPhase4Supported());
          mapDialogMobility.addInsertSubscriberDataResponse(request.getInvokeId(), teleserviceList,
              bearerserviceList, null, null, null);
          mapDialogMobility.close(false);
          numberOfTest = 0;
        } else {
          numberOfTest--;
          mapDialogMobility.addInsertSubscriberDataRequest(imsi, msisdn, category, subscriberStatus,
              bearerserviceList, teleserviceList, provisionedSS, odbData, false,
              regionalSubscriptionData, vbsSubscriptionData, vgcsSubscriptionData,
              vlrCamelSubscriptionInfo);
          mapDialogMobility.send();
        }
      } catch (MAPException mapException) {
        logger.error("MAP Exception while processing insertSubscriberDataRequest ", mapException);
      } catch (Exception e) {
        logger.error("Exception while processing insertSubscriberDataRequest ", e);
      }
    } catch (Exception e) {
      logger.error("Exception while processing onInsertSubscriberDataResponse ", e);
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
}
