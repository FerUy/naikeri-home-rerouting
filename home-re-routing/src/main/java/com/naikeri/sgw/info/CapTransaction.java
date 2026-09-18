package com.naikeri.sgw.info;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.naikeri.sgw.impl.app.cap.BcsmCallContent;

/**
 * CapTransaction
 */
public class CapTransaction {

  private static final Logger logger = LoggerFactory.getLogger(CapTransaction.class);
  private int initialCapacity = 1000;
  private int maxTransaction = 10000;

  private ConcurrentHashMap<Long, BcsmCallContent> scftransaction;
  private ConcurrentHashMap<Long, BcsmCallContent> ssftransaction;
  private ConcurrentHashMap<String, BcsmCallContent> msrnMapping;
  private ConcurrentHashMap<Long, BcsmCallContent> leg2Transaction;
  private static CapTransaction sInstance;

  public CapTransaction() {
    scftransaction = new ConcurrentHashMap<>(this.initialCapacity);
    ssftransaction = new ConcurrentHashMap<>(this.initialCapacity);
    leg2Transaction = new ConcurrentHashMap<>(this.initialCapacity);
    msrnMapping = new ConcurrentHashMap<>(this.initialCapacity);

  }

  public static CapTransaction instance() {
    if (sInstance == null) {
      sInstance = new CapTransaction();
    }
    return sInstance;
  }

  public void setMaxTransaction(int maxTransaction) {
    if (maxTransaction > this.maxTransaction)
      this.maxTransaction = maxTransaction;
  }

  public synchronized void setSCFSSFBcsmCallContent(Long scfdialogId, Long ssfDialogId,
      BcsmCallContent callContent) {
    if (scfdialogId == null || ssfDialogId == null)
      return;
    this.scftransaction.put(scfdialogId, callContent);
    this.ssftransaction.put(ssfDialogId, callContent);
  }

  public synchronized void updateSSFBcsmCallContent(Long ssfdialogId, BcsmCallContent callContent) {
    if (ssfdialogId == null)
      return;
    this.ssftransaction.put(ssfdialogId, callContent);
  }

  public synchronized void updateSCFBcsmCallContent(Long scfdialogId, BcsmCallContent callContent){
    if (scfdialogId == null) return;
    this.scftransaction.put(scfdialogId, callContent);
  }

  public synchronized BcsmCallContent removSCFCallContent(Long scfdialogId){
    return getScfBcsmCallContent(scfdialogId, true);
  }
  public synchronized BcsmCallContent getSSFBcsmCallContent(Long ssfdialogId, Boolean isRemove) {
    if (ssfdialogId == null)
      return null;
    if (Boolean.TRUE.equals(isRemove)) {
      return this.ssftransaction.remove(ssfdialogId);
    }
    return this.ssftransaction.get(ssfdialogId);
  }

  public synchronized BcsmCallContent getScfBcsmCallContent(Long scfdialogId, Boolean isRemove) {
    if (scfdialogId == null)
      return null;
    if (Boolean.TRUE.equals(isRemove)) {
      return this.scftransaction.remove(scfdialogId);
    }
    return this.scftransaction.get(scfdialogId);
  }

  public synchronized BcsmCallContent delSCFBcsmCallContent(Long scfdialogId) {
    if (scfdialogId == null)
      return null;
    return this.scftransaction.remove(scfdialogId);
  }

  public synchronized void setMsrnTransaction(String callingPartyNumber, String msrnNumber,
      BcsmCallContent callcontent) {
    String key = "";
    if (callingPartyNumber != null && !callingPartyNumber.isEmpty()) {
      key = callingPartyNumber;
    }
    if (msrnNumber != null) {
      key = String.format("%s-%s", callingPartyNumber, msrnNumber);
    }
    if (key.isEmpty())
      return;
    this.msrnMapping.put(key, callcontent);
  }

  public synchronized BcsmCallContent getMsrnMapping(String callingPartyNumber, String msrnNumber,
      Boolean isRemove) {
    logger.trace("CallingNumber = " + callingPartyNumber + ", MSRN = " + msrnNumber + ", Remove = "
        + isRemove);
    String key = "";
    if (callingPartyNumber != null && !callingPartyNumber.isEmpty()) {
      key = callingPartyNumber;
    }
    if (msrnNumber != null) {
      key = String.format("%s-%s", callingPartyNumber, msrnNumber);
    }
    if (key.isEmpty())
      return null;
    if (Boolean.TRUE.equals(isRemove)) {
      return this.msrnMapping.remove(key);
    }
    return this.msrnMapping.get(key);
  }

  public synchronized void setLeg2BcsmCallContent(Long dialogId, BcsmCallContent callContent) {
    if (dialogId == null || callContent == null)
      return;
    this.leg2Transaction.put(dialogId, callContent);
  }

  public synchronized BcsmCallContent getLeg2BcsmCall(Long dialogId) {
    return getLeg2BcsmCall(dialogId, false);
  }

  public synchronized BcsmCallContent removeLeg2BcsmCall(Long dialogId) {
    return getLeg2BcsmCall(dialogId, true);
  }

  public synchronized BcsmCallContent getLeg2BcsmCall(Long dialogId, Boolean isRemove) {
    logger.trace("DialogId = " + dialogId + ", Remove = " + isRemove);
    if (dialogId == null)
      return null;
    if (Boolean.TRUE.equals(isRemove)) {
      return this.leg2Transaction.remove(dialogId);
    }
    return this.leg2Transaction.get(dialogId);
  }

}
