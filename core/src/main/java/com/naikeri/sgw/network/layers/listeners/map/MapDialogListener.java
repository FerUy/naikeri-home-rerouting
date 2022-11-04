package com.naikeri.sgw.network.layers.listeners.map;

import java.util.UUID;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.chn.IChannelHandler;
import com.naikeri.sgw.network.layers.listeners.MapProxyContants;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPDialogListener;
import org.restcomm.protocols.ss7.map.api.dialog.MAPAbortProviderReason;
import org.restcomm.protocols.ss7.map.api.dialog.MAPAbortSource;
import org.restcomm.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic;
import org.restcomm.protocols.ss7.map.api.dialog.MAPRefuseReason;
import org.restcomm.protocols.ss7.map.api.dialog.MAPUserAbortChoice;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.tcap.asn.ApplicationContextName;

public class MapDialogListener implements MAPDialogListener {

  private static Logger logger = Logger.getLogger(MapDialogListener.class);

  private IChannelHandler channelHandler;

  public MapDialogListener(IChannelHandler channelHandler) {
    this.channelHandler = channelHandler;
    logger.debug("MapDialogListener running");
  }

  private ChannelMessage getMessage(String message) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter(MapProxyContants.MESSAGE_TYPE, message);
    return channelMessage;
  }

  @Override
  public void onDialogDelimiter(MAPDialog mapDialog) {
    ChannelMessage channelMessage = getMessage("onDialogDelimiter");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogRequest(MAPDialog mapDialog, AddressString addressString,
      AddressString addressString1, MAPExtensionContainer mapExtensionContainer) {
    ChannelMessage channelMessage = getMessage("onDialogRequest");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter("addressString", addressString);
    channelMessage.setParameter("addressString1", addressString1);
    channelMessage.setParameter(MapProxyContants.MAP_EXTENSION_CONTAINER, mapExtensionContainer);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString addressString,
      AddressString addressString1, AddressString addressString2, AddressString addressString3) {
    ChannelMessage channelMessage = getMessage("onDialogRequestEricsson");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter("addressString", addressString);
    channelMessage.setParameter("addressString1", addressString1);
    channelMessage.setParameter("addressString3", addressString3);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogAccept(MAPDialog mapDialog, MAPExtensionContainer mapExtensionContainer) {
    ChannelMessage channelMessage = getMessage("onDialogAccept");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter(MapProxyContants.MAP_EXTENSION_CONTAINER, mapExtensionContainer);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogReject(MAPDialog mapDialog, MAPRefuseReason mapRefuseReason,
      ApplicationContextName applicationContextName, MAPExtensionContainer mapExtensionContainer) {
    ChannelMessage channelMessage = getMessage("onDialogReject");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter("mapRefuseReason", mapRefuseReason);
    channelMessage.setParameter(MapProxyContants.MAP_EXTENSION_CONTAINER, mapExtensionContainer);
    channelMessage.setParameter("applicationContextName", applicationContextName);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogUserAbort(MAPDialog mapDialog, MAPUserAbortChoice mapUserAbortChoice,
      MAPExtensionContainer mapExtensionContainer) {
    ChannelMessage channelMessage = getMessage("onDialogUserAbort");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter("mapUserAbortChoice", mapUserAbortChoice);
    channelMessage.setParameter(MapProxyContants.MAP_EXTENSION_CONTAINER, mapExtensionContainer);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogProviderAbort(MAPDialog mapDialog,
      MAPAbortProviderReason mapAbortProviderReason, MAPAbortSource mapAbortSource,
      MAPExtensionContainer mapExtensionContainer) {
    ChannelMessage channelMessage = getMessage("onDialogProviderAbort");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter("mapAbortSource", mapAbortSource);
    channelMessage.setParameter(MapProxyContants.MAP_EXTENSION_CONTAINER, mapExtensionContainer);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogClose(MAPDialog mapDialog) {
    ChannelMessage channelMessage = getMessage(MapProxyContants.ON_DIALOG_CLOSE);
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogNotice(MAPDialog mapDialog,
      MAPNoticeProblemDiagnostic mapNoticeProblemDiagnostic) {
    ChannelMessage channelMessage = getMessage("onDialogNotice");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    channelMessage.setParameter("mapNoticeProblemDiagnostic", mapNoticeProblemDiagnostic);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogRelease(MAPDialog mapDialog) {
    ChannelMessage channelMessage = getMessage("onDialogRelease");
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogTimeout(MAPDialog mapDialog) {
    ChannelMessage channelMessage = getMessage(MapProxyContants.ON_DIALOG_TIMEOUT);
    channelMessage.setParameter(MapProxyContants.DIALOG, mapDialog);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }
}
