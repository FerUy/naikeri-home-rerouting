package com.naikeri.sgw.impl.queue;

import java.util.HashMap;
import java.util.Map;

public class QueueInstancePool {

    private static Map<String, BlockingQueue> queueList = new HashMap<String, BlockingQueue>();

    private QueueInstancePool() {
    }

    public static synchronized BlockingQueue getQueueInstance(String name) {
        BlockingQueue queueIn;

        if (queueList.containsKey(name)) {
            queueIn = queueList.get(name);
        } else {
            queueIn = BlockingQueue.getInstance(name);
            queueList.put(queueIn.getName(), queueIn);
        }

        return queueIn;
    }
}