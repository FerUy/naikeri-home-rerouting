package com.naikeri.sgw.impl.app.cap;

import com.naikeri.sgw.impl.app.cap.helper.CapProxyConnectRequest;
import com.naikeri.sgw.impl.app.cap.helper.CapProxyContinueRequest;
import com.naikeri.sgw.impl.app.cap.helper.CapProxyETCRequest;
import com.naikeri.sgw.impl.app.cap.helper.CapProxyEventReportBCSMRequest;
import com.naikeri.sgw.impl.app.cap.helper.CapProxyInitialDPRequest;
import com.naikeri.sgw.impl.app.cap.helper.CapProxyRRBEventRequest;
import com.naikeri.sgw.impl.app.cap.helper.CapProxyReleaseCallRequest;
import com.naikeri.sgw.network.layers.CapLayer;
import org.restcomm.protocols.ss7.cap.api.CAPStack;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ConnectRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ContinueRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EstablishTemporaryConnectionRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EventReportBCSMRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.InitialDPRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ReleaseCallRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.RequestReportBCSMEventRequest;


public class CamelBcsmChannelMessages {

  private Object capOperationObject;
  CapProcessingNode capProcessingNode;
  private CAPStack capStack;
  private CAPStack capStackOut;
  private String channelTransId;

  public CamelBcsmChannelMessages(CapLayer capLayerIn, CapLayer capLayerOut, Object requestObject,
      String transactionid) {
    this.capOperationObject = requestObject;
    this.capStack = capLayerIn.getCapStack();
    this.capStackOut = capLayerOut.getCapStack();
    this.channelTransId = transactionid;
  }

  public CapDialogOut initialDP_Request() {
    InitialDPRequest request = (InitialDPRequest) this.capOperationObject;
    CapProxyInitialDPRequest iDpRequest =
        new CapProxyInitialDPRequest(request, capStack, capStackOut, channelTransId);
    return iDpRequest.process();
  }

  // RRB
  public CapDialogOut requestReportBCSMEvent_Request() {
    RequestReportBCSMEventRequest request = (RequestReportBCSMEventRequest) this.capOperationObject;
    CapProxyRRBEventRequest rrbEventRequest = new CapProxyRRBEventRequest(request, channelTransId);
    return rrbEventRequest.process();
  }

  public CapDialogOut continue_Request() {
    ContinueRequest request = (ContinueRequest) this.capOperationObject;
    CapProxyContinueRequest cueRequest =
        new CapProxyContinueRequest(request, channelTransId, capStack);
    return cueRequest.process();
  }

  public CapDialogOut establishTemporaryConnection_Request() {
    EstablishTemporaryConnectionRequest request =
        (EstablishTemporaryConnectionRequest) this.capOperationObject;
    CapProxyETCRequest etcRequest = new CapProxyETCRequest(request, channelTransId, capStack);
    return etcRequest.process();
  }

  public CapDialogOut connect_Request() {
    ConnectRequest request = (ConnectRequest) this.capOperationObject;
    CapProxyConnectRequest connectRequest =
        new CapProxyConnectRequest(request, channelTransId, capStack);
    return connectRequest.process();
  }

  public CapDialogOut eventReportBCSM_Request() {
    EventReportBCSMRequest request = (EventReportBCSMRequest) this.capOperationObject;
    CapProxyEventReportBCSMRequest erbRequest =
        new CapProxyEventReportBCSMRequest(request, channelTransId);
    return erbRequest.process();
  }

  public CapDialogOut releaseCall_Request() {
    ReleaseCallRequest request = (ReleaseCallRequest) this.capOperationObject;
    CapProxyReleaseCallRequest relRequest = new CapProxyReleaseCallRequest(request, channelTransId);
    return relRequest.process();
  }
}
