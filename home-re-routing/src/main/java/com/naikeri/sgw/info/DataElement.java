package com.naikeri.sgw.info;

public class DataElement{
  private final String messageType;
  private final Object requestObject;
  private final Long newInvokeId;
  private final Long dialogId;

  public DataElement(String messageType, Long newInvokeId, Long dialogId, Object requestObject){
    this.messageType = messageType;
    this.newInvokeId = newInvokeId;
    this.requestObject = requestObject;
    this.dialogId = dialogId;
  }
  
  public String getMessageType() {
    return messageType;
  }

  public Object getRequestObject() {
    return requestObject;
  }

  public Long getInvokeId() {
    return newInvokeId;
  }
  public Long getDialogId(){
    return this.dialogId;
  }
}