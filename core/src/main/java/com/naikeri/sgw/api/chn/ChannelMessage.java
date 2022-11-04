package com.naikeri.sgw.api.chn;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChannelMessage {

    private String transactionId;
    private String originId;

    private Map<String, Object> payloadParameters = new HashMap<String, Object>();

    public String getTransactionId() {
        return transactionId;
    }

    public ChannelMessage(String originId) {
        transactionId = UUID.randomUUID().toString();
        this.originId = originId;
    }

    public ChannelMessage(String transactionId, String originId) {
        this.transactionId = transactionId;
        this.originId = originId;
    }

    public ChannelMessage(String transactionId, String originId, Map<String, Object> payloadParameters) {
        this.payloadParameters = payloadParameters;
        this.transactionId = transactionId;
        this.originId = originId;
    }

    public void setParameter(String name, Object value) {
        if (payloadParameters.containsKey(name))
            payloadParameters.replace(name, value);
        else
            payloadParameters.put(name, value);
    }

    public Object getParameter(String name) {
        return payloadParameters.get(name);
    }

    @Override
    public String toString() {
        return String.format("[tid = %s, origin = %s]", transactionId, originId);
    }

}
