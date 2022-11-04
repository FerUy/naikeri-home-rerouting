package com.naikeri.sgw.impl.cdr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Cdr {
  private Map<String, Object> fields;
  private String name;

  public Cdr(Map<String, Object> fields, String name) {
    this.fields = fields;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Cdr addField(String key, Object value) {
    this.fields.put(key, value);
    return this;
  }

  public String stringifyValues(String separator, List<String> fieldsToPrint) {
    List<String> dataToWrite = new ArrayList<>();
    for (String settingField : fieldsToPrint) {
      if (fields.containsKey(settingField)) {
        dataToWrite.add(Objects.toString(fields.get(settingField), ""));
      }
    }
    return String.join(separator, dataToWrite);
  }

  public String stringifyAll(String separator, List<String> fieldsToPrint) {
    List<String> dataToWrite = new ArrayList<>();
    if (fieldsToPrint == null) {
      for (Map.Entry<String, Object> entry : fields.entrySet()) {
        dataToWrite.add(entry.getKey().concat(":").concat(Objects.toString(entry.getValue(), "")));
      }
    } else {
      for (String settingField : fieldsToPrint) {
        if (fields.containsKey(settingField)) {
          dataToWrite
              .add(settingField.concat(":").concat(Objects.toString(fields.get(settingField), "")));
        }
      }
    }
    return String.join(separator, dataToWrite);
  }
}
