package com.naikeri.sgw.impl.app.map.helper;

import com.naikeri.sgw.impl.app.map.MapDialogOut;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapProxyUtilsHelper {
  private static final Logger logger = LogManager.getLogger(MapProxyUtilsHelper.class);
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
    logger.debug("Discard for <{}>. TransactionId = {}, Reason = {}", messageType, transactionId, message);
    builder.setDiscardReason(message);
    return builder;
  }
}
