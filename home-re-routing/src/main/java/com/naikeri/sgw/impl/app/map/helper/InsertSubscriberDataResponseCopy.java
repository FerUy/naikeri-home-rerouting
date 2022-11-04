package com.naikeri.sgw.impl.app.map.helper;

import java.util.ArrayList;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedFeatures;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBearerServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtTeleserviceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ODBGeneralData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OfferedCamel4CSIs;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.RegionalSubscriptionResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SupportedCamelPhases;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SSCode;

/**
 * InsertSubscriberDataResponseCopy
 */
public class InsertSubscriberDataResponseCopy {

  private ArrayList<ExtTeleserviceCode> teleserviceList;

  private ArrayList<ExtBearerServiceCode> bearerServiceList;

  private ArrayList<SSCode> sSList;

  private ODBGeneralData oDBGeneralData;

  private RegionalSubscriptionResponse regionalSubscriptionResponse;

  private SupportedCamelPhases supportedCamelPhases;

  private MAPExtensionContainer extensionContainer;

  private OfferedCamel4CSIs offeredCamel4CSIs;

  private SupportedFeatures supportedFeatures;

  public InsertSubscriberDataResponseCopy(InsertSubscriberDataResponse response){
    this.teleserviceList = response.getTeleserviceList();
    this.bearerServiceList = response.getBearerServiceList();
    this.sSList = response.getSSList();
    this.oDBGeneralData = response.getODBGeneralData();
    this.regionalSubscriptionResponse = response.getRegionalSubscriptionResponse();
    this.supportedCamelPhases = response.getSupportedCamelPhases();
    this.extensionContainer = response.getExtensionContainer();
    this.offeredCamel4CSIs = response.getOfferedCamel4CSIs();
    this.supportedFeatures = response.getSupportedFeatures();
  }

  public ArrayList<ExtTeleserviceCode> getTeleserviceList() {
    return teleserviceList;
  }

  public void setTeleserviceList(ArrayList<ExtTeleserviceCode> teleserviceList) {
    this.teleserviceList = teleserviceList;
  }

  public ArrayList<ExtBearerServiceCode> getBearerServiceList() {
    return bearerServiceList;
  }

  public void setBearerServiceList(ArrayList<ExtBearerServiceCode> bearerServiceList) {
    this.bearerServiceList = bearerServiceList;
  }

  public ArrayList<SSCode> getSSList() {
    return sSList;
  }

  public void setsSList(ArrayList<SSCode> sSList) {
    this.sSList = sSList;
  }

  public ODBGeneralData getODBGeneralData() {
    return oDBGeneralData;
  }

  public void setoDBGeneralData(ODBGeneralData oDBGeneralData) {
    this.oDBGeneralData = oDBGeneralData;
  }

  public RegionalSubscriptionResponse getRegionalSubscriptionResponse() {
    return regionalSubscriptionResponse;
  }

  public void setRegionalSubscriptionResponse(
      RegionalSubscriptionResponse regionalSubscriptionResponse) {
    this.regionalSubscriptionResponse = regionalSubscriptionResponse;
  }

  public SupportedCamelPhases getSupportedCamelPhases() {
    return supportedCamelPhases;
  }

  public void setSupportedCamelPhases(SupportedCamelPhases supportedCamelPhases) {
    this.supportedCamelPhases = supportedCamelPhases;
  }

  public MAPExtensionContainer getExtensionContainer() {
    return extensionContainer;
  }

  public void setExtensionContainer(MAPExtensionContainer extensionContainer) {
    this.extensionContainer = extensionContainer;
  }

  public OfferedCamel4CSIs getOfferedCamel4CSIs() {
    return offeredCamel4CSIs;
  }

  public void setOfferedCamel4CSIs(OfferedCamel4CSIs offeredCamel4CSIs) {
    this.offeredCamel4CSIs = offeredCamel4CSIs;
  }

  public SupportedFeatures getSupportedFeatures() {
    return supportedFeatures;
  }

  public void setSupportedFeatures(SupportedFeatures supportedFeatures) {
    this.supportedFeatures = supportedFeatures;
  }

}