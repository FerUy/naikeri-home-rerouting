package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.dialog.Reason;
import org.restcomm.protocols.ss7.map.api.service.callhandling.MAPDialogCallHandling;
import org.restcomm.protocols.ss7.map.api.service.callhandling.SendRoutingInformationRequest;
import org.restcomm.protocols.ss7.map.api.service.callhandling.SendRoutingInformationResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxySendRoutingInfo
 */
public class MapProxySendRoutingInfo {

  private static final Logger logger = LogManager.getLogger(MapProxySendRoutingInfo.class);

  private MapProxySendRoutingInfo(){}

  public static MapDialogOut getResponse(Object message, String transactionId) {
    SendRoutingInformationResponse sendRoutingInfoResponse =
        (SendRoutingInformationResponse) message;
    Long dialogId = sendRoutingInfoResponse.getMAPDialog().getLocalDialogId();
    String messageType = sendRoutingInfoResponse.getMessageType().toString();
    try {
      Long respInvokeId = sendRoutingInfoResponse.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);

      logger.debug("[MAP::RESPONSE<{}>] Incoming DialogId '{}', invokeId '{}', {}", sendRoutingInfoResponse.getMessageType().toString(), dialogId, sendRoutingInfoResponse.getInvokeId(), transactionId);
      DataElement dataElement;
      logger.debug("TCAP Message Type = '{}', dialogId = {}, InvokeId = {}, Service = '{}'", sendRoutingInfoResponse.getMAPDialog().getTCAPMessageType(), dialogId, sendRoutingInfoResponse.getInvokeId(), sendRoutingInfoResponse.getMAPDialog().getService().toString());

      if (sendRoutingInfoResponse.getMAPDialog().getTCAPMessageType() == MessageType.End
          || sendRoutingInfoResponse.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        logger.debug("Closing for dialogId = {}", dialogId);
        dataElement = Transaction.getInstance().removeDialogData(dialogId, respInvokeId);
        mapDialogOut.setIsResponse();
      } else {
        dataElement = Transaction.getInstance().getDialogData(dialogId, respInvokeId);
      }

      if (dataElement == null) {
        String logmsg = String.format("Dialog Id = %d not found in Transaction Map. %s", dialogId,
            transactionId);
        return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
      }
      SendRoutingInformationRequest origEvent =
          (SendRoutingInformationRequest) dataElement.getRequestObject();
      MAPDialogCallHandling origCallHandlingOut = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();
      origCallHandlingOut.setUserObject(invokeId);
      origCallHandlingOut.addSendRoutingInformationResponse(invokeId,
          sendRoutingInfoResponse.getIMSI(), sendRoutingInfoResponse.getCUGCheckInfo(),
          sendRoutingInfoResponse.getRoutingInfo2());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(origCallHandlingOut);
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("SendRoutingInformationResponse with DialogId {} failed {}. Exception caught '{}'", dialogId, transactionId, mapex);
      return MapProxyUtilsHelper.discardReason(mapex.getMessage(), messageType, transactionId);
    }catch(Exception ex){
      return MapProxyUtilsHelper.discardReason(ex.getMessage(), messageType, transactionId);
    }
  }


  public static MapDialogOut getRequest(MapProxyDialog mapProxyDialog, SendRoutingInformationRequest dialogInSendRoutingInfo, String transactionId) {
    Long dialogId = 0L;
    String messageType = dialogInSendRoutingInfo.getMessageType().toString();
    try {
      dialogId = dialogInSendRoutingInfo.getMAPDialog().getLocalDialogId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      if (mapProxyDialog == null) {
        logger.info("{}, MAP Application Rule not found for DialogId = '{}', InvokeId = '{}', MessageType = '{}'. MAP Message will be discarded", transactionId, dialogId, dialogInSendRoutingInfo.getInvokeId(), dialogInSendRoutingInfo.getMessageType().toString());
        MAPDialogCallHandling dialogIn = dialogInSendRoutingInfo.getMAPDialog();
        dialogIn.refuse(Reason.noReasonGiven);
        mapDialogOut.setMapDialog(dialogIn);
        mapDialogOut.setInvokeId(null);
      } else {
        logger.debug("[MAP::REQUEST<{}>] Incoming DialogId = '{}', InvokeId = '{}', {}", dialogInSendRoutingInfo.getMessageType().toString(), dialogId, dialogInSendRoutingInfo.getInvokeId(), transactionId);

        MAPDialogCallHandling callHandlingOut = mapProxyDialog.getMapDialogCallHandling();
        Long newInvokeId = callHandlingOut.addSendRoutingInformationRequest(
            dialogInSendRoutingInfo.getMsisdn(), dialogInSendRoutingInfo.getCUGCheckInfo(),
            dialogInSendRoutingInfo.getNumberOfForwarding(),
            dialogInSendRoutingInfo.getInterrogationType(),
            dialogInSendRoutingInfo.getORInterrogation(), dialogInSendRoutingInfo.getORCapability(),
            dialogInSendRoutingInfo.getGmscOrGsmSCFAddress(),
            dialogInSendRoutingInfo.getCallReferenceNumber(),
            dialogInSendRoutingInfo.getForwardingReason(),
            dialogInSendRoutingInfo.getBasicServiceGroup(),
            dialogInSendRoutingInfo.getNetworkSignalInfo(), dialogInSendRoutingInfo.getCamelInfo(),
            dialogInSendRoutingInfo.getSuppressionOfAnnouncement(),
            dialogInSendRoutingInfo.getExtensionContainer(),
            dialogInSendRoutingInfo.getAlertingPattern(), dialogInSendRoutingInfo.getCCBSCall(),
            dialogInSendRoutingInfo.getSupportedCCBSPhase(),
            dialogInSendRoutingInfo.getAdditionalSignalInfo(),
            dialogInSendRoutingInfo.getIstSupportIndicator(),
            dialogInSendRoutingInfo.getPrePagingSupported(),
            dialogInSendRoutingInfo.getCallDiversionTreatmentIndicator(),
            dialogInSendRoutingInfo.getLongFTNSupported(),
            dialogInSendRoutingInfo.getSuppressVtCSI(),
            dialogInSendRoutingInfo.getSuppressIncomingCallBarring(),
            dialogInSendRoutingInfo.getGsmSCFInitiatedCall(),
            dialogInSendRoutingInfo.getBasicServiceGroup2(),
            dialogInSendRoutingInfo.getNetworkSignalInfo2(),
            dialogInSendRoutingInfo.getSuppressMTSS(),
            dialogInSendRoutingInfo.getMTRoamingRetrySupported(),
            dialogInSendRoutingInfo.getCallPriority());

        mapDialogOut.setMapDialog(callHandlingOut);
        mapDialogOut.setInvokeId(newInvokeId);
        mapDialogOut.setProxyDialog(mapProxyDialog);
      }
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("SendRoutingInformationRequest with DialogId {} failed. Exception caught '{}', {}", dialogId, mapex, transactionId);
      return MapProxyUtilsHelper.discardReason(mapex.getMessage(), messageType, transactionId);
    } catch (Exception ex) {
      logger.error("Error occurred: ", ex);
      return MapProxyUtilsHelper.discardReason(ex.getMessage(), messageType, transactionId);
    }
  }
}
