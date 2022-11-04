package com.naikeri.sgw.impl.app;

import com.naikeri.sgw.api.app.IApplication;
import com.naikeri.sgw.api.app.QueueInterface;
import com.naikeri.sgw.api.chn.ChannelMessage;
import com.naikeri.sgw.impl.chn.ChannelHandler;
import com.naikeri.sgw.impl.settings.ApplicationSettings;
import com.naikeri.sgw.impl.queue.QueueInstancePool;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class Application implements IApplication {

    private static final Logger logger = Logger.getLogger(Application.class);

    enum Status {Initialized, Running, Suspended, Terminated}
    private Status status = Status.Initialized;

    private ApplicationSettings applicationSettings;

    private List<Thread> workerThreads = new ArrayList<Thread>();
    // private Integer workerThreadCount = 10;
    private QueueInterface queue;

    ChannelHandler channelHandler;

    Status getStatus() {
        return status;
    }

    ApplicationSettings getApplicationSettings() {
        return applicationSettings;
    }

    ChannelMessage getNextMessage() {
        return queue.receive();
    }

    ChannelHandler getChannelHandler() {
        return channelHandler;
    }

    public void setChannelHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    protected Application(ApplicationSettings applicationSettings) {
        this.applicationSettings = applicationSettings;
        // this.channelHandler = channelHandler;

        queue = QueueInstancePool.getQueueInstance(applicationSettings.getChannelName());
        logger.debug("Application '" + applicationSettings.getName() + "' using queue '" +
            applicationSettings.getChannelName() + "' with '" + applicationSettings.getWorkers() + "' workers.");

        for (Integer workerThreadId = 0; workerThreadId < applicationSettings.getWorkers(); workerThreadId++) {
            workerThreads.add(new Worker(workerThreadId, this));
        }
    }

    public void start() {
        status = Status.Running;

        for (Thread worker : workerThreads)
            worker.start();
    }

    void suspend(Boolean suspendApplication) {
        status = suspendApplication ? Status.Suspended : Status.Running;
    }

    void stop() {
        status = Status.Terminated;
    }

}
