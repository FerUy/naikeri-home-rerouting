package com.naikeri.sgw.info;

import java.util.HashMap;
import java.util.Map;

public class MapProxyCdrRecords {
  private static MapProxyCdrRecords sInstance = null;
  private final Map<Long, Map<String, Object>> cdrMap;

  /**
   * get an instance of the MapProxyCDRRecords Object
   *
   * @return MapProxyCdrRecords instance
   */
  public static MapProxyCdrRecords getInstance() {
    if (sInstance == null) {
      sInstance = new MapProxyCdrRecords();
    }
    return sInstance;
  }

  private MapProxyCdrRecords() {
    cdrMap = new HashMap<>();
  }

  /**
   * Add field to the CDR map object associated with the dialogId
   *
   * @param dialogId MAP dialog id
   * @param field    an object of string-object pair
   */
  public void addCDRFields(Long dialogId, Map<String, Object> field) {
    if (dialogId == null){
      return;
    }
    this.cdrMap.put(dialogId, field);
  }

  /**
   * Add a single key-value pair to the cdr record
   *
   * @param dialogId the MAP Dialog Id
   * @param name     the name of the CDR field
   * @param value    the value (Object) for the CDR
   */
  public void addCDRField(Long dialogId, String name, Object value) {
    if (dialogId == null) {
      return;
    }
    Map<String, Object> prvValue = this.cdrMap.get(dialogId);
    if (prvValue != null) {
      prvValue.put(name, value);
      this.addCDRFields(dialogId, prvValue);
    }
  }

  /**
   * The get method remove the fields from memory and return the map object
   *
   * @param dialogId The dialogId
   * @return a map object for all the fields associated with the dialog Id
   */
  public Map<String, Object> getCDRFields(Long dialogId) {
    if (dialogId == null) {
      return null;
    }
    return this.cdrMap.remove(dialogId);
  }
}
