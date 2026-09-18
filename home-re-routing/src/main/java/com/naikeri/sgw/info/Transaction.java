package com.naikeri.sgw.info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TransactionMap
 */
public class Transaction {
  private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

  private int initialCapacity = 5000;
  private int maxTransactions = 10000;
  private ConcurrentHashMap<Long, DataElement> dialogMap;
  private ConcurrentHashMap<String, DataElement> dataMap;

  static Transaction sInstance = null;

  public static Transaction getInstance() {
    if (sInstance == null)
      sInstance = new Transaction();
    return sInstance;
  }

  private Transaction() {
    dialogMap = new ConcurrentHashMap<>(this.initialCapacity);
    dataMap = new ConcurrentHashMap<>(this.initialCapacity);
  }

  public void setMaxDialog(int maxTransactions) {
    if (maxTransactions > 0) {
      this.maxTransactions = maxTransactions;
    }
  }

  public DataElement removeDialogData(Long dialogId, Long invokeId) {
    if (dialogId == null)
      return null;
    String id = generateTransId(dialogId, invokeId);
    return dataMap.remove(id);
  }

  public DataElement getDialogData(Long dialogId, Long invokeId) {
    // ensure the key is not null
    if (dialogId == null)
      return null;
    String id = generateTransId(dialogId, invokeId);
    return dataMap.get(id);
  }

  public Boolean setDialogData(Long newDialogId, Long invokeId, DataElement originalRequestObj) {
    if (dataMap.size() >= this.maxTransactions) {
      logger.error("The dialog map size is reached. Storing incoming request will be rejected");
      return false;
    }
    String id = generateTransId(newDialogId, invokeId);
    dataMap.put(id, originalRequestObj);
    return true;
  }

  private String generateTransId(Long dialogId, Long invokeId) {
    return String.format("%d-%d", dialogId, invokeId);
  }

  // =================== dialog id only ==========
  public Boolean setDialogId(Long newDialogId, DataElement originalRequestObj) {
    if (dataMap.size() >= this.maxTransactions) {
      logger.error("The dialog map size is reached. Storing incoming request will be rejected");
      Optional<Long> firstKeyOption = dialogMap.keySet().stream().findFirst();
      if (firstKeyOption.isPresent() && firstKeyOption.get() != null) {
        dialogMap.remove(firstKeyOption.get());
      }
    }
    dialogMap.put(newDialogId, originalRequestObj);
    return true;
  }

  public List<DataElement> removeAllDialogs(Long dialogId) {
    if (dialogId == null)
      return Collections.emptyList();
    Map<Long, DataElement> tempMap = new HashMap<>();
    DataElement dataElement = dialogMap.get(dialogId);
    if (dataElement != null) {
      tempMap.put(dialogId, dataElement);
    } else {
      for (Long i = 0L; i < 10; i++) {
        String id = generateTransId(dialogId, i);
        DataElement nElement = dataMap.get(id);
        if (nElement != null) {
          tempMap.put(dialogId, nElement);
        }
      }
    }
    return new ArrayList<>(tempMap.values());
  }

  public DataElement getDialogId(Long dialogId) {
    // ensure the key is not null
    if (dialogId == null)
      return null;
    return dialogMap.get(dialogId);
  }

  public DataElement removeDialogId(Long dialogId) {
    if (dialogId == null)
      return null;
    return dialogMap.remove(dialogId);
  }
}
