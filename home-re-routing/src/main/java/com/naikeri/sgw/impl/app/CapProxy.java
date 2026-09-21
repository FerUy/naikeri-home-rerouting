package com.naikeri.sgw.impl.app;

import java.util.Optional;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.impl.app.cap.CapDialogOut;
import com.naikeri.sgw.impl.app.cap.CapProcessingNode;
import com.naikeri.sgw.impl.app.cap.CapProxyCDRWriter;
import com.naikeri.sgw.impl.app.cap.CapProxyHelperUtils;
import com.naikeri.sgw.impl.rules.CapProxyApplicationRules;
import com.naikeri.sgw.impl.settings.ApplicationSettings;
import com.naikeri.sgw.impl.settings.ServiceFunctionSetting.ServiceFunctionType;
import com.naikeri.sgw.network.layers.CapLayer;
import com.naikeri.sgw.network.layers.listeners.ProxyConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPDialog;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessage;

public class CapProxy extends Application {

  private static final Logger logger = LogManager.getLogger(CapProxy.class);

  public CapProxy(ApplicationSettings applicationSettings) {
    super(applicationSettings);
    CapProxyApplicationRules.instance()
        .addCapApplicationRules(applicationSettings.getRuleFileName());
    CapProxyHelperUtils.setCDRName(applicationSettings.getCDRName());
  }

  @Override
  public void processMessage(ChannelMessage channelMessage) {
    // process incoming message extracted from incoming queue
    String messageType = (String) channelMessage.getParameter(ProxyConstants.MESSAGE_TYPE);
    if (messageType == null) {
      logger.debug("Unknown message type. Discarding message");
      return;
    }
    try {
      // check for onDialogtimeout processing
      if (messageType.equalsIgnoreCase(ProxyConstants.ON_DIALOG_TIMEOUT)) {
        Optional.ofNullable(channelMessage.getParameter(ProxyConstants.DIALOG))
            .map(u -> ((CAPDialog) u).getLocalDialogId())
            .ifPresent(dialogId -> CapProxyCDRWriter.writeCDR(dialogId, "Timeout", null, null));
        return;
      }
      // check for error and write the logs
      if (messageType.equalsIgnoreCase(ProxyConstants.ON_ERROR_COMPONENT)) {
        CAPErrorMessage capErrorMessage =
            (CAPErrorMessage) channelMessage.getParameter(ProxyConstants.CAP_ERROR_MESSAGE);
        long errorCode = Optional.ofNullable(capErrorMessage).map(CAPErrorMessage::getErrorCode).orElse(-1L);
        String errorMsg = ProxyConstants.getCapErrorCodeToString(capErrorMessage);
        CAPDialog mDialog = (CAPDialog) channelMessage.getParameter(ProxyConstants.DIALOG);
        if (mDialog != null) {
          Long dialogId = mDialog.getLocalDialogId();
          CapProxyCDRWriter.writeCDR(dialogId, "Failed", errorCode, errorMsg);
        }
        return;
      }
      // get the corresponse layers
      String rcvLayerName = (String) channelMessage.getParameter(ProxyConstants.CAP_LAYER_NAME);
      CapLayer rcvCapLayer = (CapLayer) getChannelHandler().getLayerInterface(rcvLayerName);
      CapProcessingNode.Builder builder = new CapProcessingNode.Builder();
      builder.setChannelMessage(channelMessage);

      getSSFSCFLayers(rcvLayerName, rcvCapLayer, builder);

      logger.info("Processing CAP Message Type <{}>: {}", messageType, channelMessage);

      Object messageObject = channelMessage.getParameter(ProxyConstants.MESSAGE);
      if (messageObject == null) {
        logger.debug("No message object found for {}", messageType);
      }

      CapProcessingNode capProcessingNode = builder.build();

      if (messageType.endsWith("Request")) {
        CapDialogOut capDialogOut = capProcessingNode.processRequest();
        Long dialogId = capDialogOut.getNewCapDialogId();
        logger.trace("New DialogId = {}, Message Type = {}", dialogId, messageType);
        channelMessage.setParameter("DIALOGOUT", capDialogOut);
        if (CapProxyHelperUtils.isCDREnabled()) {
          CapProxyCDRWriter.addCDRRecords(capDialogOut, channelMessage.getTransactionId());
        }
      } else if (messageType.endsWith("Response")) {
        logger.debug("Primitive Response for CAP not handled. Message Type = {}", messageType);
      } else {
        logger.debug("Unhandled message type: {}", messageType);
      }
      // send reply
      getChannelHandler().sendMessageResponse(channelMessage);
    } catch (Exception ex) {
      logger.error("Unhandled exception when processing message. Error: ", ex);
    }
  }

  private void getSSFSCFLayers(String rcvLayerName, CapLayer rcvCapLayer,
                               CapProcessingNode.Builder builder) {
    getApplicationSettings().getServiceFunctions().stream().forEach(srvFunc -> {
      if (srvFunc.getLayerName().equalsIgnoreCase(rcvLayerName)) {
        if (srvFunc.getServiceType() == ServiceFunctionType.SSF) {
          builder.setSSFLayer(rcvCapLayer);
          builder.setReceivedOn(ServiceFunctionType.SSF);
        } else {
          builder.setSCFLayer(rcvCapLayer);
          builder.setReceivedOn(ServiceFunctionType.SCF);
        }
      } else {
        CapLayer otherLeg =
            (CapLayer) getChannelHandler().getLayerInterface(srvFunc.getLayerName());
        if (srvFunc.getServiceType() == ServiceFunctionType.SSF) {
          builder.setSSFLayer(otherLeg);
        } else {
          builder.setSCFLayer(otherLeg);
        }
      }
    });
  }
}
