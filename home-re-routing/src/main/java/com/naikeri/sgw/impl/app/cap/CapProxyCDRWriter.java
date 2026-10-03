package com.naikeri.sgw.impl.app.cap;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.naikeri.sgw.impl.app.cap.CapDialogOut.WriteLogState;
import com.naikeri.sgw.impl.cdr.Cdr;
import com.naikeri.sgw.impl.cdr.CdrImpl;
import com.naikeri.sgw.info.CapProxyCdrRecords;

public class CapProxyCDRWriter {

  private CapProxyCDRWriter() {
  }

  private static final String RULENAME = "RULE_NAME";
  // the old format "MM-dd-yyyy HH:mm:ss.SSSZ"
  private static final String FORMAT_DATE = "yyyyMMddHHmmssSSSZ";
  private static long incrId = 0;

  private static void writeCDRNoError(long dialogId) {
    writeCDR(dialogId, "Success", null, null);
  }

  public static void writeCDR(Long dialogId, String status, Long errorCode, String errorMsg) {
    if (dialogId == null) {
      return;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE);
    CapProxyCdrRecords.getInstance().getCDRRecords(dialogId).ifPresent(fields -> {
      Cdr cdrRecord = new Cdr(fields, CapProxyHelperUtils.getCDRName());
      Instant starttime = (Instant) fields.get("_computeDurationOnly");
      long duration = ChronoUnit.MILLIS.between(starttime, Instant.now());
      cdrRecord.addField("DURATION", duration);
      cdrRecord.addField("STATUS", status);
      cdrRecord.addField("ERROR_CODE", errorCode);
      cdrRecord.addField("ERROR_CODE_MESSAGE", errorMsg);
      cdrRecord.addField("ENDTIME", simpleDateFormat.format(new Date()));
      // increment the index
      incrId += 1;
      cdrRecord.addField("ID", incrId);
      CdrImpl.getInstance().write(cdrRecord);
    });
  }

  private static void addCDRRecord(long dialogId, CapDialogOut dialogOut, String sessionId) {
    if (dialogOut == null) {
      return;
    }
    // add the fields here
    Map<String, Object> fields = new HashMap<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE);
    fields.put("ORIGINAL_IMSI", dialogOut.getOriginalImsi());
    fields.put("NEW_IMSI", dialogOut.getNewIMSI());
    fields.put("START_TIME", simpleDateFormat.format(new Date()));
    fields.put("TIMESTAMP", simpleDateFormat.format(new Date()));
    fields.put("_computeDurationOnly", Instant.now());
    fields.put("LOCAL_DIALOG_ID", dialogOut.getLocalDialogId());
    fields.put("REMOTE_DIALOG_ID", dialogOut.getRemoteDialogId());
    fields.put("NEW_DIALOG_ID", dialogOut.getTransDialogId());
    fields.put("SERVICE_KEY", dialogOut.getServiceKey());
    fields.put(RULENAME, dialogOut.getRuleName());
    fields.put("SESSION_ID", sessionId);
    if (dialogOut.getCalledSccpAddress() == null) {
      fields.put("LOCAL_GT", "");
      fields.put("LOCAL_ROUTING_INDICATOR", "");
      fields.put("LOCAL_SSN", "");
      fields.put("LOCAL_SPC", "");
    } else {
      fields.put("LOCAL_GT", dialogOut.getCalledSccpAddress().getGlobalTitle().getDigits());
      fields.put("LOCAL_ROUTING_INDICATOR",
          dialogOut.getCalledSccpAddress().getAddressIndicator().getRoutingIndicator().getValue());
      fields.put("LOCAL_SSN", dialogOut.getCalledSccpAddress().getSubsystemNumber());
      fields.put("LOCAL_SPC", dialogOut.getCalledSccpAddress().getSignalingPointCode());
    }

    if (dialogOut.getCallingSccpAddress() != null) {
      fields.put("REMOTE_GT", dialogOut.getCallingSccpAddress().getGlobalTitle().getDigits());
      fields.put("REMOTE_ROUTING_INDICATOR",
          dialogOut.getCallingSccpAddress().getAddressIndicator().getRoutingIndicator().getValue());
      fields.put("REMOTE_SSN", dialogOut.getCallingSccpAddress().getSubsystemNumber());
      fields.put("REMOTE_SPC", dialogOut.getCallingSccpAddress().getSignalingPointCode());
    } else {
      fields.put("REMOTE_GT", "");
      fields.put("REMOTE_ROUTING_INDICATOR", "");
      fields.put("REMOTE_SSN", "");
      fields.put("REMOTE_SPC", "");
    }
    // new generated GT for calling and called
    if (dialogOut.getNewCallingSccpAddress() != null) {
      fields.put("NEW_CALLING_GT",
          dialogOut.getNewCallingSccpAddress().getGlobalTitle().getDigits());
      fields.put("NEW_REMOTE_GT",
          dialogOut.getNewCallingSccpAddress().getGlobalTitle().getDigits());
      fields.put("NEW_REMOTE_ROUTING_INDICATOR", dialogOut.getNewCallingSccpAddress()
          .getAddressIndicator().getRoutingIndicator().getValue());
      fields.put("NEW_REMOTE_SSN", dialogOut.getNewCallingSccpAddress().getSubsystemNumber());
      fields.put("NEW_REMOTE_SPC", dialogOut.getNewCallingSccpAddress().getSignalingPointCode());
    } else {
      fields.put("NEW_CALLING_GT", "");
      fields.put("NEW_REMOTE_GT", "");
      fields.put("NEW_REMOTE_ROUTING_INDICATOR", "");
      fields.put("NEW_REMOTE_SSN", "");
      fields.put("NEW_REMOTE_SPC", "");
    }
    if (dialogOut.getNewCalledSccpAddress() != null) {
      fields.put("NEW_CALLED_GT", dialogOut.getNewCalledSccpAddress().getGlobalTitle().getDigits());
      fields.put("NEW_LOCAL_ROUTING_INDICATOR", dialogOut.getNewCalledSccpAddress()
          .getAddressIndicator().getRoutingIndicator().getValue());
      fields.put("NEW_LOCAL_SSN", dialogOut.getNewCalledSccpAddress().getSubsystemNumber());
      fields.put("NEW_LOCAL_SPC", dialogOut.getNewCalledSccpAddress().getSignalingPointCode());
    } else {
      fields.put("NEW_CALLED_GT", "");
      fields.put("NEW_LOCAL_ROUTING_INDICATOR", "");
      fields.put("NEW_LOCAL_SSN", "");
      fields.put("NEW_LOCAL_SPC", "");
    }

    fields.put("LEG", dialogOut.isFirstLegCall() ? "1" : "2");
    fields.put("ERB_EVENT_TYPE", dialogOut.getErbEventName());
    fields.put("MSRN", dialogOut.getMSRN());
    fields.put("CALLING_PARTY_NUMBER", dialogOut.getCallingPartyNumber());
    fields.put("CALLED_PARTY_NUMBER", dialogOut.getCalledPartyNumber());
    CapProxyCdrRecords.getInstance().addCDRRecord(dialogId, fields);
  }

  public static void addCDRRecords(CapDialogOut dialogOut, String sessionId) {
    if (dialogOut == null) {
      return;
    }
    long dialogId = dialogOut.getTransDialogId();
    if (dialogOut.getWriteCDRState() == WriteLogState.WRITE) {
      writeCDRNoError(dialogId);
    } else if (dialogOut.getWriteCDRState() == WriteLogState.INITIAL) {
      addCDRRecord(dialogId, dialogOut, sessionId);
    } else if (dialogOut.getWriteCDRState() == WriteLogState.ADDMORE) {
      addMoreFields(dialogId, dialogOut);
      if (dialogOut.getIsClose()) {
        writeCDRNoError(dialogId);
      }
    }
  }

  private static void addMoreFields(long dialogId, CapDialogOut dialogOut) {
    if (dialogOut == null) {
      return;
    }
    boolean ruleNumeExist = dialogOut.getRuleName() != null && !dialogOut.getRuleName().isEmpty();
    // get the data and update it with the new rule and save it back
    CapProxyCdrRecords.getInstance().getCDRRecords(dialogId).ifPresent(fields -> {
      // update rule if it exist
      if (ruleNumeExist) {
        String rulesNames = String.format("%s-->%s", fields.get(RULENAME), dialogOut.getRuleName());
        fields.put(RULENAME, rulesNames);
      }
      checkAndAddFields(dialogOut, fields);
      CapProxyCdrRecords.getInstance().addCDRRecord(dialogId, fields);
    });
  }

  private static void checkAndAddFields(CapDialogOut dialogOut, Map<String, Object> fields) {
    if (dialogOut.getMSRN() != null && !dialogOut.getMSRN().isEmpty()) {
      fields.put("MSRN", dialogOut.getMSRN());
    }
    if (dialogOut.getCallingPartyNumber() != null && !dialogOut.getCallingPartyNumber().isEmpty()) {
      fields.put("CALLING_PARTY_NUMBER", dialogOut.getCallingPartyNumber());
    }
    if (dialogOut.getCalledPartyNumber() != null && !dialogOut.getCalledPartyNumber().isEmpty()) {
      fields.put("CALLED_PARTY_NUMBER", dialogOut.getCalledPartyNumber());
    }
    if (dialogOut.getErbEventName() != null && !dialogOut.getErbEventName().isEmpty()) {
      fields.put("ERB_EVENT_TYPE", dialogOut.getErbEventName());
    }
  }
}
