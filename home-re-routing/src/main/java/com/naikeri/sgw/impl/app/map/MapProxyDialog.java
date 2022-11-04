package com.naikeri.sgw.impl.app.map;

import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.service.callhandling.MAPDialogCallHandling;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.oam.MAPDialogOam;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPDialogSms;
import org.restcomm.protocols.ss7.map.api.service.supplementary.MAPDialogSupplementary;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

public class MapProxyDialog {
  private MAPDialogMobility mapDialogMobility;
  private MAPDialogCallHandling mapDialogCallHandling;
  private MAPDialogSms mapDialogSms;
  private MapDialogType mapDialogType;
  private IMSI updatedImsi;
  private MAPDialogOam mapDialogOam;
  private MAPDialogSupplementary mapDialogSupplementary;
  private SccpAddress callingAddress;
  private SccpAddress calledAddress;
  private SccpAddress newCallingAddress;
  private SccpAddress newCalledAddress;
  private String origImsi;
  private String ruleName;
  private String newImsi;
  private String newCallingGt;
  private String newCalledGt;

  /**
   * Instantiate the MapProxyDialog class
   * @param callingAddress The SCCP Callling Address
   * @param calledAddress The SCCP Called Address
   * @param imsi The IMSI
   */
  public MapProxyDialog(SccpAddress callingAddress, SccpAddress calledAddress, String imsi) {
    this.calledAddress = calledAddress;
    this.callingAddress = callingAddress;
    this.origImsi = imsi;
  }

  public SccpAddress getCallingAddress(){
    return this.callingAddress;
  }
  public SccpAddress getCalledAddress(){
    return this.calledAddress;
  }
  public String getOriginalImsi(){
    return this.origImsi;
  }
  public String getRuleName(){
    return this.ruleName;
  }
  public void setRuleName(String rulename){
    this.ruleName = rulename;
  }
  public void setUpdatedImsi(String imsi){
    this.newImsi = imsi;
    if (imsi != null && !imsi.isEmpty()) {
      this.updatedImsi = new IMSIImpl(imsi);
    } else {
      this.updatedImsi = null;
    }
  }

  public String getNewIMSI(){
    return this.newImsi;
  }
  public IMSI getImsi(){
    return this.updatedImsi;
  }
  public MAPDialogMobility getMapDialogMobility() {
    return mapDialogMobility;
  }

  public void setMapDialogMobility(MAPDialogMobility mapDialogMobility) {
    this.mapDialogMobility = mapDialogMobility;
  }

  public MAPDialogCallHandling getMapDialogCallHandling() {
    return mapDialogCallHandling;
  }

  public void setMapDialogCallHandling(MAPDialogCallHandling mapDialogCallHandling) {
    this.mapDialogCallHandling = mapDialogCallHandling;
  }

  public MAPDialogSms getMapDialogSms() {
    return mapDialogSms;
  }

  public void setMapDialogSms(MAPDialogSms mapDialogSms) {
    this.mapDialogSms = mapDialogSms;
  }

  public MapDialogType getMapDialogType() {
    return mapDialogType;
  }

  public void setMapDialogType(MapDialogType mapDialogType) {
    this.mapDialogType = mapDialogType;
  }

  public MAPDialogOam getMapDialogOam() {
    return mapDialogOam;
  }

  public void setMapDialogOam(MAPDialogOam mapDialogOam) {
    this.mapDialogOam = mapDialogOam;
  }

  public MAPDialogSupplementary getMapDialogSupplementary() {
    return mapDialogSupplementary;
  }

  public void setMapDialogSupplementary(MAPDialogSupplementary mapDialogSupplementary) {
    this.mapDialogSupplementary = mapDialogSupplementary;
  }

  public String getNewCallingGt() {
    return newCallingGt;
  }

  public void setNewCallingGt(String newCallingGt) {
    this.newCallingGt = newCallingGt;
  }

  public String getNewCalledGt() {
    return newCalledGt;
  }

  public void setNewCalledGt(String newCalledGt) {
    this.newCalledGt = newCalledGt;
  }

  public SccpAddress getNewCallingAddress() {
    return newCallingAddress;
  }

  public void setNewCallingAddress(SccpAddress newCallingAddress) {
    this.newCallingAddress = newCallingAddress;
  }

  public SccpAddress getNewCalledAddress() {
    return newCalledAddress;
  }

  public void setNewCalledAddress(SccpAddress newCalledAddress) {
    this.newCalledAddress = newCalledAddress;
  }
}