package com.naikeri.sgw.network.monitoring;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.Management;
import org.restcomm.protocols.ss7.m3ua.As;
import org.restcomm.protocols.ss7.m3ua.Asp;
import org.restcomm.protocols.ss7.m3ua.AspFactory;
import org.restcomm.protocols.ss7.m3ua.M3UACounterProvider;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.statistics.api.LongValue;

class MonitoringTask extends TimerTask {

  private final MonitorLayers monitorLayers;
  private M3UACounterProvider cprovider;
  private M3UAManagementImpl m3uaMgmt;
  private Management sctpMgmt;
  private String log_file;
  private HashMap<String, Integer> ASPmonitorList = new HashMap<String, Integer>();
  private static final Logger logger = Logger.getLogger(MonitoringTask.class);

  public MonitoringTask(MonitorLayers monitorLayers, M3UACounterProvider cprovider,
      M3UAManagementImpl m3uaMgmt, Management sctpMgmt, String log_file) {
    this.monitorLayers = monitorLayers;
    this.cprovider = cprovider;
    this.m3uaMgmt = m3uaMgmt;
    this.sctpMgmt = sctpMgmt;
    this.log_file = log_file;
  }

  @Override
  public void run() {
    double asgauge = 0.0;
    double aspgauge = 0.0;
    double sctpgauge = 0.0;
    String log = "";
    FileWriter fw;
    BufferedWriter bw;
    log = log + "# TYPE sctp_association gauge\n";
    if (!(sctpMgmt.getAssociations().isEmpty())) {
      Map<String, Association> assocs = sctpMgmt.getAssociations();
      for (Map.Entry<String, Association> entry : assocs.entrySet()) {
        // we need to generate 2 lines (for each possible status: UP, DOWN) since for prometheus
        // to count and monitor each state
        try {
          if (sctpMgmt.getAssociation(entry.getKey()).isUp()) {
            sctpgauge = 1.0;
            log = log + "sctp_association{name=\""
                + sctpMgmt.getAssociation(entry.getKey()).getName() + "\",host_address=\""
                + printString(sctpMgmt.getAssociation(entry.getKey()).getHostAddress()) + "\",host_port=\""
                + sctpMgmt.getAssociation(entry.getKey()).getHostPort() + "\",association_type=\""
                + sctpMgmt.getAssociation(entry.getKey()).getAssociationType()
                + "\",peer_address=\"" + sctpMgmt.getAssociation(entry.getKey()).getPeerAddress()
                + "\",status=\"UP\",ip_channel_type=\""
                + sctpMgmt.getAssociation(entry.getKey()).getAssociationType() + "\",peer_port=\""
                + sctpMgmt.getAssociation(entry.getKey()).getPeerPort() + "\"} " + sctpgauge + "\n";
            log = log + "sctp_association{name=\""
                + sctpMgmt.getAssociation(entry.getKey()).getName() + "\",host_address=\""
                + printString(sctpMgmt.getAssociation(entry.getKey()).getHostAddress()) + "\",host_port=\""
                + sctpMgmt.getAssociation(entry.getKey()).getHostPort() + "\",association_type=\""
                + sctpMgmt.getAssociation(entry.getKey()).getAssociationType()
                + "\",peer_address=\"" + sctpMgmt.getAssociation(entry.getKey()).getPeerAddress()
                + "\",status=\"DOWN\",ip_channel_type=\""
                + sctpMgmt.getAssociation(entry.getKey()).getAssociationType() + "\",peer_port=\""
                + sctpMgmt.getAssociation(entry.getKey()).getPeerPort() + "\"} " + (sctpgauge - 1.0)
                + "\n";
          } else {
            sctpgauge = 1.0;
            log = log + "sctp_association{name=\""
                + sctpMgmt.getAssociation(entry.getKey()).getName() + "\",host_address=\""
                + printString(sctpMgmt.getAssociation(entry.getKey()).getHostAddress()) + "\",host_port=\""
                + sctpMgmt.getAssociation(entry.getKey()).getHostPort() + "\",association_type=\""
                + sctpMgmt.getAssociation(entry.getKey()).getAssociationType()
                + "\",peer_address=\"" + sctpMgmt.getAssociation(entry.getKey()).getPeerAddress()
                + "\",status=\"DOWN\",ip_channel_type=\""
                + sctpMgmt.getAssociation(entry.getKey()).getAssociationType() + "\",peer_port=\""
                + sctpMgmt.getAssociation(entry.getKey()).getPeerPort() + "\"} " + sctpgauge + "\n";
            log = log + "sctp_association{name=\""
                + sctpMgmt.getAssociation(entry.getKey()).getName() + "\",host_address=\""
                + printString(sctpMgmt.getAssociation(entry.getKey()).getHostAddress()) + "\",host_port=\""
                + sctpMgmt.getAssociation(entry.getKey()).getHostPort() + "\",association_type=\""
                + sctpMgmt.getAssociation(entry.getKey()).getAssociationType()
                + "\",peer_address=\"" + sctpMgmt.getAssociation(entry.getKey()).getPeerAddress()
                + "\",status=\"UP\",ip_channel_type=\""
                + sctpMgmt.getAssociation(entry.getKey()).getAssociationType() + "\",peer_port=\""
                + sctpMgmt.getAssociation(entry.getKey()).getPeerPort() + "\"} " + (sctpgauge - 1.0)
                + "\n";
          }
        } catch (Exception e) {
          logger.error("Exception: " + e);
          e.printStackTrace();
        }
      }
    }
    // log = log +sctpMgmt.getPersistDir());
    Iterator<As> iterator = m3uaMgmt.getAppServers().iterator();
    log = log + "# TYPE m3ua_as gauge" + "\n";
    while (iterator.hasNext()) {
      // we need to generate 4 lines (for each possible status: DOWN, PENDING, INACTIVE, ACTIVE)
      // since for prometheus to count and monitor each state

      As as = (As) iterator.next();
      asgauge = 1.0;
      if (as.getState().toString().equals("ACTIVE")) {
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"ACTIVE\"} " + asgauge + "\n";
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"DOWN\"} " + (asgauge - 1.0)
            + "\n";
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"PENDING\"} " + (asgauge - 1.0)
            + "\n";
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"INACTIVE\"} " + (asgauge - 1.0)
            + "\n";
      } else if (as.getState().toString().equals("DOWN")) {
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"DOWN\"} " + asgauge + "\n";
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"ACTIVE\"} " + (asgauge - 1.0)
            + "\n";
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"PENDING\"} " + (asgauge - 1.0)
            + "\n";
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"INACTIVE\"} " + (asgauge - 1.0)
            + "\n";
      } else if (as.getState().toString().equals("PENDING")) {
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"PENDING\"} " + asgauge + "\n";
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"ACTIVE\"} " + (asgauge - 1.0)
            + "\n";
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"DOWN\"} " + (asgauge - 1.0)
            + "\n";
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"INACTIVE\"} " + (asgauge - 1.0)
            + "\n";
      } else if (as.getState().toString().equals("INACTIVE")) {
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"INACTIVE\"} " + asgauge + "\n";
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"ACTIVE\"} " + (asgauge - 1.0)
            + "\n";
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"DOWN\"} " + (asgauge - 1.0)
            + "\n";
        log = log + "m3ua_as{name=\"" + as.getName() + "\",status=\"PENDING\"} " + (asgauge - 1.0)
            + "\n";
      }
    }
    log = log + "# TYPE m3ua_asp gauge" + "\n";

    Iterator<AspFactory> iteratoraspf = m3uaMgmt.getAspfactories().iterator();
    while (iteratoraspf.hasNext()) {
      AspFactory aspf = (AspFactory) iteratoraspf.next();
      Iterator<Asp> iteratorasp = aspf.getAspList().iterator();
      String asp_assocname = aspf.getAssociation().getName();

      while (iteratorasp.hasNext()) {
        // we need to generate 3 lines (for each possible status: DOWN, INACTIVE, ACTIVE) since
        // for prometheus to count and monitor each state
        Asp asp = (Asp) iteratorasp.next();
        As linked_as = asp.getAs();
        if (asp.getState().toString().equals("ACTIVE")) {
          aspgauge = 1.0;
          log =
              log + "m3ua_asp{name=\"" + asp.getName() + "\",status=\"ACTIVE\"} " + aspgauge + "\n";
          log = log + "m3ua_asp{name=\"" + asp.getName() + "\",status=\"INACTIVE\"} "
              + (aspgauge - 1.0) + "\n";
          log = log + "m3ua_asp{name=\"" + asp.getName() + "\",status=\"DOWN\"} " + (aspgauge - 1.0)
              + "\n";
        } else if (asp.getState().toString().equals("INACTIVE")) {
          aspgauge = 1.0;
          log = log + "m3ua_asp{name=\"" + asp.getName() + "\",status=\"INACTIVE\"} " + aspgauge
              + "\n";
          log = log + "m3ua_asp{name=\"" + asp.getName() + "\",status=\"ACTIVE\"} "
              + (aspgauge - 1.0) + "\n";
          log = log + "m3ua_asp{name=\"" + asp.getName() + "\",status=\"DOWN\"} " + (aspgauge - 1.0)
              + "\n";
        } else if (asp.getState().toString().equals("DOWN")) {
          aspgauge = 1.0;
          log = log + "m3ua_asp{name=\"" + asp.getName() + "\",status=\"DOWN\"} " + aspgauge + "\n";
          log = log + "m3ua_asp{name=\"" + asp.getName() + "\",status=\"ACTIVE\"} "
              + (aspgauge - 1.0) + "\n";
          log = log + "m3ua_asp{name=\"" + asp.getName() + "\",status=\"INACTIVE\"} "
              + (aspgauge - 1.0) + "\n";
        }
        if ((!(asp.getState().toString().equals("ACTIVE")))
            && linked_as.getState().toString().equals("ACTIVE")) {
          // verify if the association is already on the list
          if (ASPmonitorList.get(asp_assocname.toString()) != null) {
            logger.info("ASP Auditor: ASP " + asp.getName() + " not ACTIVE and AS "
                + linked_as.getName() + " is ACTIVE, auditor counter value = "
                + ASPmonitorList.get(asp_assocname.toString()));
            if (ASPmonitorList.get(asp_assocname.toString()) == 5) {
              try {
                logger.info(
                    "Threshold reached, stopping SCTP association: " + asp_assocname.toString());
                sctpMgmt.stopAssociation(asp_assocname.toString());
                int counter = 0;
                counter = ASPmonitorList.get(asp_assocname.toString());
                counter = counter + 1;
                ASPmonitorList.put(asp_assocname.toString(), counter);
              } catch (Exception e) { 
                logger.error(e);
                e.printStackTrace();
                ASPmonitorList.put(asp_assocname.toString(), 3);
              }
            } else if (ASPmonitorList.get(asp_assocname.toString()) == 6) {
              try {
                sctpMgmt.startAssociation(asp_assocname);
                ASPmonitorList.remove(asp_assocname);
              } catch (Exception e) {
                logger.error(e);
                e.printStackTrace();
                ASPmonitorList.put(asp_assocname.toString(), 3);
              }
            } else {
              int counter = 0;
              counter = ASPmonitorList.get(asp_assocname.toString());
              counter = counter + 1;
              ASPmonitorList.put(asp_assocname.toString(), counter);
            }
          } else {
            ASPmonitorList.put(asp_assocname, 1);
          }

        } else if (ASPmonitorList.get(asp_assocname.toString()) != null) {
          if (ASPmonitorList.get(asp_assocname.toString()) == 6) {
            try {
              sctpMgmt.startAssociation(asp_assocname);
              ASPmonitorList.remove(asp_assocname);
            } catch (Exception e) {
              logger.error(e);
              e.printStackTrace();
              ASPmonitorList.put(asp_assocname.toString(), 3);
            }
          }
        } else {
          if (ASPmonitorList.get(asp_assocname.toString()) != null) {
            ASPmonitorList.remove(asp_assocname);
          }
        }

        // M3UA OUT Counters
        Map<String, LongValue> aspcounterout =
            cprovider.getPacketsPerAssTx(aspf.getAssociation().getName());
        if (aspcounterout != null && aspcounterout.size() > 0) {
          for (Map.Entry<String, LongValue> entry : aspcounterout.entrySet()) {
            if (entry.getValue().getValue() > 0) {
              this.monitorLayers.m3uaout = this.monitorLayers.m3uaout + entry.getValue().getValue();
            }
          }
        }

        // M3UA IN Counter
        Map<String, LongValue> aspcounterin =
            cprovider.getPacketsPerAssRx(aspf.getAssociation().getName());
        if (aspcounterin != null && aspcounterin.size() > 0) {
          for (Map.Entry<String, LongValue> entry : aspcounterin.entrySet()) {
            if (entry.getValue().getValue() > 0) {
              this.monitorLayers.m3uain = this.monitorLayers.m3uain + entry.getValue().getValue();
            }
          }
        }
      }
    }
    logger.debug("M3UA TX Message counter = " + this.monitorLayers.m3uaout);
    logger.debug("M3UA RX Message counter = " + this.monitorLayers.m3uain);
    logger.debug("MTP Payload bytes counter = " + this.monitorLayers.mtp3Bytes);
    log = log + "# TYPE m3ua_traffic counter" + "\n";
    if (!this.monitorLayers.custom) {
      log = log + "m3ua_message_total{component=\"m3ua-metrics\",type=\"transfer-in\"} "
          + this.monitorLayers.m3uain + "\n";
      log = log + "m3ua_message_total{component=\"m3ua-metrics\",type=\"transfer-out\"} "
          + this.monitorLayers.m3uaout + "\n";
      log = log + "m3ua_payload_total{component=\"m3ua-metrics\",type=\"transfer-bytes\"} "
          + this.monitorLayers.mtp3Bytes + "\n";
    }
    try {
      fw = new FileWriter(log_file, false);
      // fw = new FileWriter("/root/jboss-5.1.0.GA/server/default/log/monitor.log");
      bw = new BufferedWriter(fw);
      bw.write(log);
      bw.close();
      fw.close();
    } catch (IOException e1) {
      logger.error("Failed to write log to file. Exception: " + e1);
      e1.printStackTrace();
    }
    this.monitorLayers.m3uain = 0;
    this.monitorLayers.m3uaout = 0;
    this.monitorLayers.mtp3Bytes = 0;
  }
  private String printString(String str){
    if (str == null || str.isEmpty()) return "";
    return str;
  }
}
