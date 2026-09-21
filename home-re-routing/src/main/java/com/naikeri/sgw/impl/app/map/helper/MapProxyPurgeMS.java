package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProxyDialog;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSResponse;
import org.restcomm.protocols.ss7.tcap.api.MessageType;

/**
 * MapProxyPurgeMS
 */
public class MapProxyPurgeMS {

  private static final Logger logger = LogManager.getLogger(MapProxyPurgeMS.class);

  private MapProxyPurgeMS() {
  }

  /**
   * Process response for PurgeMSResponse
   *
   * @return MapDialogOut
   */
  public static MapDialogOut getResponse(Object message, String transactionId) {
    PurgeMSResponse purgeMSResponse = (PurgeMSResponse) message;
    Long dialogId = purgeMSResponse.getMAPDialog().getLocalDialogId();
    try {

      Long respInvokeId = purgeMSResponse.getInvokeId();
      MapDialogOut mapDialogOut = new MapDialogOut();
      mapDialogOut.setOriginalDialogId(dialogId);
      logger.debug("[MAP::RESPONSE<{}>] Incoming DialogId '{}', invokeId '{}', {}", purgeMSResponse.getMessageType().toString(), purgeMSResponse.getMAPDialog().getLocalDialogId(), purgeMSResponse.getInvokeId(), transactionId);

      DataElement dataElement;
      logger.debug("TCAP Message Type = '{}', dialogId = {}, InvokeId = {}, Service = '{}'", purgeMSResponse.getMAPDialog().getTCAPMessageType(), dialogId, purgeMSResponse.getInvokeId(), purgeMSResponse.getMAPDialog().getService().toString());

      if (purgeMSResponse.getMAPDialog().getTCAPMessageType() == MessageType.End
          || purgeMSResponse.getMAPDialog().getTCAPMessageType() == MessageType.Abort) {
        logger.debug("Closing for dialogId = {}", dialogId);
        dataElement = Transaction.getInstance().removeDialogData(dialogId, respInvokeId);
        mapDialogOut.setIsResponse();
      } else {
        dataElement = Transaction.getInstance().getDialogData(dialogId, respInvokeId);
      }

      if (dataElement == null) {
        logger.debug("Dialog Id = {} not found in Transaction Map. {}", dialogId, transactionId);
        return null;
      }
      PurgeMSRequest origEvent = (PurgeMSRequest) dataElement.getRequestObject();
      MAPDialogMobility mapDialogMobility = origEvent.getMAPDialog();
      Long invokeId = origEvent.getInvokeId();
      mapDialogMobility.setUserObject(invokeId);
      mapDialogMobility.addPurgeMSResponse(invokeId, purgeMSResponse.getFreezeTMSI(),
          purgeMSResponse.getFreezePTMSI(), purgeMSResponse.getExtensionContainer(),
          purgeMSResponse.getFreezeMTMSI());

      mapDialogOut.setLogInvokeIds(respInvokeId, invokeId);
      mapDialogOut.setMapDialog(mapDialogMobility);
      return mapDialogOut;
    } catch (MAPException mapex) {
      logger.error("PurgeMSResponse with DialogId {} failed {}. Exception caught '{}'", dialogId, transactionId, mapex);
    }
    return null;
  }

  /**
   * Process Purge MS request
   *
   * @param mapProxyDialog MapProxyDialog
   * @param purgeMSRequest PurgeMSRequest
   * @return MapDialogOut
   */
  public static MapDialogOut getRequest(MapProxyDialog mapProxyDialog,
                                        PurgeMSRequest purgeMSRequest, String transactionId) {
    Long dialogId = purgeMSRequest.getMAPDialog().getLocalDialogId();
    String messageType = purgeMSRequest.getMessageType().toString();
    String logmsg;
    if (mapProxyDialog == null) {
      logmsg = String.format(
          "%s, MAP Application Rule not found for DialogId = '%d', InvokeId = '%d', MessageType = '%s'. MAP Message will be discarded",
          transactionId, dialogId, purgeMSRequest.getInvokeId(),
          purgeMSRequest.getMessageType().toString());
      return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
    }
    try {
      logger.debug("[MAP::REQUEST<{}>] Incoming DialogId = '{}', InvokeId = '{}', {}", messageType, dialogId, purgeMSRequest.getInvokeId(), transactionId);

      MAPDialogMobility mapMobilityOut = mapProxyDialog.getMapDialogMobility();

      Long newInvokeId = mapMobilityOut.addPurgeMSRequest(mapProxyDialog.getImsi(),
          purgeMSRequest.getVlrNumber(),
          purgeMSRequest.getSgsnNumber(),
          purgeMSRequest.getExtensionContainer(),
          purgeMSRequest.getLocationInformation(),
          purgeMSRequest.getLocationInformationGPRS(),
          purgeMSRequest.getLocationInformationEPS());
      return new MapDialogOut(mapMobilityOut, newInvokeId, dialogId, mapProxyDialog);
    } catch (MAPException mapex) {
      logger.error("PurgeMSRequest with DialogId {} failed. Exception caught '{}', {}", dialogId, mapex, transactionId);
      logmsg = mapex.getMessage();
    } catch (Exception ex) {
      logmsg = ex.getMessage();
    }
    return MapProxyUtilsHelper.discardReason(logmsg, messageType, transactionId);
  }
}
