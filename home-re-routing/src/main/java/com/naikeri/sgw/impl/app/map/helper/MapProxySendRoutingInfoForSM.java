package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.dialog.Reason;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPDialogSms;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxySendRoutingInfoForSM
 */
public class MapProxySendRoutingInfoForSM {

  private static final Logger logger = LogManager.getLogger(MapProxySendRoutingInfoForSM.class);

  private MapProxySendRoutingInfoForSM() {
  }

  public static MapDialogOut processRequest(MapProxyDialog mapProxyDialog, SendRoutingInfoForSMRequest request, String transactionId) {
    Long dialogId = request.getMAPDialog().getLocalDialogId();
    String messageType = request.getMessageType().toString();
    String logmsg;
    MapDialogOut mapDialogOut = new MapDialogOut();
    mapDialogOut.setOriginalDialogId(dialogId);
    try {
      if (mapProxyDialog == null) {
        logger.info("{}, MAP Application Rule not found for DialogId = '{}', InvokeId = '{}', MessageType = '{}'. MAP Message will be discarded",
            transactionId, dialogId, request.getInvokeId(), request.getMessageType().toString());
        mapDialogOut.setInvokeId(null);
        MAPDialogSms smsHandlerIn = request.getMAPDialog();
        smsHandlerIn.refuse(Reason.noReasonGiven);
        mapDialogOut.setMapDialog(smsHandlerIn);
      } else {
        logger.debug("[MAP::REQUEST<{}>] dialogId = '{}', InvokeId = '{}', {}", request.getMessageType().toString(), dialogId, request.getInvokeId(), transactionId);
        MAPDialogSms smsHandlerOut = mapProxyDialog.getMapDialogSms();
        Long newInvokeId = smsHandlerOut.addSendRoutingInfoForSMRequest(request.getMsisdn(),
            request.getSm_RP_PRI(), request.getServiceCentreAddress(),
            request.getExtensionContainer(), request.getGprsSupportIndicator(),
            request.getSM_RP_MTI(), request.getSM_RP_SMEA(), request.getSmDeliveryNotIntended(),
            request.getIpSmGwGuidanceIndicator(), mapProxyDialog.getImsi(),
            request.getT4TriggerIndicator(), request.getSingleAttemptDelivery(),
            request.getTeleservice(), request.getCorrelationID(),
            request.getSmsfSupportIndicator());

        mapDialogOut.setMapDialog(smsHandlerOut);
        mapDialogOut.setInvokeId(newInvokeId);
        mapDialogOut.setProxyDialog(mapProxyDialog);
      }
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("Exception caugth for SendRoutingInfoForSM, tid '{}'", transactionId, mapex);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  public static MapDialogOut processResponse(Object message, String transactionId) {
    SendRoutingInfoForSMResponse mtForwardShortMessageResp = (SendRoutingInfoForSMResponse) message;
    Long dialogId = mtForwardShortMessageResp.getMAPDialog().getLocalDialogId();
    String messageType = mtForwardShortMessageResp.getMessageType().toString();
    String logmsg;
    try {
      Long respInvokeId = mtForwardShortMessageResp.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);

      logger.debug("[MAP::RESPONSE<{}>] Incoming DialogId '{}', invokeId '{}', {}",
          mtForwardShortMessageResp.getMessageType().toString(), dialogId, mtForwardShortMessageResp.getInvokeId(), transactionId);

      DataElement dataElement;
      logger.debug("TCAP Message Type = '{}', dialogId = {}, Service = '{}', InvokeId = {}",
          mtForwardShortMessageResp.getMAPDialog().getTCAPMessageType(), dialogId, mtForwardShortMessageResp.getMAPDialog().getService().toString(), mtForwardShortMessageResp.getInvokeId());

      if (mtForwardShortMessageResp.getMAPDialog().getTCAPMessageType() == MessageType.End
          || mtForwardShortMessageResp.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
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
      SendRoutingInfoForSMRequest origEvent =
          (SendRoutingInfoForSMRequest) dataElement.getRequestObject();
      MAPDialogSms mapDialogSms = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();

      mapDialogSms.setUserObject(invokeId);
      mapDialogSms.addSendRoutingInfoForSMResponse(invokeId, mtForwardShortMessageResp.getIMSI(),
          mtForwardShortMessageResp.getLocationInfoWithLMSI(),
          mtForwardShortMessageResp.getExtensionContainer(), mtForwardShortMessageResp.getMwdSet(),
          mtForwardShortMessageResp.getIpSmGwGuidance());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogSms);

      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("SendRoutingInfoForSMResponse with DialogId {} failed {}. Exception caught '{}'", dialogId, transactionId, mapex);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }
}
