package com.naikeri.sgw.info;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CapProxyCdrRecords {

  private static CapProxyCdrRecords sInstance = null;
  private final Map<Long, Map<String, Object>> capRecords;

  /**
   * Get an instance of the CapProxyCdrRecords
   *
   * @return CapProxyCdrRecords
   */
  public static synchronized CapProxyCdrRecords getInstance() {
    if (sInstance == null){
      sInstance = new CapProxyCdrRecords();
    }
    return sInstance;
  }

  /**
   * Initialize the CapProxyCdrRecords object
   */
  private CapProxyCdrRecords() {
    capRecords = new HashMap<>();
  }

  /**
   * Return the an optional list of key value pair associated with the dialog ID
   *
   * @param dialogId the dialog id
   * @return Optional<Map<String, Object>>
   */
  public Optional<Map<String, Object>> getCDRRecords(Long dialogId) {
    return Optional.ofNullable(this.capRecords.remove(dialogId));
  }

  /**
   * Add a list of key-value pair of the CDR associated with the dialog ID
   *
   * @param dialogId Long
   * @param fields   Map<String,Object>
   */
  public void addCDRRecord(long dialogId, Map<String, Object> fields) {
    this.capRecords.put(dialogId, fields);
  }
}
