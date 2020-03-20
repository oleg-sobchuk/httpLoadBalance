package com.sobchuk;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
  private static final int THREADS_PER_PROCESSOR = 16;
  private static final long TIMEOUT = 1L;
  private static final String INFO_MSG = "Provide please unsigned integer number of needed requests per second: ";
  private static final String GREETING_MSG = "Hello! Enter number of requests per second: ";

  public static void main(String[] args) {

    ScheduledExecutorService scheduledExecutorService = Executors
        .newSingleThreadScheduledExecutor();

    scheduledExecutorService
        .scheduleAtFixedRate(new ThreadManager(readInputFromUser(), THREADS_PER_PROCESSOR),
            TIMEOUT, TIMEOUT, TimeUnit.SECONDS);
  }

  private static int readInputFromUser() {
    Scanner scannerIn = new Scanner(System.in);
    System.out.print(GREETING_MSG);
    while (true) {
      while (!scannerIn.hasNextInt()) {
        scannerIn.next();
        System.out.print(INFO_MSG);
      }

      int requestsPerSec = scannerIn.nextInt();
      if (requestsPerSec > 0) {
        return requestsPerSec;
      } else {
        System.out.print(INFO_MSG);
      }

    }
  }
}
