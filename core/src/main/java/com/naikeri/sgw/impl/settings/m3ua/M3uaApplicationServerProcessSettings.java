package com.naikeri.sgw.impl.settings.m3ua;

public class M3uaApplicationServerProcessSettings {

    private String name;
    private String sctpAssociationName;
    private Boolean heartbeatEnabled;

    public M3uaApplicationServerProcessSettings(String name, String sctpAssociationName,
            boolean heartbeatEnabled) {
        this.name = name;
        this.sctpAssociationName = sctpAssociationName;
        this.heartbeatEnabled = heartbeatEnabled;
    }

    public String getName() {
        return name;
    }

    public String getSctpAssociationName() {
        return sctpAssociationName;
    }

    public Boolean isHeartbeatEnabled() {
        return heartbeatEnabled;
    }

    @Override
    public String toString() {
        return String.format("[<%s>: Association name = %s, heartbeat = %s]", name, sctpAssociationName,
                heartbeatEnabled.toString());
    }
}
