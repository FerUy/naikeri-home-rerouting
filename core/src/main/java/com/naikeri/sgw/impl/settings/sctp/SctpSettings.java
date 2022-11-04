package com.naikeri.sgw.impl.settings.sctp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.naikeri.sgw.api.network.LayerType;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;

public class SctpSettings implements LayerSettingsInterface {

  String sctpName;
  Boolean singleThread;
  Integer connectDelay;
  private boolean isEnabled;
  Map<String, AssociationSettings> associations = new HashMap<String, AssociationSettings>();
  Map<String, SctpServerSettings> servers = new HashMap<String, SctpServerSettings>();

  public String getName() {
    return sctpName;
  }

  public Boolean isSingleThread() {
    return singleThread;
  }

  public Integer getConnectDelay() {
    return connectDelay;
  }

  public SctpSettings(String name, Boolean singleThread, int connectDelay, boolean isEnable) {
    sctpName = name;
    this.singleThread = singleThread;
    this.connectDelay = connectDelay;
    this.isEnabled = isEnable;
  }

  public boolean isEnabled() {
    return this.isEnabled;
  }

  public void addAssociation(String name, String host, String peer, Boolean sctp, String multihome) {
    associations.put(name, new AssociationSettings(name, host, peer, sctp, multihome));
  }

  public void addServerAssociation(String serverName, String associationName, String peer, Boolean sctp, String multihome) {
    servers.get(serverName).addAssociation(associationName, peer, sctp, multihome);
  }

  public void addServer(String name, String host, Boolean sctp, Boolean anonymousConnections, int concurrentConnections, String multihome) {
    servers.put(name,
        new SctpServerSettings(name, host, sctp, anonymousConnections, concurrentConnections,
            multihome));
  }

  public Set<String> getAssociationNames() {
    return associations.keySet();
  }

  public AssociationSettings getAssociationSettings(String associationName) {
    return associations.containsKey(associationName) ? associations.get(associationName) : null;
  }

  public Collection<SctpServerSettings> getServers() {
    return servers.values();
  }

  @Override
  public String getTransportName() {
    return null;
  }

  @Override
  public LayerType getType() {
    return LayerType.Sctp;
  }

  @Override
  public String toString() {
    return String.format("[<SctpSetting>]: name = %s SingleThread = %s, ConnectDelay = %d",
        sctpName, singleThread.toString(), connectDelay);
  }
}
