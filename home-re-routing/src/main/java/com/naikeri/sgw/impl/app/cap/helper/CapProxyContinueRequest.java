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
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPStack;
import org.restcomm.protocols.ss7.cap.api.isup.CalledPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ContinueRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.DestinationRoutingAddress;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.isup.message.parameter.CalledPartyNumber;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * CapProxyContinueRequest
 */
public class CapProxyContinueRequest {

  private static final Logger logger = Logger.getLogger(CapProxyContinueRequest.class);
  private ContinueRequest request;
  private String channelTransId;
  private CAPStack capStack;

  public CapProxyContinueRequest(ContinueRequest request, String channelTransId,
      CAPStack capStack) {
    this.request = request;
    this.channelTransId = channelTransId;
    this.capStack = capStack;
  }

  public CapDialogOut process() {
    try {
      logger.debug(String.format("[CAP::REQUEST<%s>] dialogId '%d', invokeId '%d'",
          request.getMessageType().toString(), request.getCAPDialog().getLocalDialogId(),
          request.getInvokeId()));
      Long dialogId = request.getCAPDialog().getLocalDialogId();

      BcsmCallContent callContent =
          CapTransaction.instance().getSSFBcsmCallContent(dialogId, false);
      if (callContent == null) {
        // call not found
        logger.trace("Previous store information not found for dialogid = " + dialogId);
        return CapProxyHelperUtils.closeDialog(request.getCAPDialog(), dialogId, channelTransId,
            false);
      }
      callContent.setCue(request);
      callContent.setStep(BcsmCallStep.cueReceived);

      SccpAddress callingSccpAddress = request.getCAPDialog().getRemoteAddress(); // calling party
      SccpAddress calledSccpAddress = request.getCAPDialog().getLocalAddress();
      String imsi = "";
      CAPDialogCircuitSwitchedCall cueDialogOut = callContent.getCapDialog();

      CapDialogOut capDialogOut =
          new CapDialogOut(CapDialogType.CircuitSwitchedCallControl, channelTransId);
      CapApplicationRulesResult result = ApplyCapApplicationRules.apply(callingSccpAddress,
          calledSccpAddress, imsi, request.getMessageType().toString(), this.channelTransId);
      if (result == null) {
        logger.trace("Rule not found for CUE: " + request + ". " + this.channelTransId);
      } else {
        capDialogOut.setRuleName(result.getRuleName());
        updateSccpAddresses(callingSccpAddress, calledSccpAddress, cueDialogOut, result);
        // check for the component
        if (result.getCapRuleComponent() != null) {
          CapProxyHelperUtils helper = new CapProxyHelperUtils();
          CapRuleComponent.Remove removeComponent = result.getCapRuleComponent().getRemove();

          CapRuleComponent.Replace replaceComponent = result.getCapRuleComponent().getReplace();
          if (replaceComponent != null && replaceComponent.getApply()) {
            // apply the argument
            CapRuleComponent.Replace.ReplaceArguments replArgs = replaceComponent.getArgument();
            String callingPartyNumberAddress =
                callContent.getCallingPartyNumberCap().getCallingPartyNumber().getAddress();
            String msrnNumber = MSRNNumbers.instance().getMSRNAddress(result.getRuleName(),
                replArgs.getCdPN(), replArgs.getRange(), dialogId, callingPartyNumberAddress);
            logger.info("MSRN Number = " + msrnNumber);
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
            DestinationRoutingAddress destinationRoutingAddress = capStack.getCAPProvider()
                .getCAPParameterFactory().createDestinationRoutingAddress(calledPartyNumber);

            helper.setDestinationRoutingAddress(destinationRoutingAddress);
            helper.replaceComponent(cueDialogOut, replaceComponent.getPrimitive(), removeComponent);
            logger
                .debug("CAP CUE removed and CAP CON to be sent on CAP proxy to VPLMN over dialog: "
                    + cueDialogOut + "; Calling Party Address=" + cueDialogOut.getLocalAddress()
                    + "; Called Party Address=" + cueDialogOut.getRemoteAddress());
            CapTransaction.instance().setMsrnTransaction(callingPartyNumberAddress, msrnNumber,
                callContent);
          }
        }
      }



      capDialogOut.setWriteCDR(CapDialogOut.WriteLogState.ADDMORE);
      capDialogOut.setCapDialogCircuitSwitchedCall(cueDialogOut);
      callContent.setStep(BcsmCallStep.cueSent);
      CapTransaction.instance().updateSSFBcsmCallContent(dialogId, callContent);
      return capDialogOut;
    } catch (CAPException capEx) {
      logger.error("Processing CAP RRB Request failed for " + channelTransId, capEx);
      return CapProxyHelperUtils.discardReason(CapDialogType.CircuitSwitchedCallControl,
          capEx.getMessage(), request.getMessageType().toString(), channelTransId);
    } catch (Exception e) {
      logger.error("Exception caught for " + channelTransId + ". Details: ", e);
      return CapProxyHelperUtils.discardReason(CapDialogType.CircuitSwitchedCallControl,
          e.getMessage(), request.getMessageType().toString(), channelTransId);
    }
  }

  private void updateSccpAddresses(SccpAddress callingSccpAddress, SccpAddress calledSccpAddress,
      CAPDialogCircuitSwitchedCall cueDialogOut, CapApplicationRulesResult result) {
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
        logger.info("SccpAddress: " + calledSccpAddress.toString());
      }
      cueDialogOut.setRemoteAddress(calledSccpAddress); // change the called address
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
      }
      cueDialogOut.setLocalAddress(callingSccpAddress);
    }
  }
}
