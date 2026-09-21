package com.naikeri.sgw.impl.app.map.helper;

import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;

/**
 * AnyTimeInterrogationResponseCopy
 */
public class AnyTimeInterrogationResponseCopy {

  private final SubscriberInfo subscriberInfo;

  private final MAPExtensionContainer extensionContainer;

  public AnyTimeInterrogationResponseCopy(AnyTimeInterrogationResponse response) {
    this.subscriberInfo = response.getSubscriberInfo();
    this.extensionContainer = response.getExtensionContainer();
  }

  public SubscriberInfo getSubscriberInfo() {
    return subscriberInfo;
  }

  public MAPExtensionContainer getExtensionContainer() {
    return extensionContainer;
  }
}