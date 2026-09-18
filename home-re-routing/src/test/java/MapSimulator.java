import java.io.InputStream;
import java.util.Random;
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
import com.naikeri.prototype.map.MapPrototypeListener;
import com.naikeri.prototype.map.MapPrototypeMobility;
import com.naikeri.prototype.map.MapSimulatorSendPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restcomm.protocols.ss7.map.MAPStackImpl;


public class MapSimulator {
  private static final Logger logger = LoggerFactory.getLogger(MapSimulator.class);
  String imsiString = "425100402000108";
  String sgsn_address = "112233445500";
  String sgsn_number = "112233445501";
  int testNumber = 5;
  private MapSimulatorSendPrimitive simul;

  private void simulate() {
    new Thread() {
      @Override
      public void run() {
        if (simul == null){
          return;
        }
        for (int i = 0; i < testNumber; i++) {
          try {
            Thread.sleep(2000);
            // test the mt sms
            simul.sendMtForwardSM(imsiString);
            // Thread.sleep(1000 + new Random().nextInt(2000));
            // simul.initiateProvideRoamingNumber("011220200198227");
            // simul.initiateUpdateGprsLocation(imsiString, sgsn_address, sgsn_number);
            // Thread.sleep(1000 + new Random().nextInt(5000));
            // simul.simulateUpdateLocationRequest(imsiString);
            // Thread.sleep(1000 + new Random().nextInt(4000));
            // simul.sendAuthenticationInfo(imsiString);
            // Thread.sleep(1000 + new Random().nextInt(6000));
            // simul.insertSubscriberDataRequest(imsiString);

          } catch (Exception e) {
            // nothing
            logger.error(e);
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

      SccpSettings sccpServerSettings = (SccpSettings) configuration.getLayerSettings("sccpserver");

      logger.info("Initializing TCAP layer...");
      TcapSettings tcapSettings = (TcapSettings) configuration.getLayerSettings("tcapclient");
      TcapLayer tcap = new TcapLayer(tcapSettings, sccp);

      logger.info("Initializing CAP layer...");
      MapSettings capSettings = (MapSettings) configuration.getLayerSettings("mapclient");
      MapLayer map = new MapLayer(capSettings, tcap);

      // start listeners
      MAPStackImpl mapClient = map.getMapStack();
      map.getMapProvider().addMAPDialogListener(new MapPrototypeListener());
      map.getMapProvider().getMAPServiceMobility()
          .addMAPServiceListener(new MapPrototypeMobility(mapClient.getMAPProvider().getMAPParameterFactory()));
      map.getMapProvider().getMAPServiceSms().addMAPServiceListener(new MapProtoTypeSMSListener());
      map.getMapProvider().getMAPServiceMobility().activate();
      
      simul = new MapSimulatorSendPrimitive(mapClient, sccpClientSettings, sccpServerSettings);
      this.simulate();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) {
    MapSimulator test = new MapSimulator();
    test.initialize();
  }
}
