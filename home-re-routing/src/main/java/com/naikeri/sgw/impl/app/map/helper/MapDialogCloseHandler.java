package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.info.DataElement;
import com.naikeri.sgw.info.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.MAPMessageType;

/**
 * MapDialogCloseHandler
 */
public class MapDialogCloseHandler {

  private MapDialogCloseHandler() {
  }

  private static final Logger logger = LoggerFactory.getLogger(MapDialogCloseHandler.class);

  // do not delete the record from the memory but delay the processing by x number of seconds
  public static long closeMapDialog(String incomingMessageType, Long dialogId) {
    if (dialogId == null)
      return -1L;
    logger.trace(String.format("Received Event = '%s', DialogId = '%s' for closing",
        incomingMessageType, dialogId));
    // close all related dialogs
    long retValue = -1;
    try {
      Thread.sleep(30000);
      for (DataElement dataElement : Transaction.getInstance().removeAllDialogs(dialogId)) {
        logger.trace(String.format("[TC-CLOSE <%s>] DialogId = %d, InvokeId = %d",
            dataElement.getMessageType(), dataElement.getDialogId(), dataElement.getInvokeId()));
        // check the message type
        MAPMessage mapMessage = (MAPMessage) dataElement.getRequestObject();
        mapMessage.getMAPDialog().close(true);
        retValue = dialogId;
        MAPMessageType messageType = MAPMessageType.valueOf(dataElement.getMessageType());
        logger.debug(String.format("Sending TC-END for \"%s\", DialogId = %d ", messageType,
            mapMessage.getMAPDialog().getLocalDialogId()));
      }
    } catch (Exception e) {
      logger.error(String.format(
          "Failed to get the data element from the Transaction class. Exception message: %s",
          e.getMessage()));

    }
    return retValue;
  }
}
