package com.naikeri.sgw.network.layers.listeners.map;

import java.util.UUID;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.chn.IChannelHandler;
import com.naikeri.sgw.network.layers.listeners.MapProxyContants;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
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
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

public class MapServiceMobilityListener implements MAPServiceMobilityListener {

  private IChannelHandler channelHandler;
  private static Logger logger = Logger.getLogger(MapServiceMobilityListener.class);


  public MapServiceMobilityListener(IChannelHandler channelHandler) {
    logger.debug("MapServiceMobilityListener running ");
    this.channelHandler = channelHandler;
  }

  private ChannelMessage getMessage(String messagetype) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter(MapProxyContants.MESSAGE_TYPE, messagetype);
    return channelMessage;
  }

  @Override
  public void onUpdateLocationRequest(UpdateLocationRequest updateLocationRequest) {
    ChannelMessage channelMessage = getMessage(updateLocationRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, updateLocationRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", updateLocationRequest.toString(),
        channelMessage.toString(), updateLocationRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onUpdateLocationResponse(UpdateLocationResponse updateLocationResponse) {
    logger.info("update Location Response received:" + updateLocationResponse + "dialogId: "
        + updateLocationResponse.getMAPDialog().getLocalDialogId() + "message Type "
        + updateLocationResponse.getMessageType().toString());
    ChannelMessage channelMessage = getMessage(updateLocationResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, updateLocationResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", updateLocationResponse.toString(),
        channelMessage.toString(), updateLocationResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onCancelLocationRequest(CancelLocationRequest cancelLocationRequest) {
    ChannelMessage channelMessage = getMessage(cancelLocationRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, cancelLocationRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", cancelLocationRequest.toString(),
        channelMessage.toString(), cancelLocationRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onCancelLocationResponse(CancelLocationResponse cancelLocationResponse) {
    ChannelMessage channelMessage = getMessage(cancelLocationResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, cancelLocationResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", cancelLocationResponse.toString(),
        channelMessage.toString(), cancelLocationResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendIdentificationRequest(SendIdentificationRequest sendIdentificationRequest) {
    ChannelMessage channelMessage =
        getMessage(sendIdentificationRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, sendIdentificationRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", sendIdentificationRequest.toString(),
        channelMessage.toString(), sendIdentificationRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendIdentificationResponse(SendIdentificationResponse sendIdentificationResponse) {
    ChannelMessage channelMessage =
        getMessage(sendIdentificationResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, sendIdentificationResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", sendIdentificationResponse.toString(),
        channelMessage.toString(), sendIdentificationResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  // server listening for on update gprs location request
  @Override
  public void onUpdateGprsLocationRequest(UpdateGprsLocationRequest updateGprsLocationRequest) {
    ChannelMessage channelMessage =
        getMessage(updateGprsLocationRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, updateGprsLocationRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", updateGprsLocationRequest.toString(),
        channelMessage.toString(), updateGprsLocationRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onUpdateGprsLocationResponse(UpdateGprsLocationResponse updateGprsLocationResponse) {
    ChannelMessage channelMessage =
        getMessage(updateGprsLocationResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, updateGprsLocationResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", updateGprsLocationResponse.toString(),
        channelMessage.toString(), updateGprsLocationResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onPurgeMSRequest(PurgeMSRequest purgeMSRequest) {
    ChannelMessage channelMessage = getMessage(purgeMSRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, purgeMSRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", purgeMSRequest.toString(),
        channelMessage.toString(), purgeMSRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onPurgeMSResponse(PurgeMSResponse purgeMSResponse) {
    ChannelMessage channelMessage = getMessage(purgeMSResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, purgeMSResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", purgeMSResponse.toString(),
        channelMessage.toString(), purgeMSResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendAuthenticationInfoRequest(SendAuthenticationInfoRequest request) {
    ChannelMessage channelMessage = getMessage(request.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, request);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", request.toString(),
        channelMessage.toString(), request.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendAuthenticationInfoResponse(
      SendAuthenticationInfoResponse sendAuthenticationInfoResponse) {
    ChannelMessage channelMessage =
        getMessage(sendAuthenticationInfoResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, sendAuthenticationInfoResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        sendAuthenticationInfoResponse.toString(), channelMessage.toString(),
        sendAuthenticationInfoResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onAuthenticationFailureReportRequest(
      AuthenticationFailureReportRequest authenticationFailureReportRequest) {
    ChannelMessage channelMessage =
        getMessage(authenticationFailureReportRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, authenticationFailureReportRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        authenticationFailureReportRequest.toString(), channelMessage.toString(),
        authenticationFailureReportRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onAuthenticationFailureReportResponse(
      AuthenticationFailureReportResponse authenticationFailureReportResponse) {
    ChannelMessage channelMessage =
        getMessage(authenticationFailureReportResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, authenticationFailureReportResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        authenticationFailureReportResponse.toString(), channelMessage.toString(),
        authenticationFailureReportResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onResetRequest(ResetRequest resetRequest) {
    ChannelMessage channelMessage = getMessage(resetRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, resetRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", resetRequest.toString(),
        channelMessage.toString(), resetRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onForwardCheckSSIndicationRequest(
      ForwardCheckSSIndicationRequest forwardCheckSSIndicationRequest) {
    ChannelMessage channelMessage =
        getMessage(forwardCheckSSIndicationRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, forwardCheckSSIndicationRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        forwardCheckSSIndicationRequest.toString(), channelMessage.toString(),
        forwardCheckSSIndicationRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRestoreDataRequest(RestoreDataRequest restoreDataRequest) {
    ChannelMessage channelMessage = getMessage(restoreDataRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, restoreDataRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", restoreDataRequest.toString(),
        channelMessage.toString(), restoreDataRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRestoreDataResponse(RestoreDataResponse restoreDataResponse) {
    ChannelMessage channelMessage = getMessage(restoreDataResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, restoreDataResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", restoreDataResponse.toString(),
        channelMessage.toString(), restoreDataResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onAnyTimeInterrogationRequest(
      AnyTimeInterrogationRequest anyTimeInterrogationRequest) {
    ChannelMessage channelMessage =
        getMessage(anyTimeInterrogationRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, anyTimeInterrogationRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", anyTimeInterrogationRequest.toString(),
        channelMessage.toString(), anyTimeInterrogationRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onAnyTimeInterrogationResponse(
      AnyTimeInterrogationResponse anyTimeInterrogationResponse) {
    ChannelMessage channelMessage =
        getMessage(anyTimeInterrogationResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, anyTimeInterrogationResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        anyTimeInterrogationResponse.toString(), channelMessage.toString(),
        anyTimeInterrogationResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onAnyTimeSubscriptionInterrogationRequest(
      AnyTimeSubscriptionInterrogationRequest anyTimeSubscriptionInterrogationRequest) {
    ChannelMessage channelMessage =
        getMessage(anyTimeSubscriptionInterrogationRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, anyTimeSubscriptionInterrogationRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        anyTimeSubscriptionInterrogationRequest.toString(), channelMessage.toString(),
        anyTimeSubscriptionInterrogationRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onAnyTimeSubscriptionInterrogationResponse(
      AnyTimeSubscriptionInterrogationResponse anyTimeSubscriptionInterrogationResponse) {
    ChannelMessage channelMessage =
        getMessage(anyTimeSubscriptionInterrogationResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, anyTimeSubscriptionInterrogationResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        anyTimeSubscriptionInterrogationResponse.toString(), channelMessage.toString(),
        anyTimeSubscriptionInterrogationResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onProvideSubscriberInfoRequest(ProvideSubscriberInfoRequest request) {
    ChannelMessage channelMessage = getMessage(request.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, request);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", request.toString(),
        channelMessage.toString(), request.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onProvideSubscriberInfoResponse(
      ProvideSubscriberInfoResponse provideSubscriberInfoResponse) {
    ChannelMessage channelMessage =
        getMessage(provideSubscriberInfoResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, provideSubscriberInfoResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        provideSubscriberInfoResponse.toString(), channelMessage.toString(),
        provideSubscriberInfoResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInsertSubscriberDataRequest(InsertSubscriberDataRequest request) {
    ChannelMessage channelMessage = getMessage(request.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, request);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", request.toString(),
        channelMessage.toString(), request.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInsertSubscriberDataResponse(
      InsertSubscriberDataResponse insertSubscriberDataResponse) {
    ChannelMessage channelMessage =
        getMessage(insertSubscriberDataResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, insertSubscriberDataResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        insertSubscriberDataResponse.toString(), channelMessage.toString(),
        insertSubscriberDataResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDeleteSubscriberDataRequest(
      DeleteSubscriberDataRequest deleteSubscriberDataRequest) {
    ChannelMessage channelMessage =
        getMessage(deleteSubscriberDataRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, deleteSubscriberDataRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", deleteSubscriberDataRequest.toString(),
        channelMessage.toString(), deleteSubscriberDataRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDeleteSubscriberDataResponse(
      DeleteSubscriberDataResponse deleteSubscriberDataResponse) {
    ChannelMessage channelMessage =
        getMessage(deleteSubscriberDataResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, deleteSubscriberDataResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        deleteSubscriberDataResponse.toString(), channelMessage.toString(),
        deleteSubscriberDataResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onCheckImeiRequest(CheckImeiRequest checkImeiRequest) {
    ChannelMessage channelMessage = getMessage(checkImeiRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, checkImeiRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", checkImeiRequest.toString(),
        channelMessage.toString(), checkImeiRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onCheckImeiResponse(CheckImeiResponse checkImeiResponse) {
    ChannelMessage channelMessage = getMessage(checkImeiResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, checkImeiResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", checkImeiResponse.toString(),
        channelMessage.toString(), checkImeiResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onActivateTraceModeRequest_Mobility(
      ActivateTraceModeRequest_Mobility activateTraceModeRequestMobility) {
    ChannelMessage channelMessage =
        getMessage(activateTraceModeRequestMobility.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, activateTraceModeRequestMobility);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        activateTraceModeRequestMobility.toString(), channelMessage.toString(),
        activateTraceModeRequestMobility.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onActivateTraceModeResponse_Mobility(
      ActivateTraceModeResponse_Mobility activateTraceModeResponseMobility) {
    ChannelMessage channelMessage =
        getMessage(activateTraceModeResponseMobility.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, activateTraceModeResponseMobility);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        activateTraceModeResponseMobility.toString(), channelMessage.toString(),
        activateTraceModeResponseMobility.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onErrorComponent(MAPDialog mapDialog, Long invokeId, MAPErrorMessage mapErrorMessage) {
    ChannelMessage channelMessage = getMessage(MapProxyContants.ON_ERROR_COMPONENT);
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, invokeId);
    channelMessage.setParameter(MapProxyContants.MAP_ERROR_MESSAGE, mapErrorMessage);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem, boolean b) {
      ChannelMessage channelMessage = getMessage(MapProxyContants.ON_REJECT_COMPONENT);
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, invokeId);
    channelMessage.setParameter(MapProxyContants.PROBLEM, problem);
    channelMessage.setParameter(MapProxyContants.LOCAL_ORIGINATED, b);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInvokeTimeout(MAPDialog mapDialog, Long aLong) {
    ChannelMessage channelMessage = getMessage("onInvokeTimeout");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, aLong);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onMAPMessage(MAPMessage mapMessage) {
    ChannelMessage channelMessage = getMessage("onMAPMessage");
    channelMessage.setParameter("mapmessage", mapMessage);
    channelHandler.receiveMessageRequest(channelMessage);
  }
}
