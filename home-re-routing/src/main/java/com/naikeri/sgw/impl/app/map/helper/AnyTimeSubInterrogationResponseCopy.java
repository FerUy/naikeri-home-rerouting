package com.naikeri.sgw.impl.app.map.helper;

import java.util.ArrayList;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeSubscriptionInterrogationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.CAMELSubscriptionInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.CallBarringData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.CallForwardingData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.CallHoldData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.CallWaitingData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ClipData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ClirData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.EctData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MSISDNBS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ODBInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CSGSubscriptionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OfferedCamel4CSIs;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.SupportedCamelPhases;

/**
 * AnyTimeSubInterrogationResponseCopy
 */
public class AnyTimeSubInterrogationResponseCopy {

  private final CallForwardingData callForwardingData;

  private final CallBarringData callBarringData;

  private final ODBInfo odbInfo;

  private final CAMELSubscriptionInfo camelSubscriptionInfo;

  private final SupportedCamelPhases supportedVlrCamelPhases;

  private final SupportedCamelPhases supportedSgsnCamelPhases;

  private final MAPExtensionContainer extensionContainer;

  private final OfferedCamel4CSIs offeredCamel4CSIsInVlr;

  private final OfferedCamel4CSIs offeredCamel4CSIsInSgsn;

  private final ArrayList<MSISDNBS> msisdnBsList;

  private final ArrayList<CSGSubscriptionData> csgSubscriptionDataList;

  private final CallWaitingData cwData;

  private final CallHoldData chData;

  private final ClipData clipData;

  private final ClirData clirData;

  private final EctData ectData;

  public AnyTimeSubInterrogationResponseCopy(AnyTimeSubscriptionInterrogationResponse response){
    this.callBarringData = response.getCallBarringData();
    this.callForwardingData = response.getCallForwardingData();
    this.camelSubscriptionInfo = response.getCamelSubscriptionInfo();
    this.chData = response.getChData();
    this.clipData = response.getClipData();
    this.clirData = response.getClirData();
    this.csgSubscriptionDataList = response.getCsgSubscriptionDataList();
    this.cwData = response.getCwData();
    this.ectData = response.getEctData();
    this.extensionContainer = response.getExtensionContainer();
    this.msisdnBsList = response.getMsisdnBsList();
    this.odbInfo = response.getOdbInfo();
    this.offeredCamel4CSIsInSgsn = response.getOfferedCamel4CSIsInSgsn();
    this.offeredCamel4CSIsInVlr = response.getOfferedCamel4CSIsInVlr();
    this.supportedSgsnCamelPhases = response.getsupportedSgsnCamelPhases();
    this.supportedVlrCamelPhases = response.getsupportedVlrCamelPhases();

  }

  public CallForwardingData getCallForwardingData() {
    return callForwardingData;
  }

  public CallBarringData getCallBarringData() {
    return callBarringData;
  }

  public ODBInfo getOdbInfo() {
    return odbInfo;
  }

  public CAMELSubscriptionInfo getCamelSubscriptionInfo() {
    return camelSubscriptionInfo;
  }

  public SupportedCamelPhases getSupportedVlrCamelPhases() {
    return supportedVlrCamelPhases;
  }

  public SupportedCamelPhases getSupportedSgsnCamelPhases() {
    return supportedSgsnCamelPhases;
  }

  public MAPExtensionContainer getExtensionContainer() {
    return extensionContainer;
  }

  public OfferedCamel4CSIs getOfferedCamel4CSIsInVlr() {
    return offeredCamel4CSIsInVlr;
  }

  public OfferedCamel4CSIs getOfferedCamel4CSIsInSgsn() {
    return offeredCamel4CSIsInSgsn;
  }

  public ArrayList<MSISDNBS> getMsisdnBsList() {
    return msisdnBsList;
  }

  public ArrayList<CSGSubscriptionData> getCsgSubscriptionDataList() {
    return csgSubscriptionDataList;
  }

  public CallWaitingData getCwData() {
    return cwData;
  }

  public CallHoldData getChData() {
    return chData;
  }

  public ClipData getClipData() {
    return clipData;
  }

  public ClirData getClirData() {
    return clirData;
  }

  public EctData getEctData() {
    return ectData;
  }
}