package com.naikeri.prototype.camel;

import static com.naikeri.sgw.api.settings.TBCDUtil.parseTBCD;
import static com.naikeri.sgw.impl.settings.sccp.SccpHelpers.createGlobalTitle0100;
import static com.naikeri.sgw.impl.settings.sccp.SccpHelpers.createLocalAddress;
import static com.naikeri.sgw.impl.settings.sccp.SccpHelpers.createRemoteAddress;
import java.util.ArrayList;
import java.util.Objects;

import com.naikeri.sgw.impl.app.cap.BcsmCallContent;
import com.naikeri.sgw.impl.app.cap.BcsmCallStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPApplicationContext;
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
import org.restcomm.protocols.ss7.cap.api.isup.CallingPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.CauseCap;
import org.restcomm.protocols.ss7.cap.api.isup.Digits;
import org.restcomm.protocols.ss7.cap.api.isup.LocationNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.OriginalCalledNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.RedirectingPartyIDCap;
import org.restcomm.protocols.ss7.cap.api.primitives.CAPExtensions;
import org.restcomm.protocols.ss7.cap.api.primitives.CalledPartyBCDNumber;
import org.restcomm.protocols.ss7.cap.api.primitives.EventTypeBCSM;
import org.restcomm.protocols.ss7.cap.api.primitives.ReceivingSideID;
import org.restcomm.protocols.ss7.cap.api.primitives.ScfID;
import org.restcomm.protocols.ss7.cap.api.primitives.TimeAndTimezone;
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
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.BearerCapability;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.CGEncountered;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.Carrier;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.EventSpecificInformationBCSM;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.IPSSPCapabilities;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.InitialDPArgExtension;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.NAOliInfo;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.ServiceInteractionIndicatorsTwo;
import org.restcomm.protocols.ss7.cap.isup.CallingPartyNumberCapImpl;
import org.restcomm.protocols.ss7.cap.primitives.CalledPartyBCDNumberImpl;
import org.restcomm.protocols.ss7.cap.primitives.TimeAndTimezoneImpl;
import org.restcomm.protocols.ss7.inap.api.isup.CallingPartysCategoryInap;
import org.restcomm.protocols.ss7.inap.api.isup.HighLayerCompatibilityInap;
import org.restcomm.protocols.ss7.inap.api.isup.RedirectionInformationInap;
import org.restcomm.protocols.ss7.inap.api.primitives.LegType;
import org.restcomm.protocols.ss7.inap.api.primitives.MiscCallInfo;
import org.restcomm.protocols.ss7.inap.api.primitives.MiscCallInfoDpAssignment;
import org.restcomm.protocols.ss7.inap.api.primitives.MiscCallInfoMessageType;
import org.restcomm.protocols.ss7.inap.primitives.MiscCallInfoImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.CallingPartyNumberImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.LocationNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.CallingPartyNumber;
import org.restcomm.protocols.ss7.isup.message.parameter.LocationNumber;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.service.callhandling.CallReferenceNumber;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CUGIndex;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CUGInterlock;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtTeleserviceCode;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdFixedLengthImpl;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdOrLAIImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.LocationInformationImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtBasicServiceCodeImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtTeleserviceCodeImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.asn.comp.PAbortCauseType;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

public class VplmnStpPrototype implements CAPDialogListener, CAPServiceCircuitSwitchedCallListener {

  protected CAPProvider ssfCapProvider, scfCapProvider, ssfForHplmnCapProvider;
  protected CAPParameterFactory ssfCapParameterFactory, scfCapParameterFactory,
      ssfForHplmnCapParameterFactory;
  private GlobalTitle vplmnVlrGt, vplmnVlrGt_tt5, proxyScpGt, hplmnScpGt, partnerMscGt;
  private BcsmCallContent leg1ScfCallContent, leg1SsfCallContent, hplmnCallContent,
      leg2SsfCallContent;
  private BcsmCallStep step;
  private final Boolean preArrangedEnd = false;
  // these should be retrieved from configuration
  // VPLMN - STP
  private static final int stpSsfPc = 82;
  private static final int stpScfPc = 1050;
  private static final int stpSsfHplmnPc = 1051;
  private static final int vplmnVlrSsn = 7;
  private static final String vplmnGtDigits = "97254121022";
  private static final String vplmnGtDigitsForHplmn = "38354121022";
  // CAP Proxy
  private static final int proxySsfPc = 948;
  private static final int proxyScfPc = 947;
  private static final int proxySsn = 146;
  private static final String proxyGtDigits = "97254160047";
  // HPLMN SCP
  private static final int hplmnScpPc = 941;
  private static final int hplmnScpSsn = 146;
  private static final String hplmnScpGtDigits = "97254121030";
  // MSC (leg2)
  private static final String partnerMscGtDigits = "97254121021";
  private static IMSI imsi;
  private static final Logger logger = LogManager.getLogger(VplmnStpPrototype.class);

  public VplmnStpPrototype(CAPProvider ssfCapProvider, CAPParameterFactory ssfCapParameterFactory,
      CAPProvider scfCapProvider, CAPParameterFactory scfCapParameterFactory,
      CAPProvider ssfForHplmnCapProvider, CAPParameterFactory ssfForHplmnCapParameterFactory) {
    // VPLMN STP SSF - CAP Proxy SCF
    this.ssfCapProvider = ssfCapProvider;
    this.ssfCapParameterFactory = ssfCapParameterFactory;
    this.ssfCapProvider.addCAPDialogListener(this);
    this.ssfCapProvider.getCAPServiceCircuitSwitchedCall().addCAPServiceListener(this);
    this.ssfCapProvider.getCAPServiceCircuitSwitchedCall().activate();
    // VPLMN STP SCF - CAP Proxy SSF
    this.scfCapProvider = scfCapProvider;
    this.scfCapParameterFactory = scfCapParameterFactory;
    this.scfCapProvider.addCAPDialogListener(this);
    this.scfCapProvider.getCAPServiceCircuitSwitchedCall().addCAPServiceListener(this);
    this.scfCapProvider.getCAPServiceCircuitSwitchedCall().activate();
    // VPLMN STP SSF - CAP Proxy SCF
    this.ssfForHplmnCapProvider = ssfForHplmnCapProvider;
    this.ssfForHplmnCapParameterFactory = ssfForHplmnCapParameterFactory;
    this.ssfForHplmnCapProvider.addCAPDialogListener(this);
    this.ssfForHplmnCapProvider.getCAPServiceCircuitSwitchedCall().addCAPServiceListener(this);
    this.ssfForHplmnCapProvider.getCAPServiceCircuitSwitchedCall().activate();
  }

  public void sendInitialDPRequest() throws CAPException, MAPException {
    this.leg1SsfCallContent = new BcsmCallContent();
    logger.debug("Call flow start, about to send CAP IDP from VPLMN to CAP Proxy");
    // Create SCCP addresses
    this.proxyScpGt = createGlobalTitle0100(proxyGtDigits);
    this.vplmnVlrGt = createGlobalTitle0100(vplmnGtDigits);
    SccpAddress idpCallingPartyAddress = createLocalAddress(this.vplmnVlrGt, stpSsfPc, vplmnVlrSsn);
    SccpAddress idpCalledPartyAddress = createRemoteAddress(this.proxyScpGt, proxyScfPc, proxySsn);
    // set IMSI
    imsi = new IMSIImpl("425100702000128");
    // Create VPLMN STP SSF - Proxy SCF CAMEL Dialog
    CAPApplicationContext acn = CAPApplicationContext.CapV2_gsmSSF_to_gsmSCF;
    this.leg1SsfCallContent.setCapDialog(ssfCapProvider.getCAPServiceCircuitSwitchedCall()
        .createNewDialog(acn, idpCallingPartyAddress, idpCalledPartyAddress));

    // Parameters obtained from provided Wireshark trace
    int serviceKey = 484;
    CallingPartyNumber callingPartyNumber =
        new CallingPartyNumberImpl(4, "237666020684", 1, 0, 0, 3);
    CallingPartyNumberCap callingPartyNumberCap = new CallingPartyNumberCapImpl(callingPartyNumber);
    CalledPartyBCDNumberImpl calledPartyBCDNumber = new CalledPartyBCDNumberImpl(
        AddressNature.international_number, NumberingPlan.ISDN, "972547818172");
    int natureOfAddressIndicator = 3;
    String locationNumberAddressDigits = "54120022";
    int numberingPlanIndicator = 1;
    int internalNetworkNumberIndicator = 1;
    int addressRepresentationRestrictedIndicator = 0;
    int screeningIndicator = 3;
    LocationNumber locationNumber = new LocationNumberImpl(natureOfAddressIndicator,
        locationNumberAddressDigits, numberingPlanIndicator, internalNetworkNumberIndicator,
        addressRepresentationRestrictedIndicator, screeningIndicator);
    logger.debug("locationNumber: {}", locationNumber);
    ISDNAddressString vlrNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, vplmnGtDigits);
    ISDNAddressString mscNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, vplmnGtDigits);
    int ageOfLocationInformation = 1;
    CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength =
        new CellGlobalIdOrServiceAreaIdFixedLengthImpl(425, 1, 5206, 64043);
    CellGlobalIdOrServiceAreaIdOrLAI cgiOrSaiOrLai =
        new CellGlobalIdOrServiceAreaIdOrLAIImpl(cellGlobalIdOrServiceAreaIdFixedLength);
    LocationInformation locationInformation =
        new LocationInformationImpl(ageOfLocationInformation, null, vlrNumber, null, cgiOrSaiOrLai,
            null, null, mscNumber, null, false, false, null, null);
    byte[] extTeleServiceCode = new byte[] {0x11};
    ExtTeleserviceCode extTeleserviceCode = new ExtTeleserviceCodeImpl(extTeleServiceCode);
    ExtBasicServiceCode extBasicServiceCode = new ExtBasicServiceCodeImpl(extTeleserviceCode);
    byte[] timeAndTimezoneByteArray = parseTBCD("0291015041340100");
    TimeAndTimezone timeAndTimezone = new TimeAndTimezoneImpl(timeAndTimezoneByteArray);

    // Step 0: start call by sending IDP from VPLMN VLR to CAP Proxy
    this.leg1SsfCallContent.getCapDialog().addInitialDPRequest(30000, serviceKey, null,
        callingPartyNumberCap, null, null, null, null, null, null, null, null, null,
        EventTypeBCSM.collectedInfo, null, null, null, null, null, null, null, false, imsi, null,
        locationInformation, extBasicServiceCode, null, null, calledPartyBCDNumber, timeAndTimezone,
        false, null);
    logger.debug("CAP IDP added to SSF-Proxy dialog");
    this.leg1SsfCallContent.getCapDialog().send();
    this.leg1SsfCallContent.setStep(BcsmCallStep.idpSent);
    logger.debug("CAP IDP sent from VPLMN SSF to CAP Proxy");
  }

  @Override
  public void onInitialDPRequest(InitialDPRequest idp) {
    this.leg1ScfCallContent = new BcsmCallContent();
    this.leg1ScfCallContent.setCapDialog(idp.getCAPDialog());
    logger.debug("CAP IDP from CAP Proxy SCF received on STP SCF");
    this.leg1ScfCallContent.setStep(BcsmCallStep.idpReceived);
    this.leg1ScfCallContent.setIdp(idp);
    logger.debug("IDP Called Party Number = {}", this.leg1ScfCallContent.getIdp().getCalledPartyBCDNumber());
    relayInitialDPRequestToHplmnScp(idp);
  }

  protected void relayInitialDPRequestToHplmnScp(InitialDPRequest initialDPRequest) {
    logger.debug("CAP IDP to be relayed to HPLMN SCP via STP SSF");
    this.hplmnCallContent = new BcsmCallContent();
    this.vplmnVlrGt = createGlobalTitle0100(vplmnGtDigitsForHplmn);
    this.hplmnScpGt = createGlobalTitle0100(hplmnScpGtDigits);
    SccpAddress idpCallingPartyAddress = createLocalAddress(this.vplmnVlrGt, stpSsfHplmnPc, vplmnVlrSsn);
    SccpAddress idpCalledPartyAddress = createRemoteAddress(this.hplmnScpGt, hplmnScpPc, hplmnScpSsn);

    try {
      // Create SSF - SCP CAP Dialog
      CAPApplicationContext acn = CAPApplicationContext.CapV2_gsmSSF_to_gsmSCF;
      this.hplmnCallContent.setCapDialog(ssfForHplmnCapProvider.getCAPServiceCircuitSwitchedCall()
          .createNewDialog(acn, idpCallingPartyAddress, idpCalledPartyAddress));

      int serviceKey = initialDPRequest.getServiceKey();
      CalledPartyNumberCap calledPartyNumberCap = initialDPRequest.getCalledPartyNumber();
      CallingPartyNumberCap callingPartyNumberCap = initialDPRequest.getCallingPartyNumber();
      CallingPartysCategoryInap callingPartysCategoryInap = initialDPRequest.getCallingPartysCategory();
      CGEncountered cgEncountered = initialDPRequest.getCGEncountered();
      IPSSPCapabilities ipsspCapabilities = initialDPRequest.getIPSSPCapabilities();
      LocationNumberCap locationNumberCap = initialDPRequest.getLocationNumber();
      OriginalCalledNumberCap originalCalledNumberCap = initialDPRequest.getOriginalCalledPartyID();
      CAPExtensions capExtensions = initialDPRequest.getExtensions();
      HighLayerCompatibilityInap highLayerCompatibilityInap = initialDPRequest.getHighLayerCompatibility();
      Digits additionalCallingPartyNumber = initialDPRequest.getAdditionalCallingPartyNumber();
      BearerCapability bearerCapability = initialDPRequest.getBearerCapability();
      EventTypeBCSM eventTypeBCSM = initialDPRequest.getEventTypeBCSM();
      RedirectingPartyIDCap redirectingPartyIDCap = initialDPRequest.getRedirectingPartyID();
      RedirectionInformationInap redirectionInformationInap = initialDPRequest.getRedirectionInformation();
      CauseCap causeCap = initialDPRequest.getCause();
      ServiceInteractionIndicatorsTwo serviceInteractionIndicatorsTwo = initialDPRequest.getServiceInteractionIndicatorsTwo();
      Carrier carrier = initialDPRequest.getCarrier();
      CUGIndex cugIndex = initialDPRequest.getCugIndex();
      CUGInterlock cugInterlock = initialDPRequest.getCugInterlock();
      boolean cugOutgoingAccess = initialDPRequest.getCugOutgoingAccess();
      IMSI imsi = initialDPRequest.getIMSI();
      SubscriberState subscriberState = initialDPRequest.getSubscriberState();
      LocationInformation locationInformation = initialDPRequest.getLocationInformation();
      ExtBasicServiceCode extBasicServiceCode = initialDPRequest.getExtBasicServiceCode();
      CallReferenceNumber callReferenceNumber = initialDPRequest.getCallReferenceNumber();
      ISDNAddressString mscAddress = initialDPRequest.getMscAddress();
      CalledPartyBCDNumber calledPartyBCDNumber = initialDPRequest.getCalledPartyBCDNumber();
      TimeAndTimezone timeAndTimezone = initialDPRequest.getTimeAndTimezone();
      boolean callForwardingSSPending = initialDPRequest.getCallForwardingSSPending();
      InitialDPArgExtension initialDPArgExtension = initialDPRequest.getInitialDPArgExtension();

      this.hplmnCallContent.getCapDialog().addInitialDPRequest(30000, serviceKey,
          calledPartyNumberCap, callingPartyNumberCap, callingPartysCategoryInap, cgEncountered,
          ipsspCapabilities, locationNumberCap, originalCalledNumberCap, capExtensions,
          highLayerCompatibilityInap, additionalCallingPartyNumber, bearerCapability, eventTypeBCSM,
          redirectingPartyIDCap, redirectionInformationInap, causeCap,
          serviceInteractionIndicatorsTwo, carrier, cugIndex, cugInterlock, cugOutgoingAccess, imsi,
          subscriberState, locationInformation, extBasicServiceCode, callReferenceNumber,
          mscAddress, calledPartyBCDNumber, timeAndTimezone, callForwardingSSPending,
          initialDPArgExtension);
      logger.debug("CAP IDP added to SSF-SCF dialog");
      this.hplmnCallContent.getCapDialog().send();
      this.hplmnCallContent.setStep(BcsmCallStep.idpSent);
      logger.debug("IDP sent from STP SSF to HPLMN SCP over dialog : {}", this.hplmnCallContent.getCapDialog());
      logger.debug("scfCurrentCapDialog state = {}", this.hplmnCallContent.getCapDialog().getState());
    } catch (CAPException e) {
      logger.error("Error: ", e);
    }
  }

  @Override
  public void onRequestReportBCSMEventRequest(RequestReportBCSMEventRequest rrb) {
    logger.info("CAP RRB event detected at VPLMN STP SSF");
    if (this.leg1SsfCallContent != null) {
      if (leg1SsfCallContent.getStep() == BcsmCallStep.idpSent) {
        // We need to check if the CAP dialog is neither closed nor a disconnect event has been received
        if (this.leg1SsfCallContent.getCapDialog() != null && this.leg1SsfCallContent.getStep() != BcsmCallStep.disconnected) {
          this.leg1SsfCallContent.setStep(BcsmCallStep.rrbReceived);
          this.leg1SsfCallContent.setRrb(rrb);
          logger.debug("CAP RRB received from HPLMN SCF : {} over dialog : {}", this.leg1SsfCallContent.getRrb(), this.leg1SsfCallContent.getCapDialog());
          return;
        } else {
          // terminate CAMEL dialog
          logger.debug("CAP RRB event at VPLMN STP for null dialog or disconnected call; sending TC-Close to release resources");
          try {
            if (this.leg1SsfCallContent.getCapDialog() != null) {
              this.leg1SsfCallContent.getCapDialog().close(preArrangedEnd);
              this.leg1SsfCallContent.setStep(BcsmCallStep.closed);
              return;
            }
          } catch (CAPException e) {
            logger.error("Error: ", e);
          }
        }
      } else if (leg1SsfCallContent.getStep() == BcsmCallStep.rrbReceived) {
        // We need to check if the CAP dialog is neither closed nor a disconnect event has
        // been received
        if (this.leg1SsfCallContent.getCapDialog() != null && this.leg1SsfCallContent.getStep() != BcsmCallStep.disconnected) {
          this.leg1SsfCallContent.setStep(BcsmCallStep.rrbReceived);
          this.leg1SsfCallContent.setRrb(rrb);
          return;
        } else {
          // terminate CAMEL dialog
          logger.debug("CAP RRB event at VPLMN STP for null dialog or disconnected call; sending TC-Close to release resources");
          try {
            if (this.leg1SsfCallContent.getCapDialog() != null) {
              this.leg1SsfCallContent.getCapDialog().close(preArrangedEnd);
              this.leg1SsfCallContent.setStep(BcsmCallStep.closed);
              return;
            }
          } catch (CAPException e) {
            logger.error("Error: ", e);
          }
        }
      }
    }
    if (this.leg2SsfCallContent != null) {
      assert leg1SsfCallContent != null;
      if (leg1SsfCallContent.getStep() == BcsmCallStep.conReceived) {
        if (this.leg2SsfCallContent.getCapDialog() != null
            && this.leg2SsfCallContent.getStep() != BcsmCallStep.idpReceived) {
          this.leg2SsfCallContent.setRrb(rrb);
          this.leg2SsfCallContent.setStep(BcsmCallStep.rrbReceived);
          logger.debug("CAP RRB received on LEG2 from CAP Proxy SCF : {} over dialog : {}", this.leg2SsfCallContent.getRrb(), this.leg2SsfCallContent.getCapDialog());
          new Thread(new VPLMNTimer(this)).start();
        }
      }
    }
  }

  public void relayRRBRequestToProxySsf(RequestReportBCSMEventRequest requestReportBCSMEventRequest) {
    logger.debug("RRB to be relayed to CAP Proxy SSF : {}", requestReportBCSMEventRequest);
    if (this.leg1ScfCallContent != null) {
      // We need to check if the CAP dialog is neither closed nor a disconnect event has been
      // received
      if (this.leg1ScfCallContent.getCapDialog() != null
          && this.leg1ScfCallContent.getStep() != BcsmCallStep.disconnected) {
        this.leg1ScfCallContent.setStep(BcsmCallStep.rrbReceived);
        this.leg1ScfCallContent.setRrb(requestReportBCSMEventRequest);

        if (Objects.equals(requestReportBCSMEventRequest.getCAPDialog().getLocalAddress().getGlobalTitle().getDigits(), vplmnGtDigitsForHplmn)) {
          try {
            this.vplmnVlrGt_tt5 = createGlobalTitle0100(vplmnGtDigitsForHplmn, 5);
            SccpAddress rrbCallingPartyAddress = createLocalAddress(this.hplmnScpGt, stpScfPc, hplmnScpSsn);
            SccpAddress rrbCalledPartyAddress = createRemoteAddress(this.vplmnVlrGt_tt5, proxySsfPc, proxySsn);
            this.leg1ScfCallContent.getCapDialog().setLocalAddress(rrbCallingPartyAddress);
            this.leg1ScfCallContent.getCapDialog().setRemoteAddress(rrbCalledPartyAddress);
            this.leg1ScfCallContent.getCapDialog().addRequestReportBCSMEventRequest(30000,
                this.leg1SsfCallContent.getRrb().getBCSMEventList(),
                this.leg1SsfCallContent.getRrb().getExtensions());
            this.leg1ScfCallContent.getCapDialog().send();
            this.leg1ScfCallContent.setStep(BcsmCallStep.rrbSent);
            logger.debug("RRB sent from VPLMN SSF to CAP Proxy SSF over dialog : {}", this.leg1ScfCallContent.getCapDialog());
            logger.debug("scfCurrentCapDialog state = {}", this.leg1ScfCallContent.getCapDialog().getState());
          } catch (CAPException e) {
            logger.error("Caught exception", e);
          }
        } else {
          long invokeId = requestReportBCSMEventRequest.getInvokeId();
          this.leg1ScfCallContent.setCapDialog(requestReportBCSMEventRequest.getCAPDialog());
          logger.debug("RRB received from VPLMN SSF on dialog: {}, with invokeID={}", this.leg1ScfCallContent.getCapDialog(), invokeId);
        }
      } else {
        // terminate CAMEL dialog
        try {
          if (this.leg1ScfCallContent.getCapDialog() != null) {
            this.leg1ScfCallContent.getCapDialog().close(preArrangedEnd);
            this.leg1ScfCallContent.setStep(BcsmCallStep.closed);
          }
        } catch (CAPException e) {
          logger.error("Error: ", e);
        }
      }
    }
  }

  @Override
  public void onEstablishTemporaryConnectionRequest(EstablishTemporaryConnectionRequest etc) {
    logger.debug("CAP ETC received from HPLMN SCF : {}", etc);
    if (this.leg1SsfCallContent != null) {
      if (leg1SsfCallContent.getStep() == BcsmCallStep.cueReceived
          || leg1SsfCallContent.getStep() == BcsmCallStep.rrbReceived
          || leg1SsfCallContent.getStep() == BcsmCallStep.idpSent) {
        // We need to check if the CAP dialog is neither closed nor a disconnect event has
        // been received
        if (this.leg1SsfCallContent.getCapDialog() != null
            && this.leg1SsfCallContent.getStep() != BcsmCallStep.disconnected) {
          this.leg1SsfCallContent.setStep(BcsmCallStep.etcReceived);
          this.leg1SsfCallContent.setEtc(etc);
          logger.debug("CAP ETC received from HPLMN SCF : {} over dialog : {}", this.leg1SsfCallContent.getEtc(), this.leg1SsfCallContent.getCapDialog());
        } else {
          // terminate the CAMEL dialog
          logger.debug("CAP ETC event at VPLMN STP for null dialog or disconnected call; sending TC-Close to release resources");
          try {
            if (this.leg1SsfCallContent.getCapDialog() != null) {
              this.leg1SsfCallContent.getCapDialog().close(preArrangedEnd);
              this.leg1SsfCallContent.setStep(BcsmCallStep.closed);
            }
          } catch (CAPException e) {
            logger.error("Error: ", e);
          }
        }
      }
    }
  }

  protected void relayETCtoCapProxySsf(EstablishTemporaryConnectionRequest etc) {
    logger.debug("CAP ETC to be relayed to CAP Proxy SSF : {}", etc);
    if (this.leg1ScfCallContent != null) {
      // We need to check if the CAP dialog is neither closed nor a disconnect event has been
      // received
      if (this.leg1ScfCallContent.getCapDialog() != null
          && this.leg1ScfCallContent.getStep() != BcsmCallStep.disconnected) {
        this.leg1ScfCallContent.setStep(BcsmCallStep.etcReceived);
        this.leg1ScfCallContent.setEtc(etc);
        logger.debug("CAP ETC to be relayed from HPLMN SCF to Proxy SSF on dialog: {}, with invokeID={}", this.leg1ScfCallContent.getCapDialog(), etc.getInvokeId());
        SccpAddress etcCueCallingPartyAddress = createLocalAddress(this.hplmnScpGt, stpScfPc, hplmnScpSsn);
        SccpAddress etcCueCalledPartyAddress = createRemoteAddress(this.vplmnVlrGt_tt5, proxySsfPc, proxySsn);
        this.leg1ScfCallContent.getCapDialog().setLocalAddress(etcCueCallingPartyAddress);
        this.leg1ScfCallContent.getCapDialog().setRemoteAddress(etcCueCalledPartyAddress);

        try {
          this.leg1ScfCallContent.getCapDialog().addContinueRequest();
          logger.debug("CAP CUE added to SSF-SCF dialog");
          Digits assistingSSPIPRoutingAddress = etc.getAssistingSSPIPRoutingAddress();
          Digits correlationID = etc.getCorrelationID();
          ScfID scfID = etc.getScfID();
          CAPExtensions capExtensions = etc.getExtensions();
          Carrier carrier = etc.getCarrier();
          ServiceInteractionIndicatorsTwo serviceInteractionIndicatorsTwo = etc.getServiceInteractionIndicatorsTwo();
          Integer callSegmentID = etc.getCallSegmentID();
          NAOliInfo naOliInfo = etc.getNAOliInfo();
          LocationNumberCap locationNumberCap = etc.getChargeNumber();
          OriginalCalledNumberCap originalCalledNumberCap = etc.getOriginalCalledPartyID();
          CallingPartyNumberCap callingPartyNumberCap = etc.getCallingPartyNumber();

          this.leg1ScfCallContent.getCapDialog().addEstablishTemporaryConnectionRequest(30000,
              assistingSSPIPRoutingAddress, correlationID, scfID, capExtensions, carrier,
              serviceInteractionIndicatorsTwo, callSegmentID, naOliInfo, locationNumberCap,
              originalCalledNumberCap, callingPartyNumberCap);
          logger.debug("CAP ETC added to SSF-SCF dialog");
          this.leg1ScfCallContent.getCapDialog().send();
          this.leg1ScfCallContent.setStep(BcsmCallStep.etcSent);
          logger.debug("ETC-CUE sent from VPLMN SSF to CAP Proxy over dialog : {}", this.leg1ScfCallContent.getCapDialog());
          logger.debug("SSF Cap Dialog state = {}", this.leg1ScfCallContent.getCapDialog().getState());
        } catch (CAPException e) {
          logger.error("Caught exception", e);
        }
      }
    }
  }

  @Override
  public void onConnectRequest(ConnectRequest connectRequest) {
    if (connectRequest.getCAPDialog().getRemoteAddress().getGlobalTitle().getDigits().equals(proxyGtDigits))
      logger.debug("CAP CON event detected at VPLMN STP SSF from GT = {}", proxyGtDigits);
    if (connectRequest.getCAPDialog().getRemoteAddress().getGlobalTitle().getDigits().equals(hplmnScpGtDigits))
      logger.debug("CAP CON event detected at VPLMN STP SSF from GT = {}", hplmnScpGtDigits);

    // CAP CON received from CAP Proxy either for generating CAP IDP(#A->#C) or within "leg 2"
    // to process joint RRB
    if (connectRequest.getCAPDialog().getRemoteAddress().getGlobalTitle().getDigits().equals(proxyGtDigits)) {
      if (this.leg1SsfCallContent != null) {
        if (leg1SsfCallContent.getStep() == BcsmCallStep.etcReceived
            || leg1SsfCallContent.getStep() == BcsmCallStep.cueReceived
            || leg1SsfCallContent.getStep() == BcsmCallStep.conReceived) {
          this.leg1SsfCallContent.setStep(BcsmCallStep.conReceived);
          this.leg1SsfCallContent.setCon(connectRequest);
          if (this.leg2SsfCallContent == null) {
            setInitialDP_leg2_fromConnectRequest(this.leg1SsfCallContent.getCon());
          }
          logger.debug("CAP CON received from Cap Proxy SCF : {} over dialog : {}, with invokeID = {}", this.leg1SsfCallContent.getCon(), this.leg1SsfCallContent.getCapDialog(), connectRequest.getInvokeId());
        } else if (this.leg1SsfCallContent.getStep() == BcsmCallStep.disconnected) {
          // terminate the CAMEL dialog
          logger.debug("CAP CON event at VPLMN STP SSF for null dialog or disconnected call; sending TC-Close to release resources");
          try {
            if (this.leg1SsfCallContent.getCapDialog() != null) {
              this.leg1SsfCallContent.getCapDialog().close(preArrangedEnd);
              this.leg1SsfCallContent.setStep(BcsmCallStep.closed);
            }
          } catch (CAPException e) {
            logger.error("Error: ", e);
          }
        }
      }
    } // CAP CON received from HPLMN SCP for relaying to CAP Proxy with corresponding SCCP Cg/Cd Party Addresses
    else if (connectRequest.getCAPDialog().getRemoteAddress().getGlobalTitle().getDigits().equals(hplmnScpGtDigits)) {
      if (this.leg1SsfCallContent != null) {
        // We need to check if the CAP dialog is neither closed nor a disconnect event has been received
        if (this.leg1SsfCallContent.getCapDialog() != null
            && this.leg1SsfCallContent.getStep() != BcsmCallStep.disconnected) {
          this.leg1SsfCallContent.setStep(BcsmCallStep.conReceived);
          this.leg1SsfCallContent.setCon(connectRequest);
          logger.debug("CAP CON received from HPLMN SCF : {} over dialog : {}, with invokeID={}", this.leg1SsfCallContent.getCon(), this.leg1SsfCallContent.getCapDialog(), connectRequest.getInvokeId());
        } else {
          // terminate the CAMEL dialog
          logger.debug("CAP CON event at VPLMN STP for null dialog or disconnected call; sending TC-Close to release resources");
          try {
            if (this.leg1SsfCallContent.getCapDialog() != null) {
              this.leg1SsfCallContent.getCapDialog().close(preArrangedEnd);
              this.leg1SsfCallContent.setStep(BcsmCallStep.closed);
            }
          } catch (CAPException e) {
            logger.error("Error: ", e);
          }
        }
      }
    }
  }

  private void relayCONtoCapProxySsf(ConnectRequest con) {
    logger.debug("CAP CON to be relayed to CAP Proxy SSF : {}", con);
    if (this.leg1ScfCallContent != null) {
      // We need to check if the CAP dialog is neither closed nor a disconnect event has been
      // received
      if (this.leg1ScfCallContent.getCapDialog() != null && this.leg1ScfCallContent.getStep() != BcsmCallStep.disconnected) {
        this.leg1ScfCallContent.setStep(BcsmCallStep.conReceived);
        this.leg1ScfCallContent.setCon(con);
        logger.debug("CAP CON to be relayed from HPLMN SCF to Proxy SSF on dialog: {}, with invokeID={}", this.leg1ScfCallContent.getCapDialog(), con.getInvokeId());
        SccpAddress conCallingPartyAddress = createLocalAddress(this.hplmnScpGt, stpScfPc, hplmnScpSsn);
        SccpAddress conCalledPartyAddress = createRemoteAddress(this.vplmnVlrGt_tt5, proxySsfPc, proxySsn);
        this.leg1ScfCallContent.getCapDialog().setLocalAddress(conCallingPartyAddress);
        this.leg1ScfCallContent.getCapDialog().setRemoteAddress(conCalledPartyAddress);

        try {
          this.leg1ScfCallContent.getCapDialog().addContinueRequest();
          logger.debug("CAP CUE added to SSF-SCF dialog");

          this.leg1ScfCallContent.getCapDialog().addConnectRequest(30000,
              con.getDestinationRoutingAddress(), con.getAlertingPattern(),
              con.getOriginalCalledPartyID(), con.getExtensions(), con.getCarrier(),
              con.getCallingPartysCategory(), con.getRedirectingPartyID(),
              con.getRedirectionInformation(), con.getGenericNumbers(),
              con.getServiceInteractionIndicatorsTwo(), con.getChargeNumber(),
              con.getLegToBeConnected(), con.getCUGInterlock(), con.getCugOutgoingAccess(),
              con.getSuppressionOfAnnouncement(), con.getOCSIApplicable(), con.getNAOliInfo(),
              con.getBorInterrogationRequested(), con.getSuppressNCSI());

          logger.debug("CAP CON added to SSF-SCF dialog");
          this.leg1ScfCallContent.getCapDialog().send();
          this.leg1ScfCallContent.setStep(BcsmCallStep.conSent);
          logger.debug("CON/CUE sent from VPLMN SSF to CAP Proxy over dialog : {}", this.leg1ScfCallContent.getCapDialog());
          logger.debug("SSF Cap Dialog state = {}", this.leg1ScfCallContent.getCapDialog().getState());
        } catch (CAPException e) {
          logger.error("Error: ", e);
        }
      }
    }
  }

  public void setInitialDP_leg2_fromConnectRequest(ConnectRequest connectRequest) {
    this.leg2SsfCallContent = new BcsmCallContent();
    this.leg2SsfCallContent.setStep(BcsmCallStep.conReceived);
    this.leg2SsfCallContent.setCon(connectRequest);
    CalledPartyNumberCap calledPartyNumberCap_C_number = null;
    ArrayList<CalledPartyNumberCap> calledPartyNumberArrayList = connectRequest.getDestinationRoutingAddress().getCalledPartyNumber();
    for (int i = 0; i < calledPartyNumberArrayList.size(); i++) {
      calledPartyNumberCap_C_number = calledPartyNumberArrayList.get(i);
      try {
        logger.debug("Called Party Number (#C) from CAP CON to be sent on CAP IDP = {}", calledPartyNumberCap_C_number.getCalledPartyNumber().getAddress());
      } catch (CAPException e) {
        logger.error("Error: ", e);
      }
    }
    if (calledPartyNumberCap_C_number != null) {
      try {
        int serviceKey = 485; // from configuration
        logger.debug("About to send CAP IDP from VPLMN to CAP Proxy with new service-key = {}\nCalling Party Number (#A) = {}\nCalled Party Number (#C) = {}", serviceKey, leg1ScfCallContent.getIdp().getCallingPartyNumber(), calledPartyNumberCap_C_number.getCalledPartyNumber().getAddress());
        this.leg2SsfCallContent.setServiceKey(serviceKey);
        this.leg2SsfCallContent.setCalledPartyNumberCap(calledPartyNumberCap_C_number);
      } catch (CAPException e) {
        logger.error("Error: ", e);
      }
    }
  }

  public void sendInitialDPRequestFromConnectRequest(int serviceKey, CalledPartyNumberCap calledPartyNumberCap_C_number) {
    try {
      logger.debug("On sendInitialDPRequestFromConnectRequest({}, {})", serviceKey, calledPartyNumberCap_C_number.getCalledPartyNumber().getAddress());
      GlobalTitle vplmnGt2 = createGlobalTitle0100(vplmnGtDigitsForHplmn);
      SccpAddress idpCallingPartyAddress = createLocalAddress(vplmnGt2, stpSsfPc, vplmnVlrSsn);
      idpCallingPartyAddress = createLocalAddress(vplmnGt2, stpSsfPc, 251);
      SccpAddress idpCalledPartyAddress = createRemoteAddress(this.proxyScpGt, proxyScfPc, proxySsn);
      idpCalledPartyAddress = createRemoteAddress(this.proxyScpGt, proxyScfPc, 252);
      // Create SSF - SCP CAP Dialog
      CAPApplicationContext acn = CAPApplicationContext.CapV2_gsmSSF_to_gsmSCF;
      this.leg2SsfCallContent.setCapDialog(ssfCapProvider.getCAPServiceCircuitSwitchedCall()
          .createNewDialog(acn, idpCallingPartyAddress, idpCalledPartyAddress));
      logger.debug("New SSF-SCF dialog = {}", leg2SsfCallContent.getCapDialog());
      logger.debug("About to populate leg2SsfCallContent CAP dialog with IDP ****");
      logger.debug("CAP IDP service key ={}, Called Party Number = {}, CgPN = {}, CgPN Category = {}, Location Number = {}, Bearer Capability =  {}, Event Type BCSM = {}", serviceKey, calledPartyNumberCap_C_number, leg1ScfCallContent.getIdp().getCallingPartyNumber(), leg1ScfCallContent.getIdp().getCallingPartysCategory(), leg1ScfCallContent.getIdp().getLocationNumber(), leg1ScfCallContent.getIdp().getBearerCapability(), leg1ScfCallContent.getIdp().getEventTypeBCSM());
      this.leg2SsfCallContent.getCapDialog().addInitialDPRequest(30000, serviceKey,
          calledPartyNumberCap_C_number, leg1ScfCallContent.getIdp().getCallingPartyNumber(),
          leg1ScfCallContent.getIdp().getCallingPartysCategory(), null, null,
          leg1ScfCallContent.getIdp().getLocationNumber(), null, null, null, null,
          leg1ScfCallContent.getIdp().getBearerCapability(),
          leg1ScfCallContent.getIdp().getEventTypeBCSM(), null, null, null, null, null, null, null,
          false, imsi, null, null, null, null, null, null, null, false, null);
      logger.debug("CAP IDP added to SSF-SCF dialog");
      logger.debug("CAP IDP to be sent from MSC SSF to CAP Proxy SCF over new dialog : {}", this.leg2SsfCallContent.getCapDialog());
      logger.debug("scfCurrentCapDialog state = {}", this.leg2SsfCallContent.getCapDialog().getState());
      this.leg2SsfCallContent.getCapDialog().send();
      this.leg2SsfCallContent.setStep(BcsmCallStep.idpSent);
    } catch (Exception e) {
      logger.error("Error: ", e);
    }
  }

  @Override
  public void onContinueRequest(ContinueRequest continueRequest) {
    logger.debug("onContinueRequest at VPLMN STP SSF");
    // We need to check if the CAP dialog is neither closed nor a disconnect event has been
    // received
    if (this.leg1SsfCallContent != null) {
      if (this.leg1SsfCallContent.getCapDialog() != null && this.leg1SsfCallContent.getStep() != BcsmCallStep.disconnected) {
        this.leg1SsfCallContent.setStep(BcsmCallStep.cueReceived);
        this.leg1SsfCallContent.setCue(continueRequest);
        logger.debug("CUE received from GT : {} on dialog: {}, with invokeID={}", leg1SsfCallContent.getCapDialog().getRemoteAddress().getGlobalTitle().getDigits(), this.leg1SsfCallContent.getCapDialog(), continueRequest.getInvokeId());
        logger.info("CAP CUE processed without answer onContinueRequest at VPLMN STP SSF");
      } else {
        // terminate CAMEL dialog
        try {
          if (this.leg1SsfCallContent.getCapDialog() != null) {
            this.leg1SsfCallContent.getCapDialog().close(preArrangedEnd);
            this.leg1SsfCallContent.setStep(BcsmCallStep.closed);
          }
        } catch (CAPException e) {
          logger.error("Error: ", e);
        }
      }
    }
  }

  @Override
  public void onDialogDelimiter(CAPDialog capDialog) {
    logger.info("*** onDialogDelimiter at VPLMN STP Prototype:\n\t\t*** capDialog={}\n\t\t*** TCAP message type = {}\n\t\t*** Local Address: GT = {}, PC = {}, SSN = {}\n\t\t*** Remote Address: GT = {}, PC = {} SSN = {}\n\t\t****************************", capDialog, capDialog.getTCAPMessageType(), capDialog.getLocalAddress().getGlobalTitle(), capDialog.getLocalAddress().getSignalingPointCode(), capDialog.getLocalAddress().getSubsystemNumber(), capDialog.getRemoteAddress().getGlobalTitle(), capDialog.getRemoteAddress().getSignalingPointCode(), capDialog.getRemoteAddress().getSubsystemNumber());

    try {
      if (this.leg1ScfCallContent != null) {
        if (this.leg1ScfCallContent.getStep() != null) {
          switch (this.leg1ScfCallContent.getStep()) {
            case idpReceived:
              logger.info("VPLMN STP SCF onDialogDelimiter case: initialDPReceived");
              this.leg1ScfCallContent.getCapDialog().keepAlive();
              break;
            case rrbSent:
              logger.info("VPLMN STP SCF onDialogDelimiter case: rrbSent");
              this.leg1ScfCallContent.getCapDialog().keepAlive();
              break;
            case answerReceived:
              logger.info("onDialogDelimiter, CAP ERB received (oAnswer) at VPLMN STP SCF");
              if (this.hplmnCallContent.getStep() != BcsmCallStep.answerSent)
                relayERBtoHPLMNScp(this.leg1ScfCallContent.getErb());
              break;
            case disconnectReceived:
              if (this.leg1ScfCallContent.getErb() != null) {
                logger.info("onDialogDelimiter, CAP ERB received (oDisconnect) at VPLMN STP SCF");
                this.relayERBtoHPLMNScp(this.leg1ScfCallContent.getErb());
              }
              break;
            case disconnected:
              logger.info("VPLMN STP SCF onDialogDelimiter case: disconnected");
              break;
            default:
              break;
          }
        }
      }
      if (this.leg1SsfCallContent != null) {
        if (this.leg1SsfCallContent.getStep() != null) {
          switch (this.leg1SsfCallContent.getStep()) {
            case idpSent:
              logger.info("onDialogDelimiter, CAP IDP sent from VPLMN STP SSF to HPLMN SCF");
              this.leg1SsfCallContent.getCapDialog().keepAlive();
              break;
            case rrbReceived:
              logger.info("onDialogDelimiter, CAP RRB received at VPLMN STP SSF");
              if (leg1ScfCallContent.getStep() == BcsmCallStep.idpReceived)
                relayRRBRequestToProxySsf(this.leg1SsfCallContent.getRrb());
              break;
            case etcReceived:
              logger.info("onDialogDelimiter, CAP ETC received at VPLMN STP SSF");
              relayETCtoCapProxySsf(this.leg1SsfCallContent.getEtc());
              break;
            case cueReceived:
              logger.info("onDialogDelimiter, CAP CUE received at VPLMN STP SSF");
              logger.info("CAP CUE received from GT : {}", this.leg1SsfCallContent.getCapDialog().getRemoteAddress().getGlobalTitle().getDigits());
              logger.info("this.leg1ScfCallContent.getStep() == {}", this.leg1ScfCallContent.getStep());
              break;
            case conReceived:
              logger.debug("onDialogDelimiter, CAP CON received at VPLMN STP SSF from GT : {}", this.leg1SsfCallContent.getCapDialog().getRemoteAddress().getGlobalTitle()
                  .getDigits());
              if (leg2SsfCallContent != null)
                logger.debug("CAP CON Called Party Number = {}", this.leg2SsfCallContent
                    .getCalledPartyNumberCap().getCalledPartyNumber().getAddress());
              if (this.leg1ScfCallContent != null) {
                if (this.leg1ScfCallContent.getIdp() != null)
                  logger.debug("CAP IDP Called Party BCD Number = {}", this.leg1ScfCallContent.getIdp().getCalledPartyBCDNumber().getAddress());
              }
              if (this.leg1SsfCallContent.getCon().getCAPDialog().getRemoteAddress()
                  .getGlobalTitle().getDigits().equals(hplmnScpGtDigits)) {
                relayCONtoCapProxySsf(this.leg1SsfCallContent.getCon());
              }
              if (this.leg2SsfCallContent != null) {
                if (!Objects.equals(this.leg2SsfCallContent.getCalledPartyNumberCap().getCalledPartyNumber().getAddress(),
                    this.leg1ScfCallContent.getIdp().getCalledPartyBCDNumber().getAddress())
                    && this.leg2SsfCallContent.getStep() != BcsmCallStep.rrbReceived
                    && this.leg2SsfCallContent.getStep() != BcsmCallStep.fciReceived) {
                  setInitialDP_leg2_fromConnectRequest(this.leg1SsfCallContent.getCon());
                  sendInitialDPRequestFromConnectRequest(leg2SsfCallContent.getServiceKey(),
                      leg2SsfCallContent.getCalledPartyNumberCap());
                }
              }
              break;
            case relReceived:
              logger.info("onDialogDelimiter, CAP REL received at VPLMN STP SSF from GT : {}", this.leg1SsfCallContent.getCapDialog().getRemoteAddress().getGlobalTitle().getDigits());
              relayRELtoCapProxySsf(this.leg1SsfCallContent.getRel());
              break;
            case disconnected:
              logger.info("onDialogDelimiter, disconnected on STP SSF, about to send TC END");
              break;
            default:
              break;
          }
        }
      }
      if (this.leg2SsfCallContent != null) {
        switch (this.leg2SsfCallContent.getStep()) {
          case rrbReceived:
            logger.debug("onDialogDelimiter, CAP RRB received at VPLMN STP SSF on Leg 2");
            logger.debug("CAP RRB received from GT : {}", this.leg2SsfCallContent.getCapDialog()
                  .getRemoteAddress().getGlobalTitle().getDigits());
            break;
          case conReceived:
            if (leg2SsfCallContent.getCalledPartyNumberCap() == leg1ScfCallContent.getIdp()
                .getCalledPartyNumber()) {
              logger.debug("onDialogDelimiter, CAP CON received at VPLMN STP SSF on Leg 2");
              logger.debug("CAP CON received from GT : {}", this.leg2SsfCallContent.getCapDialog()
                    .getRemoteAddress().getGlobalTitle().getDigits());
            }
            break;
          case fciReceived:
            logger.debug("onDialogDelimiter, CAP FCI received on STP SSF on Leg 2");
            break;
          case closed:
            logger.debug("Sending TC END from VPLMN STP to CAP Proxy on Leg 2");
            this.leg2SsfCallContent.getCapDialog().close(preArrangedEnd);
            break;
          case disconnected:
            logger.debug("onDialogDelimiter, disconnected already on STP SSF on Leg 2");
            break;
          default:
            break;
        }
      }
    } catch (Exception e) {
      logger.error("Error: ", e);
    }
  }

  private static class VPLMNTimer implements Runnable {

    private final VplmnStpPrototype vplmnStpPrototype;

    public VPLMNTimer(VplmnStpPrototype vplmnStpPrototype) {
      this.vplmnStpPrototype = vplmnStpPrototype;
    }

    @Override
    public void run() {
      try {
        Thread.sleep(100);
        logger.debug("ERB oAnswer to be sent from CAP Proxy SCF on LEG 2");
        vplmnStpPrototype.sendEventReportBCSMRequest_oAnswer_toProxyScf_Leg2();
        Thread.sleep(800);
        logger.debug("ERB oAnswer to be sent from VPLMN STP SSF to Proxy SCF");
        vplmnStpPrototype.sendEventReportBCSMRequest_oAnswer_toProxyScf_Leg1();
        Thread.sleep(1500);
        logger.debug("ERB oDisconnect to be sent from VPLMN SSF to Proxy SCF");
        vplmnStpPrototype.sendEventReportBCSMRequest_oDisconnect_toProxyScf_Leg1();
      } catch (InterruptedException e) {
        logger.error("Error: ", e);
      }
    }
  }

  protected void sendEventReportBCSMRequest_oAnswer_toProxyScf_Leg2() {
    logger.debug("ERB oAnswer to be sent from HPLMN SCP to Proxy SCF on leg 2");
    if (this.leg2SsfCallContent != null) {
      // We need to check if the CAP dialog is neither closed nor a disconnect event has been
      // received
      if (this.leg2SsfCallContent.getCapDialog() != null
          && this.leg2SsfCallContent.getStep() != BcsmCallStep.disconnected) {

        try {
          this.partnerMscGt = createGlobalTitle0100(partnerMscGtDigits);
          GlobalTitle vplmnGt2 = createGlobalTitle0100(vplmnGtDigitsForHplmn);
          SccpAddress erbCallingPartyAddress = createLocalAddress(this.partnerMscGt, stpSsfPc, vplmnVlrSsn);
          erbCallingPartyAddress = createLocalAddress(vplmnGt2, stpSsfPc, 251);
          SccpAddress erbCalledPartyAddress = createRemoteAddress(this.proxyScpGt, proxyScfPc, proxySsn);
          erbCalledPartyAddress = createRemoteAddress(this.proxyScpGt, proxyScfPc, 252);
          this.leg2SsfCallContent.getCapDialog().setLocalAddress(erbCallingPartyAddress);
          this.leg2SsfCallContent.getCapDialog().setRemoteAddress(erbCalledPartyAddress);

          EventSpecificInformationBCSM eventSpecificInformationBCSM = null;
          ReceivingSideID receivingSideID = ssfCapProvider.getCAPParameterFactory().createReceivingSideID(LegType.leg2);
          MiscCallInfoMessageType messageType = MiscCallInfoMessageType.request;
          MiscCallInfoDpAssignment dpAssignment = null;
          MiscCallInfo miscCallInfo = new MiscCallInfoImpl(messageType, dpAssignment);
          CAPExtensions capExtensions = null;
          this.leg2SsfCallContent.getCapDialog().addEventReportBCSMRequest(30000,
              EventTypeBCSM.oAnswer, eventSpecificInformationBCSM, receivingSideID, miscCallInfo,
              capExtensions);
          this.leg2SsfCallContent.getCapDialog().send();
          this.leg2SsfCallContent.setStep(BcsmCallStep.answerSent);
          this.leg2SsfCallContent.setEventTypeBCSM(EventTypeBCSM.oAnswer);
          logger.debug("ERB oAnswer sent on LEG 2 from VPLMN SSF to CAP Proxy SCF over dialog : {}", this.leg2SsfCallContent.getCapDialog());
          logger.debug("SSF CAP Dialog state = {}", this.leg2SsfCallContent.getCapDialog().getState());
        } catch (CAPException e) {
          logger.error("Error: ", e);
        }
      }
    }
  }

  @Override
  public void onFurnishChargingInformationRequest(FurnishChargingInformationRequest fci) {
    logger.debug("CAP FCI reached VPLMN: {}", fci.toString());
    if (this.leg2SsfCallContent != null) {
      this.leg2SsfCallContent.setStep(BcsmCallStep.fciReceived);
    }
  }

  @Override
  public void onCancelRequest(CancelRequest can) {
    logger.debug("CAP CAN reached VPLMN STP");
    if (this.leg2SsfCallContent != null) {
      this.leg2SsfCallContent.setStep(BcsmCallStep.cancelReceived);
      try {
        this.partnerMscGt = createGlobalTitle0100(partnerMscGtDigits);
        GlobalTitle vplmnGt2 = createGlobalTitle0100(vplmnGtDigitsForHplmn);
        SccpAddress tcEndClgPartyAddress = createLocalAddress(this.partnerMscGt, stpSsfPc, vplmnVlrSsn);
        tcEndClgPartyAddress = createLocalAddress(vplmnGt2, stpSsfPc, 251);
        SccpAddress tcEndCldPartyAddress = createRemoteAddress(this.proxyScpGt, proxyScfPc, proxySsn);
        tcEndCldPartyAddress = createRemoteAddress(this.proxyScpGt, proxyScfPc, 252);
        logger.debug("leg1SsfCallContent CAP Dialog state : {}", this.leg1SsfCallContent.getCapDialog().getState());
        this.leg2SsfCallContent.getCapDialog().setLocalAddress(tcEndClgPartyAddress);
        this.leg2SsfCallContent.getCapDialog().setRemoteAddress(tcEndCldPartyAddress);
        Thread.sleep(100);
        this.leg2SsfCallContent.getCapDialog().close(preArrangedEnd);
        logger.debug("leg2SsfCallContent CAP Dialog state : {}", this.leg2SsfCallContent.getCapDialog().getState());
        logger.debug("leg1SsfCallContent CAP Dialog state : {}", this.leg1SsfCallContent.getCapDialog().getState());
        this.leg2SsfCallContent = null;
      } catch (CAPException e) {
        logger.error("CAP Exception: ", e);
      } catch (InterruptedException e) {
        logger.error("InterruptedException: ", e);
      }
    }
  }

  protected void sendEventReportBCSMRequest_oAnswer_toProxyScf_Leg1() {
    logger.debug("CAP ERB oAnswer to be sent from HPLMN SCP to Proxy SCF on leg 1");
    if (this.leg1SsfCallContent != null) {
      // We need to check if the CAP dialog is neither closed nor a disconnect event has been
      // received
      if (this.leg1SsfCallContent.getCapDialog() != null
          && this.leg1SsfCallContent.getStep() != BcsmCallStep.disconnected) {
        GlobalTitle vplmnGt = createGlobalTitle0100(vplmnGtDigits);
        SccpAddress erbCallingPartyAddress = createLocalAddress(vplmnGt, stpSsfPc, vplmnVlrSsn);
        SccpAddress erbCalledPartyAddress = createRemoteAddress(this.proxyScpGt, proxyScfPc, proxySsn);
        this.leg1SsfCallContent.getCapDialog().setLocalAddress(erbCallingPartyAddress);
        this.leg1SsfCallContent.getCapDialog().setRemoteAddress(erbCalledPartyAddress);
        try {
          EventSpecificInformationBCSM eventSpecificInformationBCSM = null;
          ReceivingSideID receivingSideID = ssfCapProvider.getCAPParameterFactory().createReceivingSideID(LegType.leg2);
          MiscCallInfoMessageType messageType = MiscCallInfoMessageType.notification;
          MiscCallInfoDpAssignment dpAssignment = null;
          MiscCallInfo miscCallInfo = new MiscCallInfoImpl(messageType, dpAssignment);
          CAPExtensions capExtensions = null;
          this.leg1SsfCallContent.getCapDialog().addEventReportBCSMRequest(30000, EventTypeBCSM.oAnswer, eventSpecificInformationBCSM,
              receivingSideID, miscCallInfo, capExtensions);
          logger.debug("SSF CAP Dialog state = {}", this.leg1SsfCallContent.getCapDialog().getState());
          this.leg1SsfCallContent.getCapDialog().send();
          this.leg1SsfCallContent.setStep(BcsmCallStep.answerSent);
          this.leg1SsfCallContent.setEventTypeBCSM(EventTypeBCSM.oAnswer);
          logger.debug("ERB oAnswer sent from VPLMN SSF to HPLMN SCF over dialog : {}", this.leg1SsfCallContent.getCapDialog());
          logger.debug("SSF CAP Dialog state = {}", this.leg1SsfCallContent.getCapDialog().getState());
        } catch (CAPException e) {
          logger.error("Caught exception", e);
        }
      } else {
        // terminate CAMEL dialog
        try {
          if (this.leg1SsfCallContent.getCapDialog() != null) {
            this.leg1SsfCallContent.getCapDialog().close(preArrangedEnd);
            this.leg1SsfCallContent.setStep(BcsmCallStep.closed);
          }
        } catch (CAPException e) {
          logger.error("Error: ", e);
        }
      }
    }
  }

  protected void sendEventReportBCSMRequest_oDisconnect_toProxyScf_Leg1() {
    logger.debug("CAP ERB oDisconnect to be sent from HPLMN SCP to HPLMN SCF");
    if (this.leg1SsfCallContent != null) {
      // We need to check if the CAP dialog is neither closed nor a disconnect event has been
      // received
      if (this.leg1SsfCallContent.getCapDialog() != null
          && this.leg1SsfCallContent.getStep() != BcsmCallStep.disconnected) {
        GlobalTitle vplmnGt = createGlobalTitle0100(vplmnGtDigits);
        SccpAddress erbCallingPartyAddress = createLocalAddress(vplmnGt, stpSsfPc, vplmnVlrSsn);
        SccpAddress erbCalledPartyAddress = createRemoteAddress(this.proxyScpGt, proxyScfPc, proxySsn);
        this.leg1SsfCallContent.getCapDialog().setLocalAddress(erbCallingPartyAddress);
        this.leg1SsfCallContent.getCapDialog().setRemoteAddress(erbCalledPartyAddress);
        try {
          EventSpecificInformationBCSM eventSpecificInformationBCSM = null;
          ReceivingSideID receivingSideID =
              ssfCapProvider.getCAPParameterFactory().createReceivingSideID(LegType.leg2);
          MiscCallInfoMessageType messageType = MiscCallInfoMessageType.notification;
          MiscCallInfoDpAssignment dpAssignment = null;
          MiscCallInfo miscCallInfo = new MiscCallInfoImpl(messageType, dpAssignment);
          CAPExtensions capExtensions = null;
          this.leg1SsfCallContent.getCapDialog().addEventReportBCSMRequest(30000,
              EventTypeBCSM.oDisconnect, eventSpecificInformationBCSM, receivingSideID,
              miscCallInfo, capExtensions);
          this.leg1SsfCallContent.getCapDialog().send();
          this.leg1SsfCallContent.setStep(BcsmCallStep.disconnectSent);
          this.leg1SsfCallContent.setEventTypeBCSM(EventTypeBCSM.oDisconnect);
          logger.debug("ERB oDisconnect sent from VPLMN SSF to HPLMN SCF over dialog : {}", this.leg1SsfCallContent.getCapDialog());
          logger.debug("ssf Cap Dialog state = {}", this.leg1SsfCallContent.getCapDialog().getState());
        } catch (CAPException e) {
          logger.error("Caught exception", e);
        }
      } else {
        // terminate CAMEL dialog
        try {
          if (this.leg1SsfCallContent.getCapDialog() != null) {
            this.leg1SsfCallContent.getCapDialog().close(preArrangedEnd);
            this.leg1SsfCallContent.getCapDialog().send();
            this.leg1SsfCallContent.setStep(BcsmCallStep.closed);
          }
        } catch (CAPException e) {
          logger.error("Error: ", e);
        }
      }
    }
  }

  @Override
  public void onEventReportBCSMRequest(EventReportBCSMRequest erb) {
    logger.debug("CAP ERB received at VPLMN STP SCF : {}", erb);
    if (this.leg1ScfCallContent != null) {
      // We need to check if the CAP dialog is neither closed nor a disconnect event has been
      // received
      if (this.leg1ScfCallContent.getCapDialog() != null
          && this.leg1ScfCallContent.getStep() != BcsmCallStep.disconnected) {
        this.leg1ScfCallContent.getErbEventList().add(erb);
        this.leg1ScfCallContent.setErb(erb);
        logger.debug("ERB type of event {}", erb.getEventTypeBCSM());
        switch (erb.getEventTypeBCSM()) {
          case collectedInfo:
            this.leg1ScfCallContent.setStep(BcsmCallStep.collectedInfo);
            break;
          case analyzedInformation:
            this.leg1ScfCallContent.setStep(BcsmCallStep.analizedInformation);
            break;
          case routeSelectFailure:
            this.leg1ScfCallContent.setStep(BcsmCallStep.routeSelectFailure);
            break;
          case oNoAnswer:
          case tNoAnswer:
            this.leg1ScfCallContent.setStep(BcsmCallStep.noAnswer);
            break;
          case oAnswer:
            this.leg1ScfCallContent.setStep(BcsmCallStep.answerReceived);
            logger.debug("ERB oAnswer event captured on VPLMN STP SCF");
            break;
          case oMidCall:
          case tMidCall:
            this.leg1ScfCallContent.setStep(BcsmCallStep.midCall);
            break;
          case oDisconnect:
            this.leg1ScfCallContent.setStep(BcsmCallStep.disconnectReceived);
            logger.debug("ERB oDisconnect event captured on VPLMN STP SCF");
            break;
          case oAbandon:
            this.leg1ScfCallContent.setStep(BcsmCallStep.abandoned);
            logger.debug("ERB oAbandon event captured on CAP proxy");
            break;
          case tBusy:
            this.leg1ScfCallContent.setStep(BcsmCallStep.busy);
            break;
          case tAnswer:
            this.leg1ScfCallContent.setStep(BcsmCallStep.answerReceived);
            logger.debug("ERB tAnswer event captured on CAP proxy");
            break;
          case tDisconnect:
            logger.debug("ERB tDisconnect event captured on VPLMN STP SCF");
            this.leg1ScfCallContent.setStep(BcsmCallStep.disconnectReceived);
            break;
          case tAbandon:
            this.leg1ScfCallContent.setStep(BcsmCallStep.abandoned);
            logger.debug("ERB tAbandon event captured on CAP proxy");
            break;
          case oTermSeized:
            this.leg1ScfCallContent.setStep(BcsmCallStep.termSeized);
            break;
          case callAccepted:
            this.leg1ScfCallContent.setStep(BcsmCallStep.callAccepted);
            break;
          case oChangeOfPosition:
            this.leg1ScfCallContent.setStep(BcsmCallStep.changeOfPosition);
            break;
          case oServiceChange:
          case tServiceChange:
            this.leg1ScfCallContent.setStep(BcsmCallStep.serviceChange);
            break;
          default:
            break;
        }
      } else {
        // else, close the HPLMN CAP dialog via a TC-Close
        try {
          this.leg1ScfCallContent.getCapDialog().close(preArrangedEnd);
        } catch (CAPException e) {
          logger.error("Error: ", e);
        }
      }
    }
  }

  private void relayERBtoHPLMNScp(EventReportBCSMRequest eventReportBCSMRequest) {
    logger.info("CAP ERB with event type {} to be sent to HPLMN SCF from VPLMN STP SSF *****", eventReportBCSMRequest.getEventTypeBCSM());
    if (hplmnCallContent != null) {
      if (hplmnCallContent.getCapDialog() != null
          && hplmnCallContent.getStep() != BcsmCallStep.disconnected) {
        SccpAddress erbCallingPartyAddress = createLocalAddress(this.vplmnVlrGt, stpSsfHplmnPc, vplmnVlrSsn);
        SccpAddress erbCalledPartyAddress = createRemoteAddress(this.hplmnScpGt, hplmnScpPc, hplmnScpSsn);
        this.hplmnCallContent.getCapDialog().setLocalAddress(erbCallingPartyAddress);
        this.hplmnCallContent.getCapDialog().setRemoteAddress(erbCalledPartyAddress);
        try {
          EventTypeBCSM eventTypeBCSM = eventReportBCSMRequest.getEventTypeBCSM();
          EventSpecificInformationBCSM eventSpecificInformationBCSM = eventReportBCSMRequest.getEventSpecificInformationBCSM();
          ReceivingSideID receivingSideID = eventReportBCSMRequest.getLegID();
          MiscCallInfo miscCallInfo = eventReportBCSMRequest.getMiscCallInfo();
          CAPExtensions capExtensions = eventReportBCSMRequest.getExtensions();

          this.hplmnCallContent.getCapDialog().addEventReportBCSMRequest(30000, eventTypeBCSM,
              eventSpecificInformationBCSM, receivingSideID, miscCallInfo, capExtensions);
          logger.debug("CAP ERB added to SSF-SCF dialog");
          this.hplmnCallContent.getCapDialog().send();
          if (eventTypeBCSM == EventTypeBCSM.oAnswer) {
            this.hplmnCallContent.setStep(BcsmCallStep.answerSent);
          }
          if (eventTypeBCSM == EventTypeBCSM.oDisconnect) {
            this.hplmnCallContent.setStep(BcsmCallStep.disconnected);
          }
          logger.debug("ERB sent from VPLMN STP SSF to HPLMN SCF over dialog : {}", this.hplmnCallContent.getCapDialog());
          logger.debug("VPLMN STP SSF - HPLMN SCF current dialog state state = {}", this.hplmnCallContent.getCapDialog().getState());
        } catch (CAPException e) {
          logger.error("Error: ", e);
        }
      }
    }
  }

  @Override
  public void onReleaseCallRequest(ReleaseCallRequest rel) {
    logger.info("CAP REL event at VPLMN STP SSF");
    if (this.leg1SsfCallContent != null
        && this.leg1ScfCallContent.getStep() == BcsmCallStep.disconnectReceived) {
      // We need to check if the CAP dialog is neither closed nor a disconnect event has been
      // received
      if (this.leg1SsfCallContent.getCapDialog() != null && this.leg1SsfCallContent.getStep() != BcsmCallStep.disconnected) {
        this.leg1SsfCallContent.setStep(BcsmCallStep.relReceived);
        this.leg1SsfCallContent.setRel(rel);
        logger.debug("CAP REL received from HPLMN SCF at VPLMN STP SSF : {} over dialog : {}, release cause : {}",
            this.leg1SsfCallContent.getRel(), this.leg1SsfCallContent.getCapDialog(), rel.getCause());
        relayRELtoCapProxySsf(this.leg1SsfCallContent.getRel());
      } else {
        // terminate CAMEL dialog
        logger.debug(
            "CAP REL event at VPLMN STP for null dialog or disconnected call; sending TC-Close to release resources");
        try {
          if (this.leg1SsfCallContent.getCapDialog() != null) {
            this.leg1SsfCallContent.getCapDialog().close(preArrangedEnd);
            this.leg1SsfCallContent.setStep(BcsmCallStep.closed);
          }
        } catch (CAPException e) {
          logger.error("Error: ", e);
        }
      }
    } else if (leg1ScfCallContent != null) {
      if (leg1ScfCallContent.getStep() == BcsmCallStep.relSent) {
        // We need to check if the CAP dialog is neither closed nor a disconnect event has
        // been received
        this.leg1SsfCallContent.setStep(BcsmCallStep.disconnected);
        this.leg1SsfCallContent.setRel(rel);
        logger.debug("CAP REL received from HPLMN SCF at VPLMN STP SSF : {} over dialog : {}, release cause : {}", this.leg1SsfCallContent.getRel(), this.leg1SsfCallContent.getCapDialog(), rel.getCause());
        try {
          logger.debug("CAP REL event at VPLMN STP from CAP Proxy; sending TC-Close to VPLMN release resources");
          this.leg1SsfCallContent.getCapDialog().close(preArrangedEnd);
        } catch (CAPException e) {
          logger.error("Error: ", e);
        }
      }
    }
  }

  protected void relayRELtoCapProxySsf(ReleaseCallRequest rel) {
    logger.debug("CAP REL to be relayed to CAP Proxy SSF : {}", rel);
    if (this.leg1ScfCallContent != null) {
      // We need to check if the CAP dialog is neither closed nor a disconnect event has been received
      if (this.leg1ScfCallContent.getCapDialog() != null
          && this.leg1ScfCallContent.getStep() != BcsmCallStep.disconnected) {
        this.leg1ScfCallContent.setStep(BcsmCallStep.relReceived);
        this.leg1ScfCallContent.setRel(rel);
        logger.debug("CAP REL to be relayed from VPLMN SCF to Proxy SSF on dialog: {}, with invokeID={}", this.leg1ScfCallContent.getCapDialog(), rel.getInvokeId());
        SccpAddress relCallingPartyAddress = createLocalAddress(this.hplmnScpGt, stpScfPc, hplmnScpSsn);
        SccpAddress relCalledPartyAddress = createRemoteAddress(this.vplmnVlrGt_tt5, proxySsfPc, proxySsn);
        this.leg1ScfCallContent.getCapDialog().setLocalAddress(relCallingPartyAddress);
        this.leg1ScfCallContent.getCapDialog().setRemoteAddress(relCalledPartyAddress);
        try {
          CauseCap causeCap = rel.getCause();
          this.leg1ScfCallContent.getCapDialog().addReleaseCallRequest(30000, causeCap);
          logger.debug("CAP REL added to SSF-SCF dialog");
          this.leg1ScfCallContent.getCapDialog().close(preArrangedEnd);
          this.leg1ScfCallContent.setStep(BcsmCallStep.relSent);
          logger.debug("CAP REL sent from VPLMN SSF to CAP Proxy over dialog : {}", this.leg1ScfCallContent.getCapDialog());
          logger.debug("SSF Cap Dialog state = {}", this.leg1ScfCallContent.getCapDialog().getState());
        } catch (CAPException e) {
          logger.error("Error: ", e);
        }
      }
    }
  }

  @Override
  public void onDialogRequest(CAPDialog capDialog, CAPGprsReferenceNumber capGprsReferenceNumber) {

  }

  @Override
  public void onDialogAccept(CAPDialog capDialog, CAPGprsReferenceNumber capGprsReferenceNumber) {

  }

  @Override
  public void onDialogUserAbort(CAPDialog capDialog, CAPGeneralAbortReason capGeneralAbortReason,
                                CAPUserAbortReason capUserAbortReason) {

  }

  @Override
  public void onDialogProviderAbort(CAPDialog capDialog, PAbortCauseType pAbortCauseType) {

  }

  @Override
  public void onDialogClose(CAPDialog capDialog) {

  }

  @Override
  public void onDialogRelease(CAPDialog capDialog) {

  }

  @Override
  public void onDialogTimeout(CAPDialog capDialog) {

  }

  @Override
  public void onDialogNotice(CAPDialog capDialog, CAPNoticeProblemDiagnostic capNoticeProblemDiagnostic) {

  }

  @Override
  public void onApplyChargingRequest(ApplyChargingRequest applyChargingRequest) {

  }

  @Override
  public void onContinueWithArgumentRequest(ContinueWithArgumentRequest continueWithArgumentRequest) {

  }

  @Override
  public void onApplyChargingReportRequest(ApplyChargingReportRequest applyChargingReportRequest) {

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
  public void onActivityTestResponse(ActivityTestResponse activityTestResponse) {

  }

  @Override
  public void onAssistRequestInstructionsRequest(AssistRequestInstructionsRequest assistRequestInstructionsRequest) {

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

  }

  @Override
  public void onRejectComponent(CAPDialog capDialog, Long aLong, Problem problem, boolean b) {

  }

  @Override
  public void onInvokeTimeout(CAPDialog capDialog, Long aLong) {

  }

  @Override
  public void onCAPMessage(CAPMessage capMessage) {

  }
}
