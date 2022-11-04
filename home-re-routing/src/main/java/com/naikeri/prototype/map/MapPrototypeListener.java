package com.naikeri.prototype.map;

import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPDialogListener;
import org.restcomm.protocols.ss7.map.api.dialog.MAPAbortProviderReason;
import org.restcomm.protocols.ss7.map.api.dialog.MAPAbortSource;
import org.restcomm.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic;
import org.restcomm.protocols.ss7.map.api.dialog.MAPRefuseReason;
import org.restcomm.protocols.ss7.map.api.dialog.MAPUserAbortChoice;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.tcap.asn.ApplicationContextName;

public class MapPrototypeListener implements MAPDialogListener {

  @Override
  public void onDialogDelimiter(MAPDialog mapDialog) {
    //  Auto-generated method stub

  }

  @Override
  public void onDialogRequest(MAPDialog mapDialog, AddressString destReference,
      AddressString origReference, MAPExtensionContainer extensionContainer) {
    //  Auto-generated method stub

  }

  @Override
  public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString destReference,
      AddressString origReference, AddressString eriMsisdn, AddressString eriVlrNo) {
    //  Auto-generated method stub

  }

  @Override
  public void onDialogAccept(MAPDialog mapDialog, MAPExtensionContainer extensionContainer) {
    //  Auto-generated method stub

  }

  @Override
  public void onDialogReject(MAPDialog mapDialog, MAPRefuseReason refuseReason,
      ApplicationContextName alternativeApplicationContext,
      MAPExtensionContainer extensionContainer) {
    //  Auto-generated method stub

  }

  @Override
  public void onDialogUserAbort(MAPDialog mapDialog, MAPUserAbortChoice userReason,
      MAPExtensionContainer extensionContainer) {
    //  Auto-generated method stub

  }

  @Override
  public void onDialogProviderAbort(MAPDialog mapDialog, MAPAbortProviderReason abortProviderReason,
      MAPAbortSource abortSource, MAPExtensionContainer extensionContainer) {
    //  Auto-generated method stub

  }

  @Override
  public void onDialogClose(MAPDialog mapDialog) {
    //  Auto-generated method stub

  }

  @Override
  public void onDialogNotice(MAPDialog mapDialog,
      MAPNoticeProblemDiagnostic noticeProblemDiagnostic) {
    //  Auto-generated method stub

  }

  @Override
  public void onDialogRelease(MAPDialog mapDialog) {
    //  Auto-generated method stub

  }

  @Override
  public void onDialogTimeout(MAPDialog mapDialog) {
    //  Auto-generated method stub

  }
  
}