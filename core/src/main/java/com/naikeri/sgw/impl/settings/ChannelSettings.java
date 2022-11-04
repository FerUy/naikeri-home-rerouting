package com.naikeri.sgw.impl.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.naikeri.sgw.api.settings.SettingsInterface;

public class ChannelSettings implements SettingsInterface {

  private String handler;
  private String layer;
  private String queue;
  private String name;
  private Set<String> primitives = Collections.synchronizedSet(new HashSet<String>());

  public String getHandler() {
    return handler;
  }

  public String getName() {
    return name;
  }

  public String getLayerName() {
    return layer;
  }

  public String getQueueName() {
    return queue;
  }

  public ChannelSettings(String name, String handler, String layer, String queue) {
    this.name = name;
    this.handler = handler;
    this.layer = layer;
    this.queue = queue;
  }

  public void addPrimitive(String primitive) {
    if (primitive == null || primitive.isEmpty())
      return;
    // replace the _Request or _Response if any
    String newprimitive =
        primitive.replaceAll("_Re(q|s).{5}", "").replaceAll("\\s+", "").toLowerCase();
    primitives.add(newprimitive);
  }

  /**
   * Returns TRUE if primitive is defined in the configuration otherwise it returns FALSE 
   * @param primitive String. The search primitive string
   * @return Boolean: 
   */
  public Boolean isPrimitiveExist(String primitive){
    if (primitive == null || primitive.isEmpty()) return false;
    if (this.primitives.size() == 0) return true;
    String newprimitive = primitive.toLowerCase().replaceAll("_re(q|s).+", "").replaceAll("\\s+", "");
    return this.primitives.contains(newprimitive);
  }

  public List<String> getPrimitives() {
    return new ArrayList<>(primitives);
  }

}
