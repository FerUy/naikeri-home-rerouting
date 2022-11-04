package com.naikeri.sgw.impl.settings.sccp;

import org.restcomm.protocols.ss7.sccp.LongMessageRuleType;

public class SccpLongMessageRule {

  private String name;
  private int id;
  private int firstSpc;
  private int lastSpc;
  private LongMessageRuleType ruleType;

  public SccpLongMessageRule(String name, int id, int firstSpc, int lastSpc, String ruleType){
    this.name = name;
    this.id = id;
    this.firstSpc = firstSpc;
    this.lastSpc = lastSpc;
    // LUDT_ENABLED, LUDT_ENABLED_WITH_SEGMENTATION, XUDT_ENABLED,
    // LONG_MESSAGE_FORBBIDEN
    this.ruleType = LongMessageRuleType.valueOf(ruleType.toUpperCase());
  }

  public String getName() {
    return this.name;
  }

  public int getId() {
    return this.id;
  }

  public int getFirstSpc() {
    return this.firstSpc;
  }

  public int getLastSpc() {
    return this.lastSpc;
  }

  public LongMessageRuleType getRuleType() {
    return this.ruleType;
  }

  @Override
  public String toString() {
    return String.format("[name = %s, id = %d, firstSpc = %d, lastSpc = %d]", name, id, firstSpc, lastSpc);
  }
}