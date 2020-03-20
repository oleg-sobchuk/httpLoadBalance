package com.sobchuk;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestCounter {
  private AtomicInteger counter = new AtomicInteger();
  private long timeStart = System.currentTimeMillis();
  private long timeSpend = 0;

  private RequestCounter(){}

  private static class Holder {
    private static final RequestCounter INSTANCE = new RequestCounter();
  }

  public static RequestCounter getInstance() {
    return Holder.INSTANCE;
  }

  public int increment() {
    return counter.incrementAndGet();
  }

  public long getTimeSpend() {
    return timeSpend;
  }

  public void calcTimeSpend(long lastRequestTime) {
    timeSpend = lastRequestTime - timeStart;
  }

  public void reset() {
    counter.set(0);
    timeSpend = 0;
    timeStart = System.currentTimeMillis();
  }

  public int getCounter() {
    return counter.get();
  }
}
