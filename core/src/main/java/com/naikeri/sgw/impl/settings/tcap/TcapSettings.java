package com.naikeri.sgw.impl.settings.tcap;

import java.util.ArrayList;
import java.util.List;
import com.naikeri.sgw.api.network.LayerType;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;

public class TcapSettings implements LayerSettingsInterface {

  private String tcapName;
  private String sccpName;
  private boolean isEnabled;
  private Integer ssn;
  private List<Integer> extraSubsystemNumbers = new ArrayList<Integer>();
  // default values
  private int dialogIdleTimeout = 0;
  private int invokeTimeOut = 0;
  private int maxDialogs = 0;

  public List<Integer> getExtraSubsystemNumbers() {
    return extraSubsystemNumbers;
  }

  public TcapSettings(String tcapName, String sccpName, Integer ssn, boolean isEnable) {
    this.tcapName = tcapName;
    this.sccpName = sccpName;
    this.isEnabled = isEnable;
    this.ssn = ssn;
  }

  public Integer getSubSystemNumber() {
    return this.ssn;
  }

  public boolean isEnabled() {
    return this.isEnabled;
  }

  public String getName() {
    return this.tcapName;
  }

  public String getSccpName() {
    return this.sccpName;
  }

  public int getDialogIdleTimeout() {
    return this.dialogIdleTimeout;
  }

  public int getInvokeTimeOut() {
    return this.invokeTimeOut;
  }

  public int getMaxDialogs() {
    return this.maxDialogs;
  }

  public void addExtraSubSystemNumber(int ssn) {
    extraSubsystemNumbers.add(ssn);
  }

  public void setDialogIdleTimeout(int dialogIdleTimeout) {
    this.dialogIdleTimeout = dialogIdleTimeout;
  }

  public void setInvokeTimeOut(int invokeTimeOut) {
    this.invokeTimeOut = invokeTimeOut;
  }

  public void setMaxDialogs(int maxDialogs) {
    this.maxDialogs = maxDialogs;
  }

  @Override
  public String getTransportName() {
    return this.sccpName;
  }

  @Override
  public LayerType getType() {
    return LayerType.Tcap;
  }

  @Override
  public String toString() {
    String extraSSNString = "";
    for (Integer essn : extraSubsystemNumbers) {
      extraSSNString.concat(essn.toString());
    }
    return String.format("[<%s>]: SCCP = '%s', SSN = %s, MaxDialog = %d, extraSsn = '%s'", tcapName, sccpName, ssn, this.maxDialogs, extraSSNString);
  }
}
