package com.naikeri.sgw.info;

import com.naikeri.sgw.impl.app.cap.BcsmCallContent;

/**
 * BcsmCallLegs
 */
public class BcsmCallLegs {

  private final BcsmCallContent leg1CallContent;
  private final BcsmCallContent leg2CallContent;

  public BcsmCallLegs(BcsmCallContent leg1CallContent, BcsmCallContent leg2CallContent) {
    this.leg1CallContent = leg1CallContent;
    this.leg2CallContent = leg2CallContent;
  }
  public BcsmCallContent getLeg1CallContent() {
    return leg1CallContent;
  }

  public BcsmCallContent getLeg2CallContent() {
    return leg2CallContent;
  }
}