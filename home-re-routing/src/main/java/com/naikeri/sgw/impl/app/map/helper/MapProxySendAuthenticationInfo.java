package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxySendAuthenticationInfo
 */
public class MapProxySendAuthenticationInfo {

  private static final Logger logger = LogManager.getLogger(MapProxySendAuthenticationInfo.class);

  private MapProxySendAuthenticationInfo() {
    throw new IllegalStateException("Private constructor");
  }

  /**
   * Process response for sendAuthenticationInfo_Response
   *
   * @return MapDialogOut
   */
  public static MapDialogOut getSendAuthInfoResponse(Object message, String transactionId) {

    SendAuthenticationInfoResponse sendAuthInfoResp = (SendAuthenticationInfoResponse) message;
    String messageType = sendAuthInfoResp.getMessageType().toString();
    Long dialogId = sendAuthInfoResp.getMAPDialog().getLocalDialogId();
    try {
      Long respInvokeId = sendAuthInfoResp.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      SendAuthenticationInfoResponseCopy clone =
          new SendAuthenticationInfoResponseCopy(sendAuthInfoResp);
      logger.debug("[MAP::RESPONSE<{}>] Incoming DialogId '{}', invokeId '{}' {}", sendAuthInfoResp.getMessageType().toString(), dialogId, sendAuthInfoResp.getInvokeId(), transactionId);
      DataElement dataElement;

      logger.debug("TCAP Message Type = '{}', dialogId = {}, InvokeId = {}, Service = '{}'", sendAuthInfoResp.getMAPDialog().getTCAPMessageType(), dialogId, sendAuthInfoResp.getInvokeId(), sendAuthInfoResp.getMAPDialog().getService().toString());

      if (sendAuthInfoResp.getMAPDialog().getTCAPMessageType() == MessageType.End
          || sendAuthInfoResp.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        logger.debug("Closing MAP Dialog. DialogId = {}", dialogId);
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
      SendAuthenticationInfoRequest origEvent =
          (SendAuthenticationInfoRequest) dataElement.getRequestObject();
      MAPDialogMobility mapDialogMobility = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();

      mapDialogMobility.setUserObject(invokeId);
      mapDialogMobility.addSendAuthenticationInfoResponse(invokeId,
          clone.getAuthenticationSetList(),
          clone.getExtensionContainer(),
          clone.getEpsAuthenticationSetList(),
          clone.getUeUsageType());
      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogMobility);

      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("SendAuthenticationInfoResponse with DialogId {} failed {}. Exception caught '{}'", dialogId, transactionId, mapex);
      return MapProxyUtilsHelper.discardReason(mapex.getMessage(), messageType, transactionId);
    }catch(Exception e){
      logger.error("Send Authentication Info Error occurred. Error: ", e);
      return MapProxyUtilsHelper.discardReason(e.getMessage(), messageType, transactionId);
    }
  }


  /**
   * Process send authentication info request
   *
   * @param mapProxyDialog  MapProxyDialog
   * @param sendAuthInfoReq SendAuthenticationInfoRequest
   * @return MapDialogOut
   */
  public static MapDialogOut getSendAuthenticationInfoRequest(MapProxyDialog mapProxyDialog,
                                                              SendAuthenticationInfoRequest sendAuthInfoReq, String transactionId) {
    Long dialogId = sendAuthInfoReq.getMAPDialog().getLocalDialogId();
    if (mapProxyDialog == null) {
      String logMessage = String.format(
          "%s, MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'",
          transactionId, dialogId, sendAuthInfoReq.getInvokeId(),
          sendAuthInfoReq.getMessageType().toString());
      logger.debug(logMessage);
      logMessage = String.format("MAP Application Rule not found for DialogId = '%d', InvokeId = '%d'", dialogId, sendAuthInfoReq.getInvokeId());
      return MapProxyUtilsHelper.discardReason(logMessage,
          sendAuthInfoReq.getMessageType().toString(), transactionId);
    }
    try {
      logger.debug("[MAP::REQUEST<{}>] Incoming DialogId = '{}', InvokeId = '{}', {}", sendAuthInfoReq.getMessageType().toString(), dialogId, sendAuthInfoReq.getInvokeId(), transactionId);
      MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();

      Long newInvokeId = mapMobilityOut.addSendAuthenticationInfoRequest(mapProxyDialog.getImsi(),
          sendAuthInfoReq.getNumberOfRequestedVectors(),
          sendAuthInfoReq.getSegmentationProhibited(),
          sendAuthInfoReq.getImmediateResponsePreferred(),
          sendAuthInfoReq.getReSynchronisationInfo(),
          sendAuthInfoReq.getExtensionContainer(),
          sendAuthInfoReq.getRequestingNodeType(),
          sendAuthInfoReq.getRequestingPlmnId(),
          sendAuthInfoReq.getNumberOfRequestedAdditionalVectors(),
          sendAuthInfoReq.getAdditionalVectorsAreForEPS(),
          sendAuthInfoReq.getUeUsageTypeRequestIndication());

      return new MapDialogOut(mapMobilityOut, newInvokeId, dialogId, mapProxyDialog);

    } catch (MAPException mapex) {
      logger.error("SendAuthenticationInfoRequest with DialogId {} failed. Exception caught '{}', {}", dialogId, mapex, transactionId);
      return MapProxyUtilsHelper.discardReason(mapex.getMessage(),
          sendAuthInfoReq.getMessageType().toString(), transactionId);
    } catch (Exception ex) {
      return MapProxyUtilsHelper.discardReason(ex.getMessage(),
          sendAuthInfoReq.getMessageType().toString(), transactionId);
    }
  }
}