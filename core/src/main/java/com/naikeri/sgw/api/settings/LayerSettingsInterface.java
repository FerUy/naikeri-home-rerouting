package com.naikeri.sgw.api.settings;

import com.naikeri.sgw.api.network.LayerType;

/**
 * LayerSettingsInterface
 */
public interface LayerSettingsInterface {

  public String getTransportName();

  public LayerType getType();

  public String getName();
  
  public boolean isEnabled();
  
}