package com.naikeri.sgw.network.layers.listeners.map;

import java.util.UUID;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.chn.IChannelHandler;
import com.naikeri.sgw.network.layers.listeners.MapProxyContants;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.service.lsm.MAPServiceLsmListener;
import org.restcomm.protocols.ss7.map.api.service.lsm.ProvideSubscriberLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.ProvideSubscriberLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.SendRoutingInfoForLCSRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.SendRoutingInfoForLCSResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponse;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

public class MapServiceLsmListener implements MAPServiceLsmListener {

  private static Logger logger = Logger.getLogger(MapServiceLsmListener.class);

  private IChannelHandler channelHandler;

  public MapServiceLsmListener(IChannelHandler channelHandler) {
    logger.debug("MapServiceLsmListener running");
    this.channelHandler = channelHandler;
  }

  private ChannelMessage getMessage(String messagetype) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter(MapProxyContants.MESSAGE_TYPE, messagetype);
    return channelMessage;
  }

  @Override
  public void onProvideSubscriberLocationRequest(
      ProvideSubscriberLocationRequest provideSubscriberLocationRequest) {
    ChannelMessage channelMessage =
        getMessage(provideSubscriberLocationRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, provideSubscriberLocationRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        provideSubscriberLocationRequest.toString(), channelMessage.toString(),
        provideSubscriberLocationRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onProvideSubscriberLocationResponse(
      ProvideSubscriberLocationResponse provideSubscriberLocationResponse) {
    ChannelMessage channelMessage =
        getMessage(provideSubscriberLocationResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, provideSubscriberLocationResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        provideSubscriberLocationResponse.toString(), channelMessage.toString(),
        provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSubscriberLocationReportRequest(
      SubscriberLocationReportRequest subscriberLocationReportRequest) {
    ChannelMessage channelMessage =
        getMessage(subscriberLocationReportRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, subscriberLocationReportRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        subscriberLocationReportRequest.toString(), channelMessage.toString(),
        subscriberLocationReportRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSubscriberLocationReportResponse(
      SubscriberLocationReportResponse subscriberLocationReportResponse) {
    ChannelMessage channelMessage =
        getMessage(subscriberLocationReportResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, subscriberLocationReportResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        subscriberLocationReportResponse.toString(), channelMessage.toString(),
        subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendRoutingInfoForLCSRequest(
      SendRoutingInfoForLCSRequest sendRoutingInfoForLCSRequest) {
    ChannelMessage channelMessage =
        getMessage(sendRoutingInfoForLCSRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, sendRoutingInfoForLCSRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        sendRoutingInfoForLCSRequest.toString(), channelMessage.toString(),
        sendRoutingInfoForLCSRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendRoutingInfoForLCSResponse(
      SendRoutingInfoForLCSResponse sendRoutingInfoForLCSResponse) {
    ChannelMessage channelMessage =
        getMessage(sendRoutingInfoForLCSResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, sendRoutingInfoForLCSResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        sendRoutingInfoForLCSResponse.toString(), channelMessage.toString(),
        sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
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
    channelMessage.setParameter("dialog", mapDialog);
    channelMessage.setParameter("invokeId", aLong);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onMAPMessage(MAPMessage mapMessage) {
    ChannelMessage channelMessage = getMessage("onMAPMessage");
    channelMessage.setParameter("mapmessage", mapMessage);
    channelHandler.receiveMessageRequest(channelMessage);
  }
}
