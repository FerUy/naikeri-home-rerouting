package com.naikeri.sgw.impl.app.map.helper;

import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.AuthenticationSetList;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.EpsAuthenticationSetList;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoResponse;

/**
 * SendAuthenticationInfoResponseCopy
 */
public class SendAuthenticationInfoResponseCopy {

  private AuthenticationSetList authenticationSetList;

  private MAPExtensionContainer extensionContainer;

  private EpsAuthenticationSetList epsAuthenticationSetList;

  private long mapProtocolVersion;

  public SendAuthenticationInfoResponseCopy(SendAuthenticationInfoResponse response){
    this.authenticationSetList = response.getAuthenticationSetList();
    this.extensionContainer = response.getExtensionContainer();
    this.epsAuthenticationSetList = response.getEpsAuthenticationSetList();
    this.mapProtocolVersion = response.getMapProtocolVersion();
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
}