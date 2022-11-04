package com.naikeri.sgw.impl.app.map.helper;

import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.sms.MtForwardShortMessageResponse;
import org.restcomm.protocols.ss7.map.api.service.sms.SmsSignalInfo;

/**
 * MtForwardShortMessageResponseCopy
 */
public class MtForwardShortMessageResponseCopy {

  private SmsSignalInfo sM_RP_UI;

  private MAPExtensionContainer extensionContainer;

  public MtForwardShortMessageResponseCopy(MtForwardShortMessageResponse response) {
    this.sM_RP_UI = response.getSM_RP_UI();
    this.extensionContainer = response.getExtensionContainer();
  }

  public SmsSignalInfo getSM_RP_UI() {
    return sM_RP_UI;
  }

  public MAPExtensionContainer getExtensionContainer() {
    return extensionContainer;
  }
}
