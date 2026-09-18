package com.naikeri.hrr.impl;

import com.naikeri.sgw.impl.SignalingGateway;

public class HomeReRouting extends SignalingGateway {

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
          e.printStackTrace();
        }
      }
    });
    // initialize and start system
    HomeReRouting.initialize(args).start();
  }

}