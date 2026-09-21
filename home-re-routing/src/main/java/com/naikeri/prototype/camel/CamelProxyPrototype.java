package com.naikeri.prototype.camel;

import static com.naikeri.sgw.api.settings.byteUtils.hexStringToByteArray;
import static com.naikeri.sgw.impl.settings.sccp.SccpHelpers.createGlobalTitle0100;
import static com.naikeri.sgw.impl.settings.sccp.SccpHelpers.createLocalAddress;
import static com.naikeri.sgw.impl.settings.sccp.SccpHelpers.createRemoteAddress;
import java.util.ArrayList;
import com.naikeri.sgw.impl.app.cap.BcsmCallContent;
import com.naikeri.sgw.impl.app.cap.BcsmCallStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPApplicationContext;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPParameterFactory;
import org.restcomm.protocols.ss7.cap.api.CAPProvider;
import org.restcomm.protocols.ss7.cap.api.isup.CalledPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.CallingPartyNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.CauseCap;
import org.restcomm.protocols.ss7.cap.api.isup.Digits;
import org.restcomm.protocols.ss7.cap.api.isup.LocationNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.OriginalCalledNumberCap;
import org.restcomm.protocols.ss7.cap.api.isup.RedirectingPartyIDCap;
import org.restcomm.protocols.ss7.cap.api.primitives.AppendFreeFormatData;
import org.restcomm.protocols.ss7.cap.api.primitives.CAPExtensions;
import org.restcomm.protocols.ss7.cap.api.primitives.CalledPartyBCDNumber;
import org.restcomm.protocols.ss7.cap.api.primitives.EventTypeBCSM;
import org.restcomm.protocols.ss7.cap.api.primitives.ReceivingSideID;
import org.restcomm.protocols.ss7.cap.api.primitives.SendingSideID;
import org.restcomm.protocols.ss7.cap.api.primitives.TimeAndTimezone;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ConnectRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EstablishTemporaryConnectionRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EventReportBCSMRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.InitialDPRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ReleaseCallRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.RequestReportBCSMEventRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.BearerCapability;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.CGEncountered;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.Carrier;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.DestinationRoutingAddress;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.EventSpecificInformationBCSM;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.FCIBCCCAMELsequence1;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.FreeFormatData;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.IPSSPCapabilities;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.InitialDPArgExtension;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.ServiceInteractionIndicatorsTwo;
import org.restcomm.protocols.ss7.cap.primitives.SendingSideIDImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.FCIBCCCAMELsequence1Impl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.FreeFormatDataImpl;
import org.restcomm.protocols.ss7.inap.api.isup.CallingPartysCategoryInap;
import org.restcomm.protocols.ss7.inap.api.isup.HighLayerCompatibilityInap;
import org.restcomm.protocols.ss7.inap.api.isup.RedirectionInformationInap;
import org.restcomm.protocols.ss7.inap.api.primitives.LegType;
import org.restcomm.protocols.ss7.inap.api.primitives.MiscCallInfo;
import org.restcomm.protocols.ss7.isup.message.parameter.CalledPartyNumber;
import org.restcomm.protocols.ss7.isup.message.parameter.NAINumber;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.service.callhandling.CallReferenceNumber;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CUGIndex;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CUGInterlock;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

public class CamelProxyPrototype {

  protected CAPProvider scfCapProvider, ssfCapProvider;
  protected CAPParameterFactory scfCapParameterFactory, ssfCapParameterFactory;
  private final CAPApplicationContext acn = CAPApplicationContext.CapV2_gsmSSF_to_gsmSCF;
  private GlobalTitle vplmnVlrGt, hplmnScpGt, proxyScpGt, partnerMscGt;
  private BcsmCallContent leg1ScfCallContent, ssfCallContent, leg2ScfCallContent;
  private BcsmCallStep leg1ScfCallStep, leg2ScfCallStep, ssfCallStep;
  private final Boolean preArrangedEnd = false;
  private int msrnIndex = 0;
  // private static XmlConfiguration configuration;
  private static final Logger logger = LogManager.getLogger(CamelProxyPrototype.class);
  // these should be retrieved from configuration
  // VPLMN - STP
  private static final int stpScfPc = 1050;
  private static final int stpSsfPc = 82;
  private static final int stpScfSsn = 146;
  private static final int vplmnVlrSsn = 7;
  // CAP Proxy
  private static final int proxyScfPc = 947;
  private static final int proxySsfPc = 948;
  private static final int proxyScpSsn = 146;
  // HPLMN SCP
  private static final String hplmnScpGtDigits = "97254121030";
  // MSC (leg2)
  private static final int partnerMscPc = 1001;
  private static final int partnerMscSsn = 8;
  private static final String partnerMscGtDigits = "97254121021";
  private static String[] msrnArray = new String[200];

  public CamelProxyPrototype(CAPProvider scfCapProvider, CAPParameterFactory scfCapParameterFactory,
                             CAPProvider ssfCapProvider, CAPParameterFactory ssfCapParameterFactory) {
    // CAP Proxy SCF - VPLMN STP SSF
    this.scfCapProvider = scfCapProvider;
    this.scfCapParameterFactory = scfCapParameterFactory;
    // CAP Proxy SSF - VPLMN STP SCF
    this.ssfCapProvider = ssfCapProvider;
    this.ssfCapParameterFactory = ssfCapParameterFactory;
  }

  protected BcsmCallStep getLeg1ScfCallStep() {
    if (this.leg1ScfCallContent != null)
      return this.leg1ScfCallContent.getStep();
    else
      return null;
  }

  protected BcsmCallStep getLeg2ScfCallStep() {
    if (this.leg2ScfCallContent != null)
      return this.leg2ScfCallContent.getStep();
    else
      return null;
  }

  protected BcsmCallStep getSsfCallStep() {
    if (this.ssfCallContent != null)
      return this.ssfCallContent.getStep();
    else
      return null;
  }

  protected BcsmCallStep getCapProxyCallStep(boolean leg1ScfCallStep, boolean ssfCallStep,
                                             boolean leg2ScfCallStep) {
    if (leg1ScfCallStep && this.leg1ScfCallContent != null)
      return this.leg1ScfCallContent.getStep();
    else if (ssfCallStep && this.ssfCallContent != null)
      return this.ssfCallContent.getStep();
    else if (leg2ScfCallStep && this.leg2ScfCallContent != null)
      return this.leg2ScfCallContent.getStep();
    else
      return null;
  }

  protected void onInitialDPRequestFromVPLMN_leg1(InitialDPRequest idp) {
    logger.debug("CAP IDP from VPLMN VLR received on CAP proxy: {}, over CAP dialog: {}", idp, idp.getCAPDialog());
    this.leg1ScfCallContent = new BcsmCallContent();
    this.leg1ScfCallContent.setCapDialog(idp.getCAPDialog());
    this.leg1ScfCallContent.setIdp(idp);
    this.leg1ScfCallContent.setStep(BcsmCallStep.idpReceived);
    this.leg1ScfCallStep = this.leg1ScfCallContent.getStep();
    SccpAddress vplmnVlrAddress = idp.getCAPDialog().getRemoteAddress();
    this.vplmnVlrGt = vplmnVlrAddress.getGlobalTitle();
    SccpAddress proxyScpAddress = idp.getCAPDialog().getLocalAddress();
    this.proxyScpGt = proxyScpAddress.getGlobalTitle();
    this.leg1ScfCallContent.setOriginImsi(idp.getIMSI());
  }

  protected void relayInitialDPRequestToHPLMNviaSTP(InitialDPRequest initialDPRequest)
      throws CAPException {
    logger.debug("CAP IDP received on CAP Proxy SCF relayed to CAP Proxy SSF");
    this.ssfCallContent = new BcsmCallContent();
    this.ssfCallContent.setIdp(initialDPRequest);
    this.ssfCallContent.setStep(BcsmCallStep.idpReceived);
    this.ssfCallStep = this.ssfCallContent.getStep();
    // New SCCP Called Party Address to replace proxyScpAddress
    this.hplmnScpGt = createGlobalTitle0100(hplmnScpGtDigits);
    SccpAddress idpCallingPartyAddress =
        createLocalAddress(this.vplmnVlrGt, proxySsfPc, proxyScpSsn);
    SccpAddress idpCalledPartyAddress = createRemoteAddress(this.hplmnScpGt, stpScfPc, stpScfSsn);
    // New IMSI
    String imsiNewPrefix = "42507" + removePrefix(initialDPRequest.getIMSI().getData(), "42501");
    this.ssfCallContent.setNewImsi(new IMSIImpl(imsiNewPrefix));

    int serviceKey = initialDPRequest.getServiceKey();
    CalledPartyNumberCap calledPartyNumberCap = initialDPRequest.getCalledPartyNumber();
    CallingPartyNumberCap callingPartyNumberCap = initialDPRequest.getCallingPartyNumber();
    CallingPartysCategoryInap callingPartysCategoryInap =
        initialDPRequest.getCallingPartysCategory();
    CGEncountered cgEncountered = initialDPRequest.getCGEncountered();
    IPSSPCapabilities ipsspCapabilities = initialDPRequest.getIPSSPCapabilities();
    LocationNumberCap locationNumberCap = initialDPRequest.getLocationNumber();
    OriginalCalledNumberCap originalCalledNumberCap = initialDPRequest.getOriginalCalledPartyID();
    CAPExtensions capExtensions = initialDPRequest.getExtensions();
    HighLayerCompatibilityInap highLayerCompatibilityInap =
        initialDPRequest.getHighLayerCompatibility();
    Digits digits = initialDPRequest.getAdditionalCallingPartyNumber();
    BearerCapability bearerCapability = initialDPRequest.getBearerCapability();
    EventTypeBCSM eventTypeBCSM = initialDPRequest.getEventTypeBCSM();
    RedirectingPartyIDCap redirectingPartyIDCap = initialDPRequest.getRedirectingPartyID();
    RedirectionInformationInap redirectionInformationInap =
        initialDPRequest.getRedirectionInformation();
    CauseCap causeCap = initialDPRequest.getCause();
    ServiceInteractionIndicatorsTwo serviceInteractionIndicatorsTwo =
        initialDPRequest.getServiceInteractionIndicatorsTwo();
    Carrier carrier = initialDPRequest.getCarrier();
    CUGIndex cugIndex = initialDPRequest.getCugIndex();
    CUGInterlock cugInterlock = initialDPRequest.getCugInterlock();
    boolean cugOutgoingAccess = initialDPRequest.getCugOutgoingAccess();
    SubscriberState subscriberState = initialDPRequest.getSubscriberState();
    LocationInformation locationInformation = initialDPRequest.getLocationInformation();
    ExtBasicServiceCode extBasicServiceCode = initialDPRequest.getExtBasicServiceCode();
    CallReferenceNumber callReferenceNumber = initialDPRequest.getCallReferenceNumber();
    ISDNAddressString mscAddress = initialDPRequest.getMscAddress();
    CalledPartyBCDNumber calledPartyBCDNumber = initialDPRequest.getCalledPartyBCDNumber();
    TimeAndTimezone timeAndTimezone = initialDPRequest.getTimeAndTimezone();
    boolean callForwardingSSPending = initialDPRequest.getCallForwardingSSPending();
    InitialDPArgExtension initialDPArgExtension = initialDPRequest.getInitialDPArgExtension();

    // Create CAP dialog
    this.ssfCallContent.setCapDialog(ssfCapProvider.getCAPServiceCircuitSwitchedCall()
        .createNewDialog(acn, idpCallingPartyAddress, idpCalledPartyAddress));

    // Step 2: forward IDP with new SCCP address and IMSI to HPLMN SCP
    this.ssfCallContent.getCapDialog().addInitialDPRequest(30000, serviceKey, calledPartyNumberCap,
        callingPartyNumberCap, callingPartysCategoryInap, cgEncountered, ipsspCapabilities,
        locationNumberCap, originalCalledNumberCap, capExtensions, highLayerCompatibilityInap,
        digits, bearerCapability, eventTypeBCSM, redirectingPartyIDCap, redirectionInformationInap,
        causeCap, serviceInteractionIndicatorsTwo, carrier, cugIndex, cugInterlock,
        cugOutgoingAccess, this.ssfCallContent.getNewImsi(), subscriberState, locationInformation,
        extBasicServiceCode, callReferenceNumber, mscAddress, calledPartyBCDNumber, timeAndTimezone,
        callForwardingSSPending, initialDPArgExtension);

    logger.debug("CAP IDP sent from CAP Proxy SSF back to VPLMN STP SCF on dialog: {}, with Calling Party Address={}, Called Party Address={}", this.ssfCallContent.getCapDialog(), idpCallingPartyAddress, idpCalledPartyAddress);
    logger.debug("CAP IDP new IMSI:{}", this.ssfCallContent.getNewImsi());
    this.ssfCallContent.getCapDialog().send();
    this.ssfCallContent.setStep(BcsmCallStep.idpSent);
    this.ssfCallStep = this.ssfCallContent.getStep();
  }

  public void onRequestReportBCSMEventRequest(RequestReportBCSMEventRequest rrb) throws CAPException {
    logger.debug("CAP RRB event captured on CAP proxy: {}", rrb);
    if (this.leg1ScfCallContent != null) {
      // this would only come from HPLMN SCP, thus we need to check if neither the VPLMN CAP
      // dialog is already closed nor a disconnect event has been received
      if (this.leg1ScfCallContent.getCapDialog() != null
          && this.leg1ScfCallContent.getStep() != BcsmCallStep.disconnected
          && this.leg1ScfCallContent.getStep() != BcsmCallStep.rrbSent) {
        if (rrb.getCAPDialog().getLocalAddress().getGlobalTitle() == this.vplmnVlrGt) {
          this.leg1ScfCallContent.setRrb(rrb);
          this.leg1ScfCallContent.setStep(BcsmCallStep.rrbReceived);
          this.leg1ScfCallStep = this.leg1ScfCallContent.getStep();
          // New SCCP Calling/Called Party Addresses
          SccpAddress rrbCallingPartyAddress =
              createLocalAddress(this.proxyScpGt, proxyScfPc, proxyScpSsn);
          SccpAddress rrbCalledPartyAddress =
              createRemoteAddress(this.vplmnVlrGt, stpSsfPc, vplmnVlrSsn);
          this.leg1ScfCallContent.getCapDialog().setLocalAddress(rrbCallingPartyAddress);
          this.leg1ScfCallContent.getCapDialog().setRemoteAddress(rrbCalledPartyAddress);

          this.leg1ScfCallContent.getCapDialog().addRequestReportBCSMEventRequest(30000,
              rrb.getBCSMEventList(), rrb.getExtensions());
          this.leg1ScfCallContent.getCapDialog().send();
          this.leg1ScfCallContent.setStep(BcsmCallStep.rrbSent);
          this.leg1ScfCallStep = this.leg1ScfCallContent.getStep();
          logger.debug("RRB sent on CAP proxy to VPLMN STP SSF over dialog: {}; RRB Calling Party Address : {}, RRB Called Party Address : {}", this.leg1ScfCallContent.getCapDialog(), rrbCallingPartyAddress, rrbCalledPartyAddress);
        } else {
          logger.debug("RRB received from HPLMN SCF via VPLMN STP SCF on dialog : {}", this.leg1ScfCallContent.getCapDialog());
        }
      } else {
        // close the HPLMN CAP dialog via a TC-Close
        this.leg1ScfCallContent.getCapDialog().close(preArrangedEnd);
        this.leg1ScfCallContent.getCapDialog().send();
        this.leg1ScfCallContent.setStep(BcsmCallStep.disconnected);
        this.leg1ScfCallStep = this.leg1ScfCallContent.getStep();
      }
    }
  }

  protected void onEstablishTemporaryConnectionRequest(EstablishTemporaryConnectionRequest etc)
      throws CAPException {
    logger.debug("CAP ETC event captured on CAP proxy");
    if (this.leg1ScfCallContent != null) {
      if (this.leg1ScfCallContent.getCapDialog() != null
          && this.leg1ScfCallContent.getStep() != BcsmCallStep.disconnected) {
        this.leg1ScfCallContent.setEtc(etc);
        this.leg1ScfCallContent.setStep(BcsmCallStep.etcReceived);
        this.leg1ScfCallStep = this.leg1ScfCallContent.getStep();
        // New SCCP Calling/Called Party Addresses
        this.partnerMscGt = createGlobalTitle0100(partnerMscGtDigits);
        SccpAddress leg2_conCallingPartyAddress =
            createLocalAddress(this.proxyScpGt, proxyScfPc, proxyScpSsn);
        SccpAddress leg2_conCalledPartyAddress =
            createRemoteAddress(this.partnerMscGt, stpSsfPc, vplmnVlrSsn);
        this.leg1ScfCallContent.getCapDialog().setLocalAddress(leg2_conCallingPartyAddress);
        this.leg1ScfCallContent.getCapDialog().setRemoteAddress(leg2_conCalledPartyAddress);

        // FIX ME with configuration values
        ArrayList<CalledPartyNumberCap> calledPartyNumber = new ArrayList<>();
        CalledPartyNumber cpn =
            this.scfCapProvider.getISUPParameterFactory().createCalledPartyNumber();
        String msrnAddress = null;
        if (msrnIndex % msrnArray.length != 0) {
          msrnAddress = msrnArray[msrnIndex];
          cpn.setAddress(msrnAddress);
          if (msrnIndex == msrnArray.length - 1)
            this.msrnIndex = 0;
          else
            this.msrnIndex = msrnIndex + 1;
        } else if (this.msrnIndex == 0) {
          msrnAddress = msrnArray[msrnIndex];
          cpn.setAddress(msrnAddress);
          this.msrnIndex = msrnIndex + 1;
        }
        logger.debug("MSRN = {}", msrnAddress);
        cpn.setNatureOfAddresIndicator(NAINumber._NAI_INTERNATIONAL_NUMBER);
        cpn.setNumberingPlanIndicator(CalledPartyNumber._NPI_ISDN);
        cpn.setInternalNetworkNumberIndicator(CalledPartyNumber._INN_ROUTING_ALLOWED);
        CalledPartyNumberCap cpnc =
            this.scfCapProvider.getCAPParameterFactory().createCalledPartyNumberCap(cpn);
        calledPartyNumber.add(cpnc);
        DestinationRoutingAddress destinationRoutingAddress = this.scfCapProvider
            .getCAPParameterFactory().createDestinationRoutingAddress(calledPartyNumber);

        this.leg1ScfCallContent.getCapDialog().addConnectRequest(30000, destinationRoutingAddress,
            null, null, null, null, null, null, null, null, null, null, null, null, false, false,
            false, null, false, false);
        logger.debug("CAP ETC removed and CAP CON to be sent on CAP proxy to VPLMN over dialog: {}; Calling Party Address={}; Called Party Address={}", this.leg1ScfCallContent.getCapDialog(), this.leg1ScfCallContent.getCapDialog().getLocalAddress(), this.leg1ScfCallContent.getCapDialog().getRemoteAddress());
        this.leg1ScfCallContent.getCapDialog().send();
        this.leg1ScfCallContent.setStep(BcsmCallStep.conSent);
        this.leg1ScfCallStep = this.leg1ScfCallContent.getStep();
      } else {
        logger.debug(
            "onEstablishTemporaryConnectionRequest event, HPLMN CAP dialog closed via a TC-Close due to a previous event from the VPLMN (call disconnected)");
        this.leg1ScfCallContent.getCapDialog().close(preArrangedEnd);
      }
    } else {
      logger.debug("onEstablishTemporaryConnectionRequest event, HPLMN CAP dialog closed via a TC-Close due to a previous event from the VPLMN");
    }
  }

  protected void onConnectRequest(ConnectRequest con) throws CAPException {
    logger.debug("CAP CON event captured on CAP proxy");
    logger.debug("Leg 1 SCF step:{}, Leg 2 SCF step:{}, SSF call step{}", leg1ScfCallStep, leg2ScfCallStep, ssfCallStep);
    if (this.leg1ScfCallContent != null) {
      if (this.leg1ScfCallContent.getCapDialog() != null
          && this.leg1ScfCallContent.getStep() != BcsmCallStep.disconnected) {
        this.leg1ScfCallContent.setCon(con);
        this.leg1ScfCallContent.setStep(BcsmCallStep.conReceived);
        this.leg1ScfCallStep = this.leg1ScfCallContent.getStep();
        // New SCCP Calling/Called Party Addresses
        this.partnerMscGt = createGlobalTitle0100(partnerMscGtDigits);
        SccpAddress leg2_conCallingPartyAddress =
            createLocalAddress(this.proxyScpGt, proxyScfPc, proxyScpSsn);
        SccpAddress leg2_conCalledPartyAddress =
            createRemoteAddress(this.partnerMscGt, stpSsfPc, vplmnVlrSsn);
        this.leg1ScfCallContent.getCapDialog().setLocalAddress(leg2_conCallingPartyAddress);
        this.leg1ScfCallContent.getCapDialog().setRemoteAddress(leg2_conCalledPartyAddress);

        // with configuration values
        ArrayList<CalledPartyNumberCap> calledPartyNumber = new ArrayList<>();
        CalledPartyNumber cpn =
            this.scfCapProvider.getISUPParameterFactory().createCalledPartyNumber();
        String msrnAddress = null;
        if (msrnIndex % msrnArray.length != 0) {
          msrnAddress = msrnArray[msrnIndex];
          cpn.setAddress(msrnAddress);
          if (msrnIndex == msrnArray.length - 1)
            this.msrnIndex = 0;
          else
            this.msrnIndex = msrnIndex + 1;
        } else if (this.msrnIndex == 0) {
          msrnAddress = msrnArray[msrnIndex];
          cpn.setAddress(msrnAddress);
          this.msrnIndex = msrnIndex + 1;
        }
        cpn.setNatureOfAddresIndicator(NAINumber._NAI_INTERNATIONAL_NUMBER);
        cpn.setNumberingPlanIndicator(CalledPartyNumber._NPI_ISDN);
        cpn.setInternalNetworkNumberIndicator(CalledPartyNumber._INN_ROUTING_ALLOWED);
        CalledPartyNumberCap cpnc =
            this.scfCapProvider.getCAPParameterFactory().createCalledPartyNumberCap(cpn);
        calledPartyNumber.add(cpnc);
        DestinationRoutingAddress destinationRoutingAddress = this.scfCapProvider
            .getCAPParameterFactory().createDestinationRoutingAddress(calledPartyNumber);

        this.leg1ScfCallContent.getCapDialog().addConnectRequest(30000, destinationRoutingAddress,
            null, null, null, null, null, null, null, null, null, null, null, null, false, false,
            false, null, false, false);
        logger.debug("Incoming CAP CON removed and new CAP CON to be sent on CAP proxy to VPLMN over dialog: {}; Calling Party Address={}; Called Party Address={}MSRN = {}", this.leg1ScfCallContent.getCapDialog(), this.leg1ScfCallContent.getCapDialog().getLocalAddress(), this.leg1ScfCallContent.getCapDialog().getRemoteAddress(), msrnAddress);
        this.leg1ScfCallContent.getCapDialog().send();
        this.leg1ScfCallContent.setStep(BcsmCallStep.conSent);
        this.leg1ScfCallStep = this.leg1ScfCallContent.getStep();
      } else {
        logger.debug(
            "onConnectRequest event, HPLMN CAP dialog closed via a TC-Close due to a previous event from the VPLMN (call disconnected)");
        this.leg1ScfCallContent.getCapDialog().close(preArrangedEnd);
      }
    } else {
      logger.debug("onConnectRequest event, HPLMN CAP dialog closed via a TC-Close due to a previous event from the VPLMN");
    }
  }

  protected void onInitialDPRequestFromVPLMN_leg2(InitialDPRequest idp) {
    logger.debug("CAP IDP from VPLMN VLR received on CAP proxy for leg 2: {}, over CAP dialog: {}", idp, idp.getCAPDialog());
    this.leg2ScfCallContent = new BcsmCallContent();
    this.leg2ScfCallContent.setCapDialog(idp.getCAPDialog());
    this.leg2ScfCallContent.setIdp(idp);
    this.leg2ScfCallContent.setStep(leg2ScfCallStep = BcsmCallStep.idpReceived);
    leg2ScfCallStep = this.leg2ScfCallContent.getStep();

    try {
      ArrayList<CalledPartyNumberCap> calledPartyNumber = new ArrayList<>();
      CalledPartyNumber cpn =
          this.scfCapProvider.getISUPParameterFactory().createCalledPartyNumber();
      cpn.setAddress(this.leg1ScfCallContent.getIdp().getCalledPartyBCDNumber().getAddress());
      cpn.setNatureOfAddresIndicator(NAINumber._NAI_INTERNATIONAL_NUMBER);
      cpn.setNumberingPlanIndicator(CalledPartyNumber._NPI_ISDN);
      cpn.setInternalNetworkNumberIndicator(CalledPartyNumber._INN_ROUTING_ALLOWED);
      CalledPartyNumberCap cpnc =
          this.scfCapProvider.getCAPParameterFactory().createCalledPartyNumberCap(cpn);
      calledPartyNumber.add(cpnc);
      DestinationRoutingAddress destinationRoutingAddress_leg2 = this.scfCapProvider
          .getCAPParameterFactory().createDestinationRoutingAddress(calledPartyNumber);

      this.leg2ScfCallContent.getCapDialog().addConnectRequest(30000,
          destinationRoutingAddress_leg2, null, null, null, null, null, null, null, null, null,
          null, null, null, false, false, false, null, false, false);
      logger.debug("CAP CON for leg 2 to be sent on CAP proxy to VPLMN over dialog: {}; Calling Party Address={}; Called Party Address={}", this.leg2ScfCallContent.getCapDialog(), this.leg2ScfCallContent.getCapDialog().getLocalAddress(), this.leg1ScfCallContent.getCapDialog().getRemoteAddress());

      RequestReportBCSMEventRequest rrb = this.leg1ScfCallContent.getRrb();
      this.leg2ScfCallContent.setRrb(rrb);
      this.leg2ScfCallContent.getCapDialog().addRequestReportBCSMEventRequest(30000, rrb.getBCSMEventList(), rrb.getExtensions());
      logger.debug("CAP RRB for leg 2 to be sent on CAP proxy to VPLMN over dialog: {}; Calling Party Address={}; Called Party Address={}", this.leg2ScfCallContent.getCapDialog(), this.leg2ScfCallContent.getCapDialog().getLocalAddress(), this.leg1ScfCallContent.getCapDialog().getRemoteAddress());

      this.leg2ScfCallContent.getCapDialog().send();
      this.leg2ScfCallContent.setStep(BcsmCallStep.conSent);
      this.leg2ScfCallStep = this.leg2ScfCallContent.getStep();
    } catch (CAPException e) {
      logger.error("Error: ", e);
    }
  }

  protected void onEventReportBCSMRequest(EventReportBCSMRequest eventReportBCSMRequest) throws CAPException {
    logger.debug("ERB event captured on CAP proxy: {}", eventReportBCSMRequest);
    if (this.leg2ScfCallContent != null) {
      // We need to check if the CAP dialog is neither closed nor a disconnect event has been
      // received
      if (this.leg2ScfCallContent.getCapDialog() != null
          && this.leg2ScfCallContent.getStep() != BcsmCallStep.disconnected) {
        this.leg2ScfCallContent.setErb(eventReportBCSMRequest);
        this.leg2ScfCallContent.getErbEventList().add(eventReportBCSMRequest);
        switch (eventReportBCSMRequest.getEventTypeBCSM()) {
          case collectedInfo:
            this.leg2ScfCallContent.setStep(BcsmCallStep.collectedInfo);
            break;
          case analyzedInformation:
            this.leg2ScfCallContent.setStep(BcsmCallStep.analizedInformation);
            break;
          case routeSelectFailure:
            this.leg2ScfCallContent.setStep(BcsmCallStep.routeSelectFailure);
            break;
          case oNoAnswer:
          case tNoAnswer:
            this.leg2ScfCallContent.setStep(BcsmCallStep.noAnswer);
            break;
          case oAnswer:
            logger.debug("ERB {} event type captured on CAP proxy", eventReportBCSMRequest.getEventTypeBCSM());
            this.leg2ScfCallContent.setStep(BcsmCallStep.answerReceived);
            break;
          case oMidCall:
          case tMidCall:
            this.leg2ScfCallContent.setStep(BcsmCallStep.midCall);
            break;
          case oDisconnect:
            this.leg2ScfCallContent.setStep(BcsmCallStep.disconnected);
            logger.debug("ERB {} event type captured on CAP proxy", eventReportBCSMRequest.getEventTypeBCSM());
            break;
          case oAbandon:
            this.leg2ScfCallContent.setStep(BcsmCallStep.abandoned);
            logger.debug("ERB oAbandon event captured on CAP proxy");
            break;
          case tBusy:
            this.leg2ScfCallContent.setStep(BcsmCallStep.busy);
            break;
          case tAnswer:
            this.leg2ScfCallContent.setStep(BcsmCallStep.answerReceived);
            logger.debug("ERB tAnswer event captured on CAP proxy");
            break;
          case tDisconnect:
            this.leg2ScfCallContent.setStep(BcsmCallStep.disconnected);
            logger.debug("ERB tDisconnect event captured on CAP proxy");
            break;
          case tAbandon:
            this.leg2ScfCallContent.setStep(BcsmCallStep.abandoned);
            logger.debug("ERB tAbandon event captured on CAP proxy");
            break;
          case oTermSeized:
            this.leg2ScfCallContent.setStep(BcsmCallStep.termSeized);
            break;
          case callAccepted:
            this.leg2ScfCallContent.setStep(BcsmCallStep.callAccepted);
            break;
          case oChangeOfPosition:
            this.leg2ScfCallContent.setStep(BcsmCallStep.changeOfPosition);
            break;
          case oServiceChange:
          case tServiceChange:
            this.leg2ScfCallContent.setStep(BcsmCallStep.serviceChange);
            break;
          default:
            this.leg2ScfCallContent.setStep(BcsmCallStep.erbReceived);
            break;
        }
      } else {
        // else, close the HPLMN CAP dialog via a TC-Close
        this.leg1ScfCallContent.getCapDialog().close(preArrangedEnd);
      }
    }
    if (this.leg1ScfCallContent != null) {
      // We need to check if the CAP dialog is neither closed nor a disconnect event has been
      // received
      if (this.leg1ScfCallContent.getCapDialog() != null
          && this.leg1ScfCallContent.getStep() != BcsmCallStep.disconnected) {
        this.leg1ScfCallContent.setErb(eventReportBCSMRequest);
        this.leg1ScfCallContent.getErbEventList().add(eventReportBCSMRequest);
        switch (eventReportBCSMRequest.getEventTypeBCSM()) {
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
            logger.debug("ERB {} event type captured on CAP proxy", eventReportBCSMRequest.getEventTypeBCSM());
            this.leg1ScfCallContent.setStep(BcsmCallStep.answerReceived);
            break;
          case oMidCall:
          case tMidCall:
            this.leg1ScfCallContent.setStep(BcsmCallStep.midCall);
            break;
          case oDisconnect:
            this.leg1ScfCallContent.setStep(BcsmCallStep.disconnectReceived);
            logger.debug("ERB {} event type captured on CAP proxy", eventReportBCSMRequest.getEventTypeBCSM());
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
            this.leg1ScfCallContent.setStep(BcsmCallStep.disconnected);
            logger.debug("ERB tDisconnect event captured on CAP proxy");
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
            this.leg1ScfCallContent.setStep(BcsmCallStep.erbReceived);
            break;
        }
      } else {
        // else, close the HPLMN CAP dialog via a TC-Close
        this.leg1ScfCallContent.getCapDialog().close(preArrangedEnd);
      }
    }
  }

  protected void sendCancelAndFciToVPLMNonLeg2() {
    logger.debug("CAP Proxy to send CAP CAN and CAP FCI on leg 2 to VPLMN");
    if (this.leg2ScfCallContent != null) {
      if (this.leg2ScfCallContent.getCapDialog() != null
          && this.leg2ScfCallContent.getStep() != BcsmCallStep.disconnected) {
        try {
          this.leg2ScfCallContent.getCapDialog().addCancelRequest_AllRequests(30000);
          // taken from provided Wireshark trace:
          String freeFormatDataHexStr =
              "a103800101a35ba32d04036001e20402630304026c0004091124159099000001f60404121056440404131256440407168353141220f1a42a04080e010045140801f6040915000079524471109204091400007952443100700408e1010045140801f6";
          byte[] freeFormatDataByteArray = hexStringToByteArray(freeFormatDataHexStr);
          FreeFormatData freeFormatData = new FreeFormatDataImpl(freeFormatDataByteArray);
          SendingSideID sendingSideID = new SendingSideIDImpl(LegType.leg2);
          AppendFreeFormatData appendFreeFormatData = null;
          FCIBCCCAMELsequence1 fcibcccameLsequence1 = new FCIBCCCAMELsequence1Impl(freeFormatData, sendingSideID, appendFreeFormatData);
          this.leg2ScfCallContent.getCapDialog().addFurnishChargingInformationRequest(30000, fcibcccameLsequence1);
          this.leg2ScfCallContent.getCapDialog().send();
          this.leg2ScfCallContent.setStep(BcsmCallStep.cancelSent);
          leg2ScfCallStep = this.leg2ScfCallContent.getStep();
        } catch (CAPException e) {
          logger.error("Error: ", e);
        }
      }
    }
  }

  protected void relayERBtoSCPViaSTP(EventReportBCSMRequest eventReportBCSMRequest)
      throws CAPException {
    logger.debug("CAP Proxy about to relay ERB with event type {} to HPLMN SCF via VPLMN STP SSF", eventReportBCSMRequest.getEventTypeBCSM());
    if (this.ssfCallContent != null) {
      if (this.ssfCallContent.getCapDialog() != null
          && this.ssfCallContent.getStep() != BcsmCallStep.disconnected) {
        // New SCCP Calling/Called Party Addresses
        SccpAddress erbCallingPartyAddress =
            createLocalAddress(this.vplmnVlrGt, proxySsfPc, proxyScpSsn);
        SccpAddress erbCalledPartyAddress =
            createRemoteAddress(this.hplmnScpGt, stpScfPc, stpScfSsn);
        this.ssfCallContent.getCapDialog().setLocalAddress(erbCallingPartyAddress);
        this.ssfCallContent.getCapDialog().setRemoteAddress(erbCalledPartyAddress);

        EventTypeBCSM eventTypeBCSM = eventReportBCSMRequest.getEventTypeBCSM();
        EventSpecificInformationBCSM eventSpecificInformationBCSM =
            eventReportBCSMRequest.getEventSpecificInformationBCSM();
        ReceivingSideID receivingSideID = eventReportBCSMRequest.getLegID();
        MiscCallInfo miscCallInfo = eventReportBCSMRequest.getMiscCallInfo();
        CAPExtensions capExtensions = eventReportBCSMRequest.getExtensions();

        this.ssfCallContent.getCapDialog().addEventReportBCSMRequest(30000, eventTypeBCSM,
            eventSpecificInformationBCSM, receivingSideID, miscCallInfo, capExtensions);
        this.ssfCallContent.getCapDialog().send();
        logger.debug("CAP ERB sent to HPLMN SCF from CAP Proxy via VPLMN STP SSF over dialog: {}; Calling Party Address={}; Called Party Address={}", this.ssfCallContent.getCapDialog(), this.ssfCallContent.getCapDialog().getLocalAddress(), this.ssfCallContent.getCapDialog().getRemoteAddress());
        if (eventReportBCSMRequest.getEventTypeBCSM().name().equalsIgnoreCase("oAnswer")) {
          this.ssfCallContent.setStep(BcsmCallStep.answerSent);
          this.ssfCallStep = this.ssfCallContent.getStep();
        } else if (eventReportBCSMRequest.getEventTypeBCSM().name()
            .equalsIgnoreCase("oDisconnect")) {
          this.ssfCallContent.setStep(BcsmCallStep.disconnectSent);
          this.ssfCallStep = this.ssfCallContent.getStep();
        }
      } else {
        logger.debug("When sending ERB oAnswer to VPLMN SCF, CAP dialog closed via a TC-Close due to a previous event (call disconnected)");
        this.ssfCallContent.getCapDialog().close(preArrangedEnd);
      }
    } else {
      logger.debug("When sending ERB oAnswer to VPLMN SCF, CAP dialog closed via a TC-Close due to a previous event (call disconnected)");
    }
  }

  protected void onReleaseCallRequest(ReleaseCallRequest rel) throws CAPException {
    logger.debug("CAP REL event on CAP proxy");
    if (this.leg1ScfCallContent != null) {
      if (this.leg1ScfCallContent.getCapDialog() != null
          && this.leg1ScfCallContent.getStep() != BcsmCallStep.disconnected) {
        this.leg1ScfCallContent.setRel(rel);
        this.leg1ScfCallContent.setStep(BcsmCallStep.relReceived);
        this.leg1ScfCallStep = this.leg1ScfCallContent.getStep();
        // New SCCP Calling/Called Party Addresses
        SccpAddress relCallingPartyAddress =
            createLocalAddress(this.proxyScpGt, proxyScfPc, proxyScpSsn);
        SccpAddress relCalledPartyAddress =
            createRemoteAddress(this.vplmnVlrGt, stpSsfPc, vplmnVlrSsn);
        this.leg1ScfCallContent.getCapDialog().setLocalAddress(relCallingPartyAddress);
        this.leg1ScfCallContent.getCapDialog().setRemoteAddress(relCalledPartyAddress);

        CauseCap causeCap = rel.getCause();

        this.leg1ScfCallContent.getCapDialog().addReleaseCallRequest(30000, causeCap);
        logger.debug("CAP REL to be sent on CAP proxy to VPLMN over dialog: {}; Calling Party Address={}; Called Party Address={}", this.leg1ScfCallContent.getCapDialog(), this.leg1ScfCallContent.getCapDialog().getLocalAddress(), this.leg1ScfCallContent.getCapDialog().getRemoteAddress());
        this.leg1ScfCallContent.getCapDialog().close(preArrangedEnd);
        this.leg1ScfCallContent.setStep(BcsmCallStep.relSent);
        this.leg1ScfCallStep = this.leg1ScfCallContent.getStep();
      } else {
        logger.debug("onReleaseCallRequest event, HPLMN CAP dialog closed via a TC-Close due to a previous event from the VPLMN (call disconnected)");
        this.leg1ScfCallContent.getCapDialog().close(preArrangedEnd);
      }
    } else {
      logger.debug("onReleaseCallRequest event, HPLMN CAP dialog closed via a TC-Close due to a previous event from the VPLMN");
    }
  }

  protected void initializeMSRNArray() {
    msrnArray = new String[40];
    long first = 972544130000L;
    for (int i = 0; i < 40; i++) {
      msrnArray[i] = String.valueOf(first + i);
    }
  }

  public static String removePrefix(String s, String prefix) {
    if (s != null && prefix != null && s.startsWith(prefix)) {
      return s.substring(prefix.length());
    }
    return s;
  }
}
