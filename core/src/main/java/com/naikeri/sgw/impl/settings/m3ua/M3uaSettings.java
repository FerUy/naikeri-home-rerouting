package com.naikeri.sgw.impl.settings.m3ua;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.naikeri.sgw.api.network.LayerType;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;

public class M3uaSettings implements LayerSettingsInterface {

  public enum TrafficMode {

    STALL("Stall", 0), 
    OVERRIDE("override", 1), 
    LOADSHARE("loadshare", 2), 
    BROADCAST("Broadcast",3);

    private static final Map<String, TrafficMode> BY_NAME = new HashMap<>();
    private static final Map<Integer, TrafficMode> BY_MODE = new HashMap<>();
    static {
      for (TrafficMode traMode : values()) {
        BY_NAME.put(traMode.name.toUpperCase(), traMode);
        BY_MODE.put(traMode.mode, traMode);
      }
    }

    private final String name;
    private final int mode;

    private TrafficMode(String name, int mode) {
      this.name = name;
      this.mode = mode;
    }

    public int getMode() {
      return this.mode;
    }

    public static int getMode(String name) {
      TrafficMode searchMode = BY_NAME.get(name.toUpperCase());
      if (searchMode != null) {
        return searchMode.mode;
      }
      return 2; // load share by default
    }
  }

  private String name = "m3ua";
  private String productName = "naikeri_m3ua";
  private Integer heartbeatTime = 30;
  private String sctpName;
  private boolean isEnable;

  Map<String, M3uaApplicationServerSettings> applicationServers =
      new HashMap<String, M3uaApplicationServerSettings>();

  public String getName() {
    return name;
  }

  public String getProductName() {
    return productName;
  }

  public Integer getHeartbeatTime() {
    return heartbeatTime;
  }

  public M3uaSettings(String name, String productName, Integer heartbeatTime, String sctpName, boolean isEnable) {
    this.name = name;
    this.productName = productName;
    this.heartbeatTime = heartbeatTime;
    this.sctpName = sctpName;
    this.isEnable = isEnable;
  }

  public boolean isEnabled(){
    return this.isEnable;
  }

  public String getSctpName() {
    return this.sctpName;
  }

  public Set<String> getApplicationServerNames() {
    return applicationServers.keySet();
  }

  public M3uaApplicationServerSettings getApplicationServerSettings(String applicationServerName) {
    return applicationServers.containsKey(applicationServerName)
        ? applicationServers.get(applicationServerName)
        : null;
  }

  public void addApplicationServer(String name, String functionality, String exchangeType,
      String ipspType, int routingContext, String trafficMode, int minAspActiveForLoadBalance,
      long networkAppearance) {

    TrafficMode trafficModeType = TrafficMode.LOADSHARE;
    try {
      trafficModeType = TrafficMode.valueOf(trafficMode);
    } catch (Exception e) {
    }

    applicationServers.put(name,
        new M3uaApplicationServerSettings(name, functionality, exchangeType, ipspType,
            routingContext, trafficModeType.getMode(), minAspActiveForLoadBalance,
            networkAppearance));
  }

  public void addApplicationServerRoute(String applicationServerName, int destinationPointCode,
      int originatingPointCode, int serviceIndicator) {
    if (applicationServers.containsKey(applicationServerName))
      applicationServers.get(applicationServerName).setMtpParameters(destinationPointCode,
          originatingPointCode, serviceIndicator);
  }

  public void addApplicationServerProcess(String applicationServerName,
      String applicationServerProcessName, String sctpAssociationName, boolean heartbeatEnabled) {
    if (applicationServers.containsKey(applicationServerName))
      applicationServers.get(applicationServerName).addApplicationServerProcess(
          applicationServerProcessName, sctpAssociationName, heartbeatEnabled);
  }

  @Override
  public String getTransportName() {
    return this.sctpName;
  }

  @Override
  public LayerType getType() {
    return LayerType.M3ua;
  }

  @Override
  public String toString() {
    return String.format("[<%s>: ProductName = %s, heartbeatTime = %d, sctp = %s ]", name, productName, heartbeatTime, sctpName);
  }

}

