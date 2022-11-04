package com.naikeri.sgw.impl.app.map.helper;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.naikeri.sgw.impl.app.map.MapDialogOut;
import com.naikeri.sgw.impl.cdr.Cdr;
import com.naikeri.sgw.impl.cdr.CdrImpl;
import com.naikeri.sgw.info.MapProxyCdrRecords;

public class MapProxyCDRWriter {

  private MapProxyCDRWriter() {
  }

  private static final String FORMAT_DATE = "yyyyddMMHHmmssSSSZ";
  private static long incrId = 0;

  public static void addFields(MapDialogOut dialogOut, String messageType, String sessionId) {
    if (dialogOut.getProxyDialog() == null) {
      return;
    }
    Map<String, Object> fields = new HashMap<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE);
    fields.put("ORIGINAL_IMSI", dialogOut.getProxyDialog().getOriginalImsi());
    fields.put("NEW_IMSI", dialogOut.getProxyDialog().getNewIMSI());
    fields.put("START_TIME", simpleDateFormat.format(new Date()));
    fields.put("TIMESTAMP", simpleDateFormat.format(new Date()));
    fields.put("_computeDurationOnly", Instant.now());
    fields.put("LOCAL_DIALOG_ID", dialogOut.getOriginalDialogId());
    fields.put("REMOTE_DIALOG_ID", dialogOut.getOriginalDialogId());
    fields.put("NEW_DIALOG_ID", dialogOut.getNewDialogId());
    fields.put("PRIMITIVE", dialogOut.getDialogOutName(messageType));
    fields.put("RULE_NAME", dialogOut.getProxyDialog().getRuleName());
    fields.put("SESSION_ID", sessionId);
    if (dialogOut.getProxyDialog().getCalledAddress() != null) {
      fields.put("LOCAL_GT",
          dialogOut.getProxyDialog().getCalledAddress().getGlobalTitle().getDigits());
      fields.put("LOCAL_ROUTING_INDICATOR", dialogOut.getProxyDialog().getCalledAddress()
          .getAddressIndicator().getRoutingIndicator().getValue());
      fields.put("LOCAL_SSN", dialogOut.getProxyDialog().getCalledAddress().getSubsystemNumber());
      fields.put("LOCAL_SPC",
          dialogOut.getProxyDialog().getCalledAddress().getSignalingPointCode());
    } else {
      fields.put("LOCAL_GT", "");
      fields.put("LOCAL_ROUTING_INDICATOR", "");
      fields.put("LOCAL_SSN", "");
      fields.put("LOCAL_SPC", "");
    }

    if (dialogOut.getProxyDialog().getCallingAddress() != null) {
      fields.put("REMOTE_GT",
          dialogOut.getProxyDialog().getCallingAddress().getGlobalTitle().getDigits());
      fields.put("REMOTE_ROUTING_INDICATOR", dialogOut.getProxyDialog().getCallingAddress()
          .getAddressIndicator().getRoutingIndicator().getValue());
      fields.put("REMOTE_SSN", dialogOut.getProxyDialog().getCallingAddress().getSubsystemNumber());
      fields.put("REMOTE_SPC",
          dialogOut.getProxyDialog().getCallingAddress().getSignalingPointCode());
    } else {
      fields.put("REMOTE_GT", "");
      fields.put("REMOTE_ROUTING_INDICATOR", "");
      fields.put("REMOTE_SSN", "");
      fields.put("REMOTE_SPC", "");
    }
    // new generated GT for calling and called
    fields.put("NEW_CALLING_GT", dialogOut.getProxyDialog().getNewCallingGt());
    fields.put("NEW_CALLED_GT", dialogOut.getProxyDialog().getNewCalledGt());
    // new spc, ri, ssn
    if (dialogOut.getProxyDialog().getNewCalledAddress() != null) {
      fields.put("NEW_LOCAL_ROUTING_INDICATOR", dialogOut.getProxyDialog().getNewCalledAddress()
          .getAddressIndicator().getRoutingIndicator().getValue());
      fields.put("NEW_LOCAL_SSN",
          dialogOut.getProxyDialog().getNewCalledAddress().getSubsystemNumber());
      fields.put("NEW_LOCAL_SPC",
          dialogOut.getProxyDialog().getNewCalledAddress().getSignalingPointCode());
    } else {
      fields.put("NEW_LOCAL_ROUTING_INDICATOR", "");
      fields.put("NEW_LOCAL_SSN", "");
      fields.put("NEW_LOCAL_SPC", "");
    }
    if (dialogOut.getProxyDialog().getNewCallingAddress() != null) {
      fields.put("NEW_REMOTE_GT",
          dialogOut.getProxyDialog().getNewCallingAddress().getGlobalTitle().getDigits());
      fields.put("NEW_REMOTE_ROUTING_INDICATOR", dialogOut.getProxyDialog().getNewCallingAddress()
          .getAddressIndicator().getRoutingIndicator().getValue());
      fields.put("NEW_REMOTE_SSN",
          dialogOut.getProxyDialog().getNewCallingAddress().getSubsystemNumber());
      fields.put("NEW_REMOTE_SPC",
          dialogOut.getProxyDialog().getNewCallingAddress().getSignalingPointCode());
    } else {
      fields.put("NEW_REMOTE_GT", "");
      fields.put("NEW_REMOTE_ROUTING_INDICATOR", "");
      fields.put("NEW_REMOTE_SSN", "");
      fields.put("NEW_REMOTE_SPC", "");
    }
    MapProxyCdrRecords.getInstance().addCDRFields(dialogOut.getNewDialogId(), fields);
  }

  public static void writeCDR(Long dialogId, String status, Long errorCode, String errorMsg) {
    if (dialogId == null) {
      return;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE);
    Optional.ofNullable(MapProxyCdrRecords.getInstance().getCDRFields(dialogId))
        .ifPresent(fields -> {
          Cdr cdrRecord = new Cdr(fields, MapProxyUtilsHelper.getCDRName());
          Instant starttime = (Instant) fields.get("_computeDurationOnly");
          long duration = ChronoUnit.MILLIS.between(starttime, Instant.now());
          cdrRecord.addField("DURATION", duration);
          cdrRecord.addField("STATUS", status);
          cdrRecord.addField("ERROR_CODE", errorCode);
          cdrRecord.addField("ERROR_CODE_MESSAGE", errorMsg);
          cdrRecord.addField("ENDTIME", simpleDateFormat.format(new Date()));
          incrId += 1;
          cdrRecord.addField("ID", incrId);
          CdrImpl.getInstance().write(cdrRecord);
        });
  }
}
