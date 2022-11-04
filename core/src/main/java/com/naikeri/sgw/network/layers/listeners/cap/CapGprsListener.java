package com.naikeri.sgw.network.layers.listeners.cap;

import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.chn.IChannelHandler;
import com.naikeri.sgw.network.layers.listeners.MapProxyContants;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPDialog;
import org.restcomm.protocols.ss7.cap.api.CAPMessage;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessage;
import org.restcomm.protocols.ss7.cap.api.service.gprs.ActivityTestGPRSRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.ActivityTestGPRSResponse;
import org.restcomm.protocols.ss7.cap.api.service.gprs.ApplyChargingGPRSRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.ApplyChargingReportGPRSRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.ApplyChargingReportGPRSResponse;
import org.restcomm.protocols.ss7.cap.api.service.gprs.CAPServiceGprsListener;
import org.restcomm.protocols.ss7.cap.api.service.gprs.CancelGPRSRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.ConnectGPRSRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.ContinueGPRSRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.EntityReleasedGPRSRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.EntityReleasedGPRSResponse;
import org.restcomm.protocols.ss7.cap.api.service.gprs.EventReportGPRSRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.EventReportGPRSResponse;
import org.restcomm.protocols.ss7.cap.api.service.gprs.FurnishChargingInformationGPRSRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.InitialDpGprsRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.ReleaseGPRSRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.RequestReportGPRSEventRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.ResetTimerGPRSRequest;
import org.restcomm.protocols.ss7.cap.api.service.gprs.SendChargingInformationGPRSRequest;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

import java.util.UUID;

public class CapGprsListener implements CAPServiceGprsListener {

  private static Logger logger = Logger.getLogger(CapGprsListener.class);

  private IChannelHandler channelHandler;
  private String layerName;

  public CapGprsListener(IChannelHandler channelHandler, String layerName) {
    this.channelHandler = channelHandler;
    this.layerName = layerName;
    logger.info("CapGprsListener started.....");
  }

  private ChannelMessage getMessage(String message) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE_TYPE, message);
    channelMessage.setParameter("layerName", this.layerName);
    return channelMessage;
  }

  @Override
  public void onCAPMessage(CAPMessage capMessage) {
    ChannelMessage channelMessage = getMessage(capMessage.getMessageType().toString());
    channelMessage.setParameter("capMessage", capMessage);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, capMessage.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, capMessage.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInitialDpGprsRequest(InitialDpGprsRequest initialDpGprsRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, initialDpGprsRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, initialDpGprsRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, initialDpGprsRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRequestReportGPRSEventRequest(RequestReportGPRSEventRequest requestReportGPRSEventRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, requestReportGPRSEventRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, requestReportGPRSEventRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, requestReportGPRSEventRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onApplyChargingGPRSRequest(ApplyChargingGPRSRequest applyChargingGPRSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, applyChargingGPRSRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, applyChargingGPRSRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, applyChargingGPRSRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onEntityReleasedGPRSRequest(EntityReleasedGPRSRequest entityReleasedGPRSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, entityReleasedGPRSRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, entityReleasedGPRSRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, entityReleasedGPRSRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onEntityReleasedGPRSResponse(EntityReleasedGPRSResponse entityReleasedGPRSResponse) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, entityReleasedGPRSResponse);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, entityReleasedGPRSResponse.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, entityReleasedGPRSResponse.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onConnectGPRSRequest(ConnectGPRSRequest connectGPRSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, connectGPRSRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, connectGPRSRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, connectGPRSRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onContinueGPRSRequest(ContinueGPRSRequest continueGPRSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, continueGPRSRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, continueGPRSRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, continueGPRSRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onReleaseGPRSRequest(ReleaseGPRSRequest releaseGPRSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, releaseGPRSRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, releaseGPRSRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, releaseGPRSRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onResetTimerGPRSRequest(ResetTimerGPRSRequest resetTimerGPRSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, resetTimerGPRSRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, resetTimerGPRSRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, resetTimerGPRSRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onFurnishChargingInformationGPRSRequest(
      FurnishChargingInformationGPRSRequest furnishChargingInformationGPRSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, furnishChargingInformationGPRSRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, furnishChargingInformationGPRSRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, furnishChargingInformationGPRSRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onCancelGPRSRequest(CancelGPRSRequest cancelGPRSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, cancelGPRSRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, cancelGPRSRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, cancelGPRSRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendChargingInformationGPRSRequest(SendChargingInformationGPRSRequest sendChargingInformationGPRSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, sendChargingInformationGPRSRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, sendChargingInformationGPRSRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, sendChargingInformationGPRSRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onApplyChargingReportGPRSRequest(ApplyChargingReportGPRSRequest applyChargingReportGPRSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, applyChargingReportGPRSRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, applyChargingReportGPRSRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, applyChargingReportGPRSRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onApplyChargingReportGPRSResponse(
      ApplyChargingReportGPRSResponse applyChargingReportGPRSResponse) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, applyChargingReportGPRSResponse);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, applyChargingReportGPRSResponse.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, applyChargingReportGPRSResponse.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onEventReportGPRSRequest(EventReportGPRSRequest eventReportGPRSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, eventReportGPRSRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, eventReportGPRSRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, eventReportGPRSRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onEventReportGPRSResponse(EventReportGPRSResponse eventReportGPRSResponse) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, eventReportGPRSResponse);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, eventReportGPRSResponse.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, eventReportGPRSResponse.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onActivityTestGPRSRequest(ActivityTestGPRSRequest activityTestGPRSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, activityTestGPRSRequest);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, activityTestGPRSRequest.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, activityTestGPRSRequest.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onActivityTestGPRSResponse(ActivityTestGPRSResponse activityTestGPRSResponse) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE, activityTestGPRSResponse);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, activityTestGPRSResponse.getInvokeId());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, activityTestGPRSResponse.getCAPDialog().getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onErrorComponent(CAPDialog capDialog, Long invokeId, CAPErrorMessage capErrorMessage) {
    ChannelMessage channelMessage = getMessage("onErrorComponent");
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, invokeId);
    channelMessage.setParameter("errorCode", capErrorMessage == null ? -1 : capErrorMessage.getErrorCode());
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, capDialog.getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRejectComponent(CAPDialog capDialog, Long invokeId, Problem problem, boolean isLocalOriginated) {
    ChannelMessage channelMessage = getMessage("onRejectComponent");
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, invokeId);
    channelMessage.setParameter("problem", problem);
    channelMessage.setParameter("isLocalOriginated", isLocalOriginated);
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, capDialog.getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInvokeTimeout(CAPDialog capDialog, Long invokeId) {
    ChannelMessage channelMessage = getMessage("onInvokeTimeout");
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, invokeId);
    channelMessage.setParameter(MapProxyContants.GPRS_REF_NUM, capDialog.getGprsReferenceNumber());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }
}
