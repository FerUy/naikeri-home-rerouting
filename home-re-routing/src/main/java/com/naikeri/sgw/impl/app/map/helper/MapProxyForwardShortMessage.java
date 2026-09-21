package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.dialog.Reason;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPDialogSms;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxyForwardShortMessage
 */
public class MapProxyForwardShortMessage {

  private MapProxyForwardShortMessage() {
  }

  private static final Logger logger = LogManager.getLogger(MapProxyForwardShortMessage.class);

  public static MapDialogOut processRequest(MapProxyDialog mapProxyDialog, ForwardShortMessageRequest request, String transactionId) {
    Long dialogId = request.getMAPDialog().getLocalDialogId();
    String messageType = request.getMessageType().toString();
    String logmsg;
    MapDialogOut mapDialogOut = new MapDialogOut();
    mapDialogOut.setOriginalDialogId(dialogId);
    try {
      if (mapProxyDialog == null) {
        logger.info("{}, MAP Application Rule not found for DialogId = '{}', InvokeId = '{}', MessageType = '{}'. MAP Message will be discarded", transactionId, dialogId, request.getInvokeId(), request.getMessageType().toString());
        mapDialogOut.setInvokeId(null);
        MAPDialogSms smsHandlerIn = request.getMAPDialog();
        smsHandlerIn.refuse(Reason.noReasonGiven);
        mapDialogOut.setMapDialog(smsHandlerIn);
      } else {
        logger.debug("[MAP::REQUEST<{}>] dialogId = '{}', InvokeId = '{}', {}", request.getMessageType().toString(), dialogId, request.getInvokeId(), transactionId);

        MAPDialogSms smsHandlerOut = mapProxyDialog.getMapDialogSms();
        Long newInvokeId = smsHandlerOut.addForwardShortMessageRequest(request.getSM_RP_DA(),
            request.getSM_RP_OA(), request.getSM_RP_UI(), request.getMoreMessagesToSend());
        mapDialogOut.setMapDialog(smsHandlerOut);
        mapDialogOut.setInvokeId(newInvokeId);
        mapDialogOut.setProxyDialog(mapProxyDialog);
      }
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("Processing forward SM Request failed {}, {}", mapex, transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  public static MapDialogOut processReponse(Object message, String transactionId) {
    ForwardShortMessageResponse response = (ForwardShortMessageResponse) message;
    Long dialogId = response.getMAPDialog().getLocalDialogId();
    String messageType = response.getMessageType().toString();
    String logmsg;
    try {
      Long respInvokeId = response.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);

      logger.debug("[MAP::RESPONSE<{}>] Incoming DialogId '{}', invokeId '{}', {}", response.getMessageType().toString(), dialogId, response.getInvokeId(), transactionId);

      DataElement dataElement;
      logger.debug("TCAP Message Type = '{}', dialogId = {}, Service = '{}', InvokeId = {}", response.getMAPDialog().getTCAPMessageType(), dialogId, response.getMAPDialog().getService().toString(), response.getInvokeId());

      if (response.getMAPDialog().getTCAPMessageType() == MessageType.End
          || response.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
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
      ForwardShortMessageRequest origEvent =
          (ForwardShortMessageRequest) dataElement.getRequestObject();
      MAPDialogSms mapDialogSms = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();

      mapDialogSms.setUserObject(invokeId);
      mapDialogSms.addForwardShortMessageResponse(invokeId);

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogSms);

      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("ForwardShortMessageResponse with DialogId {} failed {}. Exception caught '{}'", dialogId, transactionId, mapex);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

}
