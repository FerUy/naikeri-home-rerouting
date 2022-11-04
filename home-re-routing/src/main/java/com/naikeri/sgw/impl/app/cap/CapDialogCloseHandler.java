package com.naikeri.sgw.impl.app.cap;

import com.naikeri.sgw.info.CapTransaction;
import org.apache.log4j.Logger;

/**
 * CapDialogCloseHandler
 */
public class CapDialogCloseHandler {
  private static final Logger logger = Logger.getLogger(CapDialogCloseHandler.class);

  public static CapDialogOut closeCapDialog(Long dialogId) {
    if (dialogId == null) return null;
    try {
      BcsmCallContent callContent = CapTransaction.instance().getLeg2BcsmCall(dialogId, true);
      if (callContent == null){
        callContent = CapTransaction.instance().getScfBcsmCallContent(dialogId, true);
        if (callContent != null){
          logger.debug("Closing the dialog for leg1. DialogId = "+ dialogId);
        }    
      }else {
        logger.debug("Closing the dialog for Leg2. DialogId = "+ dialogId);
      }
      if (callContent == null){
        return null;
      }
      CapDialogOut dialogOut = new CapDialogOut(CapDialogType.CircuitSwitchedCallControl, "");
      dialogOut.setIsClose(true);
      dialogOut.setCapDialogCircuitSwitchedCall(callContent.getCapDialog());
      return dialogOut;
    } catch (Exception e) {
      logger.error("Exception caught for DialogId = " + dialogId + ". Details: ", e);
      logger.error(e.getMessage());
    }
    return null;
  }
}
