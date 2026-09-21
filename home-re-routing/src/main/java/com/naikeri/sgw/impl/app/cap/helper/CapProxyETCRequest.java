package com.naikeri.sgw.impl.app.cap.helper;

import java.util.ArrayList;
import com.naikeri.sgw.impl.app.cap.BcsmCallContent;
import com.naikeri.sgw.impl.app.cap.BcsmCallStep;
import com.naikeri.sgw.impl.app.cap.CapProxyHelperUtils;
import com.naikeri.sgw.impl.app.cap.CapDialogOut;
import com.naikeri.sgw.impl.app.cap.CapDialogType;
import com.naikeri.sgw.impl.rules.ApplyCapApplicationRules;
import com.naikeri.sgw.impl.rules.CapApplicationRulesResult;
import com.naikeri.sgw.impl.rules.CapRuleComponent;
import com.naikeri.sgw.impl.rules.MSRNNumbers;
import com.naikeri.sgw.impl.rules.PatternSccpAddress;
import com.naikeri.sgw.impl.rules.ReplacedValues;
import com.naikeri.sgw.info.CapTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPStack;
import org.restcomm.protocols.ss7.cap.api.isup.CalledPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EstablishTemporaryConnectionRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.DestinationRoutingAddress;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.isup.message.parameter.CalledPartyNumber;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * CapProxyETCRequest
 */
public class CapProxyETCRequest {

  private static final Logger logger = LogManager.getLogger(CapProxyETCRequest.class);
  private final EstablishTemporaryConnectionRequest request;
  private final String channelTransId;
  private final CAPStack capStack;

  public CapProxyETCRequest(EstablishTemporaryConnectionRequest request, String channelTransId,
      CAPStack capStack) {
    this.channelTransId = channelTransId;
    this.request = request;
    this.capStack = capStack;
  }

  public CapDialogOut process() {
    try {
      logger.debug("[CAP::REQUEST<{}>] dialogId '{}', invokeId '{}'", request.getMessageType().toString(), request.getCAPDialog().getLocalDialogId(), request.getInvokeId());
      Long dialogId = request.getCAPDialog().getLocalDialogId();
      BcsmCallContent callContent = CapTransaction.instance().getSSFBcsmCallContent(dialogId, false);
      CapDialogOut capDialogOut = new CapDialogOut(CapDialogType.CircuitSwitchedCallControl, channelTransId);
      capDialogOut.setTransDialogId(dialogId);
      if (callContent == null) {
        logger.info("CAP ETC received on CAP Proxy but no action on that CAP operation required for this HPLMN");
        /* call content not found */
        return CapProxyHelperUtils.closeDialog(request.getCAPDialog(), dialogId, channelTransId, false);
      } else {
        callContent.setEtc(request);
        callContent.setStep(BcsmCallStep.etcReceived);
        CAPDialogCircuitSwitchedCall etcDialogIn = request.getCAPDialog();
        SccpAddress callingSccpAddress = etcDialogIn.getRemoteAddress(); // calling party
        SccpAddress calledSccpAddress = etcDialogIn.getLocalAddress();
        String imsi = "";
        CAPDialogCircuitSwitchedCall etcDialogOut = callContent.getCapDialog();
        CapApplicationRulesResult result = ApplyCapApplicationRules.apply(callingSccpAddress,
            calledSccpAddress, imsi, request.getMessageType().toString(), this.channelTransId);
        if (result == null) {
          // No rule found. Proceed ....
          logger.trace("Rule not found for ETC request {}, {}", request, this.channelTransId);
          etcDialogOut.addContinueRequest();
        } else {
          capDialogOut.setRuleName(result.getRuleName());
          updateSccpAddress(callingSccpAddress, calledSccpAddress, etcDialogOut, result);
          // check for the component
          if (result.getCapRuleComponent() != null) {
            CapProxyHelperUtils helper = new CapProxyHelperUtils();
            CapRuleComponent.Remove removeComponent = result.getCapRuleComponent().getRemove();

            CapRuleComponent.Replace replaceComponent = result.getCapRuleComponent().getReplace();
            if (replaceComponent != null && replaceComponent.getApply()) {
              // apply the argument
              CapRuleComponent.Replace.ReplaceArguments replArgs = replaceComponent.getArgument();
              String callingNumber = callContent.getCallingPartyNumberCap().getCallingPartyNumber().getAddress();
              logger.trace("Getting the MSRN Number using the RULE '{}'", result.getRuleName());
              String msrnNumber = MSRNNumbers.instance().getMSRNAddress(result.getRuleName(),
                  replArgs.getCdPN(), replArgs.getRange(), dialogId, callingNumber);
              logger.info("MSRN Number = {}; {}", msrnNumber, channelTransId);
              capDialogOut.setMSRN(msrnNumber);
              ArrayList<CalledPartyNumberCap> calledPartyNumber = new ArrayList<>();
              CalledPartyNumber cpn = capStack.getCAPProvider().getISUPParameterFactory().createCalledPartyNumber();
              cpn.setAddress(msrnNumber);

              cpn.setNatureOfAddresIndicator(replArgs.getNai());
              cpn.setNumberingPlanIndicator(replArgs.getNpi());
              cpn.setInternalNetworkNumberIndicator(replArgs.getInni());
              CalledPartyNumberCap cpnc = capStack.getCAPProvider().getCAPParameterFactory().createCalledPartyNumberCap(cpn);
              calledPartyNumber.add(cpnc);
              // add the call to the bcsm
              callContent.setCalledPartyNumberCap(cpnc);
              DestinationRoutingAddress destinationRoutingAddress = capStack.getCAPProvider()
                  .getCAPParameterFactory().createDestinationRoutingAddress(calledPartyNumber);
              helper.setDestinationRoutingAddress(destinationRoutingAddress);
              helper.replaceComponent(etcDialogOut, replaceComponent.getPrimitive(), removeComponent);
              logger.debug("CAP ETC removed and CAP CON to be sent on CAP proxy to VPLMN over dialog: {}; Calling Party Address={}; Called Party Address={}; {}", etcDialogOut, etcDialogOut.getLocalAddress(), etcDialogOut.getRemoteAddress(), channelTransId);
              CapTransaction.instance().setMsrnTransaction(callingNumber, msrnNumber, callContent);
            } else {
              etcDialogOut.addContinueRequest();
              logger.trace("Replace argument equals false. {}. Relaying TCAP component with only CAP CUE and ETC removed", channelTransId);
            }
          } else {
            etcDialogOut.addContinueRequest();
            logger.trace("Rule Component not found. {}. Relaying TCAP component with only CAP CUE and ETC removed", channelTransId);
          }
        }
        // update the bcsm
        CapTransaction.instance().updateSSFBcsmCallContent(dialogId, callContent);
        // return the cap dialog out
        capDialogOut.setWriteCDR(CapDialogOut.WriteLogState.ADDMORE);
        capDialogOut.setCapDialogCircuitSwitchedCall(etcDialogOut);
        return capDialogOut;
      }
    } catch (CAPException capEx) {
      logger.error("Processing CAP ETC Request failed for {}", channelTransId, capEx);
      return CapProxyHelperUtils.discardReason(CapDialogType.CircuitSwitchedCallControl,
          capEx.getMessage(), request.getMessageType().toString(), channelTransId);
    } catch (Exception e) {
      logger.error("Exception caught for {}. Details: ", channelTransId, e);
      return CapProxyHelperUtils.discardReason(CapDialogType.CircuitSwitchedCallControl,
          e.getMessage(), request.getMessageType().toString(), channelTransId);
    }
  }

  private void updateSccpAddress(SccpAddress callingSccpAddress, SccpAddress calledSccpAddress,
      CAPDialogCircuitSwitchedCall etcDialogOut, CapApplicationRulesResult result) {
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
        logger.info("SccpAddress: {}; {}", calledSccpAddress, channelTransId);
      }

      etcDialogOut.setRemoteAddress(calledSccpAddress); // change the called address
      if (replacedValues.getCallingGlobalTitle() != null) {
        GlobalTitle gt = replacedValues.getCallingGlobalTitle();
        RoutingIndicator ri = replacedValues.getCallingSccpAddressParam()
            .flatMap(PatternSccpAddress::getRoutingIndicator)
            .orElse(callingSccpAddress.getAddressIndicator().getRoutingIndicator());
        int dpc = replacedValues.getCallingSccpAddressParam()
            .flatMap(PatternSccpAddress::getDestPointCode)
            .orElse(callingSccpAddress.getSignalingPointCode());
        int ssn = replacedValues.getCallingSccpAddressParam()
            .flatMap(PatternSccpAddress::getSubSystemNumber)
            .orElse(callingSccpAddress.getSubsystemNumber());
        callingSccpAddress = new SccpAddressImpl(ri, gt, dpc, ssn);
        logger.info("SccpAddress: {}; {}", callingSccpAddress, channelTransId);
      }
      etcDialogOut.setLocalAddress(callingSccpAddress);
    }
  }

}
