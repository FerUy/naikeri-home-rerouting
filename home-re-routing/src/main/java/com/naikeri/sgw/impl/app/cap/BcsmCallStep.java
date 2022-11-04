package com.naikeri.sgw.impl.app.cap;

public enum BcsmCallStep {
    idle,
    idpReceived, idpSent,
    rrbReceived, rrbSent,
    etcReceived, etcSent,
    cueReceived, cueSent,
    fciReceived, fciSent,
    conReceived, conSent,
    erbReceived, erbSent,
    relReceived, relSent,
    cancelReceived, cancelSent,
    achReceived, achSent,
    acrReceived, acrSent,
    closed,
    collectedInfo, answerReceived, answerSent, disconnectSent, disconnectReceived, disconnected, calledPartyBusy,
    noAnswer, midCall, busy, abandoned, termAttemptAuthorized, callAccepted, termSeized, changeOfPosition,
    serviceChange, analizedInformation, routeSelectFailure;
}
