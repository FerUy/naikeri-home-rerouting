package com.naikeri.sgw.impl.settings.map;

import com.naikeri.sgw.api.network.LayerType;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;

public class MapSettings implements LayerSettingsInterface {

  private String name;
  private String tcap;
  private boolean isEnabled;

  @Override
  public String toString() {
    return String.format("[<%s>]: tcap = %s", name, tcap);
  }

  public String getName() {
    return name;
  }

  public boolean isEnabled() {
    return this.isEnabled;
  }

  public MapSettings(String name, String tcap, boolean isEnabled) {
    this.name = name;
    this.tcap = tcap;
    this.isEnabled = isEnabled;
  }

  public String getTcapName() {
    return this.tcap;
  }

  @Override
  public String getTransportName() {
    return this.tcap;
  }

  @Override
  public LayerType getType() {
    return LayerType.Map;
  }
}
