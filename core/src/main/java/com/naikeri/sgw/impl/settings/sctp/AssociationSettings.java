package com.naikeri.sgw.impl.settings.sctp;

import java.util.ArrayList;
import java.util.List;
import org.mobicents.protocols.api.IpChannelType;

public class AssociationSettings {

  String name;
  String hostAddress;
  Integer hostPort;
  String peerAddress;
  Integer peerPort;
  Boolean isSctp;
  private List<String> multiHomes = new ArrayList<String>();

  public String getName() {
    return name;
  }

  public String getHostAddress() {
    return hostAddress;
  }

  public Integer getHostPort() {
    return hostPort;
  }

  public String getPeerAddress() {
    return peerAddress;
  }

  public Integer getPeerPort() {
    return peerPort;
  }

  public IpChannelType getIpChannelType() {
    return isSctp ? IpChannelType.SCTP : IpChannelType.TCP;
  }

  public AssociationSettings(String name, String host, String peer, Boolean sctp, String multihome) {
    this.name = name;

    String[] host_c = host.split(":");
    this.hostAddress = host_c[0];
    this.hostPort = Integer.parseInt(host_c[1]);

    String[] peer_c = peer.split(":");
    this.peerAddress = peer_c[0];
    this.peerPort = Integer.parseInt(peer_c[1]);

    this.isSctp = sctp;
    // set the multihomes
    if (multihome != null && !multihome.isEmpty()){
      for(String mhome: multihome.split(",")){
        this.multiHomes.add(mhome);
      }
    }
  }

  @Override
  public String toString() {
    return String.format("[<Association>]: Name = %s, hostAddress = %s, hostPort = %d, peerAddress = %s, peerPort = %d",
        name, hostAddress, hostPort, peerAddress, peerPort);
  }

  public String[] getMultHoming(){
    if (this.multiHomes.size() == 0){
      return null;
    }
    String[] retValue = new String[this.multiHomes.size()];
    for (int i = 0; i < this.multiHomes.size(); i++) {
      retValue[i] = this.multiHomes.get(i);
    }
    return retValue;
  }
}
