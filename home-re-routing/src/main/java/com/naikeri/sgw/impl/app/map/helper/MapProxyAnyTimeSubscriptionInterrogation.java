package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeSubscriptionInterrogationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeSubscriptionInterrogationResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxyAnyTimeSubscriptionInterrogation
 */
public class MapProxyAnyTimeSubscriptionInterrogation {

  private MapProxyAnyTimeSubscriptionInterrogation() {
  }

  private static final Logger logger =
      Logger.getLogger(MapProxyAnyTimeSubscriptionInterrogation.class);

  public static MapDialogOut getResponse(Object message, String transactionId) {
    AnyTimeSubscriptionInterrogationResponse anyTimeSubResponse =
        (AnyTimeSubscriptionInterrogationResponse) message;
    Long dialogId = anyTimeSubResponse.getMAPDialog().getLocalDialogId();
    try {
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      AnyTimeSubInterrogationResponseCopy clone =
          new AnyTimeSubInterrogationResponseCopy(anyTimeSubResponse);

      Long respInvokeId = anyTimeSubResponse.getInvokeId();
      logger.debug(String.format("[MAP::RESPONSE<%s>] Incoming DialogId '%d', invokeId '%d', %s",
          anyTimeSubResponse.getMessageType().toString(),
          anyTimeSubResponse.getMAPDialog().getLocalDialogId(), anyTimeSubResponse.getInvokeId(),
          transactionId));
      DataElement dataElement = null;

      logger.debug(
          String.format("TCAP Message Type = '%s', dialogId = %d, InvokeId = %d, Service = '%s'",
              anyTimeSubResponse.getMAPDialog().getTCAPMessageType(), dialogId,
              anyTimeSubResponse.getInvokeId(),
              anyTimeSubResponse.getMAPDialog().getService().toString()));

      if (anyTimeSubResponse.getMAPDialog().getTCAPMessageType() == MessageType.End
          || anyTimeSubResponse.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        logger.debug("Closing for dialogId = " + dialogId);
        dataElement = Transaction.getInstance().removeDialogData(dialogId, respInvokeId);
        mapDialogOut.setIsResponse();
      } else {
        dataElement = Transaction.getInstance().getDialogData(dialogId, respInvokeId);
      }

      if (dataElement == null) {
        logger.debug(String.format("Dialog Id = %d not found in Transaction Map. %s", dialogId,
            transactionId));
        return null;
      }
      AnyTimeSubscriptionInterrogationRequest origEvent =
          (AnyTimeSubscriptionInterrogationRequest) dataElement.getRequestObject();
      MAPDialogMobility mapDialogMobility = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();
      mapDialogMobility.setUserObject(invokeId);
      mapDialogMobility.addAnyTimeSubscriptionInterrogationResponse(invokeId,
          clone.getCallForwardingData(), clone.getCallBarringData(), clone.getOdbInfo(),
          clone.getCamelSubscriptionInfo(), clone.getSupportedVlrCamelPhases(),
          clone.getSupportedSgsnCamelPhases(), clone.getExtensionContainer(),
          clone.getOfferedCamel4CSIsInVlr(), clone.getOfferedCamel4CSIsInSgsn(),
          clone.getMsisdnBsList(), clone.getCsgSubscriptionDataList(), clone.getCwData(),
          clone.getChData(), clone.getClipData(), clone.getClirData(), clone.getEctData());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogMobility);
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("AnyTimeSubscriptionInterrogationResponse with DialogId " + dialogId + " failed "
          + transactionId + ". Exception caught '" + mapex + "'");
    }
    return null;
  }


  public static MapDialogOut getRequest(MapProxyDialog mapProxyDialog,
      AnyTimeSubscriptionInterrogationRequest anyTimeSubscriptionRequest, String transactionId) {
    Long dialogId = anyTimeSubscriptionRequest.getMAPDialog().getLocalDialogId();
    String messageType = anyTimeSubscriptionRequest.getMessageType().toString();
    String logmsg = "";
    if (mapProxyDialog == null) {
      logmsg = String.format(
          "%s MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'. MAP Message will be discarded",
          transactionId, dialogId, anyTimeSubscriptionRequest.getInvokeId(),
          anyTimeSubscriptionRequest.getMessageType().toString());
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    }
    try {
      logger.debug(String.format("[MAP::REQUEST<%s>] Incoming DialogId '%d', InvokeId '%d', %s",
          anyTimeSubscriptionRequest.getMessageType().toString(), dialogId,
          anyTimeSubscriptionRequest.getInvokeId(), transactionId));


      MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();

      Long newInvokeId = mapMobilityOut.addAnyTimeSubscriptionInterrogationRequest(
          anyTimeSubscriptionRequest.getSubscriberIdentity(),
          anyTimeSubscriptionRequest.getRequestedSubscriptionInfo(),
          anyTimeSubscriptionRequest.getGsmScfAddress(),
          anyTimeSubscriptionRequest.getExtensionContainer(),
          anyTimeSubscriptionRequest.getLongFTNSupported());

      return new MapDialogOut(mapMobilityOut, newInvokeId, dialogId, mapProxyDialog);
    } catch (MAPException mapex) {
      logger.error("AnyTimeSubscriptionInterrogationRequest with DialogId " + dialogId
          + " failed. Exception caught '" + mapex + "', " + transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }
}
