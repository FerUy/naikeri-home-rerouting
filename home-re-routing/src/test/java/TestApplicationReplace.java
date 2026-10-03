import static org.junit.Assert.assertNull;
import org.restcomm.protocols.ss7.map.api.MAPMessageType;
import com.naikeri.sgw.network.layers.listeners.ProxyConstants;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle0100;
import java.util.UUID;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.impl.app.map.MapProcessingNode;
import com.naikeri.sgw.impl.app.map.MapProxyBuilder;
import com.naikeri.sgw.impl.rules.MapProxyApplicationRules;
import com.naikeri.sgw.impl.rules.ReplacedValues;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TestApplicationReplace
 */
public class TestApplicationReplace {

  @BeforeClass
  public static void loadXMLInstances() {
    // load the application rules
    MapProxyApplicationRules.getInstance().addMapApplicationRules("map-application-rules.xml");
    System.out.println(MapProxyApplicationRules.getInstance().getApplicationRules().size());
  }

  @Test
  public void TestMatchWithEmptyIMSI() {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter(ProxyConstants.MESSAGE_TYPE, "provideRoamingNumber_Request");

    MapProcessingNode mapNode =
        new MapProxyBuilder.Builder().setChannelMessage(channelMessage).buildMapProcessingNode();
    mapNode.setMessageType(MAPMessageType.valueOf("provideRoamingNumber_Request"));

    ReplacedValues result =
        mapNode.getReplacedRule("972540402000108", "38354121022", "425100402000108");
    assertNull(result);
  }

  @Test
  public void MatchNullPrimitiveNull() {

    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter(ProxyConstants.MESSAGE_TYPE, "provideRoamingNumber_Request");
    MapProcessingNode mapNode =
        new MapProxyBuilder.Builder().setChannelMessage(channelMessage).buildMapProcessingNode();
    mapNode.setMessageType(MAPMessageType.valueOf("provideRoamingNumber_Request"));
    ReplacedValues result =
        mapNode.getReplacedRule("972540402000108", "38354121022", "425100402000108");
    assertNull(result);
  }

  @Test
  public void MatchNullPrimitiveEmpty() {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter(ProxyConstants.MESSAGE_TYPE, "provideRoamingNumber_Request");
    MapProcessingNode mapNode =
        new MapProxyBuilder.Builder().setChannelMessage(channelMessage).buildMapProcessingNode();
    mapNode.setMessageType(MAPMessageType.valueOf("provideRoamingNumber_Request"));
    ReplacedValues result =
        mapNode.getReplacedRule("972540402000108", "38354121022", "425100402000108");
    assertNull(result);
  }

  // One test per flow of the MAP proxy, with the GTs and IMSIs of the deployment's numbering plan:
  // the visited network under 383, the home network under 9725416, its HLR reached as 9725404.

  private static final String VPLMN = "38354121022";
  private static final String VPLMN_IMSI = "425100402000108";
  private static final String HPLMN_IMSI = "425100702000108";

  private static ReplacedValues match(String messageType, String calledGt, String callingGt, String imsi) {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter(ProxyConstants.MESSAGE_TYPE, messageType);
    MapProcessingNode mapNode =
        new MapProxyBuilder.Builder().setChannelMessage(channelMessage).buildMapProcessingNode();
    mapNode.setMessageType(MAPMessageType.valueOf(messageType));
    return mapNode.getReplacedRule(calledGt, callingGt, imsi);
  }

  private static ReplacedValues expectRule(String ruleName, ReplacedValues result) {
    assertNotNull("no rule matched, expected " + ruleName, result);
    assertEquals(ruleName, result.getRuleName());
    return result;
  }

  private static void assertE214(ReplacedValues result) {
    assertEquals(NumberingPlan.ISDN_MOBILE, ((GlobalTitle0100) result.getCalledGlobalTitle()).getNumberingPlan());
  }

  @Test
  public void sendAuthenticationInfoGoesToTheHlrAsE214() {
    ReplacedValues result = expectRule("1-SendAuthenticationInfo request",
        match("sendAuthenticationInfo_Request", "97254040200", VPLMN, VPLMN_IMSI));
    assertEquals("97254070200", result.getCalledGlobalTitle().getDigits());
    assertE214(result);
  }

  @Test
  public void updateLocationGoesToTheHlrAsE214() {
    ReplacedValues result = expectRule("5-updateLocation request",
        match("updateLocation_Request", "97254040200", VPLMN, VPLMN_IMSI));
    assertEquals("97254070200", result.getCalledGlobalTitle().getDigits());
    assertE214(result);
  }

  @Test
  public void updateGprsLocationGoesToTheHlrAsE214() {
    ReplacedValues result = expectRule("13-updateGprsLocation_Request",
        match("updateGprsLocation_Request", "97254040200", VPLMN, VPLMN_IMSI));
    assertEquals("97254070200", result.getCalledGlobalTitle().getDigits());
    assertE214(result);
  }

  @Test
  public void moForwardSmReachesTheHomeSmscWithTheHomeImsi() {
    ReplacedValues result = expectRule("3-moForwardSM_Request",
        match("moForwardSM_Request", "97254160048", VPLMN, VPLMN_IMSI));
    assertEquals(HPLMN_IMSI, result.getImsi());
    assertEquals("97254160149", result.getCalledGlobalTitle().getDigits());
  }

  @Test
  public void mtForwardSmComesFromTheSmscTheVisitedNetworkKnows() {
    ReplacedValues result = expectRule("8-mtForwardSM_Request",
        match("mtForwardSM_Request", VPLMN, "97254160149", HPLMN_IMSI));
    assertEquals("97254160048", result.getCallingGlobalTitle().getDigits());
  }

  @Test
  public void insertSubscriberDataComesFromTheProxy() {
    ReplacedValues result = expectRule("7-InsertSubscriberDataRequest",
        match("insertSubscriberData_Request", VPLMN, "97254160046", VPLMN_IMSI));
    assertEquals("97254160047", result.getCallingGlobalTitle().getDigits());
  }

  @Test
  public void provideRoamingNumberComesFromTheProxy() {
    ReplacedValues result = expectRule("10-provideRoamingNumber_Request",
        match("provideRoamingNumber_Request", VPLMN, "97254160046", HPLMN_IMSI));
    assertEquals("97254160047", result.getCallingGlobalTitle().getDigits());
  }

  // Guards: the called GT is a real condition (spelt CldGT, it was ignored and anything matched) ...
  @Test
  public void sendAuthenticationInfoToAnUnknownHlrMatchesNothing() {
    assertNull(match("sendAuthenticationInfo_Request", "50373700001", VPLMN, VPLMN_IMSI));
  }

  // ... and no rule rewrites an MT carrying the visited network's IMSI (rule 0, which did, is gone)
  @Test
  public void mtForwardSmWithTheVisitedImsiMatchesNothing() {
    assertNull(match("mtForwardSM_Request", VPLMN, "97254160149", VPLMN_IMSI));
  }
}
