package com.naikeri.sgw.impl.settings;

/**
 * ServiceFunctionSetting
 */
public class ServiceFunctionSetting {
  public enum ServiceFunctionType {
    SCF, SSF
  }

  private String name;
  private String serviceType;
  private String layer;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ServiceFunctionType getServiceType() {
    return serviceType.equalsIgnoreCase("ssf") ? ServiceFunctionType.SSF : ServiceFunctionType.SCF;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }

  public String getLayerName() {
    return layer;
  }

  public void setLayerName(String layer) {
    this.layer = layer;
  }
}
