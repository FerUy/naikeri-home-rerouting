package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

public class MapProxyUpdateLocation {

  private static final Logger logger = Logger.getLogger(MapProxyUpdateLocation.class);

  private MapProxyUpdateLocation() {
  }

  public static MapDialogOut getLocationResponse(Object message, String transactionId) {
    UpdateLocationResponse locationResponse = (UpdateLocationResponse) message;
    String messageType = locationResponse.getMessageType().toString();
    Long dialogId = locationResponse.getMAPDialog().getLocalDialogId();
    try {
      Long respInvokeId = locationResponse.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      UpdateLocationResponseCopy clone = new UpdateLocationResponseCopy(locationResponse);

      logger.debug(String.format("[MAP::RESPONSE<%s>] Incoming DialogId '%d', invokeId '%d', %s",
          locationResponse.getMessageType().toString(),
          locationResponse.getMAPDialog().getLocalDialogId(), locationResponse.getInvokeId(),
          transactionId));
      DataElement dataElement = null;
      logger.debug(String.format("TCAP Message Type = '%s', dialogId = %d, Service = '%s'",
          locationResponse.getMAPDialog().getTCAPMessageType(), dialogId,
          locationResponse.getMAPDialog().getService().toString()));
      // last result

      if (locationResponse.getMAPDialog().getTCAPMessageType() == MessageType.End
          || locationResponse.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        // close the dialog
        logger.debug("Closing for dialogId = " + dialogId);
        dataElement = Transaction.getInstance().removeDialogData(dialogId, respInvokeId);
        // close the dialog here
        mapDialogOut.setIsResponse();
      } else {
        dataElement = Transaction.getInstance().getDialogData(dialogId, respInvokeId);
      }
      // if the object is found
      if (dataElement != null) {
        UpdateLocationRequest originalRequest =
            (UpdateLocationRequest) dataElement.getRequestObject();
        MAPDialogMobility origDialogMobility = originalRequest.getMAPDialog();
        long invokeId = originalRequest.getInvokeId();

        origDialogMobility.setUserObject(invokeId);
        origDialogMobility.addUpdateLocationResponse(invokeId, clone.getHlrNumber(),
            clone.getExtensionContainer(), clone.getAddCapability(),
            clone.getPagingAreaCapability());

        mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
        mapDialogOut.setMapDialog(origDialogMobility);

        return mapDialogOut;
      }
      String logmsg =
          String.format("Dialog Id = %d not found in Transaction Map. %s", dialogId, transactionId);
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    } catch (MAPException mapex) {
      logger.error("UpdateLocationResponse with DialogId " + dialogId + " failed " + transactionId
          + ". Exception caught '" + mapex + "'");
      return MapProxyUtilsHelper.discardReason(mapex.getMessage(), messageType, transactionId);
    } catch (Exception ex) {
      logger.error("Failed", ex);
      return MapProxyUtilsHelper.discardReason(ex.getMessage(), messageType, transactionId);
    }
  }

  public static MapDialogOut getLocationRequest(MapProxyDialog mapProxyDialog,
      UpdateLocationRequest locRequest, String transactionId) {
    Long dialogId = locRequest.getMAPDialog().getLocalDialogId();
    String messageType = locRequest.getMessageType().toString();

    if (mapProxyDialog == null) {
      String debugmsg = String.format(
          "%s, MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'. ",
          transactionId, dialogId, locRequest.getInvokeId(),
          locRequest.getMessageType().toString());
      logger.debug(debugmsg);
      debugmsg =
          String.format("MAP Application Rule not found for DialogId = '%d', InvokeId = '%d'",
              dialogId, locRequest.getInvokeId());
      return MapProxyUtilsHelper.discardReason(debugmsg, messageType, transactionId);
    }
    try {
      logger.debug(String.format("[MAP::REQUEST<%s>] Incoming DialogId = '%d', InvokeId = '%d', %s",
          locRequest.getMessageType().toString(), dialogId, locRequest.getInvokeId(),
          transactionId));

      IMSI updateLocationImsi = mapProxyDialog.getImsi();
      MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();

      Long newInvokeId = mapMobilityOut.addUpdateLocationRequest(updateLocationImsi,
          locRequest.getMscNumber(), locRequest.getRoamingNumber(), locRequest.getVlrNumber(),
          locRequest.getLmsi(), locRequest.getExtensionContainer(), locRequest.getVlrCapability(),
          locRequest.getInformPreviousNetworkEntity(), locRequest.getCsLCSNotSupportedByUE(),
          locRequest.getVGmlcAddress(), locRequest.getADDInfo(), locRequest.getPagingArea(),
          locRequest.getSkipSubscriberDataUpdate(), locRequest.getRestorationIndicator());

      return new MapDialogOut(mapMobilityOut, newInvokeId, dialogId, mapProxyDialog);

    } catch (MAPException mapex) {
      logger.error("UpdateLocationRequest with DialogId " + dialogId + " failed. Exception caught '"
          + mapex + "', " + transactionId);
      return MapProxyUtilsHelper.discardReason(mapex.getMessage(), messageType, transactionId);
    } catch (Exception ex) {
      logger.error("Error: ", ex);
      return MapProxyUtilsHelper.discardReason(ex.getMessage(), messageType, transactionId);
    }
  }

  /**
   * update GPRS location response
   * 
   * @param updateResponse UpdateLocationResponse
   * @return MapDialogOut
   */
  public static MapDialogOut getGprsLocationResponse(Object message, String transactionId) {
    UpdateGprsLocationResponse event = (UpdateGprsLocationResponse) message;
    Long dialogId = event.getMAPDialog().getLocalDialogId();
    String messageType = event.getMessageType().toString();
    DataElement dataElement = null;
    try {
      Long respInvokeId = event.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      UpdateGprsLocationResponseCopy clone = new UpdateGprsLocationResponseCopy(event);
      logger.debug(String.format("[MAP::RESPONSE<%s>] Incoming dialogId '%d', InvokeId '%d' %s",
          event.getMessageType().toString(), dialogId, event.getInvokeId(), transactionId));
      logger.debug(String.format("TCAP Message Type = '%s', dialogId = %d, Service = '%s'",
          event.getMAPDialog().getTCAPMessageType(), dialogId,
          event.getMAPDialog().getService().toString()));
      if (event.getMAPDialog().getTCAPMessageType() == MessageType.End
          || event.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        logger.debug("Closing for dialogId = " + dialogId);
        dataElement = Transaction.getInstance().removeDialogData(dialogId, respInvokeId);
        mapDialogOut.setIsResponse();
      } else {
        dataElement = Transaction.getInstance().getDialogData(dialogId, respInvokeId);
      }

      if (dataElement == null) {
        String logmsg = String.format("Dialog Id = %d not found in Transaction Map. %s", dialogId,
            transactionId);
        return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
      }
      UpdateGprsLocationRequest origEvent =
          (UpdateGprsLocationRequest) dataElement.getRequestObject();
      MAPDialogMobility mapDialogMobility = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();
      mapDialogMobility.setUserObject(invokeId);

      mapDialogMobility.addUpdateGprsLocationResponse(invokeId, clone.getHlrNumber(),
          clone.getExtensionContainer(), clone.getAddCapability(),
          clone.getSgsnMmeSeparationSupported());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogMobility);

      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("UpdateGprsLocationResponse with DialogId " + dialogId + " failed "
          + transactionId + ". Exception caught '" + mapex + "'");
      return MapProxyUtilsHelper.discardReason(mapex.getMessage(), messageType, transactionId);
    } catch (Exception ex) {
      logger.error("Error occurred: ", ex);
      return MapProxyUtilsHelper.discardReason(ex.getMessage(), messageType, transactionId);
    }
  }

  /**
   * update GPRS location request
   * 
   * @param mapProxyDialog MapProxyDialog
   * @param updateGprs     UpdateGprsLocationRequest
   * @return MapDialogOut
   */
  public static MapDialogOut getGprsLocationRequest(MapProxyDialog mapProxyDialog,
      UpdateGprsLocationRequest updateGprs, String transactionId) {
    Long dialogId = updateGprs.getMAPDialog().getLocalDialogId();
    String messageType = updateGprs.getMessageType().toString();
    if (mapProxyDialog == null) {
      logger.debug(String.format(
          "%s, MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'.",
          transactionId, dialogId, updateGprs.getInvokeId(),
          updateGprs.getMessageType().toString()));
      String logmsg =
          String.format("MAP Application Rule not found for DialogId = '%d', InvokeId = '%d'",
              dialogId, updateGprs.getInvokeId());
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    }
    try {
      logger
          .debug(String.format("[MAP::REQUEST<%s>] Incoming DialogId  = '%d', invokeId = '%d', %s",
              updateGprs.getMessageType().toString(), dialogId, updateGprs.getInvokeId(),
              transactionId));

      IMSI updateLocationImsi = mapProxyDialog.getImsi();
      MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();

      Long newInvokeId = mapMobilityOut.addUpdateGprsLocationRequest(updateLocationImsi,
          updateGprs.getSgsnNumber(), updateGprs.getSgsnAddress(),
          updateGprs.getExtensionContainer(), updateGprs.getSGSNCapability(),
          updateGprs.getInformPreviousNetworkEntity(), updateGprs.getPsLCSNotSupportedByUE(),
          updateGprs.getVGmlcAddress(), updateGprs.getADDInfo(), updateGprs.getEPSInfo(),
          updateGprs.getServingNodeTypeIndicator(), updateGprs.getSkipSubscriberDataUpdate(),
          updateGprs.getUsedRATType(), updateGprs.getGprsSubscriptionDataNotNeeded(),
          updateGprs.getNodeTypeIndicator(), updateGprs.getAreaRestricted(),
          updateGprs.getUeReachableIndicator(), updateGprs.getEpsSubscriptionDataNotNeeded(),
          updateGprs.getUESRVCCCapability());
      return new MapDialogOut(mapMobilityOut, newInvokeId, dialogId, mapProxyDialog);
    } catch (MAPException mapex) {
      logger.error("UpdateGprsLocationRequest with DialogId " + dialogId
          + " failed. Exception caught '" + mapex + "', " + transactionId);
      return MapProxyUtilsHelper.discardReason(mapex.getMessage(), messageType, transactionId);
    } catch (Exception ex) {
      logger.error("Error: ", ex);
      return MapProxyUtilsHelper.discardReason(ex.getMessage(), messageType, transactionId);
    }
  }

}
