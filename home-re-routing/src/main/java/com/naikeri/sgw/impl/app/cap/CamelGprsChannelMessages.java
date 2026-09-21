package com.naikeri.sgw.impl.app.cap;

import com.naikeri.sgw.network.layers.CapLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPStack;

public class CamelGprsChannelMessages {

  private static final Logger logger = LogManager.getLogger(CamelGprsChannelMessages.class);
  private final Object capOperationObject;
  private final CAPStack capStackIn;
  private final CAPStack capStackOut;
  private final String channelTransId;

  public CamelGprsChannelMessages(CapLayer capLayerIn, CapLayer capLayerOut, Object capOperationObject, String transactionid) {
    this.capOperationObject = capOperationObject;
    this.channelTransId = transactionid;
    this.capStackIn = capLayerIn.getCapStack();
    this.capStackOut = capLayerOut.getCapStack();
    logger.trace("{} | {} | {} | {}", this.capOperationObject, this.capStackIn, this.capStackOut, this.channelTransId);
  }
}
