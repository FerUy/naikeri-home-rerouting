package com.naikeri.sgw.impl.app.cap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.isup.CalledPartyNumberCap;

public class BcsmCalls {

  // private static List<BcsmCallContent> bcsmCallList = new ArrayList<>();
  private static Map<String, BcsmCallContent> bcsmCallList = new HashMap<String, BcsmCallContent>();
  private static final Logger logger = LogManager.getLogger(BcsmCalls.class);

  public static synchronized BcsmCallContent getBcsmCallContent(String callingPartyNumber,
      String calledPartyNumber) {
    for (BcsmCallContent bcsmCallContent : bcsmCallList.values()) {
      try {
        if (bcsmCallContent.getIdp().getCallingPartyNumber().getCallingPartyNumber().getAddress()
            .equalsIgnoreCase(callingPartyNumber)) {
          Optional<CalledPartyNumberCap> destCalledPartyNumber = bcsmCallContent.getCon()
              .getDestinationRoutingAddress().getCalledPartyNumber().stream()
              .filter(
                  callpartyNumCap -> findCalledPartyNumberMatch(callpartyNumCap, calledPartyNumber))
              .findFirst();

          if (destCalledPartyNumber.isPresent()) {
            return bcsmCallContent;
          }
        }
      } catch (CAPException e) {
        logger.error("CAP exception while handling the BCSM call", e);
      }
    }
    return null;
  }

  private static Boolean findCalledPartyNumberMatch(CalledPartyNumberCap callpartyNumCap,
      String calledPartyNumber) {
    try {
      return callpartyNumCap.getCalledPartyNumber().getAddress()
          .equalsIgnoreCase(calledPartyNumber);
    } catch (CAPException e) {
        logger.error("Exception caught. Error: {}", String.valueOf(e));
    }
    return false;
  }

  public static synchronized void setBcsmCallContent(BcsmCallContent bcsmCallContent) {
    // for (int i = 0; i < bcsmCallList.size(); i++) {
    //   if (bcsmCallList.get(i).getUid().equals(bcsmCallContent.getUid())) {
    //     bcsmCallList.set(i, bcsmCallContent);
    //     break;
    //   }
    // }
    bcsmCallList.put(bcsmCallContent.getUid().toString(), bcsmCallContent);
  }

  public static synchronized boolean addBcsmCallContent(BcsmCallContent bcsmCallContent) {
    bcsmCallList.put(bcsmCallContent.getUid().toString(), bcsmCallContent);
    return true;
  }

  public static synchronized void removeBcsmCallContent(BcsmCallContent bcsmCallContent) {
    bcsmCallList.remove(bcsmCallContent.getUid().toString());
    // for (int i = 0; i < bcsmCallList.size(); i++) {
    //   if (bcsmCallList.get(i).getUid().equals(bcsmCallContent.getUid())) {
    //     bcsmCallList.remove(i);
    //     break;
    //   }
    // }
  }

}
