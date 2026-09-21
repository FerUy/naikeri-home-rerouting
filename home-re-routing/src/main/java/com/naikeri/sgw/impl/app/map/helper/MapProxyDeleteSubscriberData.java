package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.dialog.Reason;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxyDeleteSubscriberData
 */
public class MapProxyDeleteSubscriberData {

  private static final Logger logger = LogManager.getLogger(MapProxyDeleteSubscriberData.class);

  private MapProxyDeleteSubscriberData() {
  }

  /**
   * Process response for DeleteSubscriberDataResponse
   *
   * @return MapDialogOut
   */
  public static MapDialogOut getResponse(Object message, String transactionId) {
    DeleteSubscriberDataResponse deleteSubDataResponse = (DeleteSubscriberDataResponse) message;
    Long dialogId = deleteSubDataResponse.getMAPDialog().getLocalDialogId();
    String messageType = deleteSubDataResponse.getMessageType().toString();
    String logmsg;
    try {
      Long respInvokeId = deleteSubDataResponse.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);

      DeleteSubscriberDataResponseCopy clone = new DeleteSubscriberDataResponseCopy(deleteSubDataResponse);
      logger.debug("[MAP::RESPONSE<{}>] Incoming DialogId '{}', invokeId '{}', {}", deleteSubDataResponse.getMessageType().toString(), dialogId, deleteSubDataResponse.getInvokeId(), transactionId);

      DataElement dataElement;
      logger.debug("TCAP Message Type = '{}', dialogId = {}, InvokeId = {}, Service = '{}'", deleteSubDataResponse.getMAPDialog().getTCAPMessageType(), dialogId, deleteSubDataResponse.getInvokeId(), deleteSubDataResponse.getMAPDialog().getService().toString());

      if (deleteSubDataResponse.getMAPDialog().getTCAPMessageType() == MessageType.End
          || deleteSubDataResponse.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
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
      DeleteSubscriberDataRequest origEvent =
          (DeleteSubscriberDataRequest) dataElement.getRequestObject();
      MAPDialogMobility mapDialogMobility = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();
      mapDialogMobility.setUserObject(invokeId);
      mapDialogMobility.addDeleteSubscriberDataResponse(invokeId,
          clone.getRegionalSubscriptionResponse(), clone.getExtensionContainer());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogMobility);
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("DeleteSubscriberDataResponse with DialogId {} failed {}. Exception caught '{}'", dialogId, transactionId, mapex);
      logmsg = mapex.getMessage();
    } catch (Exception e) {
      logmsg = e.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  /**
   * Process Delete Subscriber Data request
   *
   * @param mapProxyDialog       MapProxyDialog
   * @param deleteSubDataRequest AnyTimeInterrogationRequest
   * @return MapDialogOut
   */
  public static MapDialogOut getRequest(MapProxyDialog mapProxyDialog,
                                        DeleteSubscriberDataRequest deleteSubDataRequest, String transactionId) {
    Long dialogId = deleteSubDataRequest.getMAPDialog().getLocalDialogId();
    MapDialogOut mapDialogOut = new MapDialogOut();
    String messageType = deleteSubDataRequest.getMessageType().toString();
    String logmsg;
    mapDialogOut.setOriginalDialogId(dialogId);
    try {
      if (mapProxyDialog == null) {
        logger.info("{}, MAP Application Rule not found for DialogId = '{}', InvokeId = '{}', MessageType = '{}'. MAP Message will be discarded", transactionId, dialogId, deleteSubDataRequest.getInvokeId(), deleteSubDataRequest.getMessageType().toString());
        mapDialogOut.setInvokeId(null);
        MAPDialogMobility mapMobilityIn = deleteSubDataRequest.getMAPDialog();
        mapMobilityIn.refuse(Reason.noReasonGiven);
        mapDialogOut.setMapDialog(mapMobilityIn);
      } else {
        logger.debug("[MAP::REQUEST<{}>] Incoming DialogId = '{}', InvokeId = '{}', {}", deleteSubDataRequest.getMessageType().toString(), dialogId, deleteSubDataRequest.getInvokeId(), transactionId);
        MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();
        Long newInvokeId = mapMobilityOut.addDeleteSubscriberDataRequest(mapProxyDialog.getImsi(),
            deleteSubDataRequest.getBasicServiceList(), deleteSubDataRequest.getSsList(),
            deleteSubDataRequest.getRoamingRestrictionDueToUnsupportedFeature(),
            deleteSubDataRequest.getRegionalSubscriptionIdentifier(),
            deleteSubDataRequest.getVbsGroupIndication(),
            deleteSubDataRequest.getVgcsGroupIndication(),
            deleteSubDataRequest.getCamelSubscriptionInfoWithdraw(),
            deleteSubDataRequest.getExtensionContainer(),
            deleteSubDataRequest.getGPRSSubscriptionDataWithdraw(),
            deleteSubDataRequest.getRoamingRestrictedInSgsnDueToUnsuppportedFeature(),
            deleteSubDataRequest.getLSAInformationWithdraw(),
            deleteSubDataRequest.getGmlcListWithdraw(),
            deleteSubDataRequest.getIstInformationWithdraw(),
            deleteSubDataRequest.getSpecificCSIWithdraw(),
            deleteSubDataRequest.getChargingCharacteristicsWithdraw(),
            deleteSubDataRequest.getStnSrWithdraw(),
            deleteSubDataRequest.getEPSSubscriptionDataWithdraw(),
            deleteSubDataRequest.getApnOiReplacementWithdraw(),
            deleteSubDataRequest.getCsgSubscriptionDeleted(),
            deleteSubDataRequest.getSubscribedPeriodicTAURAUTimerWithdraw(),
            deleteSubDataRequest.getSubscribedPeriodicLAUTimerWithdraw(),
            deleteSubDataRequest.getSubscribedVsrvccWithdraw(),
            deleteSubDataRequest.getVplmnCsgSubscriptionDeleted(),
            deleteSubDataRequest.getAdditionalMSISDNWithdraw(),
            deleteSubDataRequest.getCsToPsSRVCCWithdraw(),
            deleteSubDataRequest.getImsiGroupIdListWithdraw(),
            deleteSubDataRequest.getUserPlaneIntegrityProtectionWithdraw(),
            deleteSubDataRequest.getDlBufferingSuggestedPacketCountWithdraw(),
            deleteSubDataRequest.getUeUsageTypeWithdraw(),
            deleteSubDataRequest.getResetIdsWithdraw(),
            deleteSubDataRequest.getIabOperationWithdraw());

        mapDialogOut.setInvokeId(newInvokeId);
        mapDialogOut.setMapDialog(mapMobilityOut);
        mapDialogOut.setProxyDialog(mapProxyDialog);
      }
      return mapDialogOut;

    } catch (MAPException mapex) {
      logger.error("DeleteSubscriberDataRequest with DialogId {} failed. Exception caught '{}', {}", dialogId, mapex, transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }
}
