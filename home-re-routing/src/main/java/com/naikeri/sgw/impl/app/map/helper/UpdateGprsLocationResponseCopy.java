package com.naikeri.sgw.impl.app.map.helper;

import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationResponse;

/**
 * UpdateGprsLocationResponseCopy
 */
public class UpdateGprsLocationResponseCopy {

  private final ISDNAddressString hlrNumber;

  private final MAPExtensionContainer extensionContainer;

  private final boolean addCapability;

  private final boolean sgsnMmeSeparationSupported;

  private final boolean mmeRegisteredForSMS;

  public UpdateGprsLocationResponseCopy(UpdateGprsLocationResponse response) {
    this.hlrNumber = response.getHlrNumber();
    this.extensionContainer = response.getExtensionContainer();
    this.addCapability = response.isAddCapability();
    this.sgsnMmeSeparationSupported = response.isMmeRegisteredForSMS();
    this.mmeRegisteredForSMS = response.isMmeRegisteredForSMS();
  }

  public ISDNAddressString getHlrNumber() {
    return hlrNumber;
  }

  public MAPExtensionContainer getExtensionContainer() {
    return extensionContainer;
  }

  public boolean isCapability() {
    return addCapability;
  }

  public boolean isSgsnMmeSeparationSupported() {
    return sgsnMmeSeparationSupported;
  }

  public boolean isMmeRegisteredForSM() {
    return mmeRegisteredForSMS;
  }
}