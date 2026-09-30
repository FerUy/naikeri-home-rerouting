package com.naikeri.prototype.map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.MAPParameterFactory;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.service.callhandling.IstCommandRequest;
import org.restcomm.protocols.ss7.map.api.service.callhandling.IstCommandResponse;
import org.restcomm.protocols.ss7.map.api.service.callhandling.MAPDialogCallHandling;
import org.restcomm.protocols.ss7.map.api.service.callhandling.MAPServiceCallHandlingListener;
import org.restcomm.protocols.ss7.map.api.service.callhandling.ProvideRoamingNumberRequest;
import org.restcomm.protocols.ss7.map.api.service.callhandling.ProvideRoamingNumberResponse;
import org.restcomm.protocols.ss7.map.api.service.callhandling.SendRoutingInformationRequest;
import org.restcomm.protocols.ss7.map.api.service.callhandling.SendRoutingInformationResponse;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

/**
 * Plays the VLR for Provide-Roaming-Number: answers with an MSRN and ends the dialogue, so the
 * proxy's return path for PRN is exercised as well as its request path.
 */
public class MapPrototypeCallHandlingListener implements MAPServiceCallHandlingListener {

  private static final Logger logger = LogManager.getLogger(MapPrototypeCallHandlingListener.class);
  // the MSRN the CAP flow also uses, so the MAP and CAP captures read consistently
  private static final String MSRN = "972544130000";

  private final MAPParameterFactory mapParameterFactory;

  public MapPrototypeCallHandlingListener(MAPParameterFactory mapParameterFactory) {
    this.mapParameterFactory = mapParameterFactory;
  }

  @Override
  public void onProvideRoamingNumberRequest(ProvideRoamingNumberRequest request) {
    MAPDialogCallHandling dialog = request.getMAPDialog();
    logger.info("ProvideRoamingNumberRequest for DialogId={}, IMSI={}", dialog.getLocalDialogId(), request.getImsi());
    try {
      ISDNAddressString roamingNumber = this.mapParameterFactory.createISDNAddressString(
          AddressNature.international_number, NumberingPlan.ISDN, MSRN);
      dialog.addProvideRoamingNumberResponse(request.getInvokeId(), roamingNumber, null, false, null);
      dialog.close(false);
    } catch (Exception e) {
      logger.error("Unable to answer the Provide-Roaming-Number request", e);
    }
  }

  @Override
  public void onProvideRoamingNumberResponse(ProvideRoamingNumberResponse response) {
    logger.info("ProvideRoamingNumberResponse for DialogId={}, MSRN={}",
        response.getMAPDialog().getLocalDialogId(), response.getRoamingNumber());
  }

  @Override
  public void onSendRoutingInformationRequest(SendRoutingInformationRequest request) {
    logger.debug("SendRoutingInformationRequest for DialogId={}: not simulated", request.getMAPDialog().getLocalDialogId());
  }

  @Override
  public void onSendRoutingInformationResponse(SendRoutingInformationResponse response) {
    logger.debug("SendRoutingInformationResponse for DialogId={}", response.getMAPDialog().getLocalDialogId());
  }

  @Override
  public void onIstCommandRequest(IstCommandRequest request) {
    logger.debug("IstCommandRequest for DialogId={}: not simulated", request.getMAPDialog().getLocalDialogId());
  }

  @Override
  public void onIstCommandResponse(IstCommandResponse response) {
    logger.debug("IstCommandResponse for DialogId={}", response.getMAPDialog().getLocalDialogId());
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
}
