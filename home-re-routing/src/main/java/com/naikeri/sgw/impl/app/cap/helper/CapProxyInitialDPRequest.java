package com.naikeri.sgw.impl.app.cap.helper;

import java.util.ArrayList;
import java.util.Optional;
import com.naikeri.sgw.impl.app.cap.BcsmCallContent;
import com.naikeri.sgw.impl.app.cap.BcsmCallStep;
import com.naikeri.sgw.impl.app.cap.CapDialogOut;
import com.naikeri.sgw.impl.app.cap.CapDialogOut.WriteLogState;
import com.naikeri.sgw.impl.app.cap.CapDialogType;
import com.naikeri.sgw.impl.app.cap.CapProxyHelperUtils;
import com.naikeri.sgw.impl.rules.ApplyCapApplicationRules;
import com.naikeri.sgw.impl.rules.CapApplicationRulesResult;
import com.naikeri.sgw.impl.rules.PatternSccpAddress;
import com.naikeri.sgw.impl.rules.ReplacedValues;
import com.naikeri.sgw.info.CapTransaction;
import com.naikeri.sgw.info.ServiceKeys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPApplicationContext;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPStack;
import org.restcomm.protocols.ss7.cap.api.isup.CalledPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.InitialDPRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.RequestReportBCSMEventRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.DestinationRoutingAddress;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.isup.message.parameter.CalledPartyNumber;
import org.restcomm.protocols.ss7.isup.message.parameter.NAINumber;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * InitialDPRequest
 */
public class CapProxyInitialDPRequest {

  private static final Logger logger = LogManager.getLogger(CapProxyInitialDPRequest.class);
  private final CAPStack capStack;
  private final CAPStack capStackOut;
  private final String channelTransId;
  private final InitialDPRequest request;

  public CapProxyInitialDPRequest(InitialDPRequest request, CAPStack capStack, CAPStack capStackOut,
      String channelTransId) {
    this.request = request;
    this.capStack = capStack;
    this.capStackOut = capStackOut;
    this.channelTransId = channelTransId;
  }

  public CapDialogOut process() {
    try {
      if (request.getServiceKey() == ServiceKeys.getInstance().getServiceKey("initialDP_Request")) {
        return secondLegIDP(request);
      } else {
        return firstLegIDP(request);
      }
    } catch (CAPException capEx) {
        logger.error("Processing CAP IDP Request failed  for {}. Details: ", channelTransId, capEx);
      return CapProxyHelperUtils.discardReason(CapDialogType.CircuitSwitchedCallControl,
          capEx.getMessage(), request.getMessageType().toString(), channelTransId);
    } catch (Exception e) {
        logger.error("Exception caught for {}. Details: ", channelTransId, e);
      return CapProxyHelperUtils.discardReason(CapDialogType.CircuitSwitchedCallControl,
          e.getMessage(), request.getMessageType().toString(), channelTransId);
    }
  }

  private CapDialogOut firstLegIDP(InitialDPRequest request) throws CAPException {
    Long dialogId = request.getCAPDialog().getLocalDialogId();
    String messageType = request.getMessageType().toString();

    logger.debug("[CAP::REQUEST<{}>] LEG1: Local DialogId = '{}', Remote dialogId = '{}', invokeId = '{}' ServiceKey = '{}', {}",
        messageType, dialogId, request.getCAPDialog().getRemoteDialogId(), request.getInvokeId(), request.getServiceKey(), this.channelTransId);
    CapDialogOut capDialogOut =
        new CapDialogOut(CapDialogType.CircuitSwitchedCallControl, channelTransId);
    capDialogOut.setLocalDialogId(dialogId);
    capDialogOut.setRemoteDialogId(request.getCAPDialog().getRemoteDialogId());
    capDialogOut.setFirstLegCall(true);
    // CAP IDP is not initiated after a CAP CON (#A->#C), being #C a MSRN
    // hence, this is a new call from the VPLMN
    BcsmCallContent bcsmCallContent = new BcsmCallContent();
    bcsmCallContent.setCapDialog(request.getCAPDialog());
    bcsmCallContent.setIdp(request);
    bcsmCallContent.setStep(BcsmCallStep.idpReceived);
    bcsmCallContent.setCallingPartyNumberCap(request.getCallingPartyNumber());
    bcsmCallContent.setCalledPartyNumberCap(request.getCalledPartyNumber());
    SccpAddress callingAddress = request.getCAPDialog().getRemoteAddress();
    SccpAddress calledAddress = request.getCAPDialog().getLocalAddress();
    // add the calling and called sccp address for cdr writing
    capDialogOut.setCalledSccpAddress(calledAddress);
    capDialogOut.setCallingSccpAddress(callingAddress);
    // add the calling numbers for CDR logging
    String callingPartyNumberStr = getCallingPartyNumberAddress();
    String calledPartyNumberStr = getCalledPartyNumberAddress();
    capDialogOut.setCallingPartyNumber(callingPartyNumberStr);
    capDialogOut.setCalledPartyNumber(calledPartyNumberStr);

    String imsi = Optional.ofNullable(request.getIMSI()).map(IMSI::getData).orElse("");
    // add the IMSI for cdr writing
    capDialogOut.setOriginalIMSI(imsi);
    CapApplicationRulesResult result = ApplyCapApplicationRules.apply(callingAddress, calledAddress,
        imsi, messageType, this.channelTransId);
    if (result != null) {
      capDialogOut.setRuleName(result.getRuleName());
      ReplacedValues replacedValues = result.getReplacedValues();
      if (replacedValues == null) {
        return CapProxyHelperUtils.discardReason(CapDialogType.CircuitSwitchedCallControl,
            "Failed to replace values", messageType, channelTransId);
      }
      if (replacedValues.getCalledGlobalTitle() != null) {
        GlobalTitle gt = replacedValues.getCalledGlobalTitle();
        RoutingIndicator ri = replacedValues.getCalledSccpAddressParam()
            .flatMap(PatternSccpAddress::getRoutingIndicator)
            .orElse(calledAddress.getAddressIndicator().getRoutingIndicator());
        int dpc = replacedValues.getCalledSccpAddressParam()
            .flatMap(PatternSccpAddress::getDestPointCode).orElse(0);
        int ssn = replacedValues.getCalledSccpAddressParam()
            .flatMap(PatternSccpAddress::getSubSystemNumber)
            .orElse(calledAddress.getSubsystemNumber());

        logger.trace("Called Address dpc = {}, ssn = {}, RI = {}, {}", dpc, ssn, ri.toString(), this.channelTransId);
        calledAddress = new SccpAddressImpl(ri, gt, dpc, ssn);
        logger.info("<{}, {}> calledGT Changed: SccpAddress = '{}', New GT = '{}'", messageType, this.channelTransId, calledAddress, gt.toString());
      } else {
        // reset the point code to 0 thus use configuration rules for routing
        logger.trace("Changing pc = 0 for {}", calledAddress.getGlobalTitle());
        calledAddress = new SccpAddressImpl(calledAddress.getAddressIndicator().getRoutingIndicator(),
                calledAddress.getGlobalTitle(), 0, calledAddress.getSubsystemNumber());
      }
      // add the new or original called sccp address
      capDialogOut.setNewCalledSccpAddress(calledAddress);
      if (replacedValues.getCallingGlobalTitle() != null) {
        GlobalTitle gt = replacedValues.getCallingGlobalTitle();
        RoutingIndicator ri = replacedValues.getCallingSccpAddressParam()
            .flatMap(PatternSccpAddress::getRoutingIndicator)
            .orElse(callingAddress.getAddressIndicator().getRoutingIndicator());
        int dpc = replacedValues.getCallingSccpAddressParam()
            .flatMap(PatternSccpAddress::getDestPointCode)
            .orElse(callingAddress.getSignalingPointCode());
        int ssn = replacedValues.getCallingSccpAddressParam()
            .flatMap(PatternSccpAddress::getSubSystemNumber)
            .orElse(callingAddress.getSubsystemNumber());

        logger.trace("Calling Address pc = {}, ssn = {}, RI = {}, {}", dpc, ssn, ri.toString(), this.channelTransId);
        callingAddress = new SccpAddressImpl(ri, gt, dpc, ssn);
        logger.debug("<{}, {}> callingGT Changed: SccpAddress = '{}', New GT = '{}'",
            request.getMessageType().toString(), this.channelTransId, callingAddress, gt.toString());
      }
      capDialogOut.setNewCallingSccpAddress(callingAddress);
      IMSI idpNewImsi = new IMSIImpl(replacedValues.getImsi());
      capDialogOut.setNewIMSI(replacedValues.getImsi());
      CAPApplicationContext appCntx = CAPApplicationContext.CapV2_gsmSSF_to_gsmSCF;
      CAPDialogCircuitSwitchedCall idpDialogOut =
          capStackOut.getCAPProvider().getCAPServiceCircuitSwitchedCall().createNewDialog(appCntx,
              callingAddress, calledAddress);
      capDialogOut.setServiceKey(request.getServiceKey());
      idpDialogOut.addInitialDPRequest(request.getServiceKey(), request.getCalledPartyNumber(),
          request.getCallingPartyNumber(), request.getCallingPartysCategory(),
          request.getCGEncountered(), request.getIPSSPCapabilities(), request.getLocationNumber(),
          request.getOriginalCalledPartyID(), request.getExtensions(),
          request.getHighLayerCompatibility(), request.getAdditionalCallingPartyNumber(),
          request.getBearerCapability(), request.getEventTypeBCSM(),
          request.getRedirectingPartyID(), request.getRedirectionInformation(), request.getCause(),
          request.getServiceInteractionIndicatorsTwo(), request.getCarrier(), request.getCugIndex(),
          request.getCugInterlock(), request.getCallForwardingSSPending(), idpNewImsi,
          request.getSubscriberState(), request.getLocationInformation(),
          request.getExtBasicServiceCode(), request.getCallReferenceNumber(),
          request.getMscAddress(), request.getCalledPartyBCDNumber(), request.getTimeAndTimezone(),
          request.getCallForwardingSSPending(), request.getInitialDPArgExtension());
      bcsmCallContent.setSsfCapDialog(idpDialogOut);
      logger.trace("Storing transaction with dialogId = {} for IDP", idpDialogOut.getLocalDialogId());
      CapTransaction.instance().setSCFSSFBcsmCallContent(dialogId, idpDialogOut.getLocalDialogId(),
          bcsmCallContent);
      capDialogOut.setTransDialogId(idpDialogOut.getLocalDialogId());
      capDialogOut.setCapDialogCircuitSwitchedCall(idpDialogOut);
      capDialogOut.setWriteCDR(CapDialogOut.WriteLogState.INITIAL);
      return capDialogOut;

    } else {
      logger.info("Rule not found for {}, {}", request, this.channelTransId);
      // Rule not found. Close the dialog
      return CapProxyHelperUtils.closeDialog(request.getCAPDialog(), null, channelTransId, true);
    }
  }

  private String getCalledPartyNumberAddress() {
    try {
      if (request.getCalledPartyNumber() != null) {
        return request.getCalledPartyNumber().getCalledPartyNumber().getAddress();
      }
      return request.getCalledPartyBCDNumber().getAddress();
    } catch (Exception e) {
      return "";
    }
  }

  private String getCallingPartyNumberAddress() {
    try {
      return request.getCallingPartyNumber().getCallingPartyNumber().getAddress();
    } catch (Exception e) {
      return "";
    }
  }


  private CapDialogOut secondLegIDP(InitialDPRequest request) throws CAPException {
    Long dialogId = request.getCAPDialog().getLocalDialogId();
    String messageType = request.getMessageType().toString();
    CapDialogOut capDialogOut =
        new CapDialogOut(CapDialogType.CircuitSwitchedCallControl, channelTransId);
    capDialogOut.setServiceKey(request.getServiceKey());
    capDialogOut.setLocalDialogId(dialogId);
    capDialogOut.setRemoteDialogId(request.getCAPDialog().getRemoteDialogId());
    capDialogOut.setFirstLegCall(false);
    logger.debug("[CAP::LEG2::REQUEST<{}>] Local DialogId = '{}', Remote dialogId = '{}', invokeId = '{}' ServiceKey = '{}', {}",
        messageType, dialogId, request.getCAPDialog().getRemoteDialogId(), request.getInvokeId(), request.getServiceKey(), this.channelTransId);

    logger.trace("LEG2: CAP IDP is generated after a CAP CON (#A->#C), being #C a MSRN, {}", this.channelTransId);
    // CAP IDP is generated after a CAP CON (#A->#C), being #C a MSRN
    // About to send CAP CON (#A->#B), being #B the MSISDN called first in the other "leg"

    // retrieve the previous call information
    String callingPartyNumber =
        request.getCallingPartyNumber().getCallingPartyNumber().getAddress();
    String msrnNumber = request.getCalledPartyNumber().getCalledPartyNumber().getAddress();
    logger.debug("Searching #A->#C: Calling(#A) = '{}', MSRN(#C) = '{}', {}", callingPartyNumber, msrnNumber, channelTransId);
    BcsmCallContent callContent =
        CapTransaction.instance().getMsrnMapping(callingPartyNumber, msrnNumber, true);
    if (callContent == null) {
      logger.trace("LEG2: Previous store information not found for dialogid = {}, {}", dialogId, channelTransId);
      return CapProxyHelperUtils.closeDialog(request.getCAPDialog(), null, channelTransId, true);
    }
    BcsmCallContent leg2CallContent = new BcsmCallContent();
    leg2CallContent.setCapDialog(request.getCAPDialog());
    leg2CallContent.setIdp(request);
    leg2CallContent.setStep(BcsmCallStep.idpReceived);
    leg2CallContent.setCallingPartyNumberCap(request.getCallingPartyNumber());
    leg2CallContent.setCalledPartyNumberCap(request.getCalledPartyNumber());
    // add the calling numbers for CDR logging
    String callingPartyNumberStr = getCallingPartyNumberAddress();
    String calledPartyNumberStr = getCalledPartyNumberAddress();
    capDialogOut.setCallingPartyNumber(callingPartyNumberStr);
    capDialogOut.setCalledPartyNumber(calledPartyNumberStr);
    // get the calling number
    // get the called number from bcsmcallcontent
    ArrayList<CalledPartyNumberCap> calledPartyNumber = new ArrayList<>();
    CalledPartyNumber cpn =
        capStack.getCAPProvider().getISUPParameterFactory().createCalledPartyNumber();
    cpn.setAddress(callContent.getIdp().getCalledPartyBCDNumber().getAddress());
    cpn.setNatureOfAddresIndicator(NAINumber._NAI_INTERNATIONAL_NUMBER);
    cpn.setNumberingPlanIndicator(CalledPartyNumber._NPI_ISDN);
    cpn.setInternalNetworkNumberIndicator(CalledPartyNumber._INN_ROUTING_ALLOWED);
    CalledPartyNumberCap cpnc =
        capStack.getCAPProvider().getCAPParameterFactory().createCalledPartyNumberCap(cpn);
    calledPartyNumber.add(cpnc);
    DestinationRoutingAddress destinationRoutingAddressLeg2 = capStack.getCAPProvider()
        .getCAPParameterFactory().createDestinationRoutingAddress(calledPartyNumber);

    // we need to get the Calling Party Number (#B) from the earlier CAP IDP
    // contained in the BcsmCalls list

    capDialogOut.setServiceKey(request.getServiceKey());
    CAPDialogCircuitSwitchedCall idpDialogOut = request.getCAPDialog();
    SccpAddress callingSccpAddress = request.getCAPDialog().getRemoteAddress();
    SccpAddress calledSccpAddress = request.getCAPDialog().getLocalAddress();
    // add the calling and called sccp address for cdr writing
    capDialogOut.setCalledSccpAddress(calledSccpAddress);
    capDialogOut.setCallingSccpAddress(callingSccpAddress);
    String imsi = Optional.ofNullable(request.getIMSI()).map(IMSI::getData).orElse("");
    // add the IMSI for cdr writing
    capDialogOut.setOriginalIMSI(imsi);

    CapApplicationRulesResult result = ApplyCapApplicationRules.apply(callingSccpAddress,
        calledSccpAddress, imsi, messageType, this.channelTransId, true);
    if (result != null) {
      logger.trace("Applying Second Leg application rules. Rule name: {}", result.getRuleName());
      ReplacedValues replacedValues = result.getReplacedValues();
      capDialogOut.setRuleName(result.getRuleName());
      if (replacedValues != null) {
        if (replacedValues.getCalledGlobalTitle() != null) {
          GlobalTitle gt = replacedValues.getCalledGlobalTitle();
          RoutingIndicator ri = replacedValues.getCalledSccpAddressParam()
              .flatMap(PatternSccpAddress::getRoutingIndicator)
              .orElse(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE);
          int dpc = replacedValues.getCalledSccpAddressParam()
              .flatMap(PatternSccpAddress::getDestPointCode).orElse(0);
          int ssn = replacedValues.getCalledSccpAddressParam()
              .flatMap(PatternSccpAddress::getSubSystemNumber)
              .orElse(calledSccpAddress.getSubsystemNumber());
          calledSccpAddress = new SccpAddressImpl(ri, gt, dpc, ssn);
          logger.info("LEG2: Called SccpAddress: routingIndicator={},{}, {}", ri, calledSccpAddress, channelTransId);
          idpDialogOut.setRemoteAddress(calledSccpAddress); // change the called address
        }

        if (replacedValues.getCallingGlobalTitle() != null) {
          RoutingIndicator ri = replacedValues.getCallingSccpAddressParam()
              .flatMap(PatternSccpAddress::getRoutingIndicator)
              .orElse(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE);
          int dpc = replacedValues.getCallingSccpAddressParam()
              .flatMap(PatternSccpAddress::getDestPointCode).orElse(0);
          int ssn = replacedValues.getCallingSccpAddressParam()
              .flatMap(PatternSccpAddress::getSubSystemNumber)
              .orElse(callingSccpAddress.getSubsystemNumber());
          GlobalTitle gt = replacedValues.getCallingGlobalTitle();
          callingSccpAddress = new SccpAddressImpl(ri, gt, dpc, ssn);
          logger.info("LEG2: Calling SccpAddress: routingIndicator={},{}, {}", ri, callingSccpAddress, channelTransId);
          idpDialogOut.setLocalAddress(callingSccpAddress);
        }
      }
    }

    capDialogOut.setNewCalledSccpAddress(calledSccpAddress);
    capDialogOut.setNewCallingSccpAddress(callingSccpAddress);
    Long invokeId = idpDialogOut.addConnectRequest(destinationRoutingAddressLeg2,
        null, null, null, null, null,
        null, null, null, null, null,
        null, null, false, false, false, null, false, false);
    logger.debug("LEG2: CAP CON for leg 2 to be sent on CAP proxy to VPLMN over dialog: {}; Calling Party Address={}; Called Party Address={}; InvokeId = {}; DestinationAddress={}; {}",
        idpDialogOut, idpDialogOut.getLocalAddress(), idpDialogOut.getRemoteAddress(), invokeId, destinationRoutingAddressLeg2, channelTransId);

    RequestReportBCSMEventRequest rrb = callContent.getRrb();
    leg2CallContent.setRrb(rrb);
    Long invokeId2 = idpDialogOut.addRequestReportBCSMEventRequest(rrb.getBCSMEventList(), null);
    logger.debug("CAP RRB for leg 2 to be sent on CAP proxy to VPLMN over dialog: {}; Calling Party Address={}; Called Party Address={}; InvokeId = {}; {}",
        idpDialogOut, idpDialogOut.getLocalAddress(), idpDialogOut.getRemoteAddress(), invokeId2, channelTransId);
    leg2CallContent.setStep(BcsmCallStep.conSent);
    // store the dialogid from the idp request
    CapTransaction.instance().setLeg2BcsmCallContent(idpDialogOut.getLocalDialogId(),
        leg2CallContent);
    capDialogOut.setCapDialogCircuitSwitchedCall(idpDialogOut);
    capDialogOut.setTransDialogId(idpDialogOut.getLocalDialogId());
    capDialogOut.setWriteCDR(WriteLogState.INITIAL);

    return capDialogOut;
  }
}
