package com.naikeri.sgw.impl.app;

import java.util.Optional;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.app.map.MapProcessingNode;
import com.naikeri.sgw.impl.app.map.MapProxyBuilder;
import com.naikeri.sgw.impl.app.map.helper.MapProxyCDRWriter;
import com.naikeri.sgw.impl.app.map.helper.MapProxyUtilsHelper;
import com.naikeri.sgw.impl.rules.MapProxyApplicationRules;
import com.naikeri.sgw.impl.settings.ApplicationSettings;
import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import com.naikeri.sgw.network.layers.listeners.ProxyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;

public class MapProxy extends Application {

  private static final Logger logger = LoggerFactory.getLogger(MapProxy.class);

  public MapProxy(ApplicationSettings applicationSettings) {
    super(applicationSettings);
    MapProxyApplicationRules.getInstance()
        .addMapApplicationRules(applicationSettings.getRuleFileName());
    MapProxyUtilsHelper.setCDRName(applicationSettings.getCDRName());
  }

  private void processRequestMessages(ChannelMessage channelMessage) {
    MapProcessingNode map = new MapProxyBuilder.Builder().setChannelMessage(channelMessage)
        .setMapLayer(getChannelHandler().getLayerInterface()).buildMapProcessingNode();

    Object message = channelMessage.getParameter(ProxyConstants.MESSAGE);
    String messageType = (String) channelMessage.getParameter(ProxyConstants.MESSAGE_TYPE);
    // check if there exist a message to be processed.
    // if the message does not exist, it will create a lot of null pointer exception
    // no message
    if (message == null) {
      logger
          .debug("Unable to process <" + messageType + "> request. The MapMessage Object is NULL");
      return;
    }

    MapDialogOut dialogOut = map.processRequest();
    if (dialogOut != null) {
      Long dialogId = dialogOut.getNewDialogId();
      Long newInvokeId = dialogOut.getInvokeId();

      DataElement dataElement = new DataElement(messageType, newInvokeId, dialogId, message);
      String logInvokeId = String.format("InvokeId = %d", newInvokeId);
      if (dialogId > 0 && newInvokeId != null) {
        logger.info(String.format(
            "[MAP::REQUEST<%s>] New DialogId = '%d', Original DialogId = '%d', %s, %s", messageType,
            dialogId, dialogOut.getOriginalDialogId(), logInvokeId, channelMessage.toString()));
        // store the corresponding dialog Id with request object
        Transaction.getInstance().setDialogData(dialogId, newInvokeId, dataElement);
        // add the dialog id to the store
        if (messageType.equals("updateLocation_Request")
            || messageType.equals("updateGprsLocation_Request")) {
          Transaction.getInstance().setDialogId(dialogId, dataElement);
        }
        logger.debug(String.format("Stored ObjectId = %d-%d, Message Type ='%s'", dialogId,
            newInvokeId, messageType));
        // setting up the initials values for the CDRS
        if (MapProxyUtilsHelper.isCDREnabled()) {
          MapProxyCDRWriter.addFields(dialogOut, messageType, channelMessage.getTransactionId());
        }
      }
      channelMessage.setParameter("DIALOGOUT", dialogOut);
    }
  }

  private void processResponseMessages(ChannelMessage channelMessage) {
    MapProcessingNode map = new MapProxyBuilder.Builder().setChannelMessage(channelMessage)
        .setMapLayer(getChannelHandler().getLayerInterface()).buildMapProcessingNode();
    MapDialogOut respDialogOut = map.processResponse();
    String messageType = (String) channelMessage.getParameter(ProxyConstants.MESSAGE_TYPE);
    // check if the message is not null to avoid nullpointer exceptions
    Object message = channelMessage.getParameter(ProxyConstants.MESSAGE);
    if (message == null) {
      logger
          .debug("Failed to process <" + messageType + "> response. The MapMessage Object is NULL");
      return;
    }
    if (respDialogOut != null) {
      logger.info(String.format("[MAP::RESPONSE<%s>] DialogId = %d, Original DialogId = %d, %s, %s",
          messageType, respDialogOut.getOriginalDialogId(), respDialogOut.getNewDialogId(),
          respDialogOut.getLogInvokeIds(), channelMessage.toString()));
      // use the original dialogId
      if (MapProxyUtilsHelper.isCDREnabled()) {
        if (respDialogOut.getIsResponse()) {
          // now write the cdr
          MapProxyCDRWriter.writeCDR(respDialogOut.getOriginalDialogId(), "Success", null, null);
        } else {
          respDialogOut.getReason().ifPresent(
              u -> MapProxyCDRWriter.writeCDR(respDialogOut.getOriginalDialogId(), u, null, null));
        }
      }
      channelMessage.setParameter("DIALOGOUT", respDialogOut);
    }
  }

  @Override
  public void processMessage(ChannelMessage channelMessage) {
    try {
      // process incoming message extracted from incoming queue
      String messageType = (String) channelMessage.getParameter(ProxyConstants.MESSAGE_TYPE);
      MAPDialog mapDialog = (MAPDialog) channelMessage.getParameter(ProxyConstants.DIALOG);
      if (messageType == null) {
        logger.info("[MAP::INVALID_MESSAGE_TYPE]. Message Type is NULL. Discarding message for "
            + channelMessage.toString());
        return;
      }
      logger.debug(String.format("Processing <%s>: Message '%s' received, sending reply.",
          messageType, channelMessage.toString()));

      if (messageType.endsWith("_Request")) {
        processRequestMessages(channelMessage);
      } else if (messageType.endsWith("_Response")) {
        processResponseMessages(channelMessage);
      } else if (messageType.equalsIgnoreCase(ProxyConstants.ON_ERROR_COMPONENT)) {
        // process when there is error
        MAPErrorMessage mapErrorMessage =
            (MAPErrorMessage) channelMessage.getParameter(ProxyConstants.MAP_ERROR_MESSAGE);
        long errorCode = Optional.ofNullable(mapErrorMessage).map(MAPErrorMessage::getErrorCode)
            .map(Long::longValue).orElse(-1L);
        String errorMsg = ProxyConstants.getMapErrorCodeToString(mapErrorMessage);
        MAPDialog mDialog = (MAPDialog) channelMessage.getParameter(ProxyConstants.DIALOG);
        if (mDialog != null) {
          Long dialogId = mDialog.getLocalDialogId();
          MapProxyCDRWriter.writeCDR(dialogId, "Error", errorCode, errorMsg);
        }
      } else {
        procesSignals(messageType, mapDialog, channelMessage.toString());
      }
      // send a response back to the channel
      getChannelHandler().sendMessageResponse(channelMessage);
    } catch (Exception ex) {
      logger.error("Exception when processing " + channelMessage.toString() + ". Error: ", ex);
    }
  }

  private void procesSignals(String messageType, MAPDialog mapDialog, String channelId) {
    if (messageType.equalsIgnoreCase(ProxyConstants.ON_DIALOG_TIMEOUT)) {
      processOnDialogTimeout(mapDialog);
    } else if (messageType.equalsIgnoreCase(ProxyConstants.ON_DIALOG_CLOSE)) {
      // processOnDialogClose(mapDialog)
    } else {
      logger.info(
          String.format("MessageType = '%s' cannot be processed, %s", messageType, channelId));
    }
  }

  private void processOnDialogTimeout(MAPDialog mapDialog) {
    // write the CDR
    if (mapDialog != null) {
      Long dialogId = mapDialog.getLocalDialogId();
      MapProxyCDRWriter.writeCDR(dialogId, "Timeout", null, null);
    }
  }
}
