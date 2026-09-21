package com.naikeri.sgw.impl.app.map.helper;

import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.AuthenticationSetList;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.EpsAuthenticationSetList;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.UEUsageType;

/**
 * SendAuthenticationInfoResponseCopy
 */
public class SendAuthenticationInfoResponseCopy {

  private final AuthenticationSetList authenticationSetList;

  private final MAPExtensionContainer extensionContainer;

  private final EpsAuthenticationSetList epsAuthenticationSetList;

  private final long mapProtocolVersion;

  private final UEUsageType ueUsageType;

  public SendAuthenticationInfoResponseCopy(SendAuthenticationInfoResponse response){
    this.authenticationSetList = response.getAuthenticationSetList();
    this.extensionContainer = response.getExtensionContainer();
    this.epsAuthenticationSetList = response.getEpsAuthenticationSetList();
    this.mapProtocolVersion = response.getMapProtocolVersion();
    this.ueUsageType = response.getUeUsageType();
  }

  public AuthenticationSetList getAuthenticationSetList() {
    return authenticationSetList;
  }

  public MAPExtensionContainer getExtensionContainer() {
    return extensionContainer;
  }

  public EpsAuthenticationSetList getEpsAuthenticationSetList() {
    return epsAuthenticationSetList;
  }

  public long getMapProtocolVersion() {
    return mapProtocolVersion;
  }

  public UEUsageType getUeUsageType() { return ueUsageType; }
}