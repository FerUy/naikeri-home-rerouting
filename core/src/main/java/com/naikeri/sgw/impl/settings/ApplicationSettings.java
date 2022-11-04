package com.naikeri.sgw.impl.settings;

import java.util.ArrayList;
import java.util.List;
import com.naikeri.sgw.api.settings.SettingsInterface;

public class ApplicationSettings implements SettingsInterface {

  private int workers;
  private String name;
  private String channelName;
  private String ruleConfigurationFile;
  private String cdrName;
  private List<ServiceFunctionSetting> serviceFunctionSettings = new ArrayList<>();

  public String getName() {
    return name;
  }

  public int getWorkers() {
    return workers;
  }

  public String getChannelName() {
    return channelName;
  }

  public String getCDRName(){
    return this.cdrName;
  }

  ApplicationSettings(String name, int workers, String channelName, String cdrName) {
    this.workers = workers;
    this.channelName = channelName;
    this.name = name;
    this.cdrName = cdrName;
  }

  public void setApplicationRuleFilename(String filename) {
    this.ruleConfigurationFile = filename;
  }

  public String getRuleFileName() {
    return this.ruleConfigurationFile;
  }

  public List<ServiceFunctionSetting> getServiceFunctions() {
    return serviceFunctionSettings;
  }

  public void addServiceFunction(ServiceFunctionSetting serviceFunctionSetting) {
    this.serviceFunctionSettings.add(serviceFunctionSetting);
  }
}
