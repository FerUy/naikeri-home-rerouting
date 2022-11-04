package com.naikeri.sgw.network.layers.listeners.map;

import java.util.UUID;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.chn.IChannelHandler;
import com.naikeri.sgw.network.layers.listeners.MapProxyContants;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.service.supplementary.ActivateSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.ActivateSSResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.DeactivateSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.DeactivateSSResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.EraseSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.EraseSSResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.GetPasswordRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.GetPasswordResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.InterrogateSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.InterrogateSSResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.MAPServiceSupplementaryListener;
import org.restcomm.protocols.ss7.map.api.service.supplementary.ProcessUnstructuredSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.ProcessUnstructuredSSResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.RegisterPasswordRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.RegisterPasswordResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.RegisterSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.RegisterSSResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.UnstructuredSSNotifyRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.UnstructuredSSNotifyResponse;
import org.restcomm.protocols.ss7.map.api.service.supplementary.UnstructuredSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.UnstructuredSSResponse;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

public class MapSupplementaryServiceListener implements MAPServiceSupplementaryListener {

  private static Logger logger = Logger.getLogger(MapSupplementaryServiceListener.class);
  private IChannelHandler channelHandler;

  public MapSupplementaryServiceListener(IChannelHandler channelHandler) {
    this.channelHandler = channelHandler;
    logger.debug("MapSupplementaryServiceListener listening for events...");
  }

  private ChannelMessage getMessage(String messagetype) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter("messageType", messagetype);
    return channelMessage;
  }

  @Override
  public void onRegisterSSRequest(RegisterSSRequest registerSSRequest) {

    ChannelMessage channelMessage = getMessage(registerSSRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, registerSSRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", registerSSRequest.toString(),
        channelMessage.toString(), registerSSRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRegisterSSResponse(RegisterSSResponse registerSSResponse) {
    ChannelMessage channelMessage = getMessage(registerSSResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, registerSSResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", registerSSResponse.toString(),
        channelMessage.toString(), registerSSResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onEraseSSRequest(EraseSSRequest eraseSSRequest) {
    ChannelMessage channelMessage = getMessage(eraseSSRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, eraseSSRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", eraseSSRequest.toString(),
        channelMessage.toString(), eraseSSRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onEraseSSResponse(EraseSSResponse eraseSSResponse) {
    ChannelMessage channelMessage = getMessage(eraseSSResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, eraseSSResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", eraseSSResponse.toString(),
        channelMessage.toString(), eraseSSResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onActivateSSRequest(ActivateSSRequest activateSSRequest) {
    ChannelMessage channelMessage = getMessage(activateSSRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, activateSSRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", activateSSRequest.toString(),
        channelMessage.toString(), activateSSRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onActivateSSResponse(ActivateSSResponse activateSSResponse) {
    ChannelMessage channelMessage = getMessage(activateSSResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, activateSSResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", activateSSResponse.toString(),
        channelMessage.toString(), activateSSResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDeactivateSSRequest(DeactivateSSRequest deactivateSSRequest) {
    ChannelMessage channelMessage = getMessage(deactivateSSRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, deactivateSSRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", deactivateSSRequest.toString(),
        channelMessage.toString(), deactivateSSRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDeactivateSSResponse(DeactivateSSResponse deactivateSSResponse) {
    ChannelMessage channelMessage = getMessage(deactivateSSResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, deactivateSSResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", deactivateSSResponse.toString(),
        channelMessage.toString(), deactivateSSResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInterrogateSSRequest(InterrogateSSRequest interrogateSSRequest) {
    ChannelMessage channelMessage = getMessage(interrogateSSRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, interrogateSSRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", interrogateSSRequest.toString(),
        channelMessage.toString(), interrogateSSRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInterrogateSSResponse(InterrogateSSResponse interrogateSSResponse) {
    ChannelMessage channelMessage = getMessage(interrogateSSResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, interrogateSSResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", interrogateSSResponse.toString(),
        channelMessage.toString(), interrogateSSResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onGetPasswordRequest(GetPasswordRequest getPasswordRequest) {
    ChannelMessage channelMessage = getMessage(getPasswordRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, getPasswordRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", getPasswordRequest.toString(),
        channelMessage.toString(), getPasswordRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onGetPasswordResponse(GetPasswordResponse getPasswordResponse) {
    ChannelMessage channelMessage = getMessage(getPasswordResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, getPasswordResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", getPasswordResponse.toString(),
        channelMessage.toString(), getPasswordResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRegisterPasswordRequest(RegisterPasswordRequest registerPasswordRequest) {
    ChannelMessage channelMessage = getMessage(registerPasswordRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, registerPasswordRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", registerPasswordRequest.toString(),
        channelMessage.toString(), registerPasswordRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRegisterPasswordResponse(RegisterPasswordResponse registerPasswordResponse) {
    ChannelMessage channelMessage =
        getMessage(registerPasswordResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, registerPasswordResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", registerPasswordResponse.toString(),
        channelMessage.toString(), registerPasswordResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onProcessUnstructuredSSRequest(
      ProcessUnstructuredSSRequest processUnstructuredSSRequest) {
    ChannelMessage channelMessage =
        getMessage(processUnstructuredSSRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, processUnstructuredSSRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        processUnstructuredSSRequest.toString(), channelMessage.toString(),
        processUnstructuredSSRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onProcessUnstructuredSSResponse(
      ProcessUnstructuredSSResponse processUnstructuredSSResponse) {
    ChannelMessage channelMessage =
        getMessage(processUnstructuredSSResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, processUnstructuredSSResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        processUnstructuredSSResponse.toString(), channelMessage.toString(),
        processUnstructuredSSResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onUnstructuredSSRequest(UnstructuredSSRequest unstructuredSSRequest) {
    ChannelMessage channelMessage = getMessage(unstructuredSSRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, unstructuredSSRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", unstructuredSSRequest.toString(),
        channelMessage.toString(), unstructuredSSRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onUnstructuredSSResponse(UnstructuredSSResponse unstructuredSSResponse) {
    ChannelMessage channelMessage = getMessage(unstructuredSSResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, unstructuredSSResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", unstructuredSSResponse.toString(),
        channelMessage.toString(), unstructuredSSResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onUnstructuredSSNotifyRequest(
      UnstructuredSSNotifyRequest unstructuredSSNotifyRequest) {
    ChannelMessage channelMessage =
        getMessage(unstructuredSSNotifyRequest.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, unstructuredSSNotifyRequest);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ", unstructuredSSNotifyRequest.toString(),
        channelMessage.toString(), unstructuredSSNotifyRequest.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onUnstructuredSSNotifyResponse(
      UnstructuredSSNotifyResponse unstructuredSSNotifyResponse) {
    ChannelMessage channelMessage =
        getMessage(unstructuredSSNotifyResponse.getMessageType().toString());
    channelMessage.setParameter(MapProxyContants.MESSAGE, unstructuredSSNotifyResponse);
    logger.info(String.format("%s, %s, [ DialogId = '%d'] ",
        unstructuredSSNotifyResponse.toString(), channelMessage.toString(),
        unstructuredSSNotifyResponse.getMAPDialog().getLocalDialogId()));
    channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onErrorComponent(MAPDialog mapDialog, Long aLong, MAPErrorMessage mapErrorMessage) {
    ChannelMessage channelMessage = getMessage("onErrorComponent");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, aLong);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onRejectComponent(MAPDialog mapDialog, Long aLong, Problem problem, boolean b) {
    ChannelMessage channelMessage = getMessage("onRejectComponent");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter(MapProxyContants.INVOKE_ID, aLong);
    channelMessage.setParameter("problem", problem);
    channelMessage.setParameter("boolean", b);
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
