package com.naikeri.sgw.impl.app.cap;

import com.naikeri.sgw.network.layers.CapLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPStack;

public class CamelSmsChannelMessages {

    private static final Logger logger = LogManager.getLogger(CamelSmsChannelMessages.class);
    private final Object capOperationObject;
    private final CAPStack capStackIn;
    private final CAPStack capStackOut;
    private final String channelTransId;

    public CamelSmsChannelMessages(CapLayer capLayerIn, CapLayer capLayerOut, Object capOperationObject, String transactionid) {
        this.capOperationObject = capOperationObject;
        this.capStackIn = capLayerIn.getCapStack();
        this.capStackOut = capLayerOut.getCapStack();
        this.channelTransId = transactionid;
        logger.trace("{} | {} | {} | {}", this.capOperationObject, this.capStackIn, this.capStackOut, this.channelTransId);
    }
}
