package com.naikeri.sgw.impl.settings.sccp;

import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

public final class SccpAddressWithSettings {

  private SccpAddress sccpAddress;
  private String routingIndicator;
  private int dpc;
  private int ssn;
  private String type;
  private int translationType;
  private EncodingScheme encodingScheme;
  private NumberingPlan numberingPlan;
  private NatureOfAddress natureofAddress;
  private String digit;

  public SccpAddress getSccpAddress() {
    return sccpAddress;
  }

  public void setSccpAddress(SccpAddress sccpAddress) {
    this.sccpAddress = sccpAddress;
  }

  public String getRoutingIndicator() {
    return routingIndicator;
  }

  public void setRoutingIndicator(String routingIndicator) {
    this.routingIndicator = routingIndicator;
  }

  public int getDpc() {
    return dpc;
  }

  public void setDpc(int dpc) {
    this.dpc = dpc;
  }

  public int getSsn() {
    return ssn;
  }

  public void setSsn(int ssn) {
    this.ssn = ssn;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getTranslationType() {
    return translationType;
  }

  public void setTranslationType(int translationType) {
    this.translationType = translationType;
  }

  public EncodingScheme getEncodingScheme() {
    return encodingScheme;
  }

  public void setEncodingScheme(EncodingScheme encodingScheme) {
    this.encodingScheme = encodingScheme;
  }

  public NumberingPlan getNumberingPlan() {
    return numberingPlan;
  }

  public void setNumberingPlan(NumberingPlan numberingPlan) {
    this.numberingPlan = numberingPlan;
  }

  public NatureOfAddress getNatureofAddress() {
    return natureofAddress;
  }

  public void setNatureofAddress(NatureOfAddress natureofAddress) {
    this.natureofAddress = natureofAddress;
  }

  public String getDigit() {
    return digit;
  }

  public void setDigit(String digit) {
    this.digit = digit;
  }

  public Boolean compareDigits(String newdigit){
    return this.digit.equalsIgnoreCase(newdigit);
  }

}

// SccpAddressWithSettings sccpWithSettings = new SccpAddressWithSettings();
//     sccpWithSettings.setDigit(digit);
//     sccpWithSettings.setDpc(dpc);
//     sccpWithSettings.setEncodingScheme(ec);
//     sccpWithSettings.setNatureofAddress(noa);
//     sccpWithSettings.setRoutingIndicator(routingIndicator);
//     sccpWithSettings.setSccpAddress(retCccpAddress);
//     sccpWithSettings.setSsn(ssn);
//     sccpWithSettings.setTranslationType(translationType);
//     sccpWithSettings.setType(type);
//     sccpWithSettings.setNumberingPlan(np);