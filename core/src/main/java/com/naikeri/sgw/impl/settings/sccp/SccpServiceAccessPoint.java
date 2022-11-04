
package com.naikeri.sgw.impl.settings.sccp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SccpServiceAccessPoint {

  private int id;
  private int mtp3Id;
  private int opc;
  private int ni;
  private int networkId;
  private String localGtDigits;

  @Override
  public String toString() {
    return String.format("[<%d>]: OPC = %d, NI = %d, networkId = %d, Digits = %s, mtp3Id = %d", id, opc, ni, networkId, localGtDigits, mtp3Id);
  }

  private Map<String, SccpMtp3Destination> mtp3DestinationMap = new HashMap<String, SccpMtp3Destination>();

  public SccpServiceAccessPoint(int id, int mtp3Id, int opc, int ni, int networkId,
      String localGtDigits) {
    this.id = id;
    this.mtp3Id = mtp3Id;
    this.opc = opc;
    this.ni = ni;
    this.networkId = networkId;
    this.localGtDigits = localGtDigits;
  }

  public void addMtp3Destination(String name, int sapId, int destId, int firstDpc, int lastDpc,
      int firstSls, int lastSls, int slsMask) {

    if (!mtp3DestinationMap.containsKey(name)) {
      mtp3DestinationMap.put(name,
          new SccpMtp3Destination(sapId, destId, firstDpc, lastDpc, firstSls, lastSls, slsMask));
    }
  }

  public int getID() {
    return this.id;
  }

  public int getMtp3Id() {
    return this.mtp3Id;
  }

  public int getOPC() {
    return this.opc;
  }

  public int getNetworkIndicator() {
    return this.ni;
  }

  public int getNetworkId() {
    return this.networkId;
  }

  public String getLocalGtDigits() {
    return this.localGtDigits;
  }

  public List<SccpMtp3Destination> getMtp3Destination() {
    return new ArrayList<SccpMtp3Destination>(this.mtp3DestinationMap.values());
  }
}
