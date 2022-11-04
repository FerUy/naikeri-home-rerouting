
package com.naikeri.sgw.impl.settings.sccp;

public class SccpRemoteSpcSettings {

  int remoteSpcId;
  int remoteSpc;
  int remoteSpcFlag;
  int mask;

  @Override
  public String toString() {
    return String.format("[<SPC>]: remoteSpcId = %d, remoteSpc = %s, remoteSpcFlag = %d, mask = %d", remoteSpcId,
        remoteSpc, remoteSpcFlag, mask);
  }

  public SccpRemoteSpcSettings(int remoteSpcId, int remoteSpc, int remoteSpcFlag, int mask) {
    this.remoteSpcId = remoteSpcId;
    this.remoteSpc = remoteSpc;
    this.remoteSpcFlag = remoteSpcFlag;
    this.mask = mask;
  }

  public int getRemoteSpcId() {
    return this.remoteSpcId;
  }

  public int getRemoteSpc() {
    return this.remoteSpc;
  }

  public int getRemoteSpcFlag() {
    return this.remoteSpcFlag;
  }

  public int getMask() {
    return this.mask;
  }
}
