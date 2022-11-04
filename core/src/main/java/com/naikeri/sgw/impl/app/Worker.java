package com.naikeri.sgw.impl.app;

import com.naikeri.sgw.api.chn.ChannelMessage;
import org.apache.log4j.Logger;

public class Worker extends Thread {

    private static final Logger logger = Logger.getLogger(Worker.class);

    private Application application;
    private Integer workerId;

    Worker(Integer workerId, Application application) {

        logger.info("Worker Id '" + workerId + "' instantiated for application '" + application.getApplicationSettings().getName() + "'");

        this.application = application;
        this.workerId = workerId;
    }

    public void run() {
        logger.info("Worker Id '" + workerId + "' started for application '" + application.getApplicationSettings().getName() + "'");
        while(application.getStatus() != Application.Status.Terminated) {
            try {
                if (application.getStatus() == Application.Status.Suspended) {
                    Thread.sleep(50);
                } else {
                    ChannelMessage message = application.getNextMessage();
                    application.processMessage(message);
                }
            } catch (Exception e) {
                logger.error("Worker id '" + workerId + "' caught exception '" + e + "'");
                e.printStackTrace();
            }
        }
    }
}
