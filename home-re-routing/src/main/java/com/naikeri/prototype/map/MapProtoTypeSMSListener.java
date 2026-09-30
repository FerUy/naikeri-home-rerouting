package com.naikeri.prototype.map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.AlertServiceCentreResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.InformServiceCentreRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPDialogSms;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPServiceSmsListener;
import org.restcomm.protocols.ss7.map.api.service.sms.MoForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MoForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.MtForwardShortMessageRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.MtForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.NoteSubscriberPresentRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ReadyForSMRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ReadyForSMResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.ReportSMDeliveryStatusRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.ReportSMDeliveryStatusResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMResponse;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

/**
 * Plays the far end of the SMS flows: the SMSC for MO-ForwardSM and the MSC for MT-ForwardSM. Each
 * request is acknowledged and the dialogue ended, so the proxy's return path is exercised in both
 * directions. The result's sm-RP-UI is optional and left out: where present it would carry an
 * SMS-DELIVER-REPORT or SMS-SUBMIT-REPORT, not the TPDU that came in.
 */
public class MapProtoTypeSMSListener implements MAPServiceSmsListener {

  private static final Logger logger = LogManager.getLogger(MapProtoTypeSMSListener.class);

  @Override
  public void onMoForwardShortMessageRequest(MoForwardShortMessageRequest request) {
    MAPDialogSms dialog = request.getMAPDialog();
    logger.info("MoForwardShortMessageRequest for DialogId={}", dialog.getLocalDialogId());
    try {
      dialog.addMoForwardShortMessageResponse(request.getInvokeId(), null, null);
      dialog.close(false);
    } catch (Exception e) {
      logger.error("Unable to answer the MO-Forward-Short-Message request", e);
    }
  }

  @Override
  public void onMoForwardShortMessageResponse(MoForwardShortMessageResponse response) {
    logger.info("MoForwardShortMessageResponse for DialogId={}", response.getMAPDialog().getLocalDialogId());
  }

  @Override
  public void onMtForwardShortMessageRequest(MtForwardShortMessageRequest request) {
    MAPDialogSms dialog = request.getMAPDialog();
    logger.info("MtForwardShortMessageRequest for DialogId={}", dialog.getLocalDialogId());
    try {
      dialog.addMtForwardShortMessageResponse(request.getInvokeId(), null, null);
      dialog.close(false);
    } catch (Exception e) {
      logger.error("Unable to answer the MT-Forward-Short-Message request", e);
    }
  }

  @Override
  public void onMtForwardShortMessageResponse(MtForwardShortMessageResponse response) {
    logger.info("MtForwardShortMessageResponse for DialogId={}", response.getMAPDialog().getLocalDialogId());
  }

  @Override
  public void onErrorComponent(MAPDialog mapDialog, Long invokeId, MAPErrorMessage mapErrorMessage) {
    logger.warn("Error component on DialogId={}, invokeId={}: {}", mapDialog.getLocalDialogId(), invokeId, mapErrorMessage);
  }

  @Override
  public void onRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem, boolean isLocalOriginated) {
    logger.warn("Reject component on DialogId={}, invokeId={}, localOriginated={}: {}",
        mapDialog.getLocalDialogId(), invokeId, isLocalOriginated, problem);
  }

  @Override
  public void onInvokeTimeout(MAPDialog mapDialog, Long invokeId) {
    logger.warn("Invoke timeout on DialogId={}, invokeId={}", mapDialog.getLocalDialogId(), invokeId);
  }

  @Override
  public void onMAPMessage(MAPMessage mapMessage) {
    logger.debug("MAP message {} on DialogId={}", mapMessage.getMessageType(), mapMessage.getMAPDialog().getLocalDialogId());
  }

  // Not simulated: none of these is sent by the MAP simulator or forwarded by the proxy.

  @Override
  public void onForwardShortMessageRequest(ForwardShortMessageRequest request) {
  }

  @Override
  public void onForwardShortMessageResponse(ForwardShortMessageResponse response) {
  }

  @Override
  public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest request) {
  }

  @Override
  public void onSendRoutingInfoForSMResponse(SendRoutingInfoForSMResponse response) {
  }

  @Override
  public void onReportSMDeliveryStatusRequest(ReportSMDeliveryStatusRequest request) {
  }

  @Override
  public void onReportSMDeliveryStatusResponse(ReportSMDeliveryStatusResponse response) {
  }

  @Override
  public void onInformServiceCentreRequest(InformServiceCentreRequest request) {
  }

  @Override
  public void onAlertServiceCentreRequest(AlertServiceCentreRequest request) {
  }

  @Override
  public void onAlertServiceCentreResponse(AlertServiceCentreResponse response) {
  }

  @Override
  public void onReadyForSMRequest(ReadyForSMRequest request) {
  }

  @Override
  public void onReadyForSMResponse(ReadyForSMResponse response) {
  }

  @Override
  public void onNoteSubscriberPresentRequest(NoteSubscriberPresentRequest request) {
  }
}
