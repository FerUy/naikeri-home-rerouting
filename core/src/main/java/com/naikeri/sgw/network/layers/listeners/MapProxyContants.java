package com.naikeri.sgw.network.layers.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;

public class MapProxyContants {
  private MapProxyContants() {
  }

  /**
   * The message type indicating the primitive event
   */
  public static final String MESSAGE_TYPE = "messageType";
  /**
   * The MAP or CAP message event Object e.g. SendRoutingInformationRequest
   */
  public static final String MESSAGE = "message";
  /**
   * The Invoke Id
   */
  public static final String INVOKE_ID = "invokeId";
  public static final String DIALOG = "dialog";
  public static final String MAP_ERROR_MESSAGE = "mapErrorMessage";
  public static final String ON_ERROR_COMPONENT = "onErrorComponent";
  public static final String ON_REJECT_COMPONENT = "onRejectComponent";
  public static final String ON_INVOKE_TIMEOUT = "onInvokeTimeout";
  public static final String ON_MAP_MESSAGE = "onMAPMessage";
  public static final String MAP_MESSAGE = "mapmessage";
  public static final String PROBLEM = "problem";
  public static final String LOCAL_ORIGINATED = "localOriginated";
  public static final String MAP_EXTENSION_CONTAINER = "mapExtensionContainer";
  public static final String ON_DIALOG_CLOSE = "onDialogClose";
  private static final Map<Long, String> errorToStringMap;
  public static final String CAP_ERROR_MESSAGE = "capErrorMessage";
  public static final String CAP_LAYER_NAME = "layerName";
  public static final String ON_CAP_MESSAGE = "on_CapMessage";
  public static final String GPRS_REF_NUM = "gprs-ref-num";
  public static final String ON_DIALOG_TIMEOUT = "onDialogTimeout";
  public static final String TCAP_MESSAGE_TYPE = "TCAP_EVENT_TYPE";
  public static final String TCAP_MESSAGE = "TCAP_MESSAGE";
  public static final String TCAP_MESSAGE_DIALOG = "TCAP_MESSAGE_DIALOG";

  static {
    errorToStringMap = new HashMap<>();
    errorToStringMap.put(1L, "Unknown Subscriber");
    errorToStringMap.put(2L, "Unknown Base Station");
    errorToStringMap.put(3L, "Unknown MSC");
    errorToStringMap.put(5L, "Unidentified Subscriber");
    errorToStringMap.put(6L, "Absent SubscriberSM");
    errorToStringMap.put(7L, "Unknown Equipment");
    errorToStringMap.put(8L, "Roaming NotAllowed");
    errorToStringMap.put(9L, "Illegal Subscriber");
    errorToStringMap.put(10L, "Bearer Service Not Provisioned");
    errorToStringMap.put(11L, "Teleservice Not Provisioned");
    errorToStringMap.put(12L, "Illegal Equipment");
    errorToStringMap.put(13L, "Call Barred");
    errorToStringMap.put(14L, "Forwarding Violation");
    errorToStringMap.put(15L, "CUG Reject");
    errorToStringMap.put(16L, "Illegal SSOperation");
    errorToStringMap.put(17L, "SS Error Status");
    errorToStringMap.put(18L, "SS Not Available");
    errorToStringMap.put(19L, "SS Subscription Violation");
    errorToStringMap.put(20L, "SS Incompatibility");
    errorToStringMap.put(21L, "Facility Not Supported");
    errorToStringMap.put(22L, "Ongoing GroupCall");
    errorToStringMap.put(23L, "Invalid Target Base Station");
    errorToStringMap.put(24L, "No Radio Resource Available");
    errorToStringMap.put(25L, "No Handover Number Available");
    errorToStringMap.put(26L, "Subsequent Handover Failure");
    errorToStringMap.put(27L, "Absent Subscriber");
    errorToStringMap.put(28L, "Incompatible Terminal");
    errorToStringMap.put(29L, "Short Term Denial");
    errorToStringMap.put(30L, "Long Term Denial");
    errorToStringMap.put(31L, "Subscriber Busy For MTSMS");
    errorToStringMap.put(32L, "SM Delivery Failure");
    errorToStringMap.put(33L, "Message Waiting List Full");
    errorToStringMap.put(34L, "System Failure");
    errorToStringMap.put(35L, "Data Missing");
    errorToStringMap.put(36L, "Unexpected DataValue");
    errorToStringMap.put(37L, "PW Registration Failure");
    errorToStringMap.put(38L, "Negative PW Check");
    errorToStringMap.put(39L, "No Roaming Number Available");
    errorToStringMap.put(40L, "Tracing Buffer Full");
    errorToStringMap.put(42L, "Target Cell Outside Group Call Area");
    errorToStringMap.put(43L, "Number Of PW Attempts Violation");
    errorToStringMap.put(44L, "Number Changed");
    errorToStringMap.put(45L, "Busy Subscriber");
    errorToStringMap.put(46L, "No Subscriber Reply");
    errorToStringMap.put(47L, "Forwarding Failed");
    errorToStringMap.put(48L, "OR Not Allowed");
    errorToStringMap.put(49L, "ATI Not Allowed");
    errorToStringMap.put(50L, "No Group Call Number Available");
    errorToStringMap.put(51L, "Resource Limitation");
    errorToStringMap.put(52L, "Unauthorized Requesting Network");
    errorToStringMap.put(53L, "Unauthorized LCS Client");
    errorToStringMap.put(54L, "Position Method Failure");
    errorToStringMap.put(58L, "Unknownor Unreachable LCS Client");
    errorToStringMap.put(59L, "MM Event Not Supported");
    errorToStringMap.put(60L, "ATSI Not Allowed");
    errorToStringMap.put(61L, "ATM Not Allowed");
    errorToStringMap.put(62L, "Information Not Available");
    errorToStringMap.put(71L, "Unknown Alphabet");
    errorToStringMap.put(72L, "USSD Busy");
    errorToStringMap.put(-1L, "System Failure");
  }

  public static String getMapErrorCodeToString(MAPErrorMessage mapErrorMessage) {
    long ecode = Optional.ofNullable(mapErrorMessage).map(MAPErrorMessage::getErrorCode)
        .map(Long::longValue).orElse(-1L);
    return errorToStringMap.get(ecode);
  }

  public static String getCapErrorCodeToString(CAPErrorMessage capErrorMessage) {
    long ecode = Optional.ofNullable(capErrorMessage).map(CAPErrorMessage::getErrorCode)
        .map(Long::longValue).orElse(-1L);
    return errorToStringMap.get(ecode);
  }
}
