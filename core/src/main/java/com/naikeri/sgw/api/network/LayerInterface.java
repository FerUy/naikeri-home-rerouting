package com.naikeri.sgw.api.network;

import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import com.naikeri.sgw.impl.chn.ChannelHandler;

/**
 * LayerInterface
 */
public interface LayerInterface {

  void setChannelHandler(ChannelHandler channelHandler);

  String getName();

  void stop();

  LayerSettingsInterface getSetting();
}