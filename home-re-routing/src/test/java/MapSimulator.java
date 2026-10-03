import java.io.InputStream;

import com.naikeri.sgw.helpers.SgwResource;
import com.naikeri.sgw.impl.settings.XmlConfiguration;
import com.naikeri.sgw.impl.settings.m3ua.M3uaSettings;
import com.naikeri.sgw.impl.settings.map.MapSettings;
import com.naikeri.sgw.impl.settings.sccp.SccpSettings;
import com.naikeri.sgw.impl.settings.sctp.SctpSettings;
import com.naikeri.sgw.impl.settings.tcap.TcapSettings;
import com.naikeri.sgw.network.layers.M3uaLayer;
import com.naikeri.sgw.network.layers.MapLayer;
import com.naikeri.sgw.network.layers.SccpLayer;
import com.naikeri.sgw.network.layers.SctpLayer;
import com.naikeri.sgw.network.layers.TcapLayer;
import com.naikeri.prototype.map.MapProtoTypeSMSListener;
import com.naikeri.prototype.map.MapPrototypeCallHandlingListener;
import com.naikeri.prototype.map.MapPrototypeListener;
import com.naikeri.prototype.map.MapPrototypeMobility;
import com.naikeri.prototype.map.MapSimulatorSendPrimitive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.map.MAPStackImpl;

public class MapSimulator {

  private static final Logger logger = LogManager.getLogger(MapSimulator.class);
  String imsiString = "425100402000108";
  // the home network's IMSI for the subscriber the visited network knows as imsiString
  String hplmnImsiString = "425100702000108";
  String sgsn_address = "112233445500";
  String sgsn_number = "112233445501";
  // -Dmapsim.cycles=<n> lengthens or shortens a run without editing the code
  int testNumber = Integer.getInteger("mapsim.cycles", 5);
  private MapSimulatorSendPrimitive mapSimulatorSendPrimitive;

  private void simulate() {
    new Thread() {
      @Override
      public void run() {
        if (mapSimulatorSendPrimitive == null) {
          return;
        }
        for (int i = 0; i < testNumber; i++) {
          try {
            Thread.sleep(2000);
            // Test MAP Send Authentication Info
            mapSimulatorSendPrimitive.sendAuthenticationInfo(imsiString);
            Thread.sleep(1000);
            // Test MAP Update Location (CS domain)
            mapSimulatorSendPrimitive.simulateUpdateLocationRequest(imsiString);
            Thread.sleep(1000);
            // Test MAP Insert Subscriber Data
            mapSimulatorSendPrimitive.insertSubscriberDataRequest(imsiString, "cs");
            // Test MAP Send Authentication Info
            mapSimulatorSendPrimitive.sendAuthenticationInfo(imsiString);
            Thread.sleep(1000);
            // Test MAP Update GPRS Location (PS Domain)
            mapSimulatorSendPrimitive.initiateUpdateGprsLocation(imsiString, sgsn_address, sgsn_number);
            Thread.sleep(1000);
            // Test MAP Insert Subscriber Data
            mapSimulatorSendPrimitive.insertSubscriberDataRequest(imsiString, "ps");
            // test MO SMS
            mapSimulatorSendPrimitive.sendMoForwardSm(imsiString);
            Thread.sleep(1000);
            // test MT SMS
            mapSimulatorSendPrimitive.sendMtForwardSM(hplmnImsiString);
            Thread.sleep(1000);
            // Test MAP Provide Roaming Number
            mapSimulatorSendPrimitive.initiateProvideRoamingNumber(hplmnImsiString);

          } catch (Exception e) {
            logger.error("MAP simulation step failed", e);
          }
        }
      }
    }.start();
  }


  public void initialize() {
    try {
      InputStream is = new SgwResource("map-simulator-config.xml").getAsStream();
      XmlConfiguration configuration = new XmlConfiguration(is);

      logger.info("Initializing the channel layers.");
      // get the transport layer name.
      logger.info("Initializing SCTP layer...");
      SctpSettings sctpSettings = (SctpSettings) configuration.getLayerSettings("sctpclient");
      SctpLayer sctp = new SctpLayer(sctpSettings);

      logger.info("Initializing M3UA layer...");
      M3uaSettings m3uaSettings = (M3uaSettings) configuration.getLayerSettings("m3uaclient");
      M3uaLayer m3ua = new M3uaLayer(m3uaSettings, sctp);

      logger.info("Initializing SCCP layer...");
      SccpSettings sccpClientSettings = (SccpSettings) configuration.getLayerSettings("sccpclient");
      SccpLayer sccp = new SccpLayer(sccpClientSettings, m3ua);

      logger.info("Initializing TCAP layer...");
      TcapSettings tcapSettings = (TcapSettings) configuration.getLayerSettings("tcapclient");
      TcapLayer tcap = new TcapLayer(tcapSettings, sccp);

      logger.info("Initializing MAP layer...");
      MapSettings mapSettings = (MapSettings) configuration.getLayerSettings("mapclient");
      MapLayer map = new MapLayer(mapSettings, tcap);

      // start listeners
      MAPStackImpl mapClient = map.getMapStack();
      map.getMapProvider().addMAPDialogListener(new MapPrototypeListener());
      map.getMapProvider().getMAPServiceMobility().addMAPServiceListener(new MapPrototypeMobility(mapClient.getMAPProvider().getMAPParameterFactory()));
      map.getMapProvider().getMAPServiceSms().addMAPServiceListener(new MapProtoTypeSMSListener());
      map.getMapProvider().getMAPServiceMobility().activate();
      map.getMapProvider().getMAPServiceCallHandling()
          .addMAPServiceListener(new MapPrototypeCallHandlingListener(mapClient.getMAPProvider().getMAPParameterFactory()));
      map.getMapProvider().getMAPServiceCallHandling().activate();
      map.getMapProvider().getMAPServiceSms().activate();

      mapSimulatorSendPrimitive = new MapSimulatorSendPrimitive(mapClient, sccpClientSettings);
      this.simulate();
    } catch (Exception e) {
      logger.error("Caught exception", e);
    }
  }

  public static void main(String[] args) {
    MapSimulator test = new MapSimulator();
    test.initialize();
  }
}
