package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.imei.CheckImeiRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.imei.CheckImeiResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxyCheckIMEI
 */
public class MapProxyCheckIMEI {

  private static final Logger logger = LogManager.getLogger(MapProxyCheckIMEI.class);

  private MapProxyCheckIMEI() {
  }

  /**
   * Process response for CheckImeiResponse
   *
   * @return MapDialogOut
   */
  public static MapDialogOut getResponse(Object message, String transactionId) {
    CheckImeiResponse checkImeiResponse = (CheckImeiResponse) message;
    Long dialogId = checkImeiResponse.getMAPDialog().getLocalDialogId();
    String messageType = checkImeiResponse.getMessageType().toString();
    String logmsg;
    try {
      Long respInvokeId = checkImeiResponse.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);

      logger.debug("[MAP::RESPONSE<{}>] Incoming DialogId '{}', invokeId '{}', {}", checkImeiResponse.getMessageType().toString(), dialogId, checkImeiResponse.getInvokeId(), transactionId);

      DataElement dataElement;
      logger.debug("TCAP Message Type = '{}', dialogId = {}, InvokeId = {}, Service = '{}'", checkImeiResponse.getMAPDialog().getTCAPMessageType(), dialogId, checkImeiResponse.getInvokeId(), checkImeiResponse.getMAPDialog().getService().toString());

      if (checkImeiResponse.getMAPDialog().getTCAPMessageType() == MessageType.End
          || checkImeiResponse.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
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
      CheckImeiRequest origEvent = (CheckImeiRequest) dataElement.getRequestObject();
      MAPDialogMobility mapDialogMobility = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();

      mapDialogMobility.setUserObject(invokeId);
      mapDialogMobility.addCheckImeiResponse(invokeId, checkImeiResponse.getEquipmentStatus(),
          checkImeiResponse.getBmuef(), checkImeiResponse.getExtensionContainer());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogMobility);
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("CheckImeiResponse with DialogId {} failed {}. Exception caught '{}'", dialogId, transactionId, mapex);
      logmsg = mapex.getMessage();
    } catch (Exception e) {
      logmsg = e.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  /**
   * Process check IMEI request
   *
   * @param mapProxyDialog MapProxyDialog
   * @param imeiRequest    CheckImeiRequest
   * @return MapDialogOut
   */
  public static MapDialogOut getRequest(MapProxyDialog mapProxyDialog, CheckImeiRequest imeiRequest,
                                        String transactionId) {
    Long dialogId = imeiRequest.getMAPDialog().getLocalDialogId();
    String messageType = imeiRequest.getMessageType().toString();
    String logmsg;
    if (mapProxyDialog == null) {
      logmsg = String.format(
          "%s MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'. MAP Message will be discarded",
          transactionId, dialogId, imeiRequest.getInvokeId(),
          imeiRequest.getMessageType().toString());
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    }
    try {
      logger.debug("[MAP::REQUEST<{}>] Incoming DialogId '{}', InvokeId '{}', {}", imeiRequest.getMessageType().toString(), dialogId, imeiRequest.getInvokeId(), transactionId);
      MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();
      Long newInvokeId = mapMobilityOut.addCheckImeiRequest(imeiRequest.getIMEI(),
          imeiRequest.getRequestedEquipmentInfo(), imeiRequest.getExtensionContainer());

      return new MapDialogOut(mapMobilityOut, newInvokeId, dialogId, mapProxyDialog);
    } catch (MAPException mapex) {
      logger.error("CheckImeiRequest with DialogId {} failed. Exception caught '{}', {}", dialogId, mapex, transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }
}
