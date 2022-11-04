package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.dialog.Reason;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxySendIdentification
 */
public class MapProxySendIdentification {

  private static final Logger logger = Logger.getLogger(MapProxySendIdentification.class);

  private MapProxySendIdentification() {
  }

  /**
   * process response for SendIdentificationResponse
   *
   * @param message
   * @param transactionId
   * @return MapDialogOut
   */
  public static MapDialogOut getResponse(Object message, String transactionId) {
    SendIdentificationResponse sendIdentificationResponse = (SendIdentificationResponse) message;
    Long dialogId = sendIdentificationResponse.getMAPDialog().getLocalDialogId();
    String messageType = sendIdentificationResponse.getMessageType().toString();
    String logmsg = "";
    try {
      Long respInvokeId = sendIdentificationResponse.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      logger.debug(String.format("[MAP::RESPONSE<%s>] Incoming DialogId '%d', invokeId '%d', %s",
          sendIdentificationResponse.getMessageType().toString(),
          sendIdentificationResponse.getMAPDialog().getLocalDialogId(),
          sendIdentificationResponse.getInvokeId(), transactionId));

      DataElement dataElement = null;
      logger.debug(
          String.format("TCAP Message Type = '%s', dialogId = %d, InvokeId = %d, Service = '%s'",
              sendIdentificationResponse.getMAPDialog().getTCAPMessageType(), dialogId,
              sendIdentificationResponse.getInvokeId(),
              sendIdentificationResponse.getMAPDialog().getService().toString()));

      if (sendIdentificationResponse.getMAPDialog().getTCAPMessageType() == MessageType.End
          || sendIdentificationResponse.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
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
      SendIdentificationRequest origEvent =
          (SendIdentificationRequest) dataElement.getRequestObject();
      MAPDialogMobility origMapDialogMobility = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();

      origMapDialogMobility.setUserObject(invokeId);
      origMapDialogMobility.addSendIdentificationResponse(invokeId,
          sendIdentificationResponse.getImsi(),
          sendIdentificationResponse.getAuthenticationSetList(),
          sendIdentificationResponse.getCurrentSecurityContext(),
          sendIdentificationResponse.getExtensionContainer());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);

      mapDialogOut.setMapDialog(origMapDialogMobility);
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("SendIdentificationResponse with DialogId " + dialogId + " failed "
          + transactionId + ". Exception caught '" + mapex + "'");
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  /**
   * process Send Identification request
   * 
   * @param mapProxyDialog            MapProxyDialog
   * @param sendIdentificationRequest AnyTimeInterrogationRequest
   * @return MapDialogOut
   */
  public static MapDialogOut getRequest(MapProxyDialog mapProxyDialog,
      SendIdentificationRequest sendIdentificationRequest, String transactionId) {
    Long dialogId = sendIdentificationRequest.getMAPDialog().getLocalDialogId();
    String messageType = sendIdentificationRequest.getMessageType().toString();
    String logmsg = "";
    MapDialogOut mapDialogOut = new MapDialogOut();
    mapDialogOut.setOriginalDialogId(dialogId);
    try {
      if (mapProxyDialog == null) {
        logger.info(String.format(
            "%s, MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'. MAP Message will be discarded",
            transactionId, dialogId, sendIdentificationRequest.getInvokeId(),
            sendIdentificationRequest.getMessageType().toString()));

        MAPDialogMobility dialogIn = sendIdentificationRequest.getMAPDialog();
        dialogIn.refuse(Reason.noReasonGiven);
        mapDialogOut.setMapDialog(dialogIn);
        mapDialogOut.setInvokeId(null);
      } else {
        logger.debug(String.format("[MAP::REQUEST<%s>] dialogId = '%d', InvokeId = '%d', %s",
            sendIdentificationRequest.getMessageType().toString(), dialogId,
            sendIdentificationRequest.getInvokeId(), transactionId));
        MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();

        Long newInvokeId = mapMobilityOut.addSendIdentificationRequest(
            sendIdentificationRequest.getTmsi(),
            sendIdentificationRequest.getNumberOfRequestedVectors(),
            sendIdentificationRequest.getSegmentationProhibited(),
            sendIdentificationRequest.getExtensionContainer(),
            sendIdentificationRequest.getMscNumber(), sendIdentificationRequest.getPreviousLAI(),
            sendIdentificationRequest.getHopCounter(),
            sendIdentificationRequest.getMtRoamingForwardingSupported(),
            sendIdentificationRequest.getNewVLRNumber(), sendIdentificationRequest.getNewLmsi());

        mapDialogOut.setInvokeId(newInvokeId);
        mapDialogOut.setMapDialog(mapMobilityOut);
        mapDialogOut.setProxyDialog(mapProxyDialog);
      }
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("SendIdentificationRequest with DialogId " + dialogId
          + " failed. Exception caught '" + mapex + "', " + transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }
}
