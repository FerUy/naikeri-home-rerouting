package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxyUpdateLocation
 */
public class MapProxyUpdateLocation {

  private static final Logger logger = LogManager.getLogger(MapProxyUpdateLocation.class);

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

      logger.debug("[MAP::RESPONSE<{}>] Incoming DialogId '{}', invokeId '{}', {}",
          locationResponse.getMessageType().toString(), locationResponse.getMAPDialog().getLocalDialogId(), locationResponse.getInvokeId(), transactionId);
      DataElement dataElement;
      logger.debug("TCAP Message Type = '{}', dialogId = {}, Service = '{}'",
          locationResponse.getMAPDialog().getTCAPMessageType(), dialogId, locationResponse.getMAPDialog().getService().toString());
      // last result

      if (locationResponse.getMAPDialog().getTCAPMessageType() == MessageType.End
          || locationResponse.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        // close the dialog
        logger.debug("Closing for dialogId = {}", dialogId);
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
        // the VLR keeps the HLR number to address the HLR later: it must be the proxy
        ProxyAddress.present(origDialogMobility, null, transactionId);
        origDialogMobility.addUpdateLocationResponse(invokeId, ProxyAddress.hlrNumber(origDialogMobility, clone.getHlrNumber()),
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
      logger.error("UpdateLocationResponse with DialogId {} failed {}. Exception caught '{}'", dialogId, transactionId, mapex);
      return MapProxyUtilsHelper.discardReason(mapex.getMessage(), messageType, transactionId);
    } catch (Exception ex) {
      logger.error("Failed", ex);
      return MapProxyUtilsHelper.discardReason(ex.getMessage(), messageType, transactionId);
    }
  }

  public static MapDialogOut getLocationRequest(MapProxyDialog mapProxyDialog,
                                                UpdateLocationRequest updateLocationRequest, String transactionId) {
    Long dialogId = updateLocationRequest.getMAPDialog().getLocalDialogId();
    String messageType = updateLocationRequest.getMessageType().toString();

    if (mapProxyDialog == null) {
      String debugmsg = String.format(
          "%s, MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'. ",
          transactionId, dialogId, updateLocationRequest.getInvokeId(),
          updateLocationRequest.getMessageType().toString());
      logger.debug(debugmsg);
      debugmsg =
          String.format("MAP Application Rule not found for DialogId = '%d', InvokeId = '%d'",
              dialogId, updateLocationRequest.getInvokeId());
      return MapProxyUtilsHelper.discardReason(debugmsg, messageType, transactionId);
    }
    try {
      logger.debug("[MAP::REQUEST<{}>] Incoming DialogId = '{}', InvokeId = '{}', {}", updateLocationRequest.getMessageType().toString(), dialogId, updateLocationRequest.getInvokeId(), transactionId);

      IMSI updateLocationImsi = mapProxyDialog.getImsi();
      MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();

      Long newInvokeId = mapMobilityOut.addUpdateLocationRequest(updateLocationImsi,
          updateLocationRequest.getMscNumber(), updateLocationRequest.getRoamingNumber(), updateLocationRequest.getVlrNumber(),
          updateLocationRequest.getLmsi(), updateLocationRequest.getExtensionContainer(), updateLocationRequest.getVlrCapability(),
          updateLocationRequest.getInformPreviousNetworkEntity(), updateLocationRequest.getCsLCSNotSupportedByUE(),
          updateLocationRequest.getVGmlcAddress(), updateLocationRequest.getADDInfo(), updateLocationRequest.getPagingArea(),
          updateLocationRequest.getSkipSubscriberDataUpdate(), updateLocationRequest.getRestorationIndicator(),
          updateLocationRequest.getEPLMNList(), updateLocationRequest.getMmeDiameterAddress());

      return new MapDialogOut(mapMobilityOut, newInvokeId, dialogId, mapProxyDialog);

    } catch (MAPException mapex) {
      logger.error("UpdateLocationRequest with DialogId {} failed. Exception caught '{}', {}", dialogId, mapex, transactionId);
      return MapProxyUtilsHelper.discardReason(mapex.getMessage(), messageType, transactionId);
    } catch (Exception ex) {
      logger.error("Error: ", ex);
      return MapProxyUtilsHelper.discardReason(ex.getMessage(), messageType, transactionId);
    }
  }

  /**
   * update GPRS location response
   *
   * @return MapDialogOut
   */
  public static MapDialogOut getGprsLocationResponse(Object message, String transactionId) {
    UpdateGprsLocationResponse event = (UpdateGprsLocationResponse) message;
    Long dialogId = event.getMAPDialog().getLocalDialogId();
    String messageType = event.getMessageType().toString();
    DataElement dataElement;
    try {
      Long respInvokeId = event.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      UpdateGprsLocationResponseCopy clone = new UpdateGprsLocationResponseCopy(event);
      logger.debug("[MAP::RESPONSE<{}>] Incoming dialogId '{}', InvokeId '{}' {}", event.getMessageType().toString(), dialogId, event.getInvokeId(), transactionId);
      logger.debug("TCAP Message Type = '{}', dialogId = {}, Service = '{}'", event.getMAPDialog().getTCAPMessageType(), dialogId, event.getMAPDialog().getService().toString());
      if (event.getMAPDialog().getTCAPMessageType() == MessageType.End
          || event.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        logger.debug("Closing for dialogId = {}", dialogId);
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
      UpdateGprsLocationRequest origEvent = (UpdateGprsLocationRequest) dataElement.getRequestObject();
      MAPDialogMobility mapDialogMobility = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();
      mapDialogMobility.setUserObject(invokeId);

      // the VLR keeps the HLR number to address the HLR later: it must be the proxy
      ProxyAddress.present(mapDialogMobility, null, transactionId);
      mapDialogMobility.addUpdateGprsLocationResponse(invokeId, ProxyAddress.hlrNumber(mapDialogMobility, clone.getHlrNumber()),
          clone.getExtensionContainer(), clone.isCapability(),
          clone.isSgsnMmeSeparationSupported(), clone.isMmeRegisteredForSM());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogMobility);

      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("UpdateGprsLocationResponse with DialogId {} failed {}. Exception caught '{}'", dialogId, transactionId, mapex);
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
   * @return MapDialogOut
   */
  public static MapDialogOut getGprsLocationRequest(MapProxyDialog mapProxyDialog,
                                                    UpdateGprsLocationRequest updateGprsLocationRequest, String transactionId) {
    Long dialogId = updateGprsLocationRequest.getMAPDialog().getLocalDialogId();
    String messageType = updateGprsLocationRequest.getMessageType().toString();
    if (mapProxyDialog == null) {
      logger.debug("{}, MAP Application Rule not found for DialogId = '{}', InvokeId = '{}', MessageType = '{}'.", transactionId, dialogId, updateGprsLocationRequest.getInvokeId(), updateGprsLocationRequest.getMessageType().toString());
      String logmsg =
          String.format("MAP Application Rule not found for DialogId = '%d', InvokeId = '%d'",
              dialogId, updateGprsLocationRequest.getInvokeId());
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    }
    try {
      logger.debug("[MAP::REQUEST<{}>] Incoming DialogId  = '{}', invokeId = '{}', {}", updateGprsLocationRequest.getMessageType().toString(), dialogId, updateGprsLocationRequest.getInvokeId(), transactionId);

      IMSI updateLocationImsi = mapProxyDialog.getImsi();
      MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();

      Long newInvokeId = mapMobilityOut.addUpdateGprsLocationRequest(updateLocationImsi,
          updateGprsLocationRequest.getSgsnNumber(), updateGprsLocationRequest.getSgsnAddress(),
          updateGprsLocationRequest.getExtensionContainer(), updateGprsLocationRequest.getSGSNCapability(),
          updateGprsLocationRequest.getInformPreviousNetworkEntity(), updateGprsLocationRequest.getPsLCSNotSupportedByUE(),
          updateGprsLocationRequest.getVGmlcAddress(), updateGprsLocationRequest.getADDInfo(), updateGprsLocationRequest.getEPSInfo(),
          updateGprsLocationRequest.getServingNodeTypeIndicator(), updateGprsLocationRequest.getSkipSubscriberDataUpdate(),
          updateGprsLocationRequest.getUsedRATType(), updateGprsLocationRequest.getGprsSubscriptionDataNotNeeded(),
          updateGprsLocationRequest.getNodeTypeIndicator(), updateGprsLocationRequest.getAreaRestricted(),
          updateGprsLocationRequest.getUeReachableIndicator(), updateGprsLocationRequest.getEpsSubscriptionDataNotNeeded(),
          updateGprsLocationRequest.getUESRVCCCapability(), updateGprsLocationRequest.getEPLMNList(),
          updateGprsLocationRequest.getMmeNumberForMTSMS(), updateGprsLocationRequest.getSMSRegisterRequest(),
          updateGprsLocationRequest.getSmsOnly(), updateGprsLocationRequest.getSgsnName(), updateGprsLocationRequest.getSgsnRealm(),
          updateGprsLocationRequest.getLgdSupportIndicator(), updateGprsLocationRequest.getRemovalOfMMERegistrationForSMS(),
          updateGprsLocationRequest.getAdjacentPLMNList());

      return new MapDialogOut(mapMobilityOut, newInvokeId, dialogId, mapProxyDialog);
    } catch (MAPException mapex) {
      logger.error("UpdateGprsLocationRequest with DialogId {} failed. Exception caught '{}', {}", dialogId, mapex, transactionId);
      return MapProxyUtilsHelper.discardReason(mapex.getMessage(), messageType, transactionId);
    } catch (Exception ex) {
      logger.error("Error: ", ex);
      return MapProxyUtilsHelper.discardReason(ex.getMessage(), messageType, transactionId);
    }
  }

}
