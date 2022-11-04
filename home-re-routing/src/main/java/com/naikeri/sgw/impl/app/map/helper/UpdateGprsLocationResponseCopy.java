package com.naikeri.sgw.impl.app.map.helper;

import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationResponse;

/**
 * UpdateGprsLocationResponseCopy
 */
public class UpdateGprsLocationResponseCopy {

  private ISDNAddressString hlrNumber;

  private MAPExtensionContainer extensionContainer;

  private boolean addCapability;

  private boolean sgsnMmeSeparationSupported;

  public UpdateGprsLocationResponseCopy(UpdateGprsLocationResponse response){
    this.hlrNumber = response.getHlrNumber();
    this.extensionContainer = response.getExtensionContainer();
    this.addCapability = response.getAddCapability();
    this.sgsnMmeSeparationSupported = response.getSgsnMmeSeparationSupported();
  }

  public ISDNAddressString getHlrNumber() {
    return hlrNumber;
  }

  public MAPExtensionContainer getExtensionContainer() {
    return extensionContainer;
  }

  public boolean getAddCapability() {
    return addCapability;
  }

  public boolean getSgsnMmeSeparationSupported() {
    return sgsnMmeSeparationSupported;
  }
}