package com.sobchuk;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager implements Runnable {

  private RequestCounter requestCounter;
  private Queue<RequestTask> threads;
  private ExecutorService executorService;
  private RequestThreadFactory threadFactory;
  private int poolCapacity;
  private int requestsPerSec;
  private int processors;

  public ThreadManager(int requestsPerSec, int threadsPerProcessor) {
    this.requestsPerSec = requestsPerSec;
    init(threadsPerProcessor);
  }

  private void init(int threadsPerProcessor) {
    processors = Runtime.getRuntime().availableProcessors();
    poolCapacity = processors * threadsPerProcessor;
    threads = new ArrayDeque<>(poolCapacity);
    executorService = Executors.newFixedThreadPool(poolCapacity);
    requestCounter = RequestCounter.getInstance();
    threadFactory = new RequestThreadFactory(requestsPerSec);
  }

  @Override
  public void run() {
    System.out.println(">>>>>>>>>>>>>>>");
    int counter = requestCounter.getCounter();
    if (counter < requestsPerSec * 0.9) {
      tryIncreasePerformance();
    } else {
      tryDecreasePerformance();
    }
    System.out.println("request send: " + counter);
    System.out.println("current pool size: " + threads.size());

    requestCounter.reset();
    System.out.println("<<<<<<<<<<<<<<<<<<");
  }

  private void tryIncreasePerformance() {
    if (poolCapacity > threads.size()) {
      if (threads.size() < processors) {
        addTreadsToExecutor(1);
      } else {
        addTreadsToExecutor(processors);
      }
    } else {
      System.out.println("Can't increase performance. Max thread number reached: " + threads.size());
    }
  }

  private void addTreadsToExecutor(int threadNumberToAdd) {
    System.out.printf("Adding %d thread(s)%n", threadNumberToAdd);
    for (int i = 0; threads.size() < poolCapacity && i < threadNumberToAdd; i++) {
      RequestTask newThread = threadFactory.getNewThread();
      threads.add(newThread);
      executorService.submit(newThread);
    }
  }

  private void tryDecreasePerformance() {
    long timeSpended = requestCounter.getTimeSpend();
    System.out.printf("time spend to send %d requests: %d%n", requestCounter.getCounter(), timeSpended);
    if (timeSpended < 1000 * 0.6 && timeSpended !=0) {
      if (threads.size() <= processors && threads.size() > 1) {
        deleteThreadFromExecutor(1);
      } else {
        deleteThreadFromExecutor(processors);
      }
    }
  }

  private void deleteThreadFromExecutor(int threadsToDelete) {
    System.out.printf("Decreasing performance... Terminating %d thread(s)%n", threadsToDelete);
    for (int i = 0; i < threadsToDelete; i++) {
      threads.remove().interrupt();
    }
  }
}
