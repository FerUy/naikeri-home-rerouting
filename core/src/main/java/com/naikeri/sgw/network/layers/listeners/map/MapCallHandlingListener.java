package com.naikeri.sgw.network.layers.listeners.map;

import java.util.UUID;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.chn.IChannelHandler;
import com.naikeri.sgw.network.layers.listeners.MapProxyContants;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.service.callhandling.IstCommandRequest;
import org.restcomm.protocols.ss7.map.api.service.callhandling.IstCommandResponse;
import org.restcomm.protocols.ss7.map.api.service.callhandling.MAPServiceCallHandlingListener;
import org.restcomm.protocols.ss7.map.api.service.callhandling.ProvideRoamingNumberRequest;
import org.restcomm.protocols.ss7.map.api.service.callhandling.ProvideRoamingNumberResponse;
import org.restcomm.protocols.ss7.map.api.service.callhandling.SendRoutingInformationRequest;
import org.restcomm.protocols.ss7.map.api.service.callhandling.SendRoutingInformationResponse;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

public class MapCallHandlingListener implements MAPServiceCallHandlingListener {

  private IChannelHandler channelHandler;
  private static Logger logger = Logger.getLogger(MapCallHandlingListener.class);
  private static final String LOGGER_FORMAT = "%s, %s, [ DialogId = '%d'] ";

  public MapCallHandlingListener(IChannelHandler channelHandler) {
    this.channelHandler = channelHandler;
    logger.debug("MapCallHandlingListener listening for events...");
  }

  private ChannelMessage getMessage(String primitive) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter(MapProxyContants.MESSAGE_TYPE, primitive);
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
    ChannelMessage channelMessage = getMessage(MapProxyContants.ON_INVOKE_TIMEOUT);
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, invokeId);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onMAPMessage(MAPMessage mapMessage) {
    ChannelMessage channelMessage = getMessage(MapProxyContants.ON_MAP_MESSAGE);
    channelMessage.setParameter(MapProxyContants.MAP_MESSAGE, mapMessage);
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendRoutingInformationRequest(SendRoutingInformationRequest request) {
    ChannelMessage channelMessage = getMessage(request.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, request);
    logger.info(String.format(LOGGER_FORMAT, request.toString(), channelMessage.toString(),
        request.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendRoutingInformationResponse(SendRoutingInformationResponse response) {
    ChannelMessage channelMessage = getMessage(response.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, response);
    logger.info(String.format(LOGGER_FORMAT, response.toString(), channelMessage.toString(),
        response.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onProvideRoamingNumberRequest(ProvideRoamingNumberRequest request) {
    ChannelMessage channelMessage = getMessage(request.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, request);
    logger.info(String.format(LOGGER_FORMAT, request.toString(), channelMessage.toString(),
        request.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onProvideRoamingNumberResponse(ProvideRoamingNumberResponse response) {
    ChannelMessage channelMessage = getMessage(response.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, response);
    logger.info(String.format(LOGGER_FORMAT, response.toString(), channelMessage.toString(),
        response.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onIstCommandRequest(IstCommandRequest request) {
    ChannelMessage channelMessage = getMessage(request.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, request);
    logger.info(String.format(LOGGER_FORMAT, request.toString(), channelMessage.toString(),
        request.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onIstCommandResponse(IstCommandResponse response) {
    ChannelMessage channelMessage = getMessage(response.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, response);
    logger.info(String.format(LOGGER_FORMAT, response.toString(), channelMessage.toString(),
        response.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }
}
