package com.naikeri.sgw.impl.app.map;

import java.util.Optional;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * MapDialogOutType
 */
public class MapDialogOut {

  private Object responseObject;
  private MAPDialog mapDialog;
  private boolean isResponse = false;
  private Long origDialogId;
  private Long newInvokeId;
  private String logInvokeIds;
  private String discardReason;
  private MapProxyDialog proxyDialog;

  public void setDiscardReason(String reason) {
    this.discardReason = reason;
  }

  public String getDiscardReason() {
    return this.discardReason;
  }
  public Optional<String> getReason(){
    return Optional.ofNullable(this.discardReason).filter(s -> !s.isEmpty());
  }

  public MapDialogOut() {
    // 
  }

 
  public MapDialogOut(MAPDialog mapDialog, Long newInvokeId, Long originalDialogId) {
    this.mapDialog = mapDialog;
    this.newInvokeId = newInvokeId;
    this.origDialogId = originalDialogId;
  }
  public MapDialogOut(MAPDialog mapDialog, Long newInvokeId, Long originalDialogId, MapProxyDialog mapProxyDialog){
    this.mapDialog = mapDialog;
    this.newInvokeId = newInvokeId;
    this.origDialogId = originalDialogId;
    this.proxyDialog = mapProxyDialog;
  }

  public void setMapDialog(MAPDialog mapDialog) {
    this.mapDialog = mapDialog;
  }

  public void setOriginalDialogId(Long dialogId) {
    this.origDialogId = dialogId;
  }

  public void setInvokeId(Long newInvokeId) {
    this.newInvokeId = newInvokeId;
  }

  public Long getInvokeId() {
    return this.newInvokeId;
  }

  public Long getOriginalDialogId() {
    return this.origDialogId;
  }

  public void setIsResponse() {
    this.isResponse = true;
  }

  public boolean getIsResponse(){
    return this.isResponse;
  }
  public Long getNewDialogId() {
    if (this.mapDialog == null) {
      return -1L;
    }
    return this.mapDialog.getLocalDialogId();
  }

  /**
   * send the map request
   *
   * @throws MAPException
   */
  public void send() throws MAPException {
    if (this.isResponse) {
      mapDialog.close(false);
    } else {
      mapDialog.send();
    }
  }

  public Object getResponseObject() {
    return responseObject;
  }

  public void setResponseObject(Object responseObject) {
    this.responseObject = responseObject;
  }

  public String getLogInvokeIds() {
    return logInvokeIds;
  }

  public void setLogInvokeIds(Long respInvokeId, Long invokeId) {
    this.logInvokeIds =
        String.format("InvokeId = %d, Original InvokeId = %d", respInvokeId, invokeId);
  }

  public String getDialogOutName(String messageType) {
    if (messageType == null || messageType.isEmpty())
      return "";
    return messageType.substring(0, messageType.indexOf('_'));
  }

  public Object getRemoteAddress() {
    return this.mapDialog.getRemoteAddress();
  }

  public SccpAddress getLocalAddress() {
    return mapDialog.getLocalAddress();
  }

  public Long getRemoteDialogId() {
    return this.origDialogId;
  }

  public Long getLocalDialogId() {
    return mapDialog.getLocalDialogId();
  }

  public MapProxyDialog getProxyDialog() {
    return proxyDialog;
  }

  public void setProxyDialog(MapProxyDialog proxyDialog) {
    this.proxyDialog = proxyDialog;
  }

}
