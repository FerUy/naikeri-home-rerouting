package com.naikeri.sgw.network.layers.listeners.map;

import java.util.UUID;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.chn.IChannelHandler;
import com.naikeri.sgw.network.layers.listeners.MapProxyContants;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.service.oam.ActivateTraceModeRequest_Oam;
import org.restcomm.protocols.ss7.map.api.service.oam.ActivateTraceModeResponse_Oam;
import org.restcomm.protocols.ss7.map.api.service.oam.MAPServiceOamListener;
import org.restcomm.protocols.ss7.map.api.service.oam.SendImsiRequest;
import org.restcomm.protocols.ss7.map.api.service.oam.SendImsiResponse;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

public class MapOamListener implements MAPServiceOamListener {

  private static Logger logger = Logger.getLogger(MapOamListener.class);
  private IChannelHandler channelHandler;

  public MapOamListener(IChannelHandler channelHandler) {
    this.channelHandler = channelHandler;
    logger.debug("MapOamListener listening...");
  }

  private ChannelMessage getMessage(String messagetype) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter(MapProxyContants.MESSAGE_TYPE, messagetype);
    return channelMessage;
  }

  @Override
  public void onActivateTraceModeRequest_Oam(
      ActivateTraceModeRequest_Oam activateTraceModeRequestoam) {
    ChannelMessage channelMessage =
        getMessage(activateTraceModeRequestoam.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, activateTraceModeRequestoam);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        activateTraceModeRequestoam.toString(), channelMessage.toString(),
        activateTraceModeRequestoam.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onActivateTraceModeResponse_Oam(
      ActivateTraceModeResponse_Oam activateTraceModeResponseOam) {
    ChannelMessage channelMessage =
        getMessage(activateTraceModeResponseOam.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, activateTraceModeResponseOam);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        activateTraceModeResponseOam.toString(), channelMessage.toString(),
        activateTraceModeResponseOam.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendImsiRequest(SendImsiRequest sendImsiRequest) {
    ChannelMessage channelMessage = getMessage(sendImsiRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, sendImsiRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", sendImsiRequest.toString(),
        channelMessage.toString(), sendImsiRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onSendImsiResponse(SendImsiResponse sendImsiResponse) {
    ChannelMessage channelMessage = getMessage(sendImsiResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, sendImsiResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", sendImsiResponse.toString(),
        channelMessage.toString(), sendImsiResponse.getMAPDialog().getLocalDialogId()));
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
}
