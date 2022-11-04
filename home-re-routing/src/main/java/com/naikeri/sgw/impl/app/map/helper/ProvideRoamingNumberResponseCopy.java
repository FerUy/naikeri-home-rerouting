package com.naikeri.sgw.impl.app.map.helper;

import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.callhandling.ProvideRoamingNumberResponse;

/**
 * ProvideRoamingNumberResponseCopy
 */
public class ProvideRoamingNumberResponseCopy {

  private ISDNAddressString roamingNumber;

  private MAPExtensionContainer extensionContainer;

  private boolean releaseResourcesSupported;

  private ISDNAddressString vmscAddress;

  private long mapProtocolVersion;

  public ProvideRoamingNumberResponseCopy(ProvideRoamingNumberResponse response) {
    this.roamingNumber = response.getRoamingNumber();
    this.extensionContainer = response.getExtensionContainer();
    this.releaseResourcesSupported = response.getReleaseResourcesSupported();
    this.vmscAddress = response.getVmscAddress();
    this.mapProtocolVersion = response.getMapProtocolVersion();
  }

  public ISDNAddressString getRoamingNumber() {
    return roamingNumber;
  }


  public MAPExtensionContainer getExtensionContainer() {
    return extensionContainer;
  }


  public boolean getReleaseResourcesSupported() {
    return releaseResourcesSupported;
  }

  public ISDNAddressString getVmscAddress() {
    return vmscAddress;
  }


  public long getMapProtocolVersion() {
    return mapProtocolVersion;
  }

}
