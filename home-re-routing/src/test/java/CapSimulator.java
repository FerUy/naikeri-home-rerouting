import java.io.InputStream;
// import java.util.Scanner;
import com.naikeri.sgw.helpers.SgwResource;
import com.naikeri.sgw.impl.settings.XmlConfiguration;
import com.naikeri.sgw.impl.settings.cap.CapSettings;
import com.naikeri.sgw.impl.settings.m3ua.M3uaSettings;
import com.naikeri.sgw.impl.settings.sccp.SccpSettings;
import com.naikeri.sgw.impl.settings.sctp.SctpSettings;
import com.naikeri.sgw.impl.settings.tcap.TcapSettings;
import com.naikeri.sgw.network.layers.CapLayer;
import com.naikeri.sgw.network.layers.M3uaLayer;
import com.naikeri.sgw.network.layers.SccpLayer;
import com.naikeri.sgw.network.layers.SctpLayer;
import com.naikeri.sgw.network.layers.TcapLayer;
import com.naikeri.prototype.camel.HplmnScpPrototype;
import com.naikeri.prototype.camel.VplmnStpPrototype;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * CapSimulator
 */
public class CapSimulator {

  private static final Logger logger = LogManager.getLogger(CapSimulator.class);
  private XmlConfiguration configuration;

  private CapLayer getCapLayer(int index) {
    try {
      logger.info("Initializing SCTP layer...");
      SctpSettings sctpSettings = (SctpSettings) configuration.getLayerSettings("sctp" + index);
      SctpLayer sctp = new SctpLayer(sctpSettings);

      logger.info("Initializing M3UA layer...");
      M3uaSettings m3uaSettings = (M3uaSettings) configuration.getLayerSettings("m3ua" + index);
      M3uaLayer m3ua = new M3uaLayer(m3uaSettings, sctp);

      logger.info("Initializing SCCP layer...");
      SccpSettings sccpClientSettings = (SccpSettings) configuration.getLayerSettings("sccp" + index);
      SccpLayer sccp = new SccpLayer(sccpClientSettings, m3ua);

      logger.info("Initializing TCAP layer...");
      TcapSettings tcapSettings = (TcapSettings) configuration.getLayerSettings("tcap" + index);
      TcapLayer tcap = new TcapLayer(tcapSettings, sccp);

      logger.info("Initializing CAP layer...");
      CapSettings capSettings = (CapSettings) configuration.getLayerSettings("cap" + index);
      return new CapLayer(capSettings, tcap);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return null;
  }

  private void initialize() {
    try {
      InputStream is = new SgwResource("cap-simulator-config.xml").getAsStream();
      this.configuration = new XmlConfiguration(is);

      logger.info("Initializing the channel layers.");
      CapLayer[] caplayers = new CapLayer[4];
      for (int i = 0; i < 4; i++) {
        caplayers[i] = getCapLayer(i);
      }

      // get the transport layer name.
      // VPLMN
      assert caplayers[0] != null;
      VplmnStpPrototype vplmnStpPrototype = new VplmnStpPrototype(caplayers[0].getCapProvider(),
          caplayers[0].getCapProvider().getCAPParameterFactory(), caplayers[1].getCapProvider(),
          caplayers[1].getCapProvider().getCAPParameterFactory(), caplayers[2].getCapProvider(),
          caplayers[2].getCapProvider().getCAPParameterFactory());
      logger.info("VPLMN started{}", vplmnStpPrototype);

      // HPLMN
      HplmnScpPrototype hplmnScpPrototype = new HplmnScpPrototype(caplayers[3].getCapProvider(),
          caplayers[3].getCapProvider().getCAPParameterFactory());
      logger.info("HPLMN started{}", hplmnScpPrototype);
      Thread.sleep(5000);

      // -Dcapsim.calls=<n> lengthens or shortens a run without editing the code

      int calls = Integer.getInteger("capsim.calls", 120);

      for (int i = 0; i < calls; i++) {
        try {
          Thread.sleep(8000);
          vplmnStpPrototype.sendInitialDPRequest();
        } catch (Exception e) {
          logger.error("Caught exception", e);
        }
      }
      logger.info("DONE");
    } catch (Exception ex) {
      logger.error("Simulation Error: ", ex);
    }
  }

  public CapSimulator() {

  }

  public static void main(String[] args) {
    CapSimulator vplmn = new CapSimulator();
    vplmn.initialize();
  }

}
