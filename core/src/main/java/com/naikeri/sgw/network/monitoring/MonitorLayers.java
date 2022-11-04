package com.naikeri.sgw.network.monitoring;

import java.io.FileNotFoundException;
import java.util.Timer;
import com.naikeri.sgw.impl.settings.MonitoringSettings;
import com.naikeri.sgw.network.layers.M3uaLayer;
import com.naikeri.sgw.network.layers.SctpLayer;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.m3ua.As;
import org.restcomm.protocols.ss7.m3ua.Asp;
import org.restcomm.protocols.ss7.m3ua.AspFactory;
import org.restcomm.protocols.ss7.m3ua.M3UAManagementEventListener;
import org.restcomm.protocols.ss7.m3ua.State;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.mtp.Mtp3EndCongestionPrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3PausePrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3ResumePrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3StatusPrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3TransferPrimitive;
import org.restcomm.protocols.ss7.mtp.Mtp3UserPartListener;
import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.Management;
import org.mobicents.protocols.api.ManagementEventListener;
import org.mobicents.protocols.api.Server;


public class MonitorLayers implements M3UAManagementEventListener, Mtp3UserPartListener, ManagementEventListener {

  private M3UAManagementImpl m3uaMgmt;
  private Management sctpMgmt;
  public String endpoint;
  public String job;
  public String instance;
  public Logger logger = Logger.getLogger(MonitorLayers.class);
  public long m3uain = 0;
  public long m3uaout = 0;
  public long mtp3Bytes = 0;
  public boolean custom = false;
  private MonitoringSettings monitoringObj;
  private Timer monitoringTimer;

  public MonitorLayers(M3uaLayer m3uaLayer, SctpLayer sctpLayer,
      MonitoringSettings monitoringLayerObject) throws FileNotFoundException {
    this.m3uaMgmt = m3uaLayer.getM3uaManagement();
    this.sctpMgmt = sctpLayer.getSctpManagement();
    this.monitoringObj = monitoringLayerObject;
    m3uaMgmt.addM3UAManagementEventListener(this);
    m3uaMgmt.addMtp3UserPartListener(this);
    sctpMgmt.addManagementEventListener(this);
  }

  public void start() throws IllegalStateException {
    logger.info(
        String.format("starting Monitoring Layer for '%s' ...", this.monitoringObj.getM3uaName()));
    try {
      this.m3uaMgmt.setStatisticsEnabled(true);
    } catch (Exception e) {
      logger.error("Cannot enable stats! " + e.getMessage());
    }
    monitoringTimer = new Timer();
    MonitoringTask task = new MonitoringTask(this, m3uaMgmt.getCounterProviderImpl(), this.m3uaMgmt,
        this.sctpMgmt, this.monitoringObj.getFileName());
    monitoringTimer.scheduleAtFixedRate(task, this.monitoringObj.getRefreshInterval(),
        this.monitoringObj.getRefreshInterval());
  }

  public void stop() {
    monitoringTimer.cancel();
  }

  @Override
  public void onServerAdded(Server server) {

  }

  @Override
  public void onServerRemoved(Server server) {

  }

  @Override
  public void onAssociationAdded(Association association) {

  }

  @Override
  public void onAssociationRemoved(Association association) {

  }

  @Override
  public void onAssociationStarted(Association association) {

  }

  @Override
  public void onAssociationStopped(Association association) {

  }

  @Override
  public void onAssociationUp(Association association) {

  }

  @Override
  public void onAssociationDown(Association association) {

  }

  @Override
  public void onServerModified(Server server) {

  }

  @Override
  public void onAssociationModified(Association association) {

  }

  @Override
  public void onServiceStarted() {

  }

  @Override
  public void onServiceStopped() {

  }

  @Override
  public void onRemoveAllResources() {

  }

  @Override
  public void onAsCreated(As as) {

  }

  @Override
  public void onAsDestroyed(As as) {

  }

  @Override
  public void onAspFactoryCreated(AspFactory aspFactory) {

  }

  @Override
  public void onAspFactoryDestroyed(AspFactory aspFactory) {

  }

  @Override
  public void onAspAssignedToAs(As as, Asp asp) {

  }

  @Override
  public void onAspUnassignedFromAs(As as, Asp asp) {

  }

  @Override
  public void onAspFactoryStarted(AspFactory aspFactory) {

  }

  @Override
  public void onAspFactoryStopped(AspFactory aspFactory) {

  }

  @Override
  public void onAspActive(Asp asp, State state) {

  }

  @Override
  public void onAspInactive(Asp asp, State state) {

  }

  @Override
  public void onAspDown(Asp asp, State state) {

  }

  @Override
  public void onAsActive(As as, State state) {

  }

  @Override
  public void onAsPending(As as, State state) {

  }

  @Override
  public void onAsInactive(As as, State state) {

  }

  @Override
  public void onAsDown(As as, State state) {

  }

  @Override
  public void onMtp3TransferMessage(Mtp3TransferPrimitive mtp3TransferPrimitive) {

  }

  @Override
  public void onMtp3PauseMessage(Mtp3PausePrimitive mtp3PausePrimitive) {

  }

  @Override
  public void onMtp3ResumeMessage(Mtp3ResumePrimitive mtp3ResumePrimitive) {

  }

  @Override
  public void onMtp3StatusMessage(Mtp3StatusPrimitive mtp3StatusPrimitive) {

  }

  @Override
  public void onMtp3EndCongestionMessage(Mtp3EndCongestionPrimitive mtp3EndCongestionPrimitive) {

  }


}
