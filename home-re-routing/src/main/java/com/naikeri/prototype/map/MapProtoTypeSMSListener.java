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

public class MapProtoTypeSMSListener implements MAPServiceSmsListener {

  private static final Logger logger = LogManager.getLogger(MapProtoTypeSMSListener.class);

  @Override
  public void onErrorComponent(MAPDialog mapDialog, Long invokeId,
                               MAPErrorMessage mapErrorMessage) {
    //  Auto-generated method stub

  }

  @Override
  public void onRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem,
                                boolean isLocalOriginated) {
    //  Auto-generated method stub

  }

  @Override
  public void onInvokeTimeout(MAPDialog mapDialog, Long invokeId) {
    //  Auto-generated method stub

  }

  @Override
  public void onMAPMessage(MAPMessage mapMessage) {
    //  Auto-generated method stub

  }

  @Override
  public void onForwardShortMessageRequest(ForwardShortMessageRequest forwSmInd) {
    //  Auto-generated method stub

  }

  @Override
  public void onForwardShortMessageResponse(ForwardShortMessageResponse forwSmRespInd) {
    //  Auto-generated method stub

  }

  @Override
  public void onMoForwardShortMessageRequest(MoForwardShortMessageRequest moForwSmInd) {
    //  Auto-generated method stub

  }

  @Override
  public void onMoForwardShortMessageResponse(MoForwardShortMessageResponse moForwSmRespInd) {
    //  Auto-generated method stub

  }

  @Override
  public void onMtForwardShortMessageRequest(MtForwardShortMessageRequest mtForwSmInd) {
    //  Auto-generated method stub
    try {
      MAPDialogSms dialogSms = mtForwSmInd.getMAPDialog();
      dialogSms.addMtForwardShortMessageResponse(mtForwSmInd.getInvokeId(), mtForwSmInd.getSM_RP_UI(), null);
    } catch (Exception e) {
      logger.error(e.getStackTrace());
    }
  }

  @Override
  public void onMtForwardShortMessageResponse(MtForwardShortMessageResponse mtForwSmRespInd) {
    //  Auto-generated method stub

  }

  @Override
  public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest sendRoutingInfoForSMInd) {
    //  Auto-generated method stub

  }

  @Override
  public void onSendRoutingInfoForSMResponse(
      SendRoutingInfoForSMResponse sendRoutingInfoForSMRespInd) {
    //  Auto-generated method stub

  }

  @Override
  public void onReportSMDeliveryStatusRequest(
      ReportSMDeliveryStatusRequest reportSMDeliveryStatusInd) {
    //  Auto-generated method stub

  }

  @Override
  public void onReportSMDeliveryStatusResponse(
      ReportSMDeliveryStatusResponse reportSMDeliveryStatusRespInd) {
    //  Auto-generated method stub

  }

  @Override
  public void onInformServiceCentreRequest(InformServiceCentreRequest informServiceCentreInd) {
    //  Auto-generated method stub

  }

  @Override
  public void onAlertServiceCentreRequest(AlertServiceCentreRequest alertServiceCentreInd) {
    //  Auto-generated method stub

  }

  @Override
  public void onAlertServiceCentreResponse(AlertServiceCentreResponse alertServiceCentreInd) {
    //  Auto-generated method stub

  }

  @Override
  public void onReadyForSMRequest(ReadyForSMRequest request) {
    //  Auto-generated method stub

  }

  @Override
  public void onReadyForSMResponse(ReadyForSMResponse response) {
    //  Auto-generated method stub

  }

  @Override
  public void onNoteSubscriberPresentRequest(NoteSubscriberPresentRequest request) {
    //  Auto-generated method stub

  }
  
}