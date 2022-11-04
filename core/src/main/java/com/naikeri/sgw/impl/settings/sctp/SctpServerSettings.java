package com.naikeri.sgw.impl.settings.sctp;

import org.mobicents.protocols.api.IpChannelType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SctpServerSettings {

  String name;
  String hostAddress;
  Integer hostPort;
  Boolean isSctp;
  Boolean acceptAnonymousConnections;
  Integer maxConcurrentConnections;
  Map<String, AssociationSettings> associations = new HashMap<String, AssociationSettings>();
  private List<String> multihoming = new ArrayList<String>();

  public String getName() {
    return name;
  }

  public String getHostAddress() {
    return hostAddress;
  }

  public Integer getHostPort() {
    return hostPort;
  }

  public IpChannelType getIpChannelType() {
    return isSctp ? IpChannelType.SCTP : IpChannelType.TCP;
  }

  public Boolean acceptsAnonymousConnections() {
    return acceptAnonymousConnections;
  }

  public Integer getMaxConcurrentConnections() {
    return maxConcurrentConnections;
  }

  public SctpServerSettings(String name, String host, boolean sctp, boolean anonymousConnections,
      int maxConcurrentConnections, String multihome) {
    this.name = name;

    String[] host_c = host.split(":");
    this.hostAddress = host_c[0];
    this.hostPort = Integer.parseInt(host_c[1]);

    isSctp = sctp;
    acceptAnonymousConnections = anonymousConnections;
    this.maxConcurrentConnections = maxConcurrentConnections;
    if (multihome != null && !multihome.isEmpty()) {
      for (String mhome : multihome.split(",")) {
        this.multihoming.add(mhome);
      }
    }
  }

  public void addAssociation(String name, String peer, Boolean sctp, String multihome) {
    associations.put(name, new AssociationSettings(name, getHostAddress() + ":" + getHostPort(),
        peer, sctp, multihome));
  }

  public Set<String> getAssociationNames() {
    return associations.keySet();
  }

  public AssociationSettings getAssociationSettings(String associationName) {
    return associations.containsKey(associationName) ? associations.get(associationName) : null;
  }

  @Override
  public String toString() {
    String printmultihome = "";
    for (String mhome : this.multihoming) {
      printmultihome.concat(mhome);
    }

    return String.format("[<SCTP Server Settings>]: Name = %s, hostAddress = %s, hostPort = %d, maxConcurrentConnections = %d, multihome = %s",
            name, hostAddress, hostPort, +maxConcurrentConnections, printmultihome);
  }

  public String[] getMultHoming() {
    if (this.multihoming.size() == 0)
      return null;
    String[] mArray = new String[this.multihoming.size()];
    for (int i = 0; i < this.multihoming.size(); i++) {
      mArray[i] = this.multihoming.get(i);
    }
    return mArray;
  }
}
