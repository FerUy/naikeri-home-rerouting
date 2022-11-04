package com.naikeri.sgw.impl.app.cap.helper;

import java.util.ArrayList;
import java.util.Optional;
import com.naikeri.sgw.impl.app.cap.BcsmCallContent;
import com.naikeri.sgw.impl.app.cap.BcsmCallStep;
import com.naikeri.sgw.impl.app.cap.CapProxyHelperUtils;
import com.naikeri.sgw.impl.app.cap.CapDialogOut.WriteLogState;
import com.naikeri.sgw.impl.app.cap.CapDialogOut;
import com.naikeri.sgw.impl.app.cap.CapDialogType;
import com.naikeri.sgw.impl.rules.ApplyCapApplicationRules;
import com.naikeri.sgw.impl.rules.CapApplicationRulesResult;
import com.naikeri.sgw.impl.rules.CapRuleComponent;
import com.naikeri.sgw.impl.rules.MSRNNumbers;
import com.naikeri.sgw.impl.rules.PatternSccpAddress;
import com.naikeri.sgw.impl.rules.ReplacedValues;
import com.naikeri.sgw.info.CapTransaction;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPStack;
import org.restcomm.protocols.ss7.cap.api.isup.CalledPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.GenericNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.LocationNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.OriginalCalledNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.RedirectingPartyIDCap;
import org.restcomm.protocols.ss7.cap.api.primitives.CAPExtensions;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ConnectRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.AlertingPatternCap;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.Carrier;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.DestinationRoutingAddress;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.NAOliInfo;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.ServiceInteractionIndicatorsTwo;
import org.restcomm.protocols.ss7.inap.api.isup.CallingPartysCategoryInap;
import org.restcomm.protocols.ss7.inap.api.isup.RedirectionInformationInap;
import org.restcomm.protocols.ss7.inap.api.primitives.LegID;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.isup.message.parameter.CalledPartyNumber;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CUGInterlock;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * CapProxyConnectRequest
 */
public class CapProxyConnectRequest {

  private static final Logger logger = Logger.getLogger(CapProxyConnectRequest.class);

  private ConnectRequest request;
  private String channelTransId;
  private CAPStack capStack;

  public CapProxyConnectRequest(ConnectRequest request, String channelTransId, CAPStack capStack) {
    this.request = request;
    this.channelTransId = channelTransId;
    this.capStack = capStack;
  }

  public CapDialogOut process() {
    try {
      logger.debug(String.format("[CAP::REQUEST<%s>] dialogId '%d', invokeId '%d', %s",
          request.getMessageType().toString(), request.getCAPDialog().getLocalDialogId(),
          request.getInvokeId(), channelTransId));
      Long dialogId = request.getCAPDialog().getLocalDialogId();
      String messageType = request.getMessageType().toString();
      DestinationRoutingAddress destinationRoutingAddress = request.getDestinationRoutingAddress();
      AlertingPatternCap alertingPatternCap = request.getAlertingPattern();
      OriginalCalledNumberCap originalCalledNumberCap = request.getOriginalCalledPartyID();
      CAPExtensions capExtensions = request.getExtensions();
      Carrier carrier = request.getCarrier();
      CallingPartysCategoryInap callingPartysCategoryInap = request.getCallingPartysCategory();
      RedirectingPartyIDCap redirectingPartyIDCap = request.getRedirectingPartyID();;
      RedirectionInformationInap redirectionInformationInap = request.getRedirectionInformation();
      ArrayList<GenericNumberCap> genericNumberCapArrayList = request.getGenericNumbers();
      ServiceInteractionIndicatorsTwo serviceInteractionIndicatorsTwo = request.getServiceInteractionIndicatorsTwo();;
      LocationNumberCap locationNumberCap = request.getChargeNumber();
      LegID legID = request.getLegToBeConnected();
      CUGInterlock cugInterlock = request.getCUGInterlock();
      boolean cugOutgoingAccess = request.getBorInterrogationRequested();
      boolean suppressionOfAnnouncement = request.getCugOutgoingAccess();
      boolean ocsIApplicable = request.getOCSIApplicable();
      NAOliInfo naOliInfo = request.getNAOliInfo();
      boolean borInterrogationRequested = request.getSuppressionOfAnnouncement();
      boolean suppressNCSI = request.getSuppressNCSI();

      CapDialogOut capDialogOut =
          new CapDialogOut(CapDialogType.CircuitSwitchedCallControl, channelTransId);
      capDialogOut.setTransDialogId(dialogId);
      // get the call
      BcsmCallContent callContent =
          CapTransaction.instance().getSSFBcsmCallContent(dialogId, false);
      if (callContent == null) {
        // call not found
        logger.trace("Previous store information not found for dialogid = " + dialogId + "; "
            + channelTransId);
        return CapProxyHelperUtils.closeDialog(request.getCAPDialog(), dialogId, channelTransId,
            false);
      }
      callContent.setCon(request);
      callContent.setStep(BcsmCallStep.conReceived);
      CAPDialogCircuitSwitchedCall conDialogIn = request.getCAPDialog();
      SccpAddress callingSccpAddress = conDialogIn.getRemoteAddress(); // calling party
      SccpAddress calledSccpAddress = conDialogIn.getLocalAddress();

      CAPDialogCircuitSwitchedCall conDialogOut = callContent.getCapDialog();

      CapApplicationRulesResult result = ApplyCapApplicationRules.apply(callingSccpAddress,
          calledSccpAddress, "", request.getMessageType().toString(), this.channelTransId);
      if (result == null) { // rule not found
        return connectRequestRuleNotFound(dialogId, callContent, conDialogOut);
      }
      capDialogOut.setRuleName(result.getRuleName());
      logger.trace("Rule found, applying the replace and component rules. " + channelTransId);
      ReplacedValues replacedValues = result.getReplacedValues();
      if (replacedValues != null) {
        if (replacedValues.getCalledGlobalTitle() != null) {
          GlobalTitle gt = replacedValues.getCalledGlobalTitle();
          RoutingIndicator ri = replacedValues.getCalledSccpAddressParam()
              .flatMap(PatternSccpAddress::getRoutingIndicator)
              .orElse(calledSccpAddress.getAddressIndicator().getRoutingIndicator());
          int dpc = replacedValues.getCalledSccpAddressParam()
              .flatMap(PatternSccpAddress::getDestPointCode).orElse(0);
          int ssn = replacedValues.getCalledSccpAddressParam()
              .flatMap(PatternSccpAddress::getSubSystemNumber)
              .orElse(calledSccpAddress.getSubsystemNumber());

          calledSccpAddress = new SccpAddressImpl(ri, gt, dpc, ssn);
          logger.info("SccpAddress: " + calledSccpAddress.toString() + "; " + channelTransId);
        }

        conDialogOut.setRemoteAddress(calledSccpAddress); // change the called address
        if (replacedValues.getCallingGlobalTitle() != null) {
          RoutingIndicator ri = replacedValues.getCallingSccpAddressParam()
              .flatMap(PatternSccpAddress::getRoutingIndicator)
              .orElse(callingSccpAddress.getAddressIndicator().getRoutingIndicator());
          int dpc = replacedValues.getCallingSccpAddressParam()
              .flatMap(PatternSccpAddress::getDestPointCode)
              .orElse(callingSccpAddress.getSignalingPointCode());
          int ssn = replacedValues.getCallingSccpAddressParam()
              .flatMap(PatternSccpAddress::getSubSystemNumber)
              .orElse(callingSccpAddress.getSubsystemNumber());
          GlobalTitle gt = replacedValues.getCallingGlobalTitle();
          callingSccpAddress = new SccpAddressImpl(ri, gt, dpc, ssn);

          logger.info("SccpAddress: " + callingSccpAddress.toString() + "; " + channelTransId);
        }
        conDialogOut.setLocalAddress(callingSccpAddress);
      }
      if (result.getCapRuleComponent() != null) {
        CapProxyHelperUtils helper = new CapProxyHelperUtils();
        CapRuleComponent.Remove removeComponent = result.getCapRuleComponent().getRemove();
        logger.trace("Applying component rule.... " + channelTransId);
        CapRuleComponent.Replace replaceComponent = result.getCapRuleComponent().getReplace();
        boolean replaceCheck = Optional.ofNullable(replaceComponent)
            .map(u -> u.getApply() && u.getPrimitive().equalsIgnoreCase(messageType)).orElse(false);
        if (replaceCheck) {
          // apply the argument
          CapRuleComponent.Replace.ReplaceArguments replArgs = replaceComponent.getArgument();
          String callingNumber = getCallingNumber(callContent);
          logger.trace(
              String.format("Getting the MSRN Number using the RULE '%s'", result.getRuleName()));
          String msrnNumber = MSRNNumbers.instance().getMSRNAddress(result.getRuleName(),
              replArgs.getCdPN(), replArgs.getRange(), dialogId, callingNumber);
          logger.debug("MSRN Number: " + msrnNumber + "; " + channelTransId);
          capDialogOut.setMSRN(msrnNumber);
          ArrayList<CalledPartyNumberCap> calledPartyNumber = new ArrayList<>();
          CalledPartyNumber cpn =
              capStack.getCAPProvider().getISUPParameterFactory().createCalledPartyNumber();
          cpn.setAddress(msrnNumber);

          cpn.setNatureOfAddresIndicator(replArgs.getNai());
          cpn.setNumberingPlanIndicator(replArgs.getNpi());
          cpn.setInternalNetworkNumberIndicator(replArgs.getInni());
          CalledPartyNumberCap cpnc =
              capStack.getCAPProvider().getCAPParameterFactory().createCalledPartyNumberCap(cpn);
          calledPartyNumber.add(cpnc);
          // add the call to the bcsm
          callContent.setCalledPartyNumberCap(cpnc);

          destinationRoutingAddress = capStack.getCAPProvider()
              .getCAPParameterFactory().createDestinationRoutingAddress(calledPartyNumber);

          helper.setDestinationRoutingAddress(destinationRoutingAddress);
          helper.replaceComponent(conDialogOut, replaceComponent.getPrimitive(), removeComponent);
          logger.debug(
              "Incoming CAP CON removed and new CAP CON to be sent on CAP proxy to VPLMN over dialog. "
                  + destinationRoutingAddress + "; Calling Party Address="
                  + conDialogOut.getLocalAddress() + "; Called Party Address="
                  + conDialogOut.getRemoteAddress() + "MSRN = " + msrnNumber + "; "
                  + channelTransId);
          CapTransaction.instance().setMsrnTransaction(callingNumber, msrnNumber, callContent);
        } else {
          logger.trace("Replace argument equals false. " + channelTransId + ". Relaying CAP CON parameters as received");
          conDialogOut.addConnectRequest(destinationRoutingAddress, alertingPatternCap, originalCalledNumberCap,
                  capExtensions, carrier, callingPartysCategoryInap, redirectingPartyIDCap, redirectionInformationInap,
                  genericNumberCapArrayList, serviceInteractionIndicatorsTwo, locationNumberCap, legID, cugInterlock,
                  cugOutgoingAccess, suppressionOfAnnouncement, ocsIApplicable, naOliInfo, borInterrogationRequested, suppressNCSI);

        }
      } else {
        logger.trace("Rule Component not found. " + channelTransId + ". Relaying CAP CON parameters as received");
        conDialogOut.addConnectRequest(destinationRoutingAddress, alertingPatternCap, originalCalledNumberCap,
                capExtensions, carrier, callingPartysCategoryInap, redirectingPartyIDCap, redirectionInformationInap,
                genericNumberCapArrayList, serviceInteractionIndicatorsTwo, locationNumberCap, legID, cugInterlock,
                cugOutgoingAccess, suppressionOfAnnouncement, ocsIApplicable, naOliInfo, borInterrogationRequested, suppressNCSI);
      }
      callContent.setStep(BcsmCallStep.conSent);
      capDialogOut.setWriteCDR(WriteLogState.ADDMORE);
      capDialogOut.setCapDialogCircuitSwitchedCall(conDialogOut);
      CapTransaction.instance().updateSSFBcsmCallContent(dialogId, callContent);
      return capDialogOut;
    } catch (CAPException capEx) {
      logger.error("Processing CAP ETC Request failed for " + channelTransId, capEx);
      return CapProxyHelperUtils.discardReason(CapDialogType.CircuitSwitchedCallControl,
          capEx.getMessage(), request.getMessageType().toString(), channelTransId);
    } catch (Exception e) {
      logger.error("Failed", e);
      return CapProxyHelperUtils.discardReason(CapDialogType.CircuitSwitchedCallControl,
          e.getMessage(), request.getMessageType().toString(), channelTransId);
    }
  }

  private String getCallingNumber(BcsmCallContent callContent) throws CAPException {
    String callingNumber = "";

    if (callContent.getCallingPartyNumberCap() != null
        && callContent.getCallingPartyNumberCap().getCallingPartyNumber() != null) {
      callingNumber = callContent.getCallingPartyNumberCap().getCallingPartyNumber().getAddress();
    }
    return callingNumber;
  }

  private CapDialogOut connectRequestRuleNotFound(Long dialogId, BcsmCallContent callContent,
      CAPDialogCircuitSwitchedCall conDialogCircuitSwitch) throws CAPException {
    logger.trace("Rule not found for connect_request" + channelTransId);
    // relay CAP CON to VPLMN as it is
    conDialogCircuitSwitch.addConnectRequest(request.getDestinationRoutingAddress(),
        request.getAlertingPattern(), request.getOriginalCalledPartyID(), null,
        request.getCarrier(), request.getCallingPartysCategory(), request.getRedirectingPartyID(),
        request.getRedirectionInformation(), request.getGenericNumbers(),
        request.getServiceInteractionIndicatorsTwo(), request.getChargeNumber(),
        request.getLegToBeConnected(), request.getCUGInterlock(), request.getCugOutgoingAccess(),
        request.getSuppressionOfAnnouncement(), request.getOCSIApplicable(), request.getNAOliInfo(),
        request.getBorInterrogationRequested(), request.getSuppressNCSI());

    callContent.setStep(BcsmCallStep.conSent);
    CapDialogOut capDialogOut =
        new CapDialogOut(CapDialogType.CircuitSwitchedCallControl, channelTransId);
    capDialogOut.setCapDialogCircuitSwitchedCall(conDialogCircuitSwitch);
    CapTransaction.instance().updateSSFBcsmCallContent(dialogId, callContent);
    return capDialogOut;
  }
}
