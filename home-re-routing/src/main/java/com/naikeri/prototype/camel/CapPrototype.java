package com.naikeri.prototype.camel;

import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.api.network.LayerInterface;
import com.naikeri.sgw.helpers.SignalingResource;
import com.naikeri.sgw.impl.app.cap.BcsmCallStep;
import com.naikeri.sgw.impl.chn.ChannelHandler;
import com.naikeri.sgw.impl.settings.ChannelSettings;
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
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.cap.api.CAPDialog;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPMessage;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ConnectRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EstablishTemporaryConnectionRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.EventReportBCSMRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.InitialDPRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.ReleaseCallRequest;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.RequestReportBCSMEventRequest;

import java.io.InputStream;

/*
 * CAP Prototype
 *
 */
public class CapPrototype extends ChannelHandler {

  private static Logger logger = Logger.getLogger(CapPrototype.class);
  // private static XmlConfiguration configuration;

  private static final int numberOfTestLayers = 6;
  private static CapLayer[] cap = new CapLayer[numberOfTestLayers];

  private VplmnStpPrototype vplmnStpPrototype;
  private CamelProxyPrototype proxyPrototype;
  private HplmnScpPrototype hplmnScpPrototype;

  public CapPrototype(ChannelSettings channelSettings) {
    super(channelSettings);
  }

  public void channelInitialize() {
    // here we are completely connected to the ESG
    proxyPrototype = new CamelProxyPrototype(cap[0].getCapProvider(),
        cap[0].getCapProvider().getCAPParameterFactory(), cap[1].getCapProvider(),
        cap[1].getCapProvider().getCAPParameterFactory());

    vplmnStpPrototype = new VplmnStpPrototype(cap[2].getCapProvider(),
        cap[2].getCapProvider().getCAPParameterFactory(), cap[3].getCapProvider(),
        cap[3].getCapProvider().getCAPParameterFactory(), cap[4].getCapProvider(),
        cap[4].getCapProvider().getCAPParameterFactory());

    hplmnScpPrototype = new HplmnScpPrototype(cap[5].getCapProvider(),
        cap[5].getCapProvider().getCAPParameterFactory());
    logger.debug(hplmnScpPrototype);
  }

  @Override
  public void receiveMessageRequest(ChannelMessage channelMessage) {
    try {
      // CAP message received from layer, apply filter and forward
      CAPMessage capMessage = (CAPMessage) channelMessage.getParameter("message");
      if (capMessage == null) {
        CAPDialog capDialog = (CAPDialog) channelMessage.getParameter("dialog");
        if (capDialog != null) {
          String applicationContext = capDialog.getApplicationContext() == null ? "NULL"
              : capDialog.getApplicationContext().toString();
          Integer applicationContextCode = capDialog.getApplicationContext() == null ? -1
              : capDialog.getApplicationContext().getCode();
          logger.debug(String.format(
              "[CAP::SIGNAL<%s>] dialogId '%d'\n appCtx<%s, %d>\nTCAP message type: %s\nService: %s"
                  + "\nLocal Address: %s\nRemote Address: %s",
              channelMessage.getParameter("dialog"), capDialog.getLocalDialogId(),
              applicationContext, applicationContextCode, capDialog.getTCAPMessageType(),
              capDialog.getService(), capDialog.getLocalAddress(), capDialog.getRemoteAddress()));
        } else {
          Object object = channelMessage.getParameter("dialog");
          logger.debug("channelMessage.getParameter(\"dialog\") = " + object);
        }
      } else {
        logger.debug(
            "capMessage is not NULL, message type = " + capMessage.getMessageType().toString());
        if (capMessage.getMessageType().toString().endsWith("Request")) {
          logger.debug(String.format("[CAP::REQUEST<%s>] dialogId '%d', invokeId '%d'",
              capMessage.getMessageType().toString(), capMessage.getCAPDialog().getLocalDialogId(),
              capMessage.getInvokeId()));
          logger.debug("CAP Proxy SCF (Leg1) call step = "
              + proxyPrototype.getCapProxyCallStep(true, false, false));
          logger.debug("CAP Proxy SSF call step = "
              + proxyPrototype.getCapProxyCallStep(false, true, false));
          logger.debug("CAP Proxy SCF (Leg2) call step = "
              + proxyPrototype.getCapProxyCallStep(false, false, true));
          // *** IDP Request ***
          if (capMessage.getMessageType().toString().equals("initialDP_Request")) {
            try {
              if (proxyPrototype.getLeg1ScfCallStep() != BcsmCallStep.conSent) {
                proxyPrototype.onInitialDPRequestFromVPLMN_leg1((InitialDPRequest) capMessage);
                proxyPrototype.relayInitialDPRequestToHPLMNviaSTP((InitialDPRequest) capMessage);
              } else {
                logger.debug(
                    "Received second initialDP_Request on dialog:" + capMessage.getCAPDialog());
                proxyPrototype.onInitialDPRequestFromVPLMN_leg2((InitialDPRequest) capMessage);
              }
            } catch (CAPException e) {
              logger.error("Error: ", e);
              e.printStackTrace();
            }
          }
          // *** RRB Request ***
          if (capMessage.getMessageType().toString().equals("requestReportBCSMEvent_Request")) {
            proxyPrototype
                .onRequestReportBCSMEventRequest((RequestReportBCSMEventRequest) capMessage);
          }
          // *** ETC Request ***
          if (capMessage.getMessageType().toString()
              .equals("establishTemporaryConnection_Request")) {
            proxyPrototype.onEstablishTemporaryConnectionRequest(
                (EstablishTemporaryConnectionRequest) capMessage);
          }
          // *** CUE Request ***
          if (capMessage.getMessageType().toString().equals("continue_Request")) {
            // logger.debug("continue_Request reached CAP Prototype");
          }
          // *** CON Request ***
          if (capMessage.getMessageType().toString().equals("connect_Request")) {
            proxyPrototype.onConnectRequest((ConnectRequest) capMessage);
          }
          // *** ERB Request ***
          if (capMessage.getMessageType().toString().equals("eventReportBCSM_Request")) {
            EventReportBCSMRequest eventReportBCSMRequest = (EventReportBCSMRequest) capMessage;
            if (proxyPrototype.getLeg2ScfCallStep() == BcsmCallStep.conSent) {
              proxyPrototype.onEventReportBCSMRequest(eventReportBCSMRequest);
              proxyPrototype.sendCancelAndFciToVPLMNonLeg2();
              return;
            } else if (proxyPrototype.getLeg2ScfCallStep() != BcsmCallStep.conSent
                && eventReportBCSMRequest.getEventTypeBCSM().name().equals("oAnswer")) {
              proxyPrototype.onEventReportBCSMRequest(eventReportBCSMRequest);
              proxyPrototype.relayERBtoSCPViaSTP(eventReportBCSMRequest);
            } else if (proxyPrototype.getLeg2ScfCallStep() != BcsmCallStep.conSent
                && eventReportBCSMRequest.getEventTypeBCSM().name().equals("oDisconnect")) {
              proxyPrototype.onEventReportBCSMRequest(eventReportBCSMRequest);
              proxyPrototype.relayERBtoSCPViaSTP(eventReportBCSMRequest);
            }
          }
          // *** REL Request ***
          if (capMessage.getMessageType().toString().equals("releaseCall_Request")) {
            proxyPrototype.onReleaseCallRequest((ReleaseCallRequest) capMessage);
          }

        } else if (capMessage.getMessageType().toString().endsWith("Response")) {
          logger.debug(String.format("[CAP::RESPONSE<%s>] dialogId '%d', invokeId '%d'",
              capMessage.getMessageType().toString(), capMessage.getCAPDialog().getLocalDialogId(),
              capMessage.getInvokeId()));
        }
      }
    } catch (Exception e) {
      logger.error("Caught exception: ", e);
    }
  }

  @Override
  public int sendMessageResponse(ChannelMessage channelMessage) {
    return 0;
  }

  public static void main(String[] args) throws Exception {
    // read configuration
    InputStream inputStream =
        new SignalingResource("naikeri-signaling-gateway-cap-prototype.xml").getAsStream();
    XmlConfiguration configuration = new XmlConfiguration(inputStream);

    // initialize jss7 stack
    SctpLayer[] sctp = new SctpLayer[numberOfTestLayers];
    M3uaLayer[] m3ua = new M3uaLayer[numberOfTestLayers];
    SccpLayer[] sccp = new SccpLayer[numberOfTestLayers];
    TcapLayer[] tcap = new TcapLayer[numberOfTestLayers];

    try {
      for (int i = 0; i < numberOfTestLayers; i++) {

        logger.info("Initializing SCTP" + i + " layer...");
        SctpSettings sctpSettings = (SctpSettings) configuration.getLayerSettings("sctp" + i);
        sctp[i] = new SctpLayer(sctpSettings);

        logger.info("Initializing M3UA" + i + " layer...");
        M3uaSettings m3uaSettings = (M3uaSettings) configuration.getLayerSettings("m3ua" + i);
        m3ua[i] = new M3uaLayer(m3uaSettings, sctp[i]);

        logger.info("Initializing SCCP" + i + " layer...");
        SccpSettings sccpSettings = (SccpSettings) configuration.getLayerSettings("sccp" + i);
        sccp[i] = new SccpLayer(sccpSettings, m3ua[i]);

        logger.info("Initializing TCAP" + i + " layer...");
        TcapSettings tcapSettings = (TcapSettings) configuration.getLayerSettings("tcap" + i);
        tcap[i] = new TcapLayer(tcapSettings, sccp[i]);

        logger.info("Initializing CAP" + i + " layer...");
        CapSettings capSettings = (CapSettings) configuration.getLayerSettings("cap" + i);
        cap[i] = new CapLayer(capSettings, tcap[i]);
      }
    } catch (Exception e) {
      logger.error("Caught exception while initializing jss7 stack!", e);
    }

    // read CAP channel configuration
    ChannelSettings channelSettings = configuration.getChannelSettings("Cap");

    // create the instance for CAP channel to connect to ESG
    try {
      CapPrototype capPrototype = new CapPrototype(channelSettings);
      capPrototype.channelInitialize();
      capPrototype.proxyPrototype.initializeMSRNArray();

      if (cap[0] != null) {
        cap[0].setChannelHandler(capPrototype);
        cap[1].setChannelHandler(capPrototype);

        int calls = 50;
        for (int i = 0; i < calls; i++) {
          Thread.sleep(10000);
          capPrototype.vplmnStpPrototype.sendInitialDPRequest();
        }
      } else {
        logger.error("Cap layer not initialized!");
      }
    } catch (Exception e) {
      logger.error("Error: ", e);
      e.printStackTrace();
    }
  }

  @Override
  public LayerInterface getLayerInterface() {
    return null;
  }

  @Override
  public void channelInitialize(LayerInterface[] layerInterface) {

  }

  @Override
  public LayerInterface getLayerInterface(String serviceFunctionName) {
    return null;
  }

}

