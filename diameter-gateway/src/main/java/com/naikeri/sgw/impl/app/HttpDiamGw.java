package com.naikeri.sgw.impl.app;

import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.impl.data.CcaDiameterAdaptor;
import com.naikeri.sgw.impl.data.CcaXmlAdaptor;
import com.naikeri.sgw.impl.settings.ApplicationSettings;
import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;

import java.io.IOException;

public class HttpDiamGw extends Application {

    private static final Logger logger = Logger.getLogger(HttpDiamGw.class);

    public HttpDiamGw(ApplicationSettings applicationSettings) {
        super(applicationSettings);
    }

    @Override
    public void processMessage(ChannelMessage channelMessage) {
        HttpRestClient httpRestClient;

        Request ccaDiameterRequest = (Request) channelMessage.getParameter("REQUEST");

        CcaDiameterAdaptor ccaDiameterAdaptor = new CcaDiameterAdaptor();
        ccaDiameterAdaptor.parse(ccaDiameterRequest);

        try {
            httpRestClient = new HttpRestClient(
                getApplicationSettings().getParameter("URL", "http://localhost:1234/DIAMETER"),
                getApplicationSettings().getParameter("METHOD", "POST"),
                Integer.parseInt(getApplicationSettings().getParameter("CONNECT-TIMEOUT", "5000")),
                Integer.parseInt(getApplicationSettings().getParameter("READ-TIMEOUT", "5000")));

            httpRestClient.setHeader("X-Message", channelMessage.getTransactionId());
            httpRestClient.setHeader("X-Protocol", "DIAMETER");

            if (getApplicationSettings().
                getParameter("TRANSLATION", "XML").equalsIgnoreCase("XML")) {

                httpRestClient.setHeader("Content-Type", "text/xml");
                httpRestClient.setHeader("Accept", "text/xml");

                CcaXmlAdaptor ccaXmlaDataAdaptor = new CcaXmlAdaptor();
                ccaXmlaDataAdaptor.set(ccaDiameterAdaptor);

                httpRestClient.performRequest(ccaXmlaDataAdaptor.serialize());

                if (httpRestClient.getResponseCode() == 200) {
                    ccaXmlaDataAdaptor.parse(httpRestClient.getResponseBody());

                    ccaDiameterAdaptor.set(ccaXmlaDataAdaptor);
                    Answer ccaDiameterResponse = (Answer)ccaDiameterAdaptor.serialize();

                    channelMessage.setParameter("ANSWER", ccaDiameterResponse);
                } else {
                    // TODO: something is missing here?!
                }
            } else {
                logger.error("Application does not support configured translation!");
            }
        } catch (IOException e) {
            logger.error("Caugth exception '"  + e.getMessage() + "' while processing diameter message", e);
        }


        channelHandler.sendMessageResponse(channelMessage);
    }

}
