package com.naikeri.sgw.impl.app.cap;

import java.lang.reflect.Method;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.impl.settings.ServiceFunctionSetting.ServiceFunctionType;
import com.naikeri.sgw.network.layers.CapLayer;
import com.naikeri.sgw.network.layers.listeners.ProxyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restcomm.protocols.ss7.cap.api.CAPMessageType;

/**
 * CapProcessingNode
 */
public class CapProcessingNode {

  private static final Logger logger = LoggerFactory.getLogger(CapProcessingNode.class);

  private CAPMessageType messageType;
  private Object capOperationObject;
  private CapLayer capLayerIn;
  private String transactionId;
  private CapLayer capLayerOut;

  public static class Builder {
    private CAPMessageType messageType;
    private Object capOperationObject;
    private String transactionId;
    private CapLayer ssfCapLayer;
    private CapLayer scfCapLayer;
    private ServiceFunctionType srvFunctionType;

    public Builder setChannelMessage(ChannelMessage channelMessage) {
      try {
        String messageTypeL = (String) channelMessage.getParameter(ProxyConstants.MESSAGE_TYPE);
        this.capOperationObject = channelMessage.getParameter(ProxyConstants.MESSAGE);
        this.transactionId = channelMessage.getTransactionId();
        this.messageType = CAPMessageType.valueOf(messageTypeL);
      } catch (Exception ex) {
        logger.error("Unknown primitive: '" + messageType + "'. Error: ", ex);
      }
      return this;
    }

    public Builder setSSFLayer(CapLayer rcvCapLayer) {
      this.ssfCapLayer = rcvCapLayer;
      return this;
    }

    public Builder setSCFLayer(CapLayer rcvCapLayer) {
      this.scfCapLayer = rcvCapLayer;
      return this;
    }

    public Builder setReceivedOn(ServiceFunctionType srvFuncType) {
      this.srvFunctionType = srvFuncType;
      return this;
    }

    public CapProcessingNode build() {
      CapProcessingNode capProcessingNode = new CapProcessingNode();
      capProcessingNode.messageType = this.messageType;

      if (this.srvFunctionType == ServiceFunctionType.SCF) {
        capProcessingNode.capLayerIn = this.scfCapLayer;
        capProcessingNode.capLayerOut = this.ssfCapLayer;
      } else {
        capProcessingNode.capLayerIn = this.ssfCapLayer;
        capProcessingNode.capLayerOut = this.scfCapLayer;
      }
      capProcessingNode.capOperationObject = this.capOperationObject;
      capProcessingNode.transactionId = this.transactionId;
      return capProcessingNode;
    }
  }

  /**
   * Invoke the appropriate method to process the primitive type
   *
   * @return CapDialogOut
   */
  public CapDialogOut processRequest() {
    logger.info(String.format("Processing primitive = %s, transactionId = %s", this.messageType,
        this.transactionId));
    // check which primitive needs to be processed
    // if no layer return
    if (capLayerIn == null || capLayerOut == null) {
      logger.error(String.format("<%s, %s> The Caplayers are not found", this.transactionId,
          this.messageType));
      return null;
    }

    switch (this.messageType) {
      // CAMEL Circuit Switched Call Control (BCSM)
      case initialDP_Request:
      case connect_Request:
      case releaseCall_Request:
      case eventReportBCSM_Request:
      case requestReportBCSMEvent_Request:
      case continue_Request:
      case activityTest_Request:
      case activityTest_Response:
      case assistRequestInstructions_Request:
      case establishTemporaryConnection_Request:
      case disconnectForwardConnection_Request:
      case disconnectForwardConnectionWithArgument_Request:
      case connectToResource_Request:
      case resetTimer_Request:
      case furnishChargingInformation_Request:
      case applyChargingReport_Request:
      case applyCharging_Request:
      case callInformationReport_Request:
      case callInformationRequest_Request:
      case sendChargingInformation_Request:
      case specializedResourceReport_Request:
      case playAnnouncement_Request:
      case promptAndCollectUserInformation_Request:
      case promptAndCollectUserInformation_Response:
      case cancel_Request:
      case continueWithArgument_Request:
      case collectInformation_Request:
      case callGap_Request:
      case entityReleased_Request:
      case disconnectLeg_Request:
      case disconnectLeg_Response:
      case moveLeg_Request:
      case moveLeg_Response:
      case splitLeg_Request:
      case playTone_Request:
      case initiateCallAttempt_Request:
      case initiateCallAttempt_Response:
      case collectInformationRequest_Request:
        return processCircuitSwitchedCallControl();

      // CAMEL GPRS Control
      case initialDPGPRS_Request:
      case requestReportGPRSEvent_Request:
      case eventReportGPRS_Request:
      case eventReportGPRS_Response:
      case applyChargingGPRS_Request:
      case applyChargingReportGPRS_Request:
      case applyChargingReportGPRS_Response:
      case entityReleasedGPRS_Request:
      case entityReleasedGPRS_Response:
      case connectGPRS_Request:
      case continueGPRS_Request:
      case releaseGPRS_Request:
      case resetTimerGPRS_Request:
      case furnishChargingInformationGPRS_Request:
      case cancelGPRS_Request:
      case sendChargingInformationGPRS_Request:
      case activityTestGPRS_Request:
      case activityTestGPRS_Response:
        return processCamelGprsControl();

      // CAMEL SMS Control
      case initialDPSMS_Request:
      case connectSMS_Request:
      case releaseSMS_Request:
      case requestReportSMSEvent_Request:
      case eventReportSMS_Request:
      case resetTimerSMS_Request:
      case furnishChargingInformationSMS_Request:
      case continueSMS_Request:
        return processCamelSmsControl();

      default:
        logger.info(
            String.format("<%s>. Unhandled message: '%s' ", this.transactionId, this.messageType));
        break;
    }
    return null;
  }

  private CapDialogOut processCircuitSwitchedCallControl() {
    try {
      CamelBcsmChannelMessages camelBcsmChannelMessages = new CamelBcsmChannelMessages(
          this.capLayerIn, this.capLayerOut, this.capOperationObject, this.transactionId);
      Method method = camelBcsmChannelMessages.getClass().getMethod(this.messageType.toString());
      Object returnValue = method.invoke(camelBcsmChannelMessages);
      if (returnValue != null) {
        return (CapDialogOut) returnValue;
      }
    } catch (Exception e) {
      logger.error("Error Processing CircuitSwitchedCallControl. Error: ", e);
    }
    return null;
  }

  private CapDialogOut processCamelGprsControl() {
    try {
      CamelGprsChannelMessages camelGprsChannelMessages = new CamelGprsChannelMessages(
          this.capLayerIn, this.capLayerOut, this.capOperationObject, this.transactionId);
      Method method = camelGprsChannelMessages.getClass().getMethod(this.messageType.toString());

      Object returnValue = method.invoke(camelGprsChannelMessages);
      if (returnValue != null) {
        return (CapDialogOut) returnValue;
      }

    } catch (Exception e) {
      logger.error("Error proessing CameGprsControl. Error: ", e);
    }
    return null;
  }

  private CapDialogOut processCamelSmsControl() {
    try {
      CamelSmsChannelMessages camelSmsChannelMessages = new CamelSmsChannelMessages(this.capLayerIn,
          this.capLayerOut, this.capOperationObject, this.transactionId);
      Method method = camelSmsChannelMessages.getClass().getMethod(this.messageType.toString());

      Object returnValue = method.invoke(camelSmsChannelMessages);
      if (returnValue != null) {
        return (CapDialogOut) returnValue;
      }

    } catch (Exception e) {
      logger.error("Error processing CamelSmsControl. Error: ", e);
    }
    return null;
  }
}

