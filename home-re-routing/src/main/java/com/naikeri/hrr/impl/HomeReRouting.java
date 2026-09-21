package com.naikeri.hrr.impl;

import com.naikeri.sgw.impl.SignalingGateway;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HomeReRouting extends SignalingGateway {

  private static final Logger logger = LogManager.getLogger(HomeReRouting.class);

  public static void main(String args[]) {
    Runtime.getRuntime().addShutdownHook(new Thread(){
      @Override
      public void run(){
        try {
          Thread.sleep(200);
          HomeReRouting.haltSignalingGateway();
          Thread.sleep(200);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          logger.error(e);
        }
      }
    });
    // initialize and start system
    HomeReRouting.initialize(args).start();
  }

}