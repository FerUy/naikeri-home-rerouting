package com.naikeri.sgw.impl.app.map.helper;

import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationResponse;

/**
 * UpdateLocationResponseCopy
 */
public class UpdateLocationResponseCopy {

  private ISDNAddressString hlrNumber;

  private MAPExtensionContainer extensionContainer;

  private boolean addCapability;

  private boolean pagingAreaCapability;

  private long mapProtocolVersion;

  public UpdateLocationResponseCopy(UpdateLocationResponse response){
    this.addCapability = response.getAddCapability();
    this.hlrNumber = response.getHlrNumber();
    this.extensionContainer = response.getExtensionContainer();
    this.mapProtocolVersion = response.getMapProtocolVersion();
    this.pagingAreaCapability = response.getPagingAreaCapability();
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

  public boolean getPagingAreaCapability() {
    return pagingAreaCapability;
  }

  public long getMapProtocolVersion() {
    return mapProtocolVersion;
  }
}