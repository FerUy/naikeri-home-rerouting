package com.naikeri.sgw.impl.chn;

import java.util.ArrayList;
import java.util.List;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.settings.ChannelSettings;
import com.naikeri.sgw.info.Transaction;
import com.naikeri.sgw.network.layers.MapLayer;
import com.naikeri.sgw.network.layers.listeners.ProxyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restcomm.protocols.ss7.map.api.MAPDialog;

/**
 * MapChannel
 */
public class MapChannel extends ChannelHandler {

  private static final Logger logger = LoggerFactory.getLogger(MapChannel.class);
  private static final List<String> handleSignalList = new ArrayList<>();
  static {
    handleSignalList.add(ProxyConstants.ON_DIALOG_TIMEOUT);
    handleSignalList.add(ProxyConstants.ON_INVOKE_TIMEOUT);
    handleSignalList.add(ProxyConstants.ON_DIALOG_CLOSE);
  }

  private MapLayer map = null;
  private ChannelSettings channelSetting = null;

  public MapChannel(ChannelSettings channelSettings) {
    super(channelSettings);
    this.channelSetting = channelSettings;
  }

  @Override
  public void channelInitialize(LayerInterface[] layerInterface) {
    map = (MapLayer) layerInterface[0];
    try {
      // Get the MaxDialog from the TCAP to initialize the TransactionMap maxTransactions
      // get the TCAPStack
      int maxDialogs = map.getMapStack().getTCAPStack().getMaxDialogs();
      Transaction.getInstance().setMaxDialog(maxDialogs);
    } catch (Exception ex) {
      logger.error("ERROR: Failed to get Max Dialogs from TCAP Layer. Setting default value");
    }
    logger.debug("MapChannel initialized complete!");
  }

  private void logMessages(String messagetype, String chnMessage) {
    if (messagetype.isEmpty())
      return;
    if (messagetype.endsWith("Request")) {
      logger.info(String.format("[MAP::REQUEST<%s>] Sending message '%s' to application.",
          messagetype, chnMessage));
    } else if (messagetype.endsWith("Response")) {
      logger.info(String.format("[MAP::RESPONSE<%s>] Sending message '%s' to application.",
          messagetype, chnMessage));
    } else {
      logger.info(String.format("Sending message '%s' to application.", chnMessage));
    }
  }

  @Override
  public void receiveMessageRequest(ChannelMessage channelMessage) {
    try {
      Object message = channelMessage.getParameter(ProxyConstants.MESSAGE);
      String messagetype = (String) channelMessage.getParameter(ProxyConstants.MESSAGE_TYPE);

      if (message != null && messagetype != null) {
        logMessages(messagetype, channelMessage.toString());
        // only send message which are defined in the configurations
        if (this.channelSetting != null && this.channelSetting.isPrimitiveExist(messagetype)) {
          sendMessageRequest(channelMessage);
        }
      } else {
        MAPDialog mapDialog = (MAPDialog) channelMessage.getParameter("dialog");
        if (mapDialog != null) {
          logger.debug(String.format(
              "[MAP::SIGNAL<%s>] dialogId = '%d', appCtx<%s>, NetworkId = %d, %s", messagetype,
              mapDialog.getLocalDialogId(), mapDialog.getApplicationContext().toString(),
              mapDialog.getNetworkId(), channelMessage.toString()));
          // handle onDialogTimeout
          if (handleSignalList.contains(messagetype)) {
            sendMessageRequest(channelMessage);
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Error occurred forwarding message to MapProxy. Error: ", ex);
    }
  }

  @Override
  public int sendMessageResponse(ChannelMessage channelMessage) {
    // send a response back to the channel
    String messageType = "";
    try {
      messageType = (String) channelMessage.getParameter(ProxyConstants.MESSAGE_TYPE);
      Object paramDialogOut = channelMessage.getParameter("DIALOGOUT");
      if (paramDialogOut != null) {
        MapDialogOut dialogOut = (MapDialogOut) paramDialogOut;
        // check if there is discard message then don't send the MAP message
        String discardMsg = dialogOut.getDiscardReason();
        if (discardMsg != null && !discardMsg.isEmpty()) {
          logger.info(String.format("MAP::DISCARD<%s>] Reason: %s, %s", messageType,
              dialogOut.getDiscardReason(), channelMessage.toString()));
        } else {
          String classDialogOut = dialogOut.getDialogOutName(messageType);

          logger.info(String.format(
              "MAPmessage '%s' sending from '%s' dialog '%d' to remote '%s' dialog '%d', %s",
              classDialogOut, dialogOut.getLocalAddress(), dialogOut.getLocalDialogId(),
              dialogOut.getRemoteAddress(), dialogOut.getRemoteDialogId(),
              channelMessage.toString()));
          dialogOut.send();
        }
      } else {
        if (!messageType.equals("onDialogClose")) {
          logger
              .info(String.format("MAP::DISCARD<%s>] Discarding message for %s with unknown reason",
                  messageType, channelMessage.toString()));
        }
      }
    } catch (Exception e) {
      logger.error("Exception caught for <" + messageType + ">: " + channelMessage.toString(), e);
    }
    return 0;

  }

  @Override
  public LayerInterface getLayerInterface() {
    return map;
  }

  @Override
  public LayerInterface getLayerInterface(String serviceFunctionName) { // scf, ssf
    return null;
  }

}
