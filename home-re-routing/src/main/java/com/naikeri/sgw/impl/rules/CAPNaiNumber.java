package com.naikeri.sgw.impl.rules;


public enum CAPNaiNumber {
  SUBSCRIBER_NUMBER(1), 
  UNKNOWN(2), 
  NATIONAL_SN(3), 
  INTERNATIONAL(4), 
  NETWORK_SPECIFIC_NUMBER(5),
  NRNINSNF(6), 
  NRNIN_SNF(7), 
  NRNCWCDN(8);

  private final int value;

  CAPNaiNumber(final int newValue){
    value = newValue;
  }
  public int getValue() {
    return value;
  }
}
