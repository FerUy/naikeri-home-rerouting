package com.naikeri.sgw.info;

import java.util.HashMap;
import java.util.Map;

/**
 * ServiceKeys
 */
public class ServiceKeys {

  private static ServiceKeys sInstance = null;
  private Map<String, Integer> serviceKeysMap = null;
  public static ServiceKeys getInstance() {
    if (sInstance == null) {
      sInstance = new ServiceKeys();
    }
    return sInstance;
  }

  private ServiceKeys(){
    // add the default for initialDP_Request
    serviceKeysMap = new HashMap<>();
    serviceKeysMap.put("initialDP_Request", 485);
  }

  public Integer getServiceKey(String primitive){
    if (primitive == null) return 0;
    return this.serviceKeysMap.get(primitive);
  }

  public void addServiceKey(String primitive, Integer serviceKey){
    this.serviceKeysMap.put(primitive, serviceKey);
  }
}
