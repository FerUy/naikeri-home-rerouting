package com.naikeri.sgw.network.layers.listeners.cap;

import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.chn.IChannelHandler;
import com.naikeri.sgw.network.layers.listeners.MapProxyContants;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPDialog;
import org.restcomm.protocols.ss7.cap.api.CAPMessage;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessage;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ActivityTestRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ActivityTestResponse;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ApplyChargingReportRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ApplyChargingRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.AssistRequestInstructionsRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPServiceCircuitSwitchedCallListener;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CallGapRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CallInformationReportRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CallInformationRequestRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CancelRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CollectInformationRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ConnectRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ConnectToResourceRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ContinueRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ContinueWithArgumentRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.DisconnectForwardConnectionRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.DisconnectForwardConnectionWithArgumentRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.DisconnectLegRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.DisconnectLegResponse;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EstablishTemporaryConnectionRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EventReportBCSMRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.FurnishChargingInformationRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.InitialDPRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.InitiateCallAttemptRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.InitiateCallAttemptResponse;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.MoveLegRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.MoveLegResponse;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.PlayAnnouncementRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.PromptAndCollectUserInformationRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.PromptAndCollectUserInformationResponse;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ReleaseCallRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.RequestReportBCSMEventRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ResetTimerRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.SendChargingInformationRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.SpecializedResourceReportRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.SplitLegRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.SplitLegResponse;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

import java.util.UUID;

public class CapCircuitSwitchedCallListener implements CAPServiceCircuitSwitchedCallListener {

  private static Logger logger = Logger.getLogger(CapCircuitSwitchedCallListener.class);
  private String layerName;
  private IChannelHandler channelHandler;

  public CapCircuitSwitchedCallListener(IChannelHandler channelHandler, String layerName) {
    this.channelHandler = channelHandler;
    this.layerName = layerName;
    logger.debug("CapCircuitSwitchedCallListener listener started.....");
  }

  private ChannelMessage getMessage(String message) {
    // dialogid + invokeid + salt
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE_TYPE, message);
    channelMessage.setParameter(MapProxyContants.CAP_LAYER_NAME, this.layerName);
    return channelMessage;
  }

  @Override
  public void onCAPMessage(CAPMessage capMessage) {
    ChannelMessage channelMessage = getMessage(MapProxyContants.ON_CAP_MESSAGE);
    channelMessage.setParameter("capMessage", capMessage);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, capMessage.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", capMessage.toString(),
        channelMessage.toString(), capMessage.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInitialDPRequest(InitialDPRequest initialDPRequest) {
    ChannelMessage channelMessage = getMessage(initialDPRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, initialDPRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, initialDPRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", initialDPRequest.toString(),
        channelMessage.toString(), initialDPRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRequestReportBCSMEventRequest(RequestReportBCSMEventRequest requestReportBCSMEventRequest) {
    ChannelMessage channelMessage = getMessage(requestReportBCSMEventRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, requestReportBCSMEventRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, requestReportBCSMEventRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        requestReportBCSMEventRequest.toString(), channelMessage.toString(),
        requestReportBCSMEventRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onApplyChargingRequest(ApplyChargingRequest applyChargingRequest) {
    ChannelMessage channelMessage = getMessage(applyChargingRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, applyChargingRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, applyChargingRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", applyChargingRequest.toString(),
        channelMessage.toString(), applyChargingRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onEventReportBCSMRequest(EventReportBCSMRequest eventReportBCSMRequest) {
    ChannelMessage channelMessage = getMessage(eventReportBCSMRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, eventReportBCSMRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, eventReportBCSMRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", eventReportBCSMRequest.toString(),
        channelMessage.toString(), eventReportBCSMRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onContinueRequest(ContinueRequest continueRequest) {
    ChannelMessage channelMessage = getMessage(continueRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, continueRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, continueRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", continueRequest.toString(),
        channelMessage.toString(), continueRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onContinueWithArgumentRequest(ContinueWithArgumentRequest continueWithArgumentRequest) {
    ChannelMessage channelMessage = getMessage(continueWithArgumentRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, continueWithArgumentRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, continueWithArgumentRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        continueWithArgumentRequest.toString(), channelMessage.toString(),
        continueWithArgumentRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onApplyChargingReportRequest(ApplyChargingReportRequest applyChargingReportRequest) {
    ChannelMessage channelMessage = getMessage(applyChargingReportRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, applyChargingReportRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, applyChargingReportRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", applyChargingReportRequest.toString(),
        channelMessage.toString(), applyChargingReportRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onReleaseCallRequest(ReleaseCallRequest releaseCallRequest) {
    ChannelMessage channelMessage = getMessage(releaseCallRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, releaseCallRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, releaseCallRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", releaseCallRequest.toString(),
        channelMessage.toString(), releaseCallRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onConnectRequest(ConnectRequest connectRequest) {
    ChannelMessage channelMessage = getMessage(connectRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, connectRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, connectRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", connectRequest.toString(),
        channelMessage.toString(), connectRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onCallInformationRequestRequest(CallInformationRequestRequest callInformationRequestRequest) {
    ChannelMessage channelMessage = getMessage(callInformationRequestRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, callInformationRequestRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, callInformationRequestRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        callInformationRequestRequest.toString(), channelMessage.toString(),
        callInformationRequestRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onCallInformationReportRequest(CallInformationReportRequest callInformationReportRequest) {
    ChannelMessage channelMessage = getMessage(callInformationReportRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, callInformationReportRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, callInformationReportRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        callInformationReportRequest.toString(), channelMessage.toString(),
        callInformationReportRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onActivityTestRequest(ActivityTestRequest activityTestRequest) {
    ChannelMessage channelMessage = getMessage(activityTestRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, activityTestRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, activityTestRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", activityTestRequest.toString(),
        channelMessage.toString(), activityTestRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onActivityTestResponse(ActivityTestResponse activityTestResponse) {
    ChannelMessage channelMessage = getMessage(activityTestResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, activityTestResponse);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, activityTestResponse.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", activityTestResponse.toString(),
        channelMessage.toString(), activityTestResponse.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onAssistRequestInstructionsRequest(AssistRequestInstructionsRequest assistRequestInstructionsRequest) {
    ChannelMessage channelMessage = getMessage(assistRequestInstructionsRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, assistRequestInstructionsRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, assistRequestInstructionsRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        assistRequestInstructionsRequest.toString(), channelMessage.toString(),
        assistRequestInstructionsRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onEstablishTemporaryConnectionRequest(EstablishTemporaryConnectionRequest establishTemporaryConnectionRequest) {
    ChannelMessage channelMessage = getMessage(establishTemporaryConnectionRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, establishTemporaryConnectionRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, establishTemporaryConnectionRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        establishTemporaryConnectionRequest.toString(), channelMessage.toString(),
        establishTemporaryConnectionRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDisconnectForwardConnectionRequest(DisconnectForwardConnectionRequest disconnectForwardConnectionRequest) {
    ChannelMessage channelMessage = getMessage(disconnectForwardConnectionRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, disconnectForwardConnectionRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, disconnectForwardConnectionRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        disconnectForwardConnectionRequest.toString(), channelMessage.toString(),
        disconnectForwardConnectionRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDisconnectLegRequest(DisconnectLegRequest disconnectLegRequest) {
    ChannelMessage channelMessage = getMessage(disconnectLegRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, disconnectLegRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, disconnectLegRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", disconnectLegRequest.toString(),
        channelMessage.toString(), disconnectLegRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDisconnectLegResponse(DisconnectLegResponse disconnectLegResponse) {
    ChannelMessage channelMessage = getMessage(disconnectLegResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, disconnectLegResponse);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, disconnectLegResponse.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", disconnectLegResponse.toString(),
        channelMessage.toString(), disconnectLegResponse.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDisconnectForwardConnectionWithArgumentRequest(DisconnectForwardConnectionWithArgumentRequest disconnectForwardConnectionWithArgumentRequest) {
    ChannelMessage channelMessage = getMessage(disconnectForwardConnectionWithArgumentRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, disconnectForwardConnectionWithArgumentRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID,
        disconnectForwardConnectionWithArgumentRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        disconnectForwardConnectionWithArgumentRequest.toString(), channelMessage.toString(),
        disconnectForwardConnectionWithArgumentRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onConnectToResourceRequest(ConnectToResourceRequest connectToResourceRequest) {
    ChannelMessage channelMessage = getMessage(connectToResourceRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, connectToResourceRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, connectToResourceRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", connectToResourceRequest.toString(),
        channelMessage.toString(), connectToResourceRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onResetTimerRequest(ResetTimerRequest resetTimerRequest) {
    ChannelMessage channelMessage = getMessage(resetTimerRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, resetTimerRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, resetTimerRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", resetTimerRequest.toString(),
        channelMessage.toString(), resetTimerRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onFurnishChargingInformationRequest(FurnishChargingInformationRequest furnishChargingInformationRequest) {
    ChannelMessage channelMessage = getMessage(furnishChargingInformationRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, furnishChargingInformationRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, furnishChargingInformationRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        furnishChargingInformationRequest.toString(), channelMessage.toString(),
        furnishChargingInformationRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendChargingInformationRequest(SendChargingInformationRequest sendChargingInformationRequest) {
    ChannelMessage channelMessage = getMessage(sendChargingInformationRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, sendChargingInformationRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, sendChargingInformationRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        sendChargingInformationRequest.toString(), channelMessage.toString(),
        sendChargingInformationRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSpecializedResourceReportRequest(SpecializedResourceReportRequest specializedResourceReportRequest) {
    ChannelMessage channelMessage = getMessage(specializedResourceReportRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, specializedResourceReportRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, specializedResourceReportRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        specializedResourceReportRequest.toString(), channelMessage.toString(),
        specializedResourceReportRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onPlayAnnouncementRequest(PlayAnnouncementRequest playAnnouncementRequest) {
    ChannelMessage channelMessage = getMessage(playAnnouncementRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, playAnnouncementRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, playAnnouncementRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", playAnnouncementRequest.toString(),
        channelMessage.toString(), playAnnouncementRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onPromptAndCollectUserInformationRequest(PromptAndCollectUserInformationRequest promptAndCollectUserInformationRequest) {
    ChannelMessage channelMessage = getMessage(promptAndCollectUserInformationRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, promptAndCollectUserInformationRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, promptAndCollectUserInformationRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        promptAndCollectUserInformationRequest.toString(), channelMessage.toString(),
        promptAndCollectUserInformationRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onPromptAndCollectUserInformationResponse(PromptAndCollectUserInformationResponse promptAndCollectUserInformationResponse) {
    ChannelMessage channelMessage = getMessage(promptAndCollectUserInformationResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, promptAndCollectUserInformationResponse);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, promptAndCollectUserInformationResponse.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        promptAndCollectUserInformationResponse.toString(), channelMessage.toString(),
        promptAndCollectUserInformationResponse.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onCancelRequest(CancelRequest cancelRequest) {
    ChannelMessage channelMessage = getMessage(cancelRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, cancelRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, cancelRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", cancelRequest.toString(),
        channelMessage.toString(), cancelRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInitiateCallAttemptRequest(InitiateCallAttemptRequest initiateCallAttemptRequest) {
    ChannelMessage channelMessage = getMessage(initiateCallAttemptRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, initiateCallAttemptRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, initiateCallAttemptRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", initiateCallAttemptRequest.toString(),
        channelMessage.toString(), initiateCallAttemptRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInitiateCallAttemptResponse(InitiateCallAttemptResponse initiateCallAttemptResponse) {
    ChannelMessage channelMessage = getMessage(initiateCallAttemptResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, initiateCallAttemptResponse);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, initiateCallAttemptResponse.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ",
        initiateCallAttemptResponse.toString(), channelMessage.toString(),
        initiateCallAttemptResponse.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onMoveLegRequest(MoveLegRequest moveLegRequest) {
    ChannelMessage channelMessage = getMessage(moveLegRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, moveLegRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, moveLegRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", moveLegRequest.toString(),
        channelMessage.toString(), moveLegRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onMoveLegResponse(MoveLegResponse moveLegResponse) {
    ChannelMessage channelMessage = getMessage(moveLegResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, moveLegResponse);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, moveLegResponse.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", moveLegResponse.toString(),
        channelMessage.toString(), moveLegResponse.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onCollectInformationRequest(CollectInformationRequest collectInformationRequest) {
    ChannelMessage channelMessage = getMessage(collectInformationRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, collectInformationRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, collectInformationRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", collectInformationRequest.toString(),
        channelMessage.toString(), collectInformationRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSplitLegRequest(SplitLegRequest splitLegRequest) {
    ChannelMessage channelMessage = getMessage(splitLegRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, splitLegRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, splitLegRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", splitLegRequest.toString(),
        channelMessage.toString(), splitLegRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSplitLegResponse(SplitLegResponse splitLegResponse) {
    ChannelMessage channelMessage = getMessage(splitLegResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, splitLegResponse);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, splitLegResponse.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", splitLegResponse.toString(),
        channelMessage.toString(), splitLegResponse.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onCallGapRequest(CallGapRequest callGapRequest) {
    ChannelMessage channelMessage = getMessage(callGapRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, callGapRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, callGapRequest.getInvokeId());
    logger.debug(String.format("%s, %s, [ DialogId = '%d'] ", callGapRequest.toString(),
        channelMessage.toString(), callGapRequest.getCAPDialog().getLocalDialogId()));
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onErrorComponent(CAPDialog capDialog, Long invokeId, CAPErrorMessage capErrorMessage) {
    ChannelMessage channelMessage = getMessage(MapProxyContants.ON_ERROR_COMPONENT);
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, invokeId); 
    channelMessage.setParameter(MapProxyContants.CAP_ERROR_MESSAGE, capErrorMessage);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRejectComponent(CAPDialog capDialog, Long invokeId, Problem problem, boolean isLocalOriginated) {
    ChannelMessage channelMessage = getMessage("onRejectComponent");
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, invokeId);
    channelMessage.setParameter("problem", problem);
    channelMessage.setParameter("isLocalOriginated", isLocalOriginated);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInvokeTimeout(CAPDialog capDialog, Long invokeId) {
    ChannelMessage channelMessage = getMessage("onInvokeTimeout");
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, invokeId);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }
}
