package com.naikeri.sgw.impl.queue;

import com.naikeri.sgw.api.app.QueueInterface;
import com.naikeri.sgw.api.chn.ChannelMessage;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class BlockingQueue implements QueueInterface {

    private static final Logger logger = Logger.getLogger(BlockingQueue.class);

    private static BlockingQueue instance = null;

    private String name;

    private List<ChannelMessage> queue = new LinkedList<ChannelMessage>();
    private int size = 9999;

    public String getName() {
        return name;
    }

    private BlockingQueue(String name) {
        this.name = name;
    }

    public static BlockingQueue getInstance(String name) {
        return instance == null ? new BlockingQueue(name) : instance;
    }

    public static synchronized BlockingQueue getInstance(String name, int size) {
        if (instance == null) {
            instance = new BlockingQueue(name);
            if (size > 0)
                instance.size = size;
        } else {
            if (size > 0) {
                if (instance.size <= size) {
                    if (instance.queue.size() >= size)
                        instance.notifyAll();
                }
                instance.size = size;
            }
        }
        return instance;
    }

    public synchronized void send(ChannelMessage element) {
        try {
            while (this.queue.size() >= size)
                wait();

            if (this.queue.size() == 0)
                notifyAll();

            this.queue.add(element);

            logger.debug("Sent message tid '" + element.getTransactionId() + "' over queue '" + name +
                "' by thread '" + Thread.currentThread().getName() + "'");
        } catch (InterruptedException e) {
            logger.warn("Sent message tid '" + element.getTransactionId() + "' over queue '" + name +
                "' by thread '" + Thread.currentThread().getName() + "' was interrupted!", e);
        }
    }

    public synchronized ChannelMessage receive() {
        ChannelMessage element = null;
        try {
            while (this.queue.size() == 0)
                wait();

            if (this.queue.size() >= size)
                notifyAll();

            element = (ChannelMessage) this.queue.remove(0);

            logger.debug("Message tid '" + element.getTransactionId() + "' received over queue '" + name +
                "' by thread '" + Thread.currentThread().getName() + "'");
        } catch (InterruptedException e) {
            logger.debug("' received over queue '" + name +
                "' by thread '" + Thread.currentThread().getName() + "' was interrupted!", e);
        }

        return element;
    }
}
