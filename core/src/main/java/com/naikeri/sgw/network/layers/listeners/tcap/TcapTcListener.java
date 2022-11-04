package com.naikeri.sgw.network.layers.listeners.tcap;

import java.util.UUID;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.chn.IChannelHandler;
import com.naikeri.sgw.network.layers.listeners.MapProxyContants;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.tcap.api.TCListener;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.Dialog;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCBeginIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCContinueIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCEndIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCNoticeIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCPAbortIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCUniIndication;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.TCUserAbortIndication;
import org.restcomm.protocols.ss7.tcap.asn.comp.Invoke;

/**
 * TcapTcListener
 */
public class TcapTcListener implements TCListener {

  private static final Logger logger = Logger.getLogger(TcapTcListener.class);
  private IChannelHandler channelHandler;

  public TcapTcListener(IChannelHandler channelHandler) {
    logger.debug("Listening for TCAP messages....");
    this.channelHandler = channelHandler;
  }

  private ChannelMessage getMessage(String tcapEventType) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter(MapProxyContants.TCAP_MESSAGE_TYPE, tcapEventType);
    return channelMessage;
  }

  @Override
  public void onTCUni(TCUniIndication ind) {
    ChannelMessage channelMessage = getMessage("onTCUni");
    channelMessage.setParameter(MapProxyContants.TCAP_MESSAGE, ind);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onTCBegin(TCBeginIndication ind) {
    ChannelMessage channelMessage = getMessage("onTCBegin");
    channelMessage.setParameter(MapProxyContants.TCAP_MESSAGE, ind);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onTCContinue(TCContinueIndication ind) {
    ChannelMessage channelMessage = getMessage("onTCContinue");
    channelMessage.setParameter(MapProxyContants.TCAP_MESSAGE, ind);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onTCEnd(TCEndIndication ind) {
    ChannelMessage channelMessage = getMessage("onTCEnd");
    channelMessage.setParameter(MapProxyContants.TCAP_MESSAGE, ind);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onTCUserAbort(TCUserAbortIndication ind) {
    ChannelMessage channelMessage = getMessage("onTCUserAbort");
    channelMessage.setParameter(MapProxyContants.TCAP_MESSAGE, ind);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onTCPAbort(TCPAbortIndication ind) {
    ChannelMessage channelMessage = getMessage("onTCPAbort");
    channelMessage.setParameter(MapProxyContants.TCAP_MESSAGE, ind);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onTCNotice(TCNoticeIndication ind) {
    ChannelMessage channelMessage = getMessage("onTCNotice");
    channelMessage.setParameter(MapProxyContants.TCAP_MESSAGE, ind);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogReleased(Dialog d) {
    ChannelMessage channelMessage = getMessage("onDialogReleased");
    channelMessage.setParameter(MapProxyContants.TCAP_MESSAGE_DIALOG, d);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onInvokeTimeout(Invoke tcInvokeRequest) {
    ChannelMessage channelMessage = getMessage("onInvokeTimeout");
    channelMessage.setParameter(MapProxyContants.TCAP_MESSAGE_DIALOG, tcInvokeRequest);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }

  @Override
  public void onDialogTimeout(Dialog d) {
    ChannelMessage channelMessage = getMessage("onDialogTimeout");
    channelMessage.setParameter(MapProxyContants.TCAP_MESSAGE_DIALOG, d);
    this.channelHandler.receiveMessageRequest(channelMessage);
  }
}
