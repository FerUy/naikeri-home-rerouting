package com.naikeri.sgw.impl.rules;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.Logger;

/**
 * MSRNNumbers
 */
public class MSRNNumbers {

  /**
   * InnerMSRNNumbers
   */
  public class MSRNNumberWithIndex {

    private long nextNumber;
    private int currentIndex;

    public MSRNNumberWithIndex(long nextNumber, int currentIndex) {
      this.currentIndex = currentIndex;
      this.nextNumber = nextNumber;
    }

    public long getNextNumber() {
      return nextNumber;
    }

    public void setNextNumber(long nextNumber) {
      this.nextNumber = nextNumber;
    }

    public int getCurrentIndex() {
      return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
      this.currentIndex = currentIndex;
    }

  }

  private static final Logger logger = Logger.getLogger(MSRNNumbers.class);
  // store the rule name in the hash map and the last active number
  private ConcurrentMap<String, MSRNNumberWithIndex> msrnNumbers;
  // keep track of the dialogid, the msrn number and the callingparty number
  // [0] => calling party number
  // [1] => generated msrn number
  private static MSRNNumbers sInstance;

  public MSRNNumbers() {
    msrnNumbers = new ConcurrentHashMap<>();
  }

  public static MSRNNumbers instance() {
    if (sInstance == null)
      sInstance = new MSRNNumbers();
    return sInstance;
  }

  // 1. create the list dynamically
  // 2. add/remove number dynamically
  // 3. use an algorithm to pick the next available number from the list
  // 4. return the available number along side if the previous call needs to be disconnected
  // 5. if all numbers are consumed, the the first in the list should be used.
  public synchronized String getMSRNAddress(String ruleName, String cdPN, String range, Long dialogId,
      String callingPartyNumber) {
    MSRNNumberWithIndex tempNum = this.msrnNumbers.get(ruleName);
    String[] cdpns = Arrays.stream(cdPN.split(",")).map(String::trim).toArray(String[]::new);
    String[] ranges = Arrays.stream(range.split(",")).map(String::trim).toArray(String[]::new);
    if (tempNum != null) {
      long nextNumber = tempNum.getNextNumber() + 1;
      int nextRange = tempNum.getCurrentIndex();
      while (nextRange < cdpns.length) {
        long maxCdpn = Long.parseLong(cdpns[nextRange]) + Long.parseLong(ranges[nextRange]);
        if (nextNumber > maxCdpn) {
          if ((nextRange + 1) >= cdpns.length) {
            // reset to zero
            this.msrnNumbers.put(ruleName, new MSRNNumberWithIndex(Long.parseLong(cdpns[0]), 0));
            break;
          } else {
            nextRange++;
            nextNumber = Long.parseLong(cdpns[nextRange]);
          }
        } else {
          this.msrnNumbers.put(ruleName, new MSRNNumberWithIndex(nextNumber, nextRange));
          break;
        }
      }
      return String.format("%d", tempNum.getNextNumber());
    }
    // use the first from the list to store as the current active msrn number
    long currentNumber = Long.parseLong(cdpns[0]);
    long maxcdpn = currentNumber + Long.parseLong(ranges[0]);
    if (currentNumber == maxcdpn) {
      this.msrnNumbers.put(ruleName, new MSRNNumberWithIndex(currentNumber, 0));
    } else {
      this.msrnNumbers.put(ruleName, new MSRNNumberWithIndex(currentNumber + 1, 0));
    }
    logger.trace(String.format("#A (%s) -> #MSRN(%s)", callingPartyNumber, cdpns[0]));
    return cdpns[0];
  }
}
