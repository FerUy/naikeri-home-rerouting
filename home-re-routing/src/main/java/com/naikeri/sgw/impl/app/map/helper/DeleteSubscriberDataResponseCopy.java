package com.naikeri.sgw.impl.app.map.helper;

import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.RegionalSubscriptionResponse;

/**
 * DeleteSubscriberDataResponseCopy
 */
public class DeleteSubscriberDataResponseCopy {

  private final RegionalSubscriptionResponse regionalSubscriptionResponse;

  private final MAPExtensionContainer extensionContainer;

  public DeleteSubscriberDataResponseCopy(DeleteSubscriberDataResponse response) {
    this.regionalSubscriptionResponse = response.getRegionalSubscriptionResponse();
    this.extensionContainer = response.getExtensionContainer();
  }

  public RegionalSubscriptionResponse getRegionalSubscriptionResponse() {
    return regionalSubscriptionResponse;
  }


  public MAPExtensionContainer getExtensionContainer() {
    return extensionContainer;
  }

}