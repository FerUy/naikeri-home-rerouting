package com.naikeri.sgw.api.chn;

import com.naikeri.sgw.api.network.LayerInterface;

public interface IChannelHandler {

    /**
     * get layer interface for specific Service Function (e.g. scf)
     * @param serviceFunctionName
     * @return
     */
    LayerInterface getLayerInterface(String serviceFunctionName);
    /**
     * get the layer interface 
     * @return LayerInterface
     */
    LayerInterface getLayerInterface();
    /**
     * This function is called to initialize the channel    
     */
    void channelInitialize(LayerInterface[] layerInterface);

    /**
     * Send Channel message from the channel to the application
     * @param channelMessage ChannelMessage
     */
    void receiveMessageRequest(ChannelMessage channelMessage);

    /**
     * Send channel message from the application back to the channel
     * @param channelMessage ChannelMessage
     * @return
     */
    int sendMessageResponse(ChannelMessage channelMessage);

}
