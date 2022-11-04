package com.naikeri.sgw.impl.settings.m3ua;

import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.IPSPType;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.NetworkAppearance;
import org.restcomm.protocols.ss7.m3ua.parameter.ParameterFactory;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class M3uaApplicationServerSettings {

  private final ParameterFactory parameterFactory = new ParameterFactoryImpl();

  Map<String, M3uaApplicationServerProcessSettings> applicationServerProcesses =
      new HashMap<String, M3uaApplicationServerProcessSettings>();

  String name;
  Functionality functionality;
  ExchangeType exchangeType;
  IPSPType ipspType;
  RoutingContext routingContext;
  TrafficModeType trafficMode;
  int minAspActiveForLoadBalance;
  NetworkAppearance networkAppearance;
  Integer destinationPointCode;
  Integer originPointCode;
  Integer serviceIndicator;

  public String getName() {
    return name;
  }

  public Functionality getFunctionality() {
    return functionality;
  }

  public ExchangeType getExchangeType() {
    return exchangeType;
  }

  public IPSPType getIpspType() {
    return ipspType;
  }

  public RoutingContext getRoutingContext() {
    return routingContext;
  }

  public TrafficModeType getTrafficModeType() {
    return trafficMode;
  }

  public Integer getMinAspActiveForLoadBalancing() {
    return minAspActiveForLoadBalance;
  }

  public NetworkAppearance getNetworkAppearance() {
    return networkAppearance;
  }

  public Integer getDestinationPointCode() {
    return destinationPointCode;
  }

  public Integer getOriginPointCode() {
    return originPointCode;
  }

  public Integer getServiceIndicator() {
    return serviceIndicator;
  }

  public M3uaApplicationServerSettings(String name, String functionality, String exchangeType,
      String ipspType, int routingContext, int trafficMode, int minAspActiveForLoadBalance,
      long networkAppearance) {
    this.name = name;

    this.functionality = Functionality.getFunctionality(functionality);
    this.exchangeType = ExchangeType.getExchangeType(exchangeType);
    this.ipspType = IPSPType.getIPSPType(ipspType);

    this.routingContext = parameterFactory.createRoutingContext(new long[] {routingContext});

    this.trafficMode = parameterFactory.createTrafficModeType(trafficMode);

    this.minAspActiveForLoadBalance = minAspActiveForLoadBalance;

    this.networkAppearance = parameterFactory.createNetworkAppearance(networkAppearance);
  }

  public Set<String> getProcesess() {
    return applicationServerProcesses.keySet();
  }

  public M3uaApplicationServerProcessSettings getApplicationServerProcess(
      String m3uaApplicationServerProcessName) {
    return applicationServerProcesses.get(m3uaApplicationServerProcessName);
  }

  public void setMtpParameters(int destinationPointCode, int originPointCode,
      int serviceIndicator) {
    this.destinationPointCode = destinationPointCode;
    this.originPointCode = originPointCode;
    this.serviceIndicator = serviceIndicator;
  }

  public void addApplicationServerProcess(String applicationServerProcessName,
      String sctpAssociationName, boolean heartbeatEnabled) {
    applicationServerProcesses.put(applicationServerProcessName,
        new M3uaApplicationServerProcessSettings(applicationServerProcessName, sctpAssociationName,
            heartbeatEnabled));
  }

  @Override
  public String toString() {
    return String.format(
        "[<%s>: Functionality = %s, exchangeType = %s, dpc = %d, opc = %d, si = %d, rc = %s]", name,
        functionality.toString(), exchangeType.toString(), destinationPointCode, originPointCode,
        serviceIndicator, routingContext.toString());
  }
}
