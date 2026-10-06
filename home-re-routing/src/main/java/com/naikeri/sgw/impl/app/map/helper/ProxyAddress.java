package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.rules.MapProxyApplicationRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;

/**
 * How the proxy presents itself to the visited network on the dialogues a VLR or SGSN opens towards the
 * HLR (Update-Location, Update-GPRS-Location). The ISD rule names the GT that stands for the home network
 * there; the proxy answers from it, and gives it as the HLR number, so the VLR keeps addressing the proxy
 * (Restore Data, Purge MS) instead of reaching the HLR around it.
 */
final class ProxyAddress {

  private static final Logger logger = LogManager.getLogger(ProxyAddress.class);
  private static final String PRESENTATION_RULE = "insertSubscriberData_Request";

  private ProxyAddress() {
  }

  /**
   * Gives the dialogue the calling address the ISD rule assigns, keeping its routing indicator, point code
   * and SSN. Only the proxy's first backward message on a dialogue may change it, so it does nothing once
   * the dialogue already presents that GT.
   */
  static void present(MAPDialogMobility dialog, String imsiOrNull, String transactionId) {
    String imsi = imsiOrNull == null ? "" : imsiOrNull;
    var local = dialog.getLocalAddress();
    var remote = dialog.getRemoteAddress();
    if (local == null || remote == null || local.getGlobalTitle() == null || remote.getGlobalTitle() == null) {
      return;
    }
    String callingGt = local.getGlobalTitle().getDigits();
    String calledGt = remote.getGlobalTitle().getDigits();
    var rule = MapProxyApplicationRules.getInstance()
        .findMAPApplicationRule(callingGt, calledGt, imsi, PRESENTATION_RULE);
    if (rule == null) {
      return;
    }
    var replaced = rule.getReplaceRule().applyReplaceRule(imsi, callingGt, calledGt);
    if (replaced == null || replaced.getCallingGlobalTitle() == null
        || replaced.getCallingGlobalTitle().getDigits().equals(callingGt)) {
      return;
    }
    dialog.setLocalAddress(new SccpAddressImpl(local.getAddressIndicator().getRoutingIndicator(),
        replaced.getCallingGlobalTitle(), local.getSignalingPointCode(), local.getSubsystemNumber()));
    logger.debug("<{}>: dialogue {} presents '{}' (rule '{}'), not '{}'", transactionId,
        dialog.getLocalDialogId(), replaced.getCallingGlobalTitle().getDigits(), rule.getName(), callingGt);
  }

  /** The HLR number to give the VLR: the GT the dialogue presents, as the original number's type of number. */
  static ISDNAddressString hlrNumber(MAPDialogMobility dialog, ISDNAddressString original) {
    var local = dialog.getLocalAddress();
    if (original == null || local == null || local.getGlobalTitle() == null) {
      return original;
    }
    return new ISDNAddressStringImpl(original.getAddressNature(), original.getNumberingPlan(),
        local.getGlobalTitle().getDigits());
  }
}
