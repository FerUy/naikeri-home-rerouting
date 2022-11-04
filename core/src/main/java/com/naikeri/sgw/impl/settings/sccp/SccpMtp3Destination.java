package com.naikeri.sgw.impl.settings.sccp;

public class SccpMtp3Destination {

  private int sapId;
  private int destId;
  private int firstDpc;
  private int lastDpc;
  private int firstSls;
  private int lastSls;
  private int slsMask;

  public SccpMtp3Destination(int sapId, int destId, int firstDpc, int lastDpc, int firstSls, int lastSls, int slsMask){
    this.sapId = sapId;
    this.destId = destId;
    this.firstDpc = firstDpc;
    this.lastDpc = lastDpc;
    this.firstSls = firstSls;
    this.lastSls = lastSls;
    this.slsMask = slsMask;
  }

  public int getSapId() {
    return this.sapId;
  }

  public int getDestinationId() {
    return this.destId;
  }

  public int getFirstDpc() {
    return this.firstDpc;
  }

  public int getLastDpc() {
    return this.lastDpc;
  }

  public int getFirstSls() {
    return this.firstSls;
  }

  public int getLastSls() {
    return this.lastSls;
  }

  public int getSlsMask() {
    return this.slsMask;
  }

}