package com.naikeri.sgw.impl.app.cap;

import com.naikeri.sgw.network.layers.CapLayer;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPStack;

public class CamelGprsChannelMessages {

  private static final Logger logger = Logger.getLogger(CamelGprsChannelMessages.class);
  private Object capOperationObject;
  private CAPStack capStackIn;
  private CAPStack capStackOut;
  private String channelTransId;

  public CamelGprsChannelMessages(CapLayer capLayerIn, CapLayer capLayerOut,
      Object capOperationObject, String transactionid) {
    this.capOperationObject = capOperationObject;
    this.channelTransId = transactionid;
    this.capStackIn = capLayerIn.getCapStack();
    this.capStackOut = capLayerOut.getCapStack();
    logger.trace(this.capOperationObject + " | " + this.capStackIn + " | " + this.capStackOut
        + " | " + this.channelTransId);
  }
}
