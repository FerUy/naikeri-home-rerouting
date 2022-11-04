package com.naikeri.sgw.impl.rules;

import java.util.Optional;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;

public class PatternSccpAddress{
  private Integer destPointCode = 0;
  private Integer subSystemNumber;
  private String routingIndicator;

  public PatternSccpAddress(Integer dpc, Integer ssn, String ri){
    this.destPointCode = dpc;
    this.subSystemNumber = ssn;
    this.routingIndicator = ri;
  }
  public Optional<Integer> getDestPointCode() {
    return Optional.ofNullable(this.destPointCode);
  }

  public Optional<Integer> getSubSystemNumber(){
    return Optional.ofNullable(this.subSystemNumber);
  }

  public Optional<RoutingIndicator> getRoutingIndicator() {
    if (routingIndicator == null || routingIndicator.isEmpty()) {
      return Optional.empty();
    }
    try {
     RoutingIndicator ri = RoutingIndicator.valueOf(routingIndicator);
     return Optional.of(ri);
    } catch (Exception e) {
      return Optional.empty();
    }
   }
}