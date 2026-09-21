package com.naikeri.sgw.impl.app.cap;

import org.restcomm.protocols.ss7.cap.api.isup.CalledPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.CallingPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.primitives.BCSMEvent;
import org.restcomm.protocols.ss7.cap.api.primitives.EventTypeBCSM;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ApplyChargingReportRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ApplyChargingRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CancelRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ConnectRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ContinueRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EstablishTemporaryConnectionRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EventReportBCSMRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.FurnishChargingInformationRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.InitialDPRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ReleaseCallRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.RequestReportBCSMEventRequest;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

import java.util.ArrayList;
import java.util.UUID;

public class BcsmCallContent {

  private final UUID uid;

  private BcsmCallStep step;
  private CAPDialogCircuitSwitchedCall capDialog, ssfCapDialog;
  private InitialDPRequest idp;
  private RequestReportBCSMEventRequest rrb;
  private EventReportBCSMRequest erb;
  private ArrayList<EventReportBCSMRequest> erbEventList = new ArrayList<>();
  private ArrayList<BCSMEvent> eventList = new ArrayList<>();
  private EventTypeBCSM eventTypeBCSM;
  private EstablishTemporaryConnectionRequest etc;
  private ContinueRequest cue;
  private ConnectRequest con;
  private CancelRequest can;
  private FurnishChargingInformationRequest fci;
  private ReleaseCallRequest rel;
  private ApplyChargingRequest ach;
  private ApplyChargingReportRequest acr;
  private IMSI originImsi, newImsi;
  private SccpAddress callingPartyAddress, calledPartyAddress;
  private int serviceKey;
  private CallingPartyNumberCap callingPartyNumberCap;
  private CalledPartyNumberCap calledPartyNumberCap;
  private String msrnNumber;

  public BcsmCallContent() {
    this.uid = UUID.randomUUID();
  }

  public BcsmCallStep getStep() {
    return step;
  }

  public void setMSRNNumber(String msrnNumber) {
    this.msrnNumber = msrnNumber;
  }

  public String getMSRNNumber() {
    return this.msrnNumber;
  }

  public void setStep(BcsmCallStep step) {
    this.step = step;
  }

  public CAPDialogCircuitSwitchedCall getCapDialog() {
    return capDialog;
  }

  public void setCapDialog(CAPDialogCircuitSwitchedCall capDialog) {
    this.capDialog = capDialog;
  }

  public InitialDPRequest getIdp() {
    return idp;
  }

  public void setIdp(InitialDPRequest idp) {
    this.idp = idp;
  }

  public RequestReportBCSMEventRequest getRrb() {
    return rrb;
  }

  public void setRrb(RequestReportBCSMEventRequest rrb) {
    this.rrb = rrb;
  }

  public EventReportBCSMRequest getErb() {
    return erb;
  }

  public void setErb(EventReportBCSMRequest erb) {
    this.erb = erb;
  }

  public ArrayList<BCSMEvent> getEventList() {
    return eventList;
  }

  public void setEventList(ArrayList<BCSMEvent> eventList) {
    this.eventList = eventList;
  }

  public ArrayList<EventReportBCSMRequest> getErbEventList() {
    return erbEventList;
  }

  public EventTypeBCSM getEventTypeBCSM() {
    return eventTypeBCSM;
  }

  public void setEventTypeBCSM(EventTypeBCSM eventTypeBCSM) {
    this.eventTypeBCSM = eventTypeBCSM;
  }

  public void setErbEventList(ArrayList<EventReportBCSMRequest> erbEventList) {
    this.erbEventList = erbEventList;
  }

  public EstablishTemporaryConnectionRequest getEtc() {
    return etc;
  }

  public void setEtc(EstablishTemporaryConnectionRequest etc) {
    this.etc = etc;
  }

  public ContinueRequest getCue() {
    return cue;
  }

  public void setCue(ContinueRequest cue) {
    this.cue = cue;
  }

  public ConnectRequest getCon() {
    return con;
  }

  public void setCon(ConnectRequest con) {
    this.con = con;
  }

  public CancelRequest getCan() {
    return can;
  }

  public void setCan(CancelRequest can) {
    this.can = can;
  }

  public FurnishChargingInformationRequest getFci() {
    return fci;
  }

  public void setFci(FurnishChargingInformationRequest fci) {
    this.fci = fci;
  }

  public IMSI getOriginImsi() {
    return originImsi;
  }

  public void setOriginImsi(IMSI originImsi) {
    this.originImsi = originImsi;
  }

  public IMSI getNewImsi() {
    return newImsi;
  }

  public void setNewImsi(IMSI newImsi) {
    this.newImsi = newImsi;
  }

  public SccpAddress getCallingPartyAddress() {
    return callingPartyAddress;
  }

  public void setCallingPartyAddress(SccpAddress callingPartyAddress) {
    this.callingPartyAddress = callingPartyAddress;
  }

  public SccpAddress getCalledPartyAddress() {
    return calledPartyAddress;
  }

  public void setCalledPartyAddress(SccpAddress calledPartyAddress) {
    this.calledPartyAddress = calledPartyAddress;
  }

  public int getServiceKey() {
    return serviceKey;
  }

  public void setServiceKey(int serviceKey) {
    this.serviceKey = serviceKey;
  }

  public CallingPartyNumberCap getCallingPartyNumberCap() {
    return callingPartyNumberCap;
  }

  public void setCallingPartyNumberCap(CallingPartyNumberCap callingPartyNumberCap) {
    this.callingPartyNumberCap = callingPartyNumberCap;
  }

  public CalledPartyNumberCap getCalledPartyNumberCap() {
    return calledPartyNumberCap;
  }

  public void setCalledPartyNumberCap(CalledPartyNumberCap calledPartyNumberCap) {
    this.calledPartyNumberCap = calledPartyNumberCap;
  }

  public ReleaseCallRequest getRel() {
    return rel;
  }

  public void setRel(ReleaseCallRequest rel) {
    this.rel = rel;
  }

  public ApplyChargingRequest getAch() {
    return ach;
  }

  public void setAch(ApplyChargingRequest ach) {
    this.ach = ach;
  }

  public ApplyChargingReportRequest getAcr() {
    return acr;
  }

  public void setAcr(ApplyChargingReportRequest acr) {
    this.acr = acr;
  }

  public UUID getUid() {
    return this.uid;
  }

  public void setSsfCapDialog(CAPDialogCircuitSwitchedCall capDialog) {
    this.ssfCapDialog = capDialog;
  }

  public CAPDialogCircuitSwitchedCall getSsfCapDialog() {
    return this.ssfCapDialog;
  }
}
