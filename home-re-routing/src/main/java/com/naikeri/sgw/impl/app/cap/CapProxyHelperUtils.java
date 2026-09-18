package com.naikeri.sgw.impl.app.cap;

import com.naikeri.sgw.impl.rules.ApplyCapApplicationRules;
import com.naikeri.sgw.impl.rules.CapApplicationRulesResult;
import com.naikeri.sgw.impl.rules.PatternSccpAddress;
import com.naikeri.sgw.impl.rules.ReplacedValues;
import com.naikeri.sgw.impl.rules.CapRuleComponent.Remove;
import com.naikeri.sgw.info.CapTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.DestinationRoutingAddress;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * CamelBcsmChannelMessagesHelper
 */
public class CapProxyHelperUtils {
  private DestinationRoutingAddress destinationRoutingAddress;
  private static final Logger logger = LoggerFactory.getLogger(CapProxyHelperUtils.class);
  private static String cdrName = "";
  private static boolean cdrIsEnabled = false;

  public CapProxyHelperUtils() {
    // only for instantiating the object
  }

  public static void setCDRName(String cdrName) {
    CapProxyHelperUtils.cdrName = cdrName;
    CapProxyHelperUtils.cdrIsEnabled = (cdrName != null && !cdrName.isEmpty());
  }

  public static String getCDRName() {
    return CapProxyHelperUtils.cdrName;
  }

  public static boolean isCDREnabled() {
    return CapProxyHelperUtils.cdrIsEnabled;
  }

  public void setDestinationRoutingAddress(DestinationRoutingAddress destinationRoutingAddress) {
    this.destinationRoutingAddress = destinationRoutingAddress;
  }

  public void replaceComponent(CAPDialogCircuitSwitchedCall dialogOut, String primitive,
      Remove removeComponent) throws CAPException {
    if (removeComponent != null && removeComponent.getPrimitives().contains(primitive)) {
      return; // return if we need to remove the component from the replace
    }
    if (primitive.equalsIgnoreCase("connect_Request")) {

      dialogOut.addConnectRequest(destinationRoutingAddress, null, null, null, null, null, null,
          null, null, null, null, null, null, false, false, false, null, false, false);
    } else if (primitive.equalsIgnoreCase("continue_Request")) {

      dialogOut.addContinueRequest();
    }
  }

  // close the dialog
  public static CapDialogOut closeDialog(CAPDialogCircuitSwitchedCall outDialog, Long dialogId,
      String channelTransId, Boolean iSScf) {
    if (dialogId != null) {
      // remove data from memory
      logger.trace(
          "Remove data from the CAP Transaction. DialogId " + dialogId + "; " + channelTransId);
      if (iSScf.booleanValue()) {
        CapTransaction.instance().removSCFCallContent(dialogId);
      } else {
        CapTransaction.instance().getSSFBcsmCallContent(dialogId, true);
      }
    }
    logger.debug(String.format("Closing dialog. Dialog Id = '%s', %s", dialogId, channelTransId));
    CapDialogOut capDialogOut =
        new CapDialogOut(CapDialogType.CircuitSwitchedCallControl, channelTransId);
    capDialogOut.setCapDialogCircuitSwitchedCall(outDialog);
    capDialogOut.setIsClose(true);
    return capDialogOut;
  }

  public static CAPDialogCircuitSwitchedCall applyReplaceRulesOnly(String messageType,
      CAPDialogCircuitSwitchedCall dialogIn, CAPDialogCircuitSwitchedCall dialogOut,
      String channelTransId, CapDialogOut capDialogOut) {
    return applyReplaceRulesOnly(messageType, dialogIn, dialogOut, false, channelTransId,
        capDialogOut);
  }

  public static CAPDialogCircuitSwitchedCall applyReplaceRulesOnly(String messageType,
      CAPDialogCircuitSwitchedCall dialogIn, CAPDialogCircuitSwitchedCall dialogOut, Boolean isLeg2,
      String channelTransId, CapDialogOut capDialogOut) {
    SccpAddress callingSccpAddress = dialogIn.getRemoteAddress(); // calling party
    SccpAddress calledSccpAddress = dialogIn.getLocalAddress();

    // change gt if applicable
    CapApplicationRulesResult result = ApplyCapApplicationRules.apply(callingSccpAddress,
        calledSccpAddress, "", messageType, channelTransId, isLeg2);
    if (result != null) {
      ReplacedValues replacedValues = result.getReplacedValues();
      capDialogOut.setRuleName(result.getRuleName());
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
          logger
              .debug("Called SccpAddress: " + calledSccpAddress.toString() + "; " + channelTransId);
        }
        dialogOut.setRemoteAddress(calledSccpAddress); // change the called address

        if (replacedValues.getCallingGlobalTitle() != null) {
          GlobalTitle gt = replacedValues.getCallingGlobalTitle();
          RoutingIndicator ri = replacedValues.getCallingSccpAddressParam()
              .flatMap(PatternSccpAddress::getRoutingIndicator)
              .orElse(callingSccpAddress.getAddressIndicator().getRoutingIndicator());
          int dpc = replacedValues.getCallingSccpAddressParam()
              .flatMap(PatternSccpAddress::getDestPointCode).orElse(0);
          int ssn = replacedValues.getCallingSccpAddressParam()
              .flatMap(PatternSccpAddress::getSubSystemNumber)
              .orElse(callingSccpAddress.getSubsystemNumber());
          callingSccpAddress = new SccpAddressImpl(ri, gt, dpc, ssn);
          logger.debug(
              "Calling SccpAddress: " + callingSccpAddress.toString() + "; " + channelTransId);
        }
        dialogOut.setLocalAddress(callingSccpAddress);
      }
    }
    return dialogOut;
  }

  public static CapDialogOut discardReason(CapDialogType capDialogType, String message,
      String messageType, String transactionId) {
    CapDialogOut capDialogOut = new CapDialogOut(capDialogType, transactionId);
    logger.debug(String.format("Discard for <%s>. TransactionId = %s, Reason = %s", messageType,
        transactionId, message));
    capDialogOut.setDiscardReason(message);
    return capDialogOut;
  }
}
