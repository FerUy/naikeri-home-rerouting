package com.naikeri.sgw.impl.settings.sccp;

import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDOddEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0100Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

public class SccpHelpers {

    public SccpHelpers() {
    }

    public static SccpAddress createLocalAddress(GlobalTitle globalTitle, int dpc, int ssn) {
        return new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, globalTitle, dpc, ssn);
    }

    public static SccpAddress createRemoteAddress(GlobalTitle globalTitle, int dpc, int ssn) {
        return new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, globalTitle, dpc, ssn);
    }

    public static GlobalTitle createGlobalTitle0100(String gtDigits) {
        NumberingPlan numberingPlan = NumberingPlan.ISDN_TELEPHONY;
        NatureOfAddress natureOfAddress = NatureOfAddress.INTERNATIONAL;
        EncodingScheme encodingScheme;
        encodingScheme = getEncodingScheme(gtDigits);
        int translationType = 0;
        GlobalTitle globalTitle = new GlobalTitle0100Impl(gtDigits, translationType, encodingScheme, numberingPlan, natureOfAddress);
        return globalTitle;
    }

    public static GlobalTitle createGlobalTitle0100(String gtDigits, int translationType) {
        NumberingPlan numberingPlan = NumberingPlan.ISDN_TELEPHONY;
        NatureOfAddress natureOfAddress = NatureOfAddress.INTERNATIONAL;
        EncodingScheme encodingScheme;
        encodingScheme = getEncodingScheme(gtDigits);
        GlobalTitle globalTitle = new GlobalTitle0100Impl(gtDigits, translationType, encodingScheme, numberingPlan, natureOfAddress);
        return globalTitle;
    }

    public static GlobalTitle createGlobalTitle0100(String gtDigits, int translationType, NatureOfAddress natureOfAddress) {
        NumberingPlan numberingPlan = NumberingPlan.ISDN_TELEPHONY;
        EncodingScheme encodingScheme;
        encodingScheme = getEncodingScheme(gtDigits);
        GlobalTitle globalTitle = new GlobalTitle0100Impl(gtDigits, translationType, encodingScheme, numberingPlan, natureOfAddress);
        return globalTitle;
    }

    public static GlobalTitle createGlobalTitle0100(String gtDigits, int translationType, NatureOfAddress natureOfAddress,
                                                    NumberingPlan numberingPlan) {
        EncodingScheme encodingScheme;
        encodingScheme = getEncodingScheme(gtDigits);
        GlobalTitle globalTitle = new GlobalTitle0100Impl(gtDigits, translationType, encodingScheme, numberingPlan, natureOfAddress);
        return globalTitle;
    }

    public static EncodingScheme getEncodingScheme(String digits) {
        EncodingScheme ec;
        if (digits.length() % 2 == 0)
            ec = new BCDEvenEncodingScheme();
        else
            ec = new BCDOddEncodingScheme();
        return ec;
    }

}
