package com.naikeri.sgw.impl.settings.sccp;

public class SccpRemoteSubSystemNumber {

  int remoteSsnId;
  int remoteSpc;
  int remoteSsn;
  int remoteSsnFlag;
  Boolean markProhibitedWhenSpcResuming;

  @Override
  public String toString() {
    return String.format("[<SSN>]: remoteSsnId = %d, remoteSpc = %d, remoteSsn = %d, remoteSsnFlag = %d", remoteSsnId, remoteSpc, remoteSsn,
            remoteSsnFlag);
  }

  public SccpRemoteSubSystemNumber(int remoteSsnid, int remoteSpc, int remoteSsn, int remoteSsnFlag, Boolean markProhibitedWhenSpcResuming) {
    this.remoteSsnId = remoteSsnid;
    this.remoteSpc = remoteSpc;
    this.remoteSsn = remoteSsn;
    this.remoteSsnFlag = remoteSsnFlag;
    this.markProhibitedWhenSpcResuming = markProhibitedWhenSpcResuming;
  }

  public int getRemoteSsnId() {
    return this.remoteSsnId;
  }

  public int getRemoteSpc() {
    return this.remoteSpc;
  }

  public int getRemoteSsn() {
    return this.remoteSsn;
  }

  public int getRemoteSsnFlag() {
    return this.remoteSsnFlag;
  }

  public Boolean getProhibitWhenSpcResuming() {
    return this.markProhibitedWhenSpcResuming;
  }
}
