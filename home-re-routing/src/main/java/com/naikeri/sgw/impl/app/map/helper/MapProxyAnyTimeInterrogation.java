package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxyAnyTimeInterrogation
 */
public class MapProxyAnyTimeInterrogation {

  private static final Logger logger = LoggerFactory.getLogger(MapProxyAnyTimeInterrogation.class);

  private MapProxyAnyTimeInterrogation() {
  }

  /**
   * process response for AnyTimeInterrogationResponse
   *
   * @param message
   * @param transactionId
   * @return MapDialogOut
   */
  public static MapDialogOut getResponse(Object message, String transactionId) {
    AnyTimeInterrogationResponse anyTimeInterrogationResponse =
        (AnyTimeInterrogationResponse) message;
    Long dialogId = anyTimeInterrogationResponse.getMAPDialog().getLocalDialogId();
    String messageType = anyTimeInterrogationResponse.getMessageType().toString();
    String logmsg = "";
    try {
      Long respInvokeId = anyTimeInterrogationResponse.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      AnyTimeInterrogationResponseCopy clone =
          new AnyTimeInterrogationResponseCopy(anyTimeInterrogationResponse);
      logger.debug(String.format("[MAP::RESPONSE<%s>] Incoming DialogId '%d', invokeId '%d', %s",
          anyTimeInterrogationResponse.getMessageType().toString(), dialogId,
          anyTimeInterrogationResponse.getInvokeId(), transactionId));
      DataElement dataElement = null;

      logger.debug(
          String.format("TCAP Message Type = '%s', dialogId = %d, InvokeId = %d, Service = '%s'",
              anyTimeInterrogationResponse.getMAPDialog().getTCAPMessageType(), dialogId,
              anyTimeInterrogationResponse.getInvokeId(),
              anyTimeInterrogationResponse.getMAPDialog().getService().toString()));

      if (anyTimeInterrogationResponse.getMAPDialog().getTCAPMessageType() == MessageType.End
          || anyTimeInterrogationResponse.getMAPDialog()
              .getTCAPMessageType() == MessageType.Abort) {
        // close the dialog
        logger.debug("Closing for dialogId = " + dialogId);
        mapDialogOut.setIsResponse();
        dataElement = Transaction.getInstance().removeDialogData(dialogId, respInvokeId);
      } else {
        dataElement = Transaction.getInstance().getDialogData(dialogId, respInvokeId);
      }

      if (dataElement == null) {
        logmsg = String.format("Dialog Id = '%d' not found in Transaction Map. %s", dialogId,
            transactionId);
        return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
      }
      AnyTimeInterrogationRequest origEvent =
          (AnyTimeInterrogationRequest) dataElement.getRequestObject();
      MAPDialogMobility mapDialogMobility = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();

      mapDialogMobility.setUserObject(invokeId);
      mapDialogMobility.addAnyTimeInterrogationResponse(invokeId, clone.getSubscriberInfo(),
          clone.getExtensionContainer());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogMobility);
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("AnyTimeInterrogationResponse with DialogId " + dialogId + " failed "
          + transactionId + ". Exception caught '" + mapex + "'");
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  /**
   * process Any Time Interrogation request
   * 
   * @param mapProxyDialog              MapProxyDialog
   * @param anyTimeInterrogationRequest AnyTimeInterrogationRequest
   * @return MapDialogOut
   */
  public static MapDialogOut getRequest(MapProxyDialog mapProxyDialog,
      AnyTimeInterrogationRequest anyTimeInterrogationRequest, String transactionId) {
    Long dialogId = anyTimeInterrogationRequest.getMAPDialog().getLocalDialogId();
    String messageType = anyTimeInterrogationRequest.getMessageType().toString();
    String logmsg = "";
    if (mapProxyDialog == null) {
      logmsg = String.format(
          "%s MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'. MAP Message will be discarded",
          transactionId, dialogId, anyTimeInterrogationRequest.getInvokeId(),
          anyTimeInterrogationRequest.getMessageType().toString());
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    }
    try {
      logger.debug(String.format("[MAP::REQUEST<%s>] Incoming DialogId '%d', InvokeId '%d', %s",
          anyTimeInterrogationRequest.getMessageType().toString(), dialogId,
          anyTimeInterrogationRequest.getInvokeId(), transactionId));
      MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();

      Long newInvokeId = mapMobilityOut.addAnyTimeInterrogationRequest(
          anyTimeInterrogationRequest.getSubscriberIdentity(),
          anyTimeInterrogationRequest.getRequestedInfo(),
          anyTimeInterrogationRequest.getGsmSCFAddress(),
          anyTimeInterrogationRequest.getExtensionContainer());

      return new MapDialogOut(mapMobilityOut, newInvokeId, dialogId, mapProxyDialog);
    } catch (MAPException mapex) {
      logger.error("AnyTimeInterrogationRequest with DialogId " + dialogId
          + " failed. Exception caught '" + mapex + "', " + transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }
}
