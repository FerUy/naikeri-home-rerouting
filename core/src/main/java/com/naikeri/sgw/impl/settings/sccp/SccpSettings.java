package com.naikeri.sgw.impl.settings.sccp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.naikeri.sgw.api.network.LayerType;
import com.naikeri.sgw.api.settings.LayerSettingsInterface;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

public class SccpSettings implements LayerSettingsInterface {

  private String name;
  // This configuration ID must initiate and increase from 1 not 0, if not it is not going to
  // initiate the SAP
  private int id;
  private String m3uaName;
  private boolean isEnabled;
  private Map<String, SccpRemoteSpcSettings> remoteSpcMap = new HashMap<String, SccpRemoteSpcSettings>();
  private Map<String, SccpRemoteSubSystemNumber> remoteSSNMap = new HashMap<String, SccpRemoteSubSystemNumber>();
  private Map<String, SccpServiceAccessPoint> serviceAccessPointsMap = new HashMap<String, SccpServiceAccessPoint>();
  // private Map<String, SccpMtp3Destination> Mtp3DestinationsMap = new HashMap<String, SccpMtp3Destination>()
  private Map<String, SccpLongMessageRule> longMessageRuleMap = new HashMap<String, SccpLongMessageRule>();
  private Map<String, SccpRoutingAddress> routingAddressMap = new HashMap<String, SccpRoutingAddress>();
  private Map<String, SccpRules> sccpRulesMap = new HashMap<String, SccpRules>();
  private SccpServiceAccessPoint sap;
  private ParameterFactoryImpl factory = new ParameterFactoryImpl();

  @Override
  public String toString() {
    return String.format("[<%s>]: m3ua = %s, id = %d", name, m3uaName, id);
  }

  // private SccpAddress sccpAddress
  public SccpSettings(String name, String m3uaName, int id, boolean isEnabled) {
    this.name = name;
    this.m3uaName = m3uaName;
    this.id = id;
    this.isEnabled = isEnabled;

  }

  public boolean isEnabled() {
    return this.isEnabled;
  }

  public String getName() {
    return name;
  }

  public String getM3uaName() {
    return this.m3uaName;
  }

  public void addRemoteSpc(String name, int remoteSpcId, int remoteSpc, int remoteSpcFlag,
      int mask) {
    if (!remoteSpcMap.containsKey(name)) {
      remoteSpcMap.put(name,
          new SccpRemoteSpcSettings(remoteSpcId, remoteSpc, remoteSpcFlag, mask));
    }
  }

  public void addRemoteSsn(String name, int remoteSsnid, int remoteSpc, int remoteSsn,
      int remoteSsnFlag, boolean markProhibitedWhenSpcResuming) {
    if (!remoteSSNMap.containsKey(name)) {
      remoteSSNMap.put(name, new SccpRemoteSubSystemNumber(remoteSsnid, remoteSpc, remoteSsn,
          remoteSsnFlag, markProhibitedWhenSpcResuming));
    }
  }

  public void addMtp3ServiceAccessPoint(String name, int id, int mtp3Id, int opc, int ni,
      int networkId, String localGtDigits) {
    if (!serviceAccessPointsMap.containsKey(name)) {
      this.sap = new SccpServiceAccessPoint(id, mtp3Id, opc, ni, networkId, localGtDigits);
      serviceAccessPointsMap.put(name, sap);
    }
  }

  public void addMtp3ServiceAccessPoint(String name, SccpServiceAccessPoint sap) {
    serviceAccessPointsMap.put(name, sap);
  }

  public void addMtp3Destination(String name, int sapid, int destid, int firstDpc, int lastDpc,
      int firstSls, int lastSls, int slsMask) {

    this.sap.addMtp3Destination(name, sapid, destid, firstDpc, lastDpc, firstSls, lastSls, slsMask);
  }

  public void addLongMessageRule(String name, int id, int firstSpc, int lastSpc, String ruleType) {
    if (!longMessageRuleMap.containsKey(name)) {
      longMessageRuleMap.put(name, new SccpLongMessageRule(name, id, firstSpc, lastSpc, ruleType));
    }
  }

  public void addRoutingAddress(String name, SccpRoutingAddress routingAddress) {
    routingAddressMap.put(name, routingAddress);
  }

  /**
   * This method is deprecated. Use the other overloaded method
   * 
   * @param name
   * @param id
   * @param ruleType
   * @param algo
   * @param originationType
   * @param pattern
   * @param mask
   * @param pAddressId
   * @param sAddressId
   * @param newCallingPartyAddressId
   * @param networkId
   * @param patternCallingAddress
   * @deprecated This is not a full implemenation. It uses static variables which is now deprecated.
   *             Use the other method
   */
  public void addSccpRules(String name, int id, String ruleType, String algo, String originationType, String pattern, String mask,
                           int pAddressId, int sAddressId, Integer newCallingPartyAddressId, int networkId, SccpAddress patternCallingAddress) {
    if (!sccpRulesMap.containsKey(name)) {
      // TO DO: Gather global variable on translation type, numbering plan, encoding schema
      // and nature of address
      EncodingScheme ec = new BCDEvenEncodingScheme();
      GlobalTitle gt = factory.createGlobalTitle(pattern, 0, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, ec,
              NatureOfAddress.INTERNATIONAL);
      // TO DO: creating pattern, please gather properly the Routing Indicator
      SccpAddress Pattern = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0, 0);
      sccpRulesMap.put(name, new SccpRules(id, ruleType, algo, originationType, Pattern, mask, pAddressId, sAddressId,
              newCallingPartyAddressId, networkId, patternCallingAddress));
    }
  }

  public void addSccpRules(String name, SccpRules sccpRules) {
    sccpRulesMap.put(name, sccpRules);
  }

  public int getId() {
    return this.id;
  }

  public List<SccpRemoteSpcSettings> getRemoteSpc() {
    return new ArrayList<SccpRemoteSpcSettings>(this.remoteSpcMap.values());
  }

  public List<SccpRemoteSubSystemNumber> getRemoteSsn() {
    return new ArrayList<SccpRemoteSubSystemNumber>(this.remoteSSNMap.values());
  }

  public List<SccpServiceAccessPoint> getServiceAccessPoint() {
    return new ArrayList<SccpServiceAccessPoint>(this.serviceAccessPointsMap.values());
  }

  public List<SccpLongMessageRule> getLongMessageRules() {
    return new ArrayList<SccpLongMessageRule>(longMessageRuleMap.values());
  }

  public List<SccpRoutingAddress> getRoutingAddresses() {
    return new ArrayList<SccpRoutingAddress>(routingAddressMap.values());
  }

  public List<SccpRules> getRules() {
    return new ArrayList<SccpRules>(sccpRulesMap.values());
  }

  @Override
  public String getTransportName() {
    return this.m3uaName;
  }

  @Override
  public LayerType getType() {
    return LayerType.Sccp;
  }
}
