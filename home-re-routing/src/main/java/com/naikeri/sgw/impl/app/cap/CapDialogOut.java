package com.naikeri.sgw.impl.app.cap;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restcomm.protocols.ss7.cap.api.CAPDialog;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.restcomm.protocols.ss7.cap.api.service.gprs.CAPDialogGprs;
import org.restcomm.protocols.ss7.cap.api.service.sms.CAPDialogSms;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * CapDialogOutType
 */
public class CapDialogOut {
  private static final Logger logger = LoggerFactory.getLogger(CapDialogOut.class);

  private CapDialogType capDialogType;
  private CAPDialog capDialog;
  private boolean isClose = false;
  private String channelTransId;
  private String discardReason;
  private boolean isDiscarded = false;
  private SccpAddress callingSccpAddress;
  private SccpAddress calledSccpAddress;
  private SccpAddress newCallingSccpAddress;
  private SccpAddress newCalledSccpAddress;
  private String imsiString;
  private String ruleNames;
  private Long remoteDialogId;
  private int serviceKey;
  private Long localDialogId;
  private String newImsi;
  private String msrnNumber;
  private Long transDialogId;
  private boolean isFirstLegCall = false;
  private String callingPartyNumber;
  private String calledPartyNumber;
  private String erbEventName;

  // used internally to check if it is at the end of the call flow
  // so the logs can be written to file
  public enum WriteLogState {
    INITIAL, ADDMORE, WRITE,
  }

  private WriteLogState writelogs;


  public CapDialogOut(CapDialogType capDialogType, String channelTransId) {
    this.capDialogType = capDialogType;
    this.channelTransId = channelTransId;
  }

  public String getErbEventName() {
    return erbEventName;
  }

  public void setErbEventName(String erbEventName) {
    this.erbEventName = erbEventName;
  }

  public String getCalledPartyNumber() {
    return calledPartyNumber;
  }

  public void setCalledPartyNumber(String calledPartyNumber) {
    this.calledPartyNumber = calledPartyNumber;
  }

  public String getCallingPartyNumber() {
    return callingPartyNumber;
  }

  public void setCallingPartyNumber(String callingPartyNumber) {
    this.callingPartyNumber = callingPartyNumber;
  }

  public boolean isFirstLegCall() {
    return isFirstLegCall;
  }

  public void setFirstLegCall(boolean isFirstLegCall) {
    this.isFirstLegCall = isFirstLegCall;
  }

  public Long getTransDialogId() {
    return transDialogId;
  }

  public void setTransDialogId(Long dialogId) {
    this.transDialogId = dialogId;
  }

  public CapDialogType getCapDialogType() {
    return capDialogType;
  }

  public void setIsClose(boolean isClose) {
    this.isClose = isClose;
  }

  public boolean getIsClose() {
    return this.isClose;
  }

  public void setCapDialogCircuitSwitchedCall(CAPDialogCircuitSwitchedCall capDialog) {
    this.capDialog = capDialog;
  }

  public void setCapDialogGprs(CAPDialogGprs capDialogGprs) {
    this.capDialog = capDialogGprs;
  }

  public void setCapDialogSms(CAPDialogSms capDialogSms) {
    this.capDialog = capDialogSms;
  }

  public Long getNewCapDialogId() {
    return capDialog.getLocalDialogId();
  }

  /**
   * send the CAP request
   *
   * @throws CAPException
   */
  public void send() throws CAPException {
    if (this.isClose) {
      logger.debug(
          String.format("CAP Response to close dialog for: %s, %s", capDialog, channelTransId));
      capDialog.close(false);
    } else {
      logger.debug(String.format("CAP Response for: %s, %s", capDialog, channelTransId));
      capDialog.send();
    }
  }

  public String getDiscardReason() {
    return discardReason;
  }

  public void setDiscardReason(String discardReason) {
    this.discardReason = discardReason;
    if (discardReason != null && !discardReason.isEmpty()) {
      this.isDiscarded = true;
    }
  }

  public boolean isDiscarded() {
    return this.isDiscarded;
  }

  public SccpAddress getCallingSccpAddress() {
    return callingSccpAddress;
  }

  public void setCallingSccpAddress(SccpAddress callingSccpAddress) {
    this.callingSccpAddress = callingSccpAddress;
  }

  public SccpAddress getCalledSccpAddress() {
    return calledSccpAddress;
  }

  public Optional<SccpAddress> getCalledSccpAddressOpt() {
    return Optional.ofNullable(calledSccpAddress);
  }

  public void setCalledSccpAddress(SccpAddress calledSccpAddress) {
    this.calledSccpAddress = calledSccpAddress;
  }

  public SccpAddress getNewCallingSccpAddress() {
    return newCallingSccpAddress;
  }

  public Optional<SccpAddress> getNewCallingSccpAddressOpt() {
    return Optional.ofNullable(newCallingSccpAddress);
  }

  public void setNewCallingSccpAddress(SccpAddress newCallingSccpAddress) {
    this.newCallingSccpAddress = newCallingSccpAddress;
  }

  public SccpAddress getNewCalledSccpAddress() {
    return newCalledSccpAddress;
  }

  public void setNewCalledSccpAddress(SccpAddress newCalledSccpAddress) {
    this.newCalledSccpAddress = newCalledSccpAddress;
  }

  public String getOriginalImsi() {
    return imsiString;
  }

  public void setOriginalIMSI(String imsiString) {
    this.imsiString = imsiString;
  }

  public void setNewIMSI(String newImsi) {
    this.newImsi = newImsi;
  }

  public String getNewIMSI() {
    return this.newImsi;
  }

  public void setServiceKey(int serviceKey) {
    this.serviceKey = serviceKey;
  }

  public int getServiceKey() {
    return this.serviceKey;
  }



  public void setLocalDialogId(Long localdialogIdLong) {
    this.localDialogId = localdialogIdLong;
  }

  public Long getLocalDialogId() {
    return this.localDialogId;
  }



  public void setRemoteDialogId(Long dialogId) {
    this.remoteDialogId = dialogId;
  }

  public Long getRemoteDialogId() {
    return this.remoteDialogId;
  }

  public String getRuleName() {
    return ruleNames;
  }

  public void setRuleName(String ruleName) {
    this.ruleNames = ruleName;
  }

  public void setWriteCDR(WriteLogState state) {
    this.writelogs = state;
  }

  public WriteLogState getWriteCDRState() {
    return this.writelogs;
  }

  public void setMSRN(String msrnNumber) {
    this.msrnNumber = msrnNumber;
  }

  public String getMSRN() {
    return this.msrnNumber;
  }
}
