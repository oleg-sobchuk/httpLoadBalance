package com.sobchuk;

import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class RequestTask implements Runnable {

  private RequestCounter counter;
  private int maxRequestNumber;
  private String name;
  private HttpGet get;
  private HttpPost post;
  private boolean interrupt;

  public RequestTask(int maxRequestNumber, String name, HttpGet get, HttpPost post) {
    this.maxRequestNumber = maxRequestNumber;
    this.name = name;
    this.get = get;
    this.post = post;
    this.counter = RequestCounter.getInstance();
    //System.out.printf("new %s created\n", name);
  }

  @Override
  public void run() {
    int increment = 0;

    while (!Thread.interrupted() && !interrupt) {
      if (isRequestNeed()) {
        choseMethodAndRequest(increment);
      }
    }
    //System.out.printf("%s terminated\n", name);
  }

  private void choseMethodAndRequest(int increment) {
    if ( increment % 2 == 0) {
      doRequest(get);
    } else {
      doRequest(post);
    }
  }

  private void doRequest(HttpUriRequest request) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(request)) {
      System.out.println(EntityUtils.toString(response.getEntity()));
    } catch (IOException e) {
      System.err.println("Received IOException");
    }
  }

  private boolean isRequestNeed() {
    int current = counter.getCounter();
    if (current >= maxRequestNumber) {
      return false;
    } else {
      int increment = counter.increment();
      //System.out.printf("%s: %d\n", name, increment);
      if (increment == maxRequestNumber) {
        counter.calcTimeSpend(System.currentTimeMillis());
        System.out.printf("%s: reached max number of requests\n", name);
        return false;
      }
    }
    return true;
  }

  public void interrupt() {
    interrupt = true;
  }
}
