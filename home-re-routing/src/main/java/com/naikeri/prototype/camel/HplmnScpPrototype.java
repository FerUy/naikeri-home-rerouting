package com.naikeri.prototype.camel;

import static com.naikeri.sgw.impl.settings.sccp.SccpHelpers.createGlobalTitle0100;
import static com.naikeri.sgw.impl.settings.sccp.SccpHelpers.createLocalAddress;
import static com.naikeri.sgw.impl.settings.sccp.SccpHelpers.createRemoteAddress;
import java.util.ArrayList;
import com.naikeri.sgw.impl.app.cap.BcsmCallContent;
import com.naikeri.sgw.impl.app.cap.BcsmCallStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPDialog;
import org.restcomm.protocols.ss7.cap.api.CAPDialogListener;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPMessage;
import org.restcomm.protocols.ss7.cap.api.CAPParameterFactory;
import org.restcomm.protocols.ss7.cap.api.CAPProvider;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPGeneralAbortReason;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPGprsReferenceNumber;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPNoticeProblemDiagnostic;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPUserAbortReason;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessage;
import org.restcomm.protocols.ss7.cap.api.isup.CalledPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.CauseCap;
import org.restcomm.protocols.ss7.cap.api.isup.GenericNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.LocationNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.OriginalCalledNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.RedirectingPartyIDCap;
import org.restcomm.protocols.ss7.cap.api.primitives.BCSMEvent;
import org.restcomm.protocols.ss7.cap.api.primitives.CAPExtensions;
import org.restcomm.protocols.ss7.cap.api.primitives.EventTypeBCSM;
import org.restcomm.protocols.ss7.cap.api.primitives.MonitorMode;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ActivityTestRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ActivityTestResponse;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ApplyChargingReportRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ApplyChargingRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.AssistRequestInstructionsRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPServiceCircuitSwitchedCallListener;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CallGapRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CallInformationReportRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CallInformationRequestRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CancelRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CollectInformationRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ConnectRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ConnectToResourceRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ContinueRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ContinueWithArgumentRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.DisconnectForwardConnectionRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.DisconnectForwardConnectionWithArgumentRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.DisconnectLegRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.DisconnectLegResponse;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EstablishTemporaryConnectionRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EventReportBCSMRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.FurnishChargingInformationRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.InitialDPRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.InitiateCallAttemptRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.InitiateCallAttemptResponse;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.MoveLegRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.MoveLegResponse;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.PlayAnnouncementRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.PromptAndCollectUserInformationRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.PromptAndCollectUserInformationResponse;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ReleaseCallRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.RequestReportBCSMEventRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ResetTimerRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.SendChargingInformationRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.SpecializedResourceReportRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.SplitLegRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.SplitLegResponse;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.AlertingPatternCap;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.Carrier;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.DestinationRoutingAddress;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.NAOliInfo;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.ServiceInteractionIndicatorsTwo;
import org.restcomm.protocols.ss7.cap.isup.CauseCapImpl;
import org.restcomm.protocols.ss7.cap.isup.DigitsImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.EstablishTemporaryConnectionRequestImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.RequestReportBCSMEventRequestImpl;
import org.restcomm.protocols.ss7.inap.api.isup.CallingPartysCategoryInap;
import org.restcomm.protocols.ss7.inap.api.isup.RedirectionInformationInap;
import org.restcomm.protocols.ss7.inap.api.primitives.LegID;
import org.restcomm.protocols.ss7.inap.api.primitives.LegType;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.CauseIndicatorsImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.GenericNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.CalledPartyNumber;
import org.restcomm.protocols.ss7.isup.message.parameter.CauseIndicators;
import org.restcomm.protocols.ss7.isup.message.parameter.NAINumber;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CUGInterlock;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.asn.comp.PAbortCauseType;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

public class HplmnScpPrototype implements CAPDialogListener, CAPServiceCircuitSwitchedCallListener {

  protected CAPProvider scfCapProvider;
  protected CAPParameterFactory scfCapParameterFactory;
  private GlobalTitle hplmnScpGt, vplmnVlrGt;
  private BcsmCallContent cc;
  // private BcsmCallStep step;
  private final Boolean preArrangedEnd = false;
  private static final Logger logger = LogManager.getLogger(HplmnScpPrototype.class);
  // HPLMN SCP
  private static final int hplmnScpPc = 941;
  private static final int hplmnScpSsn = 146;
  private static final String hplmnScpGtDigits = "97254121030";
  // VPLMN - STP
  private static final int vplmnVlrPc = 1051;
  private static final int vplmnVlrSsn = 7;
  private static final String vplmnVlrGtDigits = "38354121022";

  public HplmnScpPrototype(CAPProvider capProvider, CAPParameterFactory capParameterFactory) {
    this.scfCapProvider = capProvider;
    this.scfCapParameterFactory = capParameterFactory;
    scfCapParameterFactory = scfCapProvider.getCAPParameterFactory();
    scfCapProvider.addCAPDialogListener(this);
    scfCapProvider.getCAPServiceCircuitSwitchedCall().addCAPServiceListener(this);
    this.scfCapProvider.getCAPServiceCircuitSwitchedCall().activate();
  }

  @Override
  public void onInitialDPRequest(InitialDPRequest initialDPRequest) {
    this.cc = new BcsmCallContent();
    this.cc.setStep(BcsmCallStep.idpReceived);
    this.cc.setIdp(initialDPRequest);
    this.cc.setServiceKey(initialDPRequest.getServiceKey());
    logger.debug("InitialDPRequest detected on HPLMN SCP over dialog: {}", initialDPRequest.getCAPDialog());
    this.cc.setCapDialog(initialDPRequest.getCAPDialog());
    SccpAddress idpCalledPartyAddress = initialDPRequest.getCAPDialog().getLocalAddress();
    this.hplmnScpGt = idpCalledPartyAddress.getGlobalTitle();
    logger.debug("InitialDPRequest detected on HPLMN SCP; IDP LOCAL address: {}", idpCalledPartyAddress);
    SccpAddress idpCallingPartyAddress = initialDPRequest.getCAPDialog().getRemoteAddress();
    this.vplmnVlrGt = idpCallingPartyAddress.getGlobalTitle();
    logger.debug("InitialDPRequest detected on HPLMN SCP; IDP REMOTE address: {}", idpCallingPartyAddress);
    logger.debug("scfCurrentCapDialog state = {}", this.cc.getCapDialog().getState());
    sendRRBRequest();
  }

  private void sendRRBRequest() {
    logger.debug("scfPrototype sendRRBRequest(InitialDPRequest initialDPRequest) ");
    logger.debug("scfCurrentCapDialog state = {}", this.cc.getCapDialog().getState());
    // New SCCP Called Party Address to replace proxyScpAddress
    this.vplmnVlrGt = createGlobalTitle0100(vplmnVlrGtDigits, 5);
    this.hplmnScpGt = createGlobalTitle0100(hplmnScpGtDigits);
    SccpAddress hlpmnScpAddress = createLocalAddress(this.hplmnScpGt, hplmnScpPc, hplmnScpSsn);
    SccpAddress vplmnVlrAddress = createRemoteAddress(this.vplmnVlrGt, vplmnVlrPc, vplmnVlrSsn);
    this.cc.getCapDialog().setLocalAddress(hlpmnScpAddress);
    this.cc.getCapDialog().setRemoteAddress(vplmnVlrAddress);
    try {
      // Informing SSF of BCSM events processing
      ArrayList<BCSMEvent> bcsmEventList = new ArrayList<>();
      LegID legId_1 = this.scfCapProvider.getINAPParameterFactory().createLegID(true, LegType.leg1);
      LegID legId_2 = this.scfCapProvider.getINAPParameterFactory().createLegID(true, LegType.leg2);
      /*
       * BCSM Events for receiving side 1 (calling party, #A)
       */
      BCSMEvent leg1_oAbandon = this.scfCapProvider.getCAPParameterFactory().createBCSMEvent(
          EventTypeBCSM.oAbandon, MonitorMode.notifyAndContinue, legId_1, null, false);
      bcsmEventList.add(leg1_oAbandon);
      BCSMEvent leg1_oDisconnect = this.scfCapProvider.getCAPParameterFactory().createBCSMEvent(
          EventTypeBCSM.oDisconnect, MonitorMode.interrupted, legId_1, null, false);
      bcsmEventList.add(leg1_oDisconnect);
      /*
       * BCSM Events for receiving side 2 (called party, #B)
       */
      BCSMEvent leg2_oAnswer = this.scfCapProvider.getCAPParameterFactory().createBCSMEvent(
          EventTypeBCSM.oAnswer, MonitorMode.notifyAndContinue, legId_2, null, false);
      bcsmEventList.add(leg2_oAnswer);
      BCSMEvent leg2_oDisconnect = this.scfCapProvider.getCAPParameterFactory().createBCSMEvent(
          EventTypeBCSM.oDisconnect, MonitorMode.interrupted, legId_2, null, false);
      bcsmEventList.add(leg2_oDisconnect);
      BCSMEvent leg2_routeSelectFailure = this.scfCapProvider.getCAPParameterFactory()
          .createBCSMEvent(EventTypeBCSM.routeSelectFailure, MonitorMode.notifyAndContinue, legId_2,
              null, false);
      bcsmEventList.add(leg2_routeSelectFailure);
      BCSMEvent leg2_oCalledPartyBusy =
          this.scfCapProvider.getCAPParameterFactory().createBCSMEvent(
              EventTypeBCSM.oCalledPartyBusy, MonitorMode.notifyAndContinue, legId_2, null, false);
      bcsmEventList.add(leg2_oCalledPartyBusy);
      BCSMEvent leg2_oNoAnswer = this.scfCapProvider.getCAPParameterFactory().createBCSMEvent(
          EventTypeBCSM.oNoAnswer, MonitorMode.notifyAndContinue, legId_2, null, false);
      bcsmEventList.add(leg2_oNoAnswer);

      logger.debug("RequestReportBCSMEventRequest to be sent to CAP Proxy on dialog: {}, with Calling Party Address={}, Called Party Address={}", this.cc.getCapDialog(), this.cc.getCapDialog().getLocalAddress(), this.cc.getCapDialog().getRemoteAddress());
      // Step 4: send RRB to VPLMN VLR via CAP Proxy from HPLMN SCP
      this.cc.getCapDialog().addRequestReportBCSMEventRequest(30000, bcsmEventList, null);
      this.cc.setRrb(new RequestReportBCSMEventRequestImpl(bcsmEventList, null));
      this.cc.setEventList(bcsmEventList);
      this.cc.getCapDialog().send();
      this.cc.setStep(BcsmCallStep.rrbSent);
      logger.debug("RRB sent from HPLMN SCF to VPLMN SSF over dialog : {}", this.cc.getCapDialog());
      logger.debug("this.cc.capDialog state = {}", this.cc.getCapDialog().getState());

      new Thread(new HPLMNTimer(this)).start();

    } catch (Exception e) {
      logger.error("Caught exception", e);
    }
  }

  private static class HPLMNTimer implements Runnable {

    private final HplmnScpPrototype hplmnScpPrototype;

    public HPLMNTimer(HplmnScpPrototype hplmnScpPrototype) {
      this.hplmnScpPrototype = hplmnScpPrototype;
    }

    @Override
    public void run() {
      try {
        Thread.sleep(100);
      } catch (InterruptedException ie) {
        logger.error("Caught exception", ie);
      }
      try {
        if (hplmnScpPrototype.cc.getCapDialog().getLocalDialogId() % 2 == 1) {
          logger.debug("On HPLMN, CAP dialog is NOT even, about to send CAP ETC/CUE to VPLMN");
          hplmnScpPrototype.sendEstablishTemporaryConnectionAndContinueRequests();
        } else {
          logger.debug("On HPLMN, CAP dialog is even, about to send CAP CON/CUE to VPLMN");
          hplmnScpPrototype.sendConnectRequestAndContinueRequests();
        }
      } catch (CAPException e) {
        logger.error("Caught exception", e);
      }
    }
  }

  private void sendEstablishTemporaryConnectionAndContinueRequests() throws CAPException {
    logger.debug("CAP ETC / CAP CUE to be sent from HPLMN SCP to CAP Proxy, CAP dialog state = {}", this.cc.getCapDialog().getState());
    // We need to check if neither the CAP dialog is already closed nor a disconnect event has been received
    if (this.cc.getCapDialog() != null && this.cc != null
        && this.cc.getStep() != BcsmCallStep.disconnected) {
      // byte[] etcData = new byte[]{48, 18, (byte) 128, 11, 3, 19, 17, 32, 67, 99, 69, 96,
      // (byte) 148, 64, (byte) 167, 3, (byte) 130, 1, 1};
      GenericNumberImpl genericNumber =
          new GenericNumberImpl(3, "97254160047", 1, 1, 0, false, 3);
      DigitsImpl assistingSSPIPRoutingAddress = new DigitsImpl(genericNumber);
      int callSegmentId = 7;
      try {
        this.cc.getCapDialog().addContinueRequest(30000);
        logger.debug("CUE added in HPLMN SCP prototype over dialog : {}", this.cc.getCapDialog());
        this.cc.getCapDialog().addEstablishTemporaryConnectionRequest(30000,
            assistingSSPIPRoutingAddress, null, null, null, null, null, null, null, null, null,
            null);
        logger.debug("CAP ETC added in HPLMN SCP over dialog : {}, ETC assistingSSPIPRoutingAddress: {}", this.cc.getCapDialog(), assistingSSPIPRoutingAddress);
        logger.debug("CAP CUE added in HPLMN SCP over dialog : {}", this.cc.getCapDialog());
        this.cc.getCapDialog().send();
        this.cc.setStep(BcsmCallStep.etcSent);
        this.cc.setEtc(new EstablishTemporaryConnectionRequestImpl(assistingSSPIPRoutingAddress,
            null, null, null, null, null, callSegmentId, null, null, null, null, false));
        logger.debug("ETC and CUE sent from HPLMN SCP prototype to CAP proxy via STP over dialog : {}", this.cc.getCapDialog());
      } catch (CAPException e) {
        logger.error("Caught exception", e);
      }
    } else {
      // else, close the HPLMN CAP dialog via a TC-Close
      try {
        assert this.cc != null;
        if (this.cc.getCapDialog() != null) {
          logger.debug("No CAP dialog on SSF for sending ETC/CUE, about to close the HPLMN CAP dialog via a TC-Close");
          this.cc.getCapDialog().close(preArrangedEnd);
        }
      } catch (CAPException e) {
        logger.error("Caught exception", e);
      }
    }
  }

  private void sendConnectRequestAndContinueRequests() throws CAPException {
    logger.debug("CAP CON / CAP CUE to be sent from HPLMN SCP to CAP Proxy via STP, CAP dialog state = {}", this.cc.getCapDialog().getState());
    if (this.cc.getCapDialog() != null && this.cc != null
        && this.cc.getStep() != BcsmCallStep.disconnected) {
      // byte[] etcData = new byte[]{48, 18, (byte) 128, 11, 3, 19, 17, 32, 67, 99, 69, 96,
      // (byte) 148, 64, (byte) 167, 3, (byte) 130, 1, 1};
      // GenericNumberImpl genericNumber =
      // new GenericNumberImpl(3, "97254160047", 1, 1, 0, false, 3);
      // DigitsImpl assistingSSPIPRoutingAddress = new DigitsImpl(genericNumber);
      // int callSegmentId = 7;
      try {
        this.cc.getCapDialog().addContinueRequest(30000);
        logger.debug("CUE added in HPLMN SCP over dialog : {}", this.cc.getCapDialog());

        ArrayList<CalledPartyNumberCap> calledPartyNumber = new ArrayList<>();
        CalledPartyNumber cpn = this.scfCapProvider.getISUPParameterFactory().createCalledPartyNumber();
        String cpnAddress = "9725457161810";
        cpn.setAddress(cpnAddress);
        cpn.setNatureOfAddresIndicator(NAINumber._NAI_INTERNATIONAL_NUMBER);
        cpn.setNumberingPlanIndicator(CalledPartyNumber._NPI_ISDN);
        cpn.setInternalNetworkNumberIndicator(CalledPartyNumber._INN_ROUTING_ALLOWED);
        CalledPartyNumberCap cpnc = this.scfCapProvider.getCAPParameterFactory().createCalledPartyNumberCap(cpn);
        calledPartyNumber.add(cpnc);
        DestinationRoutingAddress destinationRoutingAddress = this.scfCapProvider.getCAPParameterFactory().createDestinationRoutingAddress(calledPartyNumber);
        AlertingPatternCap alertingPattern = null;
        OriginalCalledNumberCap originalCalledPartyID = null;
        CAPExtensions capExtensions = null;
        Carrier carrier = null;
        CallingPartysCategoryInap callingPartysCategory = null;
        RedirectingPartyIDCap redirectingPartyID = null;
        RedirectionInformationInap redirectionInformation = null;
        ArrayList<GenericNumberCap> genericNumbers = null;
        ServiceInteractionIndicatorsTwo serviceInteractionIndicatorsTwo = null;
        LocationNumberCap locationNumberCap = null;
        LegID legToBeConnected = null;
        CUGInterlock cugInterlock = null;
        boolean cugOutgoingAccess = false;
        boolean suppressionOfAnnouncement = false;
        boolean ocsIApplicable = false;
        NAOliInfo naoliInfo = null;
        boolean borInterrogationRequested = false;
        boolean suppressNCSI = false;

        this.cc.getCapDialog().addConnectRequest(30000, destinationRoutingAddress, alertingPattern,
            originalCalledPartyID, capExtensions, carrier, callingPartysCategory,
            redirectingPartyID, redirectionInformation, genericNumbers,
            serviceInteractionIndicatorsTwo, locationNumberCap, legToBeConnected, cugInterlock,
            cugOutgoingAccess, suppressionOfAnnouncement, ocsIApplicable, naoliInfo,
            borInterrogationRequested, suppressNCSI);
        logger.debug("CAP CON added in HPLMN SCP prototype over dialog : {}, CON destinationRoutingAddress: {}", this.cc.getCapDialog(), destinationRoutingAddress.toString());
        logger.debug("CAP CUE added in HPLMN SCP over dialog : {}", this.cc.getCapDialog());
        this.cc.getCapDialog().send();
        this.cc.setStep(BcsmCallStep.conSent);
        logger.debug("CON and CUE sent from HPLMN SCP to CAP proxy over dialog : {}", this.cc.getCapDialog());
      } catch (CAPException e) {
        logger.error("Caught exception", e);
      }
    } else {
      // else, close the HPLMN CAP dialog via a TC-Close
      try {
        assert this.cc != null;
        if (this.cc.getCapDialog() != null) {
          logger.debug("No CAP dialog on SSF for sending ETC/CUE, about to close the HPLMN CAP dialog via a TC-Close");
          this.cc.getCapDialog().close(preArrangedEnd);
        }
      } catch (CAPException e) {
        logger.error("Caught exception", e);
      }
    }
  }

  @Override
  public void onEventReportBCSMRequest(EventReportBCSMRequest eventReportBCSMRequest) {
    logger.debug("ERB event captured on HPLMN SCP: {}", eventReportBCSMRequest);
    // We need to check if neither the CAP dialog is already closed nor a disconnect event has
    // been received
    if (this.cc != null) {
      if (this.cc.getCapDialog() != null && this.cc.getStep() != BcsmCallStep.disconnected) {
        this.cc.getErbEventList().add(eventReportBCSMRequest);
        logger.debug("ERB oAnswer event captured on CAP proxy, about to relay to SCP {}", eventReportBCSMRequest.getEventTypeBCSM());
        switch (eventReportBCSMRequest.getEventTypeBCSM()) {
          case collectedInfo:
            this.cc.setStep(BcsmCallStep.collectedInfo);
            break;
          case analyzedInformation:
            this.cc.setStep(BcsmCallStep.analizedInformation);
            break;
          case routeSelectFailure:
            this.cc.setStep(BcsmCallStep.routeSelectFailure);
            break;
          case oNoAnswer:
            case tNoAnswer:
                this.cc.setStep(BcsmCallStep.noAnswer);
            break;
          case oAnswer:
            this.cc.setStep(BcsmCallStep.answerReceived);
            // Answer event has been received, thus forward ERB to HPLMN SCP with modified parameters
            logger.debug("ERB oAnswer event captured on HPLMN SCP from GT : {}", cc.getCapDialog().getRemoteAddress().getGlobalTitle().getDigits());
            break;
          case oMidCall:
            case tMidCall:
                this.cc.setStep(BcsmCallStep.midCall);
            break;
          case oDisconnect:
            this.cc.setStep(BcsmCallStep.disconnected);
            // Disconnect event has been received, thus close the HPLMN CAP dialog via a
            // TC-Close
            logger.debug("ERB oDisconnect event captured HPLMN SCP");
            break;
          case oAbandon:
            this.cc.setStep(BcsmCallStep.abandoned);
            // Abandon event has been received
            logger.debug("ERB oAbandon event captured on HPLMN SCP");
            break;
          case tBusy:
            this.cc.setStep(BcsmCallStep.busy);
            break;
          case tAnswer:
            this.cc.setStep(BcsmCallStep.answerReceived);
            // Answer event has been received
            logger.debug("ERB tAnswer event captured on HPLMN SCP, processing without answer");
            break;
          case tDisconnect:
            this.cc.setStep(BcsmCallStep.disconnected);
            break;
          case tAbandon:
            this.cc.setStep(BcsmCallStep.abandoned);
            logger.debug("ERB tAbandon event captured on CAP proxy, about to close the HPLMN CAP dialog via a TC-Close");
            break;
          case oTermSeized:
            this.cc.setStep(BcsmCallStep.termSeized);
            break;
          case callAccepted:
            this.cc.setStep(BcsmCallStep.callAccepted);
            logger
                .debug("ERB call accepted event captured on HPLMN SCP, processing without answer");
            break;
          case oChangeOfPosition:
            this.cc.setStep(BcsmCallStep.changeOfPosition);
            break;
          case oServiceChange:
          case tServiceChange:
                this.cc.setStep(BcsmCallStep.serviceChange);
          break;
          default:
            this.cc.setStep(BcsmCallStep.erbReceived);
            break;
        }
      } else {
        // else, close the HPLMN CAP dialog via a TC-Close
        try {
          if (this.cc.getCapDialog() != null)
            this.cc.getCapDialog().close(preArrangedEnd);
        } catch (CAPException e) {
          logger.error("Caught exception", e);
        }
      }
    }
  }

  @Override
  public void onDialogDelimiter(CAPDialog capDialog) {
    logger.info("*** onDialogDelimiter at HPLMN SCP Prototype:\n\t\t*** capDialog={}\n\t\t*** TCAP message type = {}\n\t\t*** Local Address: GT = {}, PC = {}, SSN = {}\n\t\t*** Remote Address: GT = {}, PC = {} SSN = {}\n\t\t****************************", capDialog, capDialog.getTCAPMessageType(), capDialog.getLocalAddress().getGlobalTitle(), capDialog.getLocalAddress().getSignalingPointCode(), capDialog.getLocalAddress().getSubsystemNumber(), capDialog.getRemoteAddress().getGlobalTitle(), capDialog.getRemoteAddress().getSignalingPointCode(), capDialog.getRemoteAddress().getSubsystemNumber());
    try {
      if (this.cc != null) {
        if (this.cc.getStep() != null) {
          switch (this.cc.getStep()) {
            case idpReceived:
              logger.info("HPLMN SCF onDialogDelimiter case: initialDPReceived");
              break;
            case rrbSent:
              logger.info("HPLMN SCF onDialogDelimiter case: rrbSent");
              break;
            case answerReceived:
              logger.info("HPLMN SCF onDialogDelimiter case: answered");
              this.cc.getCapDialog().keepAlive();
              break;
            case disconnected:
              logger.info("HPLMN SCF onDialogDelimiter case: disconnected");
              try {
                if (this.cc.getCapDialog() != null) {
                  CauseIndicators causeIndicators = new CauseIndicatorsImpl(0, 10, 0,
                      CauseIndicators._CV_NORMAL_UNSPECIFIED, null);
                  CauseCap causeCap = new CauseCapImpl(causeIndicators);
                  this.cc.getCapDialog().addReleaseCallRequest(30000, causeCap);
                  this.cc.getCapDialog().close(preArrangedEnd);
                }
              } catch (CAPException e) {
                logger.error("Caught exception", e);
              }
              break;
            default:
              break;
          }
        }
      }

    } catch (Exception e) {
      logger.error("Caught exception", e);
    }
  }

  @Override
  public void onDialogTimeout(CAPDialog capDialog) {
    logger.debug("onDialogTimeout captured on HPLMN SCP prototype, dialog : {}", capDialog);
  }

  @Override
  public void onActivityTestResponse(ActivityTestResponse activityTestResponse) {
    logger.debug("onActivityTestResponse captured on HPLMN SCP prototype, dialog : {}", activityTestResponse);
  }

  @Override
  public void onDialogRequest(CAPDialog capDialog, CAPGprsReferenceNumber capGprsReferenceNumber) {
    logger.debug("onDialogRequest captured on HPLMN SCP prototype, CAP dialog : {}; CAPGprsReferenceNumber : {}", capDialog, capGprsReferenceNumber);
  }

  @Override
  public void onDialogAccept(CAPDialog capDialog, CAPGprsReferenceNumber capGprsReferenceNumber) {
    logger.debug("onDialogAccept captured on HPLMN SCP prototype, CAP dialog : {}; CAPGprsReferenceNumber : {}", capDialog, capGprsReferenceNumber);
  }

  @Override
  public void onDialogUserAbort(CAPDialog capDialog, CAPGeneralAbortReason capGeneralAbortReason, CAPUserAbortReason capUserAbortReason) {
    logger.debug("onDialogAccept captured on HPLMN SCP prototype, CAP dialog : {}; CAPGeneralAbortReason : {}, CAPUserAbortReason : {}", capDialog, capGeneralAbortReason, capUserAbortReason);
  }

  @Override
  public void onDialogProviderAbort(CAPDialog capDialog, PAbortCauseType pAbortCauseType) {
    logger.debug("onDialogProviderAbort captured on HPLMN SCP prototype, CAP dialog : {}; PAbortCauseType : {}", capDialog, pAbortCauseType);
  }

  @Override
  public void onDialogClose(CAPDialog capDialog) {
    logger.debug("onDialogClose captured on HPLMN SCP prototype, dialog : {}", capDialog);
  }

  @Override
  public void onDialogRelease(CAPDialog capDialog) {
    logger.debug("onDialogRelease captured on HPLMN SCP prototype, dialog : {}", capDialog);
  }

  @Override
  public void onDialogNotice(CAPDialog capDialog, CAPNoticeProblemDiagnostic capNoticeProblemDiagnostic) {
    logger.debug("onDialogNotice captured on HPLMN SCP prototype, dialog : {}; CAPNoticeProblemDiagnostic : {}", capDialog, capNoticeProblemDiagnostic);
  }

  @Override
  public void onRequestReportBCSMEventRequest(RequestReportBCSMEventRequest requestReportBCSMEventRequest) {
    logger.debug("onRequestReportBCSMEventRequest captured on HPLMN SCP prototype, RequestReportBCSMEventRequest : {}", requestReportBCSMEventRequest);
  }

  @Override
  public void onApplyChargingRequest(ApplyChargingRequest applyChargingRequest) {
    logger.debug("onApplyChargingRequest captured on HPLMN SCP prototype, ApplyChargingRequest : {}", applyChargingRequest);
  }

  @Override
  public void onContinueRequest(ContinueRequest continueRequest) {
    logger.debug("onContinueRequest captured on HPLMN SCP prototype, ContinueRequest : {}", continueRequest);
  }

  @Override
  public void onContinueWithArgumentRequest(ContinueWithArgumentRequest continueWithArgumentRequest) {
    logger.debug("onContinueWithArgumentRequest captured on HPLMN SCP prototype, ContinueWithArgumentRequest : {}", continueWithArgumentRequest);
  }

  @Override
  public void onApplyChargingReportRequest(ApplyChargingReportRequest applyChargingReportRequest) {
    logger.debug("onApplyChargingReportRequest captured on HPLMN SCP prototype, ApplyChargingReportRequest : {}", applyChargingReportRequest);
  }

  @Override
  public void onReleaseCallRequest(ReleaseCallRequest releaseCallRequest) {

  }

  @Override
  public void onConnectRequest(ConnectRequest connectRequest) {

  }

  @Override
  public void onCallInformationRequestRequest(CallInformationRequestRequest callInformationRequestRequest) {

  }

  @Override
  public void onCallInformationReportRequest(CallInformationReportRequest callInformationReportRequest) {

  }

  @Override
  public void onActivityTestRequest(ActivityTestRequest activityTestRequest) {

  }

  @Override
  public void onAssistRequestInstructionsRequest(AssistRequestInstructionsRequest assistRequestInstructionsRequest) {

  }

  @Override
  public void onEstablishTemporaryConnectionRequest(EstablishTemporaryConnectionRequest establishTemporaryConnectionRequest) {

  }

  @Override
  public void onDisconnectForwardConnectionRequest(DisconnectForwardConnectionRequest disconnectForwardConnectionRequest) {

  }

  @Override
  public void onDisconnectLegRequest(DisconnectLegRequest disconnectLegRequest) {

  }

  @Override
  public void onDisconnectLegResponse(DisconnectLegResponse disconnectLegResponse) {

  }

  @Override
  public void onDisconnectForwardConnectionWithArgumentRequest(DisconnectForwardConnectionWithArgumentRequest disconnectForwardConnectionWithArgumentRequest) {

  }

  @Override
  public void onConnectToResourceRequest(ConnectToResourceRequest connectToResourceRequest) {

  }

  @Override
  public void onResetTimerRequest(ResetTimerRequest resetTimerRequest) {

  }

  @Override
  public void onFurnishChargingInformationRequest(FurnishChargingInformationRequest furnishChargingInformationRequest) {

  }

  @Override
  public void onSendChargingInformationRequest(SendChargingInformationRequest sendChargingInformationRequest) {

  }

  @Override
  public void onSpecializedResourceReportRequest(SpecializedResourceReportRequest specializedResourceReportRequest) {

  }

  @Override
  public void onPlayAnnouncementRequest(PlayAnnouncementRequest playAnnouncementRequest) {

  }

  @Override
  public void onPromptAndCollectUserInformationRequest(PromptAndCollectUserInformationRequest promptAndCollectUserInformationRequest) {

  }

  @Override
  public void onPromptAndCollectUserInformationResponse(PromptAndCollectUserInformationResponse promptAndCollectUserInformationResponse) {

  }

  @Override
  public void onCancelRequest(CancelRequest cancelRequest) {

  }

  @Override
  public void onInitiateCallAttemptRequest(InitiateCallAttemptRequest initiateCallAttemptRequest) {

  }

  @Override
  public void onInitiateCallAttemptResponse(InitiateCallAttemptResponse initiateCallAttemptResponse) {

  }

  @Override
  public void onMoveLegRequest(MoveLegRequest moveLegRequest) {

  }

  @Override
  public void onMoveLegResponse(MoveLegResponse moveLegResponse) {

  }

  @Override
  public void onCollectInformationRequest(CollectInformationRequest collectInformationRequest) {

  }

  @Override
  public void onSplitLegRequest(SplitLegRequest splitLegRequest) {

  }

  @Override
  public void onSplitLegResponse(SplitLegResponse splitLegResponse) {

  }

  @Override
  public void onCallGapRequest(CallGapRequest callGapRequest) {

  }

  @Override
  public void onErrorComponent(CAPDialog capDialog, Long aLong, CAPErrorMessage capErrorMessage) {
    logger.debug("onErrorComponent captured on HPLMN SCP prototype, dialog : {}, CAPErrorMessage : {}", capDialog, capErrorMessage);

  }

  @Override
  public void onRejectComponent(CAPDialog capDialog, Long aLong, Problem problem, boolean b) {
    logger.debug("onRejectComponent captured on HPLMN SCP prototype, dialog : {}, Problem : {}", capDialog, problem);
  }

  @Override
  public void onInvokeTimeout(CAPDialog capDialog, Long aLong) {
    logger.debug("onInvokeTimeout captured on HPLMN SCP prototype, dialog : {}", capDialog);
    logger.debug("onInvokeTimeout captured on HPLMN SCP prototype, invokeId : {}", aLong);
    logger.debug("onInvokeTimeout on HPLMN SCF. SCF Call Content params: Step = {}", this.cc.getStep());
  }

  @Override
  public void onCAPMessage(CAPMessage capMessage) {
    logger.debug("onCAPMessage captured on HPLMN SCP prototype, CAP message : {}", capMessage);
  }
}

