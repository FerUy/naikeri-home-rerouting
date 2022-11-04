package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.supplementary.MAPDialogSupplementary;
import org.restcomm.protocols.ss7.map.api.service.supplementary.UnstructuredSSRequest;
import org.restcomm.protocols.ss7.map.api.service.supplementary.UnstructuredSSResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxyUnstructuredSSRequest
 */
public class MapProxyUnstructuredSSRequest {

  private static final Logger logger = Logger.getLogger(MapProxyUnstructuredSSRequest.class);

  private MapProxyUnstructuredSSRequest() {
  }

  public static MapDialogOut getResponse(Object message, String transactionId) {
    UnstructuredSSResponse unstrResInd = (UnstructuredSSResponse) message;
    Long dialogId = unstrResInd.getMAPDialog().getLocalDialogId();
    String messageType = unstrResInd.getMessageType().toString();
    try {
      Long respInvokeId = unstrResInd.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);

      logger.debug(String.format("[MAP::RESPONSE<%s>] Incoming DialogId '%d', invokeId '%d', %s",
          unstrResInd.getMessageType().toString(), dialogId, unstrResInd.getInvokeId(),
          transactionId));

      DataElement dataElement = null;
      logger.debug(
          String.format("TCAP Message Type = '%s', dialogId = %d, InvokeId = %d, Service = '%s'",
              unstrResInd.getMAPDialog().getTCAPMessageType(), dialogId, unstrResInd.getInvokeId(),
              unstrResInd.getMAPDialog().getService().toString()));

      if (unstrResInd.getMAPDialog().getTCAPMessageType() == MessageType.End
          || unstrResInd.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        logger.debug("Closing for dialogId = " + dialogId);
        dataElement = Transaction.getInstance().removeDialogData(dialogId, respInvokeId);
        mapDialogOut.setIsResponse();
      } else {
        dataElement = Transaction.getInstance().getDialogData(dialogId, respInvokeId);
      }


      if (dataElement != null) {
        UnstructuredSSRequest request = (UnstructuredSSRequest) dataElement.getRequestObject();
        MAPDialogSupplementary origMapDialogSupplementary = unstrResInd.getMAPDialog();
        Long invokeId = request.getInvokeId();
        origMapDialogSupplementary.setUserObject(invokeId);
        origMapDialogSupplementary.addUnstructuredSSResponse(invokeId,
            unstrResInd.getDataCodingScheme(), unstrResInd.getUSSDString());

        mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
        mapDialogOut.setMapDialog(origMapDialogSupplementary);
        return mapDialogOut;
      }
      String logmsg =
          String.format("Dialog Id = %d not found in Transaction Map. %s", dialogId, transactionId);
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    } catch (MAPException mapex) {
      logger.error("UnstructuredSSResponse with DialogId " + dialogId + " failed " + transactionId
          + ". Exception caught '" + mapex + "'");
      return MapProxyUtilsHelper.discardReason(mapex.getMessage(), messageType, transactionId);
    } catch (Exception ex) {
      return MapProxyUtilsHelper.discardReason(ex.getMessage(), messageType, transactionId);
    }
  }


  public static MapDialogOut getRequest(MapProxyDialog mapProxyDialog,
      UnstructuredSSRequest unstrReqInd, String transactionId) {
    Long dialogId = unstrReqInd.getMAPDialog().getLocalDialogId();
    String messageType = unstrReqInd.getMessageType().toString();
    String logmsg = "";
    if (mapProxyDialog == null) {
      logmsg = String.format(
          "%s, MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'",
          transactionId, dialogId, unstrReqInd.getInvokeId(),
          unstrReqInd.getMessageType().toString());
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    }
    try {
      logger.debug(String.format("[MAP::REQUEST<%s>] dialogId = '%d', invokeId = '%d', %s",
          unstrReqInd.getMessageType().toString(), dialogId, unstrReqInd.getInvokeId(),
          transactionId));


      MAPDialogSupplementary mapDialogSupplementary = mapProxyDialog.getMapDialogSupplementary();
      Long newInvokeId = mapDialogSupplementary.addUnstructuredSSRequest(
          unstrReqInd.getDataCodingScheme(), unstrReqInd.getUSSDString(),
          unstrReqInd.getAlertingPattern(), unstrReqInd.getMSISDNAddressString());

      return new MapDialogOut(mapDialogSupplementary, newInvokeId, dialogId, mapProxyDialog);

    } catch (MAPException mapex) {
      logger.error("UnstructuredSSRequest with DialogId " + dialogId + " failed. Exception caught '"
          + mapex + "', " + transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }
}
