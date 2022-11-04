package com.naikeri.sgw.network.layers.listeners.map;

import java.util.UUID;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.chn.IChannelHandler;
import com.naikeri.sgw.network.layers.listeners.MapProxyContants;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.InformServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPServiceSmsListener;
import org.restcomm.protocols.ss7.map.api.service.sms.MoForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MoForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.MtForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MtForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.NoteSubscriberPresentRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ReadyForSMRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ReadyForSMResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.ReportSMDeliveryStatusRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ReportSMDeliveryStatusResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMResponse;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

public class MapSmsListener implements MAPServiceSmsListener {

  IChannelHandler channelHandler;

  private static final Logger logger = Logger.getLogger(MapSmsListener.class);

  public MapSmsListener(IChannelHandler channelHandler) {
    this.channelHandler = channelHandler;
    logger.debug("MapSmsListener listening for events...");
  }

  private ChannelMessage getMessage(String messagetype) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter(MapProxyContants.MESSAGE_TYPE, messagetype);
    return channelMessage;
  }

  @Override
  public void onErrorComponent(MAPDialog mapDialog, Long invokeId,
      MAPErrorMessage mapErrorMessage) {
     ChannelMessage channelMessage = getMessage(MapProxyContants.ON_ERROR_COMPONENT);
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, invokeId);
    channelMessage.setParameter(MapProxyContants.MAP_ERROR_MESSAGE, mapErrorMessage);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem,
      boolean isLocalOriginated) {
   ChannelMessage channelMessage = getMessage(MapProxyContants.ON_REJECT_COMPONENT);
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, invokeId);
    channelMessage.setParameter(MapProxyContants.PROBLEM, problem);
    channelMessage.setParameter(MapProxyContants.LOCAL_ORIGINATED, isLocalOriginated);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInvokeTimeout(MAPDialog mapDialog, Long invokeId) {
    ChannelMessage channelMessage = getMessage("onInvokeTimeout");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, invokeId);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onMAPMessage(MAPMessage mapMessage) {
    ChannelMessage channelMessage = getMessage("onMAPMessage");
    channelMessage.setParameter("mapmessage", mapMessage);
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onForwardShortMessageRequest(ForwardShortMessageRequest forwSmInd) {
    ChannelMessage channelMessage = getMessage(forwSmInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, forwSmInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", forwSmInd.toString(),
        channelMessage.toString(), forwSmInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onForwardShortMessageResponse(ForwardShortMessageResponse forwSmRespInd) {
    ChannelMessage channelMessage = getMessage(forwSmRespInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, forwSmRespInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", forwSmRespInd.toString(),
        channelMessage.toString(), forwSmRespInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onMoForwardShortMessageRequest(MoForwardShortMessageRequest moForwSmInd) {
    logger.info("MO Forward Short Message Request received:" + moForwSmInd);
    ChannelMessage channelMessage = getMessage(moForwSmInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, moForwSmInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", moForwSmInd.toString(),
        channelMessage.toString(), moForwSmInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onMoForwardShortMessageResponse(MoForwardShortMessageResponse moForwSmRespInd) {
    ChannelMessage channelMessage = getMessage(moForwSmRespInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, moForwSmRespInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", moForwSmRespInd.toString(),
        channelMessage.toString(), moForwSmRespInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onMtForwardShortMessageRequest(MtForwardShortMessageRequest mtForwSmInd) {
    logger.info("MT Forward Short Message Request received:" + mtForwSmInd);
    ChannelMessage channelMessage = getMessage(mtForwSmInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, mtForwSmInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", mtForwSmInd.toString(),
        channelMessage.toString(), mtForwSmInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onMtForwardShortMessageResponse(MtForwardShortMessageResponse mtForwSmRespInd) {
    ChannelMessage channelMessage = getMessage(mtForwSmRespInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, mtForwSmRespInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", mtForwSmRespInd.toString(),
        channelMessage.toString(), mtForwSmRespInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest sendRoutingInfoForSMInd) {
    ChannelMessage channelMessage = getMessage(sendRoutingInfoForSMInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, sendRoutingInfoForSMInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", sendRoutingInfoForSMInd.toString(),
        channelMessage.toString(), sendRoutingInfoForSMInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendRoutingInfoForSMResponse(
      SendRoutingInfoForSMResponse sendRoutingInfoForSMRespInd) {
    ChannelMessage channelMessage =
        getMessage(sendRoutingInfoForSMRespInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, sendRoutingInfoForSMRespInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", sendRoutingInfoForSMRespInd.toString(),
        channelMessage.toString(), sendRoutingInfoForSMRespInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onReportSMDeliveryStatusRequest(
      ReportSMDeliveryStatusRequest reportSMDeliveryStatusInd) {
    ChannelMessage channelMessage =
        getMessage(reportSMDeliveryStatusInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, reportSMDeliveryStatusInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", reportSMDeliveryStatusInd.toString(),
        channelMessage.toString(), reportSMDeliveryStatusInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onReportSMDeliveryStatusResponse(
      ReportSMDeliveryStatusResponse reportSMDeliveryStatusRespInd) {
    ChannelMessage channelMessage =
        getMessage(reportSMDeliveryStatusRespInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, reportSMDeliveryStatusRespInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        reportSMDeliveryStatusRespInd.toString(), channelMessage.toString(),
        reportSMDeliveryStatusRespInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInformServiceCentreRequest(InformServiceCentreRequest informServiceCentreInd) {
    ChannelMessage channelMessage = getMessage(informServiceCentreInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, informServiceCentreInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", informServiceCentreInd.toString(),
        channelMessage.toString(), informServiceCentreInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onAlertServiceCentreRequest(AlertServiceCentreRequest alertServiceCentreInd) {
    ChannelMessage channelMessage = getMessage(alertServiceCentreInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, alertServiceCentreInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", alertServiceCentreInd.toString(),
        channelMessage.toString(), alertServiceCentreInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onAlertServiceCentreResponse(AlertServiceCentreResponse alertServiceCentreInd) {
    ChannelMessage channelMessage = getMessage(alertServiceCentreInd.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, alertServiceCentreInd);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", alertServiceCentreInd.toString(),
        channelMessage.toString(), alertServiceCentreInd.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onReadyForSMRequest(ReadyForSMRequest request) {
    ChannelMessage channelMessage = getMessage(request.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, request);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", request.toString(),
        channelMessage.toString(), request.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onReadyForSMResponse(ReadyForSMResponse response) {
    ChannelMessage channelMessage = getMessage(response.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, response);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", response.toString(),
        channelMessage.toString(), response.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onNoteSubscriberPresentRequest(NoteSubscriberPresentRequest request) {
    ChannelMessage channelMessage = getMessage(request.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, request);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", request.toString(),
        channelMessage.toString(), request.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

}
