package com.naikeri.sgw.impl.app.map.helper;

import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;

import com.naikeri.sgw.impl.rules.MapProxyApplicationRules;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxyInsertSubscriberData
 */
public class MapProxyInsertSubscriberData {

  private static final Logger logger = LogManager.getLogger(MapProxyInsertSubscriberData.class);

  private MapProxyInsertSubscriberData() {
  }

  /**
   * Process response for InsertSubscriberData
   *
   * @return MapDialogOut
   */
  public static MapDialogOut getInsertSubDataResponse(Object message, String transactionId) {
    Long dialogId = 0L;
    String logmsg;
    String messageType = "";
    try {
      InsertSubscriberDataResponse insertSubDataResp = (InsertSubscriberDataResponse) message;
      dialogId = insertSubDataResp.getMAPDialog().getLocalDialogId();
      messageType = insertSubDataResp.getMessageType().toString();

      Long respInvokeId = insertSubDataResp.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      InsertSubscriberDataResponseCopy clone = new InsertSubscriberDataResponseCopy(insertSubDataResp);
      logger.debug("[MAP::RESPONSE<{}>] Incoming dialogId '{}', invokeId '{}' {}", insertSubDataResp.getMessageType().toString(), dialogId, insertSubDataResp.getInvokeId(), transactionId);
      DataElement dataElement;
      logger.debug("TCAP Message Type = '{}', dialogId = {}, InvokeId = {}, Service = '{}'", insertSubDataResp.getMAPDialog().getTCAPMessageType(), dialogId, insertSubDataResp.getInvokeId(), insertSubDataResp.getMAPDialog().getService().toString());

      if (insertSubDataResp.getMAPDialog().getTCAPMessageType() == MessageType.End
          || insertSubDataResp.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        logger.debug("Closing for dialogId = {}", dialogId);
        dataElement = Transaction.getInstance().removeDialogData(dialogId, respInvokeId);
        mapDialogOut.setIsResponse();
      } else {
        dataElement = Transaction.getInstance().getDialogData(dialogId, respInvokeId);
      }

      if (dataElement == null) {
        logmsg = String.format("Dialog Id = %d not found in Transaction Map. %s", dialogId,
            transactionId);
        return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
      }
      InsertSubscriberDataRequest origEvent =
          (InsertSubscriberDataRequest) dataElement.getRequestObject();
      MAPDialogMobility mapDialogMobility = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();

      mapDialogMobility.setUserObject(invokeId);
      mapDialogMobility.addInsertSubscriberDataResponse(invokeId, clone.getTeleserviceList(),
          clone.getBearerServiceList(), clone.getSSList(), clone.getODBGeneralData(),
          clone.getRegionalSubscriptionResponse(), clone.getSupportedCamelPhases(),
          clone.getExtensionContainer(), clone.getOfferedCamel4CSIs(),
          clone.getSupportedFeatures(), clone.getExtSupportedFeatures());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogMobility);
      // return
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("InsertSubscriberDataResponse with DialogId {} failed. Error:", dialogId, mapex);
      logmsg = mapex.getMessage();
    } catch (Exception e) {
      logmsg = e.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  /**
   * Process request for InsertSubscriberData
   *
   * @param mapProxyDialog MapProxyDialog
   * @param request        InsertSubscriberDataRequest
   * @return MapDialogOut
   */
  public static MapDialogOut getInsertSubDataRequest(MapProxyDialog mapProxyDialog,
                                                     InsertSubscriberDataRequest request, String transactionId) {
    Long dialogId = request.getMAPDialog().getLocalDialogId();
    String messageType = request.getMessageType().toString();
    String logmsg;
    if (mapProxyDialog == null) {
      logmsg = String.format(
          "%s, MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'. MAP Message will be discarded",
          transactionId, dialogId, request.getInvokeId(), request.getMessageType().toString());
      logger.debug(logmsg);
      logmsg = String.format("MAP Application Rule not found for DialogId = '%d', InvokeId = '%d'",
          dialogId, request.getInvokeId());
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    }
    try {
      logger.debug("[MAP::REQUEST<{}>] Incoming DialogId = '{}', InvokeId = '{}', {}", request.getMessageType().toString(), dialogId, request.getInvokeId(), transactionId);
      MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();

      Long newInvokeId = mapMobilityOut.addInsertSubscriberDataRequest(mapProxyDialog.getImsi(),
          request.getMsisdn(), request.getCategory(), request.getSubscriberStatus(),
          request.getBearerServiceList(), request.getTeleserviceList(), request.getProvisionedSS(),
          request.getODBData(), request.getRoamingRestrictionDueToUnsupportedFeature(),
          request.getRegionalSubscriptionData(), request.getVbsSubscriptionData(),
          request.getVgcsSubscriptionData(), request.getVlrCamelSubscriptionInfo(),
          request.getExtensionContainer(), request.getNAEAPreferredCI(),
          request.getGPRSSubscriptionData(),
          request.getRoamingRestrictedInSgsnDueToUnsupportedFeature(),
          request.getNetworkAccessMode(), request.getLSAInformation(), request.getLmuIndicator(),
          request.getLCSInformation(), request.getIstAlertTimer(),
          request.getSuperChargerSupportedInHLR(), request.getMcSsInfo(),
          request.getCSAllocationRetentionPriority(), request.getSgsnCamelSubscriptionInfo(),
          request.getChargingCharacteristics(), request.getAccessRestrictionData(),
          request.getIcsIndicator(), request.getEpsSubscriptionData(),
          request.getCsgSubscriptionDataList(), request.getUeReachabilityRequestIndicator(),
          request.getSgsnNumber(), request.getMmeName(), request.getSubscribedPeriodicRAUTAUtimer(),
          request.getVplmnLIPAAllowed(), request.getMdtUserConsent(),
          request.getSubscribedPeriodicLAUtimer(), request.getVPLMNCSGSubscriptionDataList(),
          request.getAdditionalMSISDN(), request.getPSandSMSOnlyServiceProvision(),
          request.getSMSInSGSNAllowed(), request.getCsToPsSRVCCAllowedIndicator(),
          request.getPCSCFRestorationRequest(), request.getAdjacentAccessRestrictionDataList(),
          request.getIMSIGroupIdList(), request.getUEUsageType(), request.getUserPlaneIntegrityProtectionIndicator(),
          request.getDLBufferingSuggestedPacketCount(), request.getResetIdList(),
          request.getEDRXCycleLengthList(), request.getExtAccessRestrictionData(),
          request.getIabOperationAllowedIndicator());


      return new MapDialogOut(mapMobilityOut, newInvokeId, dialogId, mapProxyDialog);
    } catch (MAPException mapex) {
      logger.error("InsertSubscriberDataRequest with DialogId {} failed. Exception caught '{}', {}", dialogId, mapex, transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  public static MapDialogOut sendUpdateLocation(InsertSubscriberDataRequest request,
                                                DataElement dataElement, String transactionId) {
    Long dialogId = request.getMAPDialog().getLocalDialogId();
    String messageType = request.getMessageType().toString();
    String logmsg;
    try {
      MAPDialogMobility mapDialogMobility;
      if (dataElement.getMessageType().equals("updateLocation_Request")) {
        UpdateLocationRequest updateLocationRequest =
            (UpdateLocationRequest) dataElement.getRequestObject();
        mapDialogMobility = updateLocationRequest.getMAPDialog();
      } else if (dataElement.getMessageType().equals("updateGprsLocation_Request")) {
        UpdateGprsLocationRequest upgprsLocation =
            (UpdateGprsLocationRequest) dataElement.getRequestObject();
        mapDialogMobility = upgprsLocation.getMAPDialog();
      } else {
        logmsg = "FAILED to process MAP Message: " + request;
        return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
      }

      // The proxy answers the VLR's Update-Location dialogue as responder, so its messages there would leave
      // from the HLR address the VLR dialled. The ISD is its first message back, which TCAP lets carry a new
      // calling address: the one the ISD rule gives standalone ISDs, so both reach the VPLMN alike.
      presentAsProxy(mapDialogMobility, request, transactionId);

      Long newInvokeId = mapDialogMobility.addInsertSubscriberDataRequest(request.getImsi(),
          request.getMsisdn(), request.getCategory(), request.getSubscriberStatus(),
          request.getBearerServiceList(), request.getTeleserviceList(), request.getProvisionedSS(),
          request.getODBData(), request.getRoamingRestrictionDueToUnsupportedFeature(),
          request.getRegionalSubscriptionData(), request.getVbsSubscriptionData(),
          request.getVgcsSubscriptionData(), request.getVlrCamelSubscriptionInfo(),
          request.getExtensionContainer(), request.getNAEAPreferredCI(),
          request.getGPRSSubscriptionData(),
          request.getRoamingRestrictedInSgsnDueToUnsupportedFeature(),
          request.getNetworkAccessMode(), request.getLSAInformation(), request.getLmuIndicator(),
          request.getLCSInformation(), request.getIstAlertTimer(),
          request.getSuperChargerSupportedInHLR(), request.getMcSsInfo(),
          request.getCSAllocationRetentionPriority(), request.getSgsnCamelSubscriptionInfo(),
          request.getChargingCharacteristics(), request.getAccessRestrictionData(),
          request.getIcsIndicator(), request.getEpsSubscriptionData(),
          request.getCsgSubscriptionDataList(), request.getUeReachabilityRequestIndicator(),
          request.getSgsnNumber(), request.getMmeName(), request.getSubscribedPeriodicRAUTAUtimer(),
          request.getVplmnLIPAAllowed(), request.getMdtUserConsent(),
          request.getSubscribedPeriodicLAUtimer(), request.getVPLMNCSGSubscriptionDataList(),
          request.getAdditionalMSISDN(), request.getPSandSMSOnlyServiceProvision(),
          request.getSMSInSGSNAllowed(), request.getCsToPsSRVCCAllowedIndicator(),
          request.getPCSCFRestorationRequest(), request.getAdjacentAccessRestrictionDataList(),
          request.getIMSIGroupIdList(), request.getUEUsageType(), request.getUserPlaneIntegrityProtectionIndicator(),
          request.getDLBufferingSuggestedPacketCount(), request.getResetIdList(),
          request.getEDRXCycleLengthList(), request.getExtAccessRestrictionData(),
          request.getIabOperationAllowedIndicator());

      return new MapDialogOut(mapDialogMobility, newInvokeId, dialogId);
    } catch (MAPException mapex) {
      logger.error("sendUpdateLocation with DialogId {} failed. Exception caught '{}', {}", dialogId, mapex, transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  /**
   * Gives an Update-Location dialogue the calling address the ISD rule assigns, when one matches: the
   * dialogue's local address keeps its routing indicator, point code and SSN, and takes the rule's GT.
   */
  private static void presentAsProxy(MAPDialogMobility dialog, InsertSubscriberDataRequest request,
                                     String transactionId) {
    var local = dialog.getLocalAddress();
    var remote = dialog.getRemoteAddress();
    if (local == null || remote == null || local.getGlobalTitle() == null || remote.getGlobalTitle() == null) {
      return;
    }
    String callingGt = local.getGlobalTitle().getDigits();
    String calledGt = remote.getGlobalTitle().getDigits();
    String imsi = request.getImsi() != null ? request.getImsi().getData() : "";
    var rule = MapProxyApplicationRules.getInstance()
        .findMAPApplicationRule(callingGt, calledGt, imsi, request.getMessageType().toString());
    if (rule == null) {
      return;
    }
    var replaced = rule.getReplaceRule().applyReplaceRule(imsi, callingGt, calledGt);
    if (replaced == null || replaced.getCallingGlobalTitle() == null) {
      return;
    }
    dialog.setLocalAddress(new SccpAddressImpl(local.getAddressIndicator().getRoutingIndicator(),
        replaced.getCallingGlobalTitle(), local.getSignalingPointCode(), local.getSubsystemNumber()));
    logger.debug("<insertSubscriberData_Request, {}>: in-dialogue ISD leaves as '{}' (rule '{}'), not '{}'",
        transactionId, replaced.getCallingGlobalTitle().getDigits(), rule.getName(), callingGt);
  }
}
