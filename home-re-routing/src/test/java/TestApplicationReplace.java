import static org.junit.Assert.assertNull;
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
    channelMessage.setParameter("messagetype", "provideRoamingNumber_Request");

    MapProcessingNode mapNode =
        new MapProxyBuilder.Builder().setChannelMessage(channelMessage).buildMapProcessingNode();

    ReplacedValues result =
        mapNode.getReplacedRule("972540402000108", "38354121022", "425100402000108");
    assertNull(result);
  }

  @Test
  public void MatchNullPrimitiveNull() {

    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter("messagetype", "provideRoamingNumber_Request");
    MapProcessingNode mapNode =
        new MapProxyBuilder.Builder().setChannelMessage(channelMessage).buildMapProcessingNode();
    ReplacedValues result =
        mapNode.getReplacedRule("972540402000108", "38354121022", "425100402000108");
    assertNull(result);
  }

  @Test
  public void MatchNullPrimitiveEmpty() {
    ChannelMessage channelMessage = new ChannelMessage(UUID.randomUUID().toString(), "Map");
    channelMessage.setParameter("messagetype", "provideRoamingNumber_Request");
    MapProcessingNode mapNode =
        new MapProxyBuilder.Builder().setChannelMessage(channelMessage).buildMapProcessingNode();
    ReplacedValues result =
        mapNode.getReplacedRule("972540402000108", "38354121022", "425100402000108");
    assertNull(result);
  }


}
