package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.oam.MAPDialogOam;
import org.restcomm.protocols.ss7.map.api.service.oam.SendImsiRequest;
import org.restcomm.protocols.ss7.map.api.service.oam.SendImsiResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxySendIMSI
 */
public class MapProxySendIMSI {

  private static final Logger logger = LogManager.getLogger(MapProxySendIMSI.class);

  private MapProxySendIMSI() {
  }

  /**
   * Process response for SendImsiResponse
   *
   * @return MapDialogOut
   */
  public static MapDialogOut getResponse(Object message, String transactionId) {
    SendImsiResponse sendImsiResp = (SendImsiResponse) message;
    Long dialogId = sendImsiResp.getMAPDialog().getLocalDialogId();
    String messageType = sendImsiResp.getMessageType().toString();
    String logmsg;
    try {
      Long respInvokeId = sendImsiResp.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      logger.debug("[MAP::RESPONSE<{}>] Incoming DialogId '{}', invokeId '{}', {}", sendImsiResp.getMessageType().toString(), dialogId, sendImsiResp.getInvokeId(), transactionId);

      DataElement dataElement;
      logger.debug("TCAP Message Type = '{}', dialogId = {}, InvokeId = {}, Service = '{}'", sendImsiResp.getMAPDialog().getTCAPMessageType(), dialogId, sendImsiResp.getInvokeId(), sendImsiResp.getMAPDialog().getService().toString());

      if (sendImsiResp.getMAPDialog().getTCAPMessageType() == MessageType.End
          || sendImsiResp.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        logger.debug("Closing for dialogId = {}", dialogId);
        dataElement = Transaction.getInstance().removeDialogData(dialogId, respInvokeId);
        mapDialogOut.setIsResponse();
      } else {
        dataElement = Transaction.getInstance().getDialogData(dialogId, respInvokeId);
      }

      if (dataElement == null) {
        logmsg = String.format("Dialog Id = %d not found in Transaction Map. %s", dialogId, transactionId);
        return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
      }
      SendImsiRequest origEvent = (SendImsiRequest) dataElement.getRequestObject();
      MAPDialogOam origMapDialogOam = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();
      origMapDialogOam.setUserObject(invokeId);
      origMapDialogOam.addSendImsiResponse(invokeId, sendImsiResp.getImsi());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(origMapDialogOam);
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("SendImsiResponse with DialogId {} failed {}. Exception caught '{}'", dialogId, transactionId, mapex);
      logmsg = mapex.getMessage();
    } catch (Exception e) {
      logmsg = e.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  /**
   * Process Send IMSI request
   *
   * @param mapProxyDialog  MapProxyDialog
   * @param sendIMSIRequest SendImsiRequest
   * @return MapDialogOut
   */
  public static MapDialogOut getRequest(MapProxyDialog mapProxyDialog,
                                        SendImsiRequest sendIMSIRequest, String transactionId) {
    Long dialogId = sendIMSIRequest.getMAPDialog().getLocalDialogId();
    String messageType = sendIMSIRequest.getMessageType().toString();
    String logmsg;
    if (mapProxyDialog == null) {
      logmsg = String.format(
          "%s, MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'. MAP Message will be discarded",
          transactionId, dialogId, sendIMSIRequest.getInvokeId(),
          sendIMSIRequest.getMessageType().toString());
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    }
    try {
      logger.debug("[MAP::REQUEST<{}>] dialogId = '{}', InvokeId = '{}', {}", sendIMSIRequest.getMessageType().toString(), dialogId, sendIMSIRequest.getInvokeId(), transactionId);

      MAPDialogOam mapDialogOam = mapProxyDialog.getMapDialogOam();
      Long newInvokeId = mapDialogOam.addSendImsiRequest(sendIMSIRequest.getMsisdn());
      return new MapDialogOut(mapDialogOam, newInvokeId, dialogId, mapProxyDialog);
    } catch (MAPException mapex) {
      logger.error("SendImsiRequest with DialogId {} failed. Exception caught '{}', {}", dialogId, mapex, transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }
}
