package com.naikeri.sgw.network.layers.listeners.cap;

import java.util.UUID;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.chn.IChannelHandler;
import com.naikeri.sgw.network.layers.listeners.MapProxyContants;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPDialog;
import org.restcomm.protocols.ss7.cap.api.CAPDialogListener;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPGeneralAbortReason;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPGprsReferenceNumber;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPNoticeProblemDiagnostic;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPUserAbortReason;
import org.restcomm.protocols.ss7.tcap.asn.comp.PAbortCauseType;

public class CapDialogListener implements CAPDialogListener {

  private static Logger logger = Logger.getLogger(CapDialogListener.class);
  private String layerName;
  private IChannelHandler channelHandler;

  public CapDialogListener(IChannelHandler channelHandler, String layerName) {
    this.channelHandler = channelHandler;
    this.layerName = layerName;
    logger.info("CapDialogListener running");
  }

  private ChannelMessage getMessage(String message) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Cap");
    channelMessage.setParameter(MapProxyContants.MESSAGE_TYPE, message);
    channelMessage.setParameter(MapProxyContants.CAP_LAYER_NAME, this.layerName);
    return channelMessage;
  }

  @Override
  public void onDialogDelimiter(CAPDialog capDialog) {
    ChannelMessage channelMessage = getMessage("onDialogDelimiter");
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogRequest(CAPDialog capDialog, CAPGprsReferenceNumber capGprsReferenceNumber) {
    ChannelMessage channelMessage = getMessage("onDialogRequest"); 
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    channelMessage.setParameter("gprs-ref-num", capGprsReferenceNumber);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogAccept(CAPDialog capDialog, CAPGprsReferenceNumber capGprsReferenceNumber) {
    ChannelMessage channelMessage = getMessage("onDialogAccept");
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    channelMessage.setParameter("gprs-ref-num", capGprsReferenceNumber);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogUserAbort(CAPDialog capDialog, CAPGeneralAbortReason capGeneralAbortReason,
                                CAPUserAbortReason capUserAbortReason) {
    ChannelMessage channelMessage = getMessage("onDialogUserAbort");
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    channelMessage.setParameter("g-abort", capGeneralAbortReason);
    channelMessage.setParameter("u-abort", capUserAbortReason);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogProviderAbort(CAPDialog capDialog, PAbortCauseType pAbortCauseType) {
    ChannelMessage channelMessage = getMessage("onDialogProviderAbort");
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    channelMessage.setParameter("p-abort", pAbortCauseType);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogClose(CAPDialog capDialog) {
    ChannelMessage channelMessage = getMessage(MapProxyContants.ON_DIALOG_CLOSE);
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogRelease(CAPDialog capDialog) {
    ChannelMessage channelMessage = getMessage("onDialogRelease");
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogTimeout(CAPDialog capDialog) {
    ChannelMessage channelMessage = getMessage("onDialogTimeout");
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogNotice(CAPDialog capDialog, CAPNoticeProblemDiagnostic capNoticeProblemDiagnostic) {
    ChannelMessage channelMessage = getMessage("onDialogNotice");
    channelMessage.setParameter(MapProxyContants.DIALOG, capDialog);
    channelMessage.setParameter("notice", capNoticeProblemDiagnostic);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }
}
