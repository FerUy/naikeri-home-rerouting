package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapProxyUtilsHelper {
  private static final Logger logger = LoggerFactory.getLogger(MapProxyUtilsHelper.class);
  private static String cdrName;
  private static boolean cdrIsEnabled = false;

  private MapProxyUtilsHelper() {
  }

  public static void setCDRName(String cdrname) {
    MapProxyUtilsHelper.cdrName = cdrname;
    MapProxyUtilsHelper.cdrIsEnabled = (cdrname != null && !cdrname.isEmpty());
  }

  public static String getCDRName() {
    return MapProxyUtilsHelper.cdrName;
  }

  public static boolean isCDREnabled() {
    return MapProxyUtilsHelper.cdrIsEnabled;
  }

  public static MapDialogOut discardReason(String message, String messageType,
      String transactionId) {
    MapDialogOut builder = new MapDialogOut();
    logger.debug(String.format("Discard for <%s>. TransactionId = %s, Reason = %s", messageType,
        transactionId, message));
    builder.setDiscardReason(message);
    return builder;
  }
}
