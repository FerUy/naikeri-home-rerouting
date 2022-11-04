package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPDialogSms;
import org.restcomm.protocols.ss7.map.api.service.sms.MoForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MoForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.MtForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MtForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.SM_RP_DA;
import org.restcomm.protocols.ss7.map.service.sms.SM_RP_DAImpl;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxyMOForwardSM
 */
public class MapProxyMoMtForwardSM {

  private static final Logger logger = Logger.getLogger(MapProxyMoMtForwardSM.class);

  private MapProxyMoMtForwardSM() {
  }

  /**
   * process response for moForwardSM
   *
   * @param message
   * @param transactionId
   * @return MapDialogOut
   */
  public static MapDialogOut getMOForwardSMResponse(Object message, String transactionId) {
    MoForwardShortMessageResponse moForwardMResp = (MoForwardShortMessageResponse) message;
    Long dialogId = moForwardMResp.getMAPDialog().getLocalDialogId();
    String logmsg = "";
    String messageType = moForwardMResp.getMessageType().toString();
    try {
      Long respInvokeId = moForwardMResp.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      MoForwardShortMessageResponseCopy clone =
          new MoForwardShortMessageResponseCopy(moForwardMResp);
      logger.debug(String.format("[MAP::RESPONSE<%s>] Incoming DialogId '%d', invokeId '%d' %s",
          moForwardMResp.getMessageType().toString(), dialogId, moForwardMResp.getInvokeId(),
          transactionId));
      DataElement dataElement = null;

      logger.debug(
          String.format("TCAP Message Type = '%s', dialogId = %d, Service = '%s', InvokeId = %d",
              moForwardMResp.getMAPDialog().getTCAPMessageType(), dialogId,
              moForwardMResp.getMAPDialog().getService().toString(), moForwardMResp.getInvokeId()));
      if (moForwardMResp.getMAPDialog().getTCAPMessageType() == MessageType.End
          || moForwardMResp.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        logger.debug("Closing for dialogId = " + dialogId);
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
      MoForwardShortMessageRequest origEvent =
          (MoForwardShortMessageRequest) dataElement.getRequestObject();
      MAPDialogSms mapDialogSms = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();

      mapDialogSms.setUserObject(invokeId);
      // addMoForwardShortMessageRequest
      mapDialogSms.addMoForwardShortMessageResponse(invokeId, clone.getSM_RP_UI(),
          clone.getExtensionContainer());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogSms);
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("MoForwardShortMessageResponse with DialogId " + dialogId + " failed "
          + transactionId + ". Exception caught '" + mapex + "'");
      logmsg = mapex.getMessage();
    } catch (Exception e) {
      logmsg = e.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  /**
   * process request for MoForwardSM
   * 
   * @param mapProxyDialog MapProxyDialog
   * @param moForwSmInd    MoForwardShortMessageRequest
   * @return MapDialogOut
   */
  public static MapDialogOut getMoForwardSMRequest(MapProxyDialog mapProxyDialog,
      MoForwardShortMessageRequest moForwSmInd, String transactionId) {
    Long dialogId = moForwSmInd.getMAPDialog().getLocalDialogId();
    String messageType = moForwSmInd.getMessageType().toString();
    String logmsg = "";
    if (mapProxyDialog == null) {
      logmsg = String.format(
          "%s, MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'. MAP Message will be discarded",
          transactionId, dialogId, moForwSmInd.getInvokeId(),
          moForwSmInd.getMessageType().toString());
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    }
    try {
      logger.debug(String.format("[MAP::REQUEST<%s>] dialogId = '%d', InvokeId = '%d', %s",
          moForwSmInd.getMessageType().toString(), dialogId, moForwSmInd.getInvokeId(),
          transactionId));
      MAPDialogSms smsHandlerOut = mapProxyDialog.getMapDialogSms();

      Long newInvokeId = smsHandlerOut.addMoForwardShortMessageRequest(moForwSmInd.getSM_RP_DA(),
          moForwSmInd.getSM_RP_OA(), moForwSmInd.getSM_RP_UI(), moForwSmInd.getExtensionContainer(),
          mapProxyDialog.getImsi());
      return new MapDialogOut(smsHandlerOut, newInvokeId, dialogId, mapProxyDialog);
    } catch (MAPException mapex) {
      logger.error("Processing MO forward SM Request failed " + mapex + ", " + transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  /**
   * process response for addMtForwardShortMessageResponse
   * 
   * @param mtForwardShortMessageResp mtForwardShortMessageResp
   * @return MapDialogOut
   */
  public static MapDialogOut getMtForwardSMResponse(Object message, String transactionId) {
    MtForwardShortMessageResponse mtForwardShortMessageResp =
        (MtForwardShortMessageResponse) message;
    Long dialogId = mtForwardShortMessageResp.getMAPDialog().getLocalDialogId();
    String messageType = mtForwardShortMessageResp.getMessageType().toString();
    String logmsg = "";
    try {
      Long respInvokeId = mtForwardShortMessageResp.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      MtForwardShortMessageResponseCopy clone =
          new MtForwardShortMessageResponseCopy(mtForwardShortMessageResp);

      logger.debug(String.format("[MAP::RESPONSE<%s>] Incoming DialogId '%d', invokeId '%d', %s",
          mtForwardShortMessageResp.getMessageType().toString(), dialogId,
          mtForwardShortMessageResp.getInvokeId(), transactionId));

      DataElement dataElement = null;
      logger.debug(
          String.format("TCAP Message Type = '%s', dialogId = %d, Service = '%s', InvokeId = %d",
              mtForwardShortMessageResp.getMAPDialog().getTCAPMessageType(), dialogId,
              mtForwardShortMessageResp.getMAPDialog().getService().toString(),
              mtForwardShortMessageResp.getInvokeId()));

      if (mtForwardShortMessageResp.getMAPDialog().getTCAPMessageType() == MessageType.End
          || mtForwardShortMessageResp.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        logger.debug("Closing for dialogId = " + dialogId);
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
      MtForwardShortMessageRequest origEvent =
          (MtForwardShortMessageRequest) dataElement.getRequestObject();
      MAPDialogSms mapDialogSms = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();

      mapDialogSms.setUserObject(invokeId);
      mapDialogSms.addMtForwardShortMessageResponse(invokeId, clone.getSM_RP_UI(),
          clone.getExtensionContainer());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogSms);

      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("MtForwardShortMessageResponse with DialogId " + dialogId + " failed "
          + transactionId + ". Exception caught '" + mapex + "'");
      logmsg = mapex.getMessage();
    } catch (Exception e) {
      logmsg = e.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  /**
   * process mtForwardSM_Request
   * 
   * @param mapProxyDialog MapProxyDialog
   * @param mtShortMessage MtForwardShortMessageRequest
   * @return MapDialogOut
   */
  public static MapDialogOut getMtForwardSMRequest(MapProxyDialog mapProxyDialog,
      MtForwardShortMessageRequest mtShortMessage, String transactionId) {
    Long dialogId = mtShortMessage.getMAPDialog().getLocalDialogId();
    String messageType = mtShortMessage.getMessageType().toString();
    String logmsg = "";

    if (mapProxyDialog == null) {
      logmsg = String.format(
          "%s, MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'. MAP Message will be discarded",
          transactionId, dialogId, mtShortMessage.getInvokeId(),
          mtShortMessage.getMessageType().toString());
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    }
    try {
      logger.debug(String.format("[MAP::REQUEST<%s>] dialogId = '%d', invokeId = '%d', %s",
          mtShortMessage.getMessageType().toString(), dialogId, mtShortMessage.getInvokeId(),
          transactionId));

      MAPDialogSms smsHandlerOut = mapProxyDialog.getMapDialogSms();
      IMSI updateLocationImsi = mapProxyDialog.getImsi();
      SM_RP_DA smRPDA = new SM_RP_DAImpl(updateLocationImsi);

      Long newInvokeId = smsHandlerOut.addMtForwardShortMessageRequest(smRPDA,
          mtShortMessage.getSM_RP_OA(), mtShortMessage.getSM_RP_UI(),
          mtShortMessage.getMoreMessagesToSend(), mtShortMessage.getExtensionContainer());

      return new MapDialogOut(smsHandlerOut, newInvokeId, dialogId, mapProxyDialog);

    } catch (MAPException mapex) {
      logger.error("MtForwardShortMessageRequest with DialogId " + dialogId
          + " failed. Exception caught '" + mapex + "', " + transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  public static MapDialogOut contMtForwardSm(MtForwardShortMessageRequest request,
      DataElement dataElement, String transactionId) {
    Long dialogId = request.getMAPDialog().getLocalDialogId();
    String messageType = request.getMessageType().toString();
    String logmsg = "";
    try {
      MAPDialogSms smsHandlerOut;
      if (dataElement.getMessageType().equals("mtForwardSM_Request")) {
        MtForwardShortMessageRequest mtForwardSm =
            (MtForwardShortMessageRequest) dataElement.getRequestObject();
        smsHandlerOut = mtForwardSm.getMAPDialog();
      } else {
        logmsg = "FAILED to process MAP Message: " + request.toString();
        return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
      }
      Long newInvokeId = smsHandlerOut.addMtForwardShortMessageRequest(request.getSM_RP_DA(),
          request.getSM_RP_OA(), request.getSM_RP_UI(), request.getMoreMessagesToSend(),
          request.getExtensionContainer());
      return new MapDialogOut(smsHandlerOut, newInvokeId, dialogId);
    } catch (MAPException mapex) {
      logger.error("Cont. MtForwardSm with DialogId " + dialogId + " failed. Exception caught '"
          + mapex + "', " + transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }
}
