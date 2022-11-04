package com.naikeri.sgw.network.layers.listeners.cap;

import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.chn.IChannelHandler;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPDialog;
import org.restcomm.protocols.ss7.cap.api.CAPMessage;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessage;
import org.restcomm.protocols.ss7.cap.api.service.sms.CAPServiceSmsListener;
import org.restcomm.protocols.ss7.cap.api.service.sms.ConnectSMSRequest;
import org.restcomm.protocols.ss7.cap.api.service.sms.ContinueSMSRequest;
import org.restcomm.protocols.ss7.cap.api.service.sms.EventReportSMSRequest;
import org.restcomm.protocols.ss7.cap.api.service.sms.FurnishChargingInformationSMSRequest;
import org.restcomm.protocols.ss7.cap.api.service.sms.InitialDPSMSRequest;
import org.restcomm.protocols.ss7.cap.api.service.sms.ReleaseSMSRequest;
import org.restcomm.protocols.ss7.cap.api.service.sms.RequestReportSMSEventRequest;
import org.restcomm.protocols.ss7.cap.api.service.sms.ResetTimerSMSRequest;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

import java.util.UUID;

public class CapSmsListener implements CAPServiceSmsListener {

  private static Logger logger = Logger.getLogger(CapSmsListener.class);
  private String layerName;
  private IChannelHandler channelHandler;

  public CapSmsListener(IChannelHandler channelHandler, String layerName) {
    this.channelHandler = channelHandler;
    this.layerName = layerName;
    logger.info("CapSmsListener listener started.....");
  }

  private ChannelMessage getMessage(String message) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter("messageType", message);
    channelMessage.setParameter("layerName", this.layerName);
    return channelMessage;
  }

  @Override
  public void onCAPMessage(CAPMessage capMessage) {
    ChannelMessage channelMessage = getMessage(capMessage.getMessageType().toString());
    channelMessage.setParameter("capMessage", capMessage);
    channelMessage.setParameter("invokeId", capMessage.getInvokeId());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onConnectSMSRequest(ConnectSMSRequest connectSMSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter("message", connectSMSRequest);
    channelMessage.setParameter("invokeId", connectSMSRequest.getInvokeId());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onEventReportSMSRequest(EventReportSMSRequest eventReportSMSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter("message", eventReportSMSRequest);
    channelMessage.setParameter("invokeId", eventReportSMSRequest.getInvokeId());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onFurnishChargingInformationSMSRequest(
      FurnishChargingInformationSMSRequest furnishChargingInformationSMSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter("message", furnishChargingInformationSMSRequest);
    channelMessage.setParameter("invokeId", furnishChargingInformationSMSRequest.getInvokeId());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInitialDPSMSRequest(InitialDPSMSRequest initialDPSMSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter("message", initialDPSMSRequest);
    channelMessage.setParameter("invokeId", initialDPSMSRequest.getInvokeId());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onReleaseSMSRequest(ReleaseSMSRequest releaseSMSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter("message", releaseSMSRequest);
    channelMessage.setParameter("invokeId", releaseSMSRequest.getInvokeId());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRequestReportSMSEventRequest(
      RequestReportSMSEventRequest requestReportSMSEventRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter("message", requestReportSMSEventRequest);
    channelMessage.setParameter("invokeId", requestReportSMSEventRequest.getInvokeId());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onResetTimerSMSRequest(ResetTimerSMSRequest resetTimerSMSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter("message", resetTimerSMSRequest);
    channelMessage.setParameter("invokeId", resetTimerSMSRequest.getInvokeId());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onContinueSMSRequest(ContinueSMSRequest continueSMSRequest) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter("message", continueSMSRequest);
    channelMessage.setParameter("invokeId", continueSMSRequest.getInvokeId());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onErrorComponent(CAPDialog capDialog, Long invokeId, CAPErrorMessage capErrorMessage) {
    ChannelMessage channelMessage = getMessage("onErrorComponent");
    channelMessage.setParameter("dialog", capDialog);
    channelMessage.setParameter("invokeId", invokeId);
    channelMessage.setParameter("errorCode", capErrorMessage == null ? -1 : capErrorMessage.getErrorCode());
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRejectComponent(CAPDialog capDialog, Long invokeId, Problem problem, boolean isLocalOriginated) {
    ChannelMessage channelMessage = getMessage("onRejectComponent");
    channelMessage.setParameter("dialog", capDialog);
    channelMessage.setParameter("invokeId", invokeId);
    channelMessage.setParameter("problem", problem);
    channelMessage.setParameter("isLocalOriginated", isLocalOriginated);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInvokeTimeout(CAPDialog capDialog, Long invokeId) {
    ChannelMessage channelMessage = getMessage("onInvokeTimeout");
    channelMessage.setParameter("dialog", capDialog);
    channelMessage.setParameter("invokeId", invokeId);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }
}
