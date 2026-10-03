package com.naikeri.sgw.impl.app.map;

import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.network.layers.MapLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPMessageType;

/**
 * MapProxyBuilder
 */
public class MapProxyBuilder {

  private static final Logger logger = LogManager.getLogger(MapProxyBuilder.class);

  public static class Builder {
    private MAPMessageType messageType;
    private MapLayer map;
    private Object message;
    private String transactionId;

    public Builder setChannelMessage(ChannelMessage channelMessage) {
      try {
        String messagetype = (String) channelMessage.getParameter("messageType");
        // check the message
        Object requestMessage = channelMessage.getParameter("message");
        if (requestMessage != null) {
          this.message = requestMessage;
        }
        // The type stands on its own. It used to be set only alongside the MAP message, so a node
        // built from a channel message carrying the type alone had none, and every rule lookup
        // stopped at "Unknown Message Type". Dialogue signals carry names that aren't MAP types.
        if (messagetype != null) {
          try {
            this.messageType = MAPMessageType.valueOf(messagetype);
          } catch (IllegalArgumentException notAMapMessageType) {
            logger.debug("'{}' is not a MAP message type; the node has none", messagetype);
          }
        }
        this.transactionId = channelMessage.toString();

      } catch (Exception ex) {
        logger.error("Exception: Message Type: '{}'. Error {}", messageType, ex);
      }
      return this;
    }

    public Builder setMapLayer(LayerInterface channelParameter) {
      this.map = (MapLayer) channelParameter;
      return this;
    }

    public MapProcessingNode buildMapProcessingNode() {
      MapProcessingNode mapProc = new MapProcessingNode();
      mapProc.setMessageType(messageType);
      mapProc.setMapLayer(map);
      mapProc.setMessage(message);
      mapProc.setTransactionId(transactionId);
      return mapProc;
    }
  }
  private MapProxyBuilder(){
    throw new IllegalStateException("Private Constructor");
  }
}
