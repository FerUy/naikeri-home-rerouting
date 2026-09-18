package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxyProvideSubscriberInfo
 */
public class MapProxyProvideSubscriberInfo {

  private static final Logger logger = LoggerFactory.getLogger(MapProxyProvideSubscriberInfo.class);

  private MapProxyProvideSubscriberInfo() {
  }

  /**
   * process response for ProvideSubscriberInfoResponse
   *
   * @param message
   * @param transactionId
   * @return MapDialogOut
   */
  public static MapDialogOut getResponse(Object message, String transactionId) {
    ProvideSubscriberInfoResponse subInfoResponse = (ProvideSubscriberInfoResponse) message;
    Long dialogId = subInfoResponse.getMAPDialog().getLocalDialogId();
    String messageType = subInfoResponse.getMessageType().toString();
    String logmsg = "";
    try {
      Long respInvokeId = subInfoResponse.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      ProvideSubscriberInfoResponseCopy clone =
          new ProvideSubscriberInfoResponseCopy(subInfoResponse);
      logger.debug(String.format("[MAP::RESPONSE<%s>] Incoming DialogId '%d', invokeId '%d', %s",
          subInfoResponse.getMessageType().toString(), dialogId, subInfoResponse.getInvokeId(),
          transactionId));
      DataElement dataElement = null;

      logger.debug(String.format(
          "TCAP Message Type = '%s', dialogId = %d, InvokeId = %d, Service = '%s'",
          subInfoResponse.getMAPDialog().getTCAPMessageType(), dialogId,
          subInfoResponse.getInvokeId(), subInfoResponse.getMAPDialog().getService().toString()));

      if (subInfoResponse.getMAPDialog().getTCAPMessageType() == MessageType.End
          || subInfoResponse.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
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
      ProvideSubscriberInfoRequest origEvent =
          (ProvideSubscriberInfoRequest) dataElement.getRequestObject();
      MAPDialogMobility mapDialogMobility = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();

      mapDialogMobility.setUserObject(invokeId);
      mapDialogMobility.addAnyTimeInterrogationResponse(invokeId, clone.getSubscriberInfo(),
          clone.getExtensionContainer());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogMobility);
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("UpdateGprsLocationResponse with DialogId " + dialogId + " failed "
          + transactionId + ". Exception caught '" + mapex + "'");
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }

  /**
   * process Provide Subscriber Info request
   * 
   * @param mapProxyDialog MapProxyDialog
   * @param subInfoRequest ProvideSubscriberInfoRequest
   * @return MapDialogOut
   */
  public static MapDialogOut getRequest(MapProxyDialog mapProxyDialog,
      ProvideSubscriberInfoRequest subInfoRequest, String transactionId) {
    Long dialogId = subInfoRequest.getMAPDialog().getLocalDialogId();
    String messageType = subInfoRequest.getMessageType().toString();
    String logmsg = "";
    if (mapProxyDialog == null) {
      logmsg = String.format(
          "%s, MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'. MAP Message will be discarded",
          transactionId, dialogId, subInfoRequest.getInvokeId(),
          subInfoRequest.getMessageType().toString());
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    }
    try {
      logger.debug(String.format("[MAP::REQUEST<%s>] Incoming DialogId = '%d', InvokeId = '%d', %s",
          subInfoRequest.getMessageType().toString(), dialogId, subInfoRequest.getInvokeId(),
          transactionId));
      MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();
      Long newInvokeId = mapMobilityOut.addProvideSubscriberInfoRequest(mapProxyDialog.getImsi(),
          subInfoRequest.getLmsi(), subInfoRequest.getRequestedInfo(),
          subInfoRequest.getExtensionContainer(), subInfoRequest.getCallPriority());
      return new MapDialogOut(mapMobilityOut, newInvokeId, dialogId, mapProxyDialog);
    } catch (MAPException mapex) {
      logger.error("ProvideSubscriberInfoRequest with DialogId " + dialogId
          + " failed. Exception caught '" + mapex + "', " + transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }
}
