package com.naikeri.sgw.impl.settings.sccp;

import org.restcomm.protocols.ss7.sccp.LoadSharingAlgorithm;
import org.restcomm.protocols.ss7.sccp.OriginationType;
import org.restcomm.protocols.ss7.sccp.RuleType;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

public class SccpRules {

  private int id;
  private RuleType ruleType;
  private LoadSharingAlgorithm loadSharingAlgorithm;
  private OriginationType originationType;
  private SccpAddress pattern;
  private String mask;
  private int pAddressId;
  private int sAddressId;
  private Integer newCallingPartyAddressAddressId;
  private int networkId;
  private SccpAddress patternCallingAddress;

  public SccpRules (int id, String ruleType, String algo, String originationType, SccpAddress pattern,
            String mask, int pAddressId, int sAddressId, Integer newCallingPartyAddressAddressId, int networkId,
            SccpAddress patternCallingAddress){
    this.id = id;
    this.ruleType = RuleType.valueOf(ruleType.toUpperCase());
    this.loadSharingAlgorithm = LoadSharingAlgorithm.valueOf(algo);
    this.originationType = OriginationType.valueOf(originationType.toUpperCase());
    this.pattern = pattern;
    this.mask = mask;
    this.pAddressId = pAddressId;
    this.sAddressId = sAddressId;
    this.newCallingPartyAddressAddressId = newCallingPartyAddressAddressId;
    this.networkId = networkId;
    this.patternCallingAddress = patternCallingAddress;
  }

  public int getID() {
    return this.id;
  }

  public RuleType getRuleType() {
    return this.ruleType;
  }

  public LoadSharingAlgorithm getLoadSharingAlgorithm() {
    return this.loadSharingAlgorithm;
  }

  public OriginationType getOriginationType() {
    return this.originationType;
  }

  public SccpAddress getSccpPatternAddress(){
    return this.pattern;
  }

  public String getMask(){
    return this.mask;
  }

  public int getPAddressId() {
    return this.pAddressId;
  }

  public int getSAddressId() {
    return this.sAddressId;
  }

  /**
   * get the calling party address id
   * @return return null if the value is not defined
   */
  public Integer getCallingPartyAddressAddressId() {
    return this.newCallingPartyAddressAddressId;
  }

  public int getNetworkId() {
    return this.networkId;
  }

  public SccpAddress getPatternCallingAddress() {
    return this.patternCallingAddress;
  }

  @Override
  public String toString() {
    return String.format("[id = %d, ruleType = %s]", id, ruleType.toString());
  }
}