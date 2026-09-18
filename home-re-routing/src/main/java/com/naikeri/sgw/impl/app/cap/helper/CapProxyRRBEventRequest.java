package com.naikeri.sgw.impl.app.cap.helper;

import com.naikeri.sgw.impl.app.cap.BcsmCallContent;
import com.naikeri.sgw.impl.app.cap.BcsmCallStep;
import com.naikeri.sgw.impl.app.cap.CapDialogOut;
import com.naikeri.sgw.impl.app.cap.CapDialogType;
import com.naikeri.sgw.impl.app.cap.CapProxyHelperUtils;
import com.naikeri.sgw.impl.app.cap.CapDialogOut.WriteLogState;
import com.naikeri.sgw.info.CapTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.RequestReportBCSMEventRequest;

/**
 * CapProxyRRBEventRequest
 */
public class CapProxyRRBEventRequest {

  private static final Logger logger = LoggerFactory.getLogger(CapProxyRRBEventRequest.class);
  private RequestReportBCSMEventRequest request;
  private String channelTransId;

  public CapProxyRRBEventRequest(RequestReportBCSMEventRequest request, String channelTransId) {
    this.request = request;
    this.channelTransId = channelTransId;
  }

  public CapDialogOut process() {
    try {
      Long dialogId = request.getCAPDialog().getLocalDialogId();
      CapDialogOut capDialogOut =
          new CapDialogOut(CapDialogType.CircuitSwitchedCallControl, channelTransId);
      capDialogOut.setTransDialogId(dialogId);
      logger.debug(String.format("[CAP::REQUEST<%s>] dialogId '%d', invokeId '%d'",
          request.getMessageType().toString(), request.getCAPDialog().getLocalDialogId(),
          request.getInvokeId()));
      // get the dialog id from transaction
      BcsmCallContent callContent =
          CapTransaction.instance().getSSFBcsmCallContent(dialogId, false);
      if (callContent == null) {
        // call not found in the store/cache - reject
        logger.trace("Previous store information not found for dialogid = " + dialogId);
        return CapProxyHelperUtils.closeDialog(request.getCAPDialog(), dialogId, channelTransId,
            false);
      }
      // process rrb
      callContent.setRrb(request);
      callContent.setStep(BcsmCallStep.rrbReceived);

      CAPDialogCircuitSwitchedCall rrbDialogOut =
          CapProxyHelperUtils.applyReplaceRulesOnly(request.getMessageType().toString(),
              request.getCAPDialog(), callContent.getCapDialog(), channelTransId, capDialogOut);

      rrbDialogOut.addRequestReportBCSMEventRequest(request.getBCSMEventList(),
          request.getExtensions());
      // update the cap transaction

      capDialogOut.setCapDialogCircuitSwitchedCall(rrbDialogOut);
      capDialogOut.setWriteCDR(WriteLogState.ADDMORE);
      callContent.setStep(BcsmCallStep.rrbSent);

      CapTransaction.instance().updateSSFBcsmCallContent(dialogId, callContent);
      return capDialogOut;
    } catch (CAPException capEx) {
      logger.error("Processing CAP RRB Request failed for " + channelTransId + ". Details: ",
          capEx);
      return CapProxyHelperUtils.discardReason(CapDialogType.CircuitSwitchedCallControl,
          capEx.getMessage(), request.getMessageType().toString(), channelTransId);
    } catch (Exception e) {
      logger.error("Exception caught for " + channelTransId + ". Details: ", e);
      return CapProxyHelperUtils.discardReason(CapDialogType.CircuitSwitchedCallControl,
          e.getMessage(), request.getMessageType().toString(), channelTransId);
    }
  }
}
