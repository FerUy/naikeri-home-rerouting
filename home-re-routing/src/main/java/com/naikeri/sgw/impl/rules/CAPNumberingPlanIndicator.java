package com.naikeri.sgw.impl.rules;

/**
 * CAPNumberingPlanIndicator
 */
public enum CAPNumberingPlanIndicator {
  ISDN(1),
  DATA(3),
  TELEX(4);

  private final int value;

  CAPNumberingPlanIndicator(final int newValue) {
    value = newValue;
  }
  
  public int getValue (){
    return value;
  }
}