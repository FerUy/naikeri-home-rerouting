package com.naikeri.sgw.api.app;

import com.naikeri.sgw.api.chn.ChannelMessage;

public interface QueueInterface {
    void send(ChannelMessage element);
    ChannelMessage receive();
}
