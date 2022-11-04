package com.naikeri.sgw.impl.settings;

/**
 * MonitoringLayerObject
 */
public class MonitoringSettings {

  private String m3uaName;
  private String fileName;
  private Integer refreshInterval;
  private Boolean isEnabled;

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public Integer getRefreshInterval() {
    return refreshInterval;
  }

  public void setRefreshInterval(Integer refreshInterval) {
    this.refreshInterval = refreshInterval;
  }

  public Boolean getIsEnabled() {
    return isEnabled;
  }

  public void setIsEnabled(Boolean isEnabled) {
    this.isEnabled = isEnabled;
  }

  public String getM3uaName() {
    return m3uaName;
  }

  public void setM3uaName(String m3uaName) {
    this.m3uaName = m3uaName;
  }

}