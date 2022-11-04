package com.naikeri.sgw.api.app;

import com.naikeri.sgw.api.chn.ChannelMessage;

public interface IApplication {
    void processMessage(ChannelMessage channelMessage);
}
