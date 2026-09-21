package com.naikeri.sgw.impl.chn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.impl.app.cap.CapDialogOut;
import com.naikeri.sgw.impl.settings.ChannelSettings;
import com.naikeri.sgw.info.CapTransaction;
import com.naikeri.sgw.network.layers.CapLayer;
import com.naikeri.sgw.network.layers.listeners.ProxyConstants;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.restcomm.protocols.ss7.cap.api.CAPMessage;

/**
 * CapChannel
 */
public class CapChannel extends ChannelHandler {

  private static final Logger logger = LogManager.getLogger(CapChannel.class);
  private static final List<String> handleSignalList = new ArrayList<>();
  static {
    handleSignalList.add(ProxyConstants.ON_DIALOG_TIMEOUT);
    handleSignalList.add(ProxyConstants.ON_INVOKE_TIMEOUT);
    handleSignalList.add(ProxyConstants.ON_DIALOG_CLOSE);
  }
  private final List<CapLayer> capLayers = new ArrayList<>();
  private final ChannelSettings channelSetting;


  public CapChannel(ChannelSettings channelSettings) {
    super(channelSettings);
    this.channelSetting = channelSettings;
  }

  @Override
  public void channelInitialize(LayerInterface[] layerInterface) {
    for (LayerInterface layer : layerInterface) {
      capLayers.add((CapLayer) layer);
    }
    try {
      // set the max dialog
      Optional<CapLayer> intValue = capLayers.stream().findFirst();
      int maxDialog = 1000;
      if (intValue.isPresent()) {
        maxDialog = intValue.get().getCapStack().getTCAPStack().getMaxDialogs();
      }
      CapTransaction.instance().setMaxTransaction(maxDialog);
    } catch (Exception e) {
      logger.error("ERROR: Failed to get Max Dialogs from TCAP Layer. Setting default value. Error: {}", String.valueOf(e));
    }
    logger.debug("CapChannel initialization complete!");
  }

  @Override
  public void receiveMessageRequest(ChannelMessage channelMessage) {
    CAPMessage capMessage = (CAPMessage) channelMessage.getParameter(ProxyConstants.MESSAGE);
    String messageType = (String) channelMessage.getParameter(ProxyConstants.MESSAGE_TYPE);
    if (capMessage != null && messageType != null) {
      if (capMessage.getMessageType().toString().endsWith("Request")) {
        logger.debug("[CAP::REQUEST<{}>] dialogId '{}', invokeId '{}', {}", capMessage.getMessageType().toString(), capMessage.getCAPDialog().getLocalDialogId(), capMessage.getInvokeId(), channelMessage.getTransactionId());
      } else if (capMessage.getMessageType().toString().endsWith("Response")) {
        logger.debug("[CAP::RESPONSE<{}>] dialogId '{}', invokeId '{}', {}", capMessage.getMessageType().toString(), capMessage.getCAPDialog().getLocalDialogId(), capMessage.getInvokeId(), channelMessage.getTransactionId());
      } else {
        logger.info("Sending message '{}' to application. {}", capMessage.getMessageType().toString(), channelMessage.getTransactionId());
      }
      // process only primitives defined in the Channel
      if ((this.channelSetting != null && this.channelSetting.isPrimitiveExist(messageType))
          || handleSignalList.contains(messageType)) {
        sendMessageRequest(channelMessage);
      }
    }
  }

  @Override
  public int sendMessageResponse(ChannelMessage channelMessage) {
    // send a response back to the channel
    try {
      String messageType = (String) channelMessage.getParameter(ProxyConstants.MESSAGE_TYPE);
      Object paramDialogOut = channelMessage.getParameter("DIALOGOUT");
      if (paramDialogOut != null) {
        CapDialogOut dialogOut = (CapDialogOut) paramDialogOut;
        logger.debug("[CAP::RESPONSE<{}>]  {}", messageType, channelMessage);
        dialogOut.send();
      } else {
        logger.debug("Discarding message '{}", channelMessage);
      }
    } catch (Exception e) {
      logger.error("Exception caught for {}Details: ", channelMessage.getTransactionId(), e);
    }
    return 0;
  }

  @Override
  public LayerInterface getLayerInterface() {
    return null;
  }

  @Override
  public LayerInterface getLayerInterface(String layerName) {
    CapLayer cap = null;
    for (CapLayer capLayer : capLayers) {
      if (capLayer.getName().equalsIgnoreCase(layerName)) {
        cap = capLayer;
        break;
      }
    }
    return cap;
  }
}

