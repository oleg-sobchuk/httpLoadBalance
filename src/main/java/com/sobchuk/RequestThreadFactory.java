package com.sobchuk;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

public class RequestThreadFactory {
  private final String url = "http://google.com";
  private final HttpGet get = new HttpGet(url);
  private final HttpPost post = new HttpPost(url);
  private final int maxRequestCount;
  private int nameCounter;

  public RequestThreadFactory(int maxRequestCount) {
    this.maxRequestCount = maxRequestCount;
  }

  public RequestTask getNewThread() {
    return new RequestTask(maxRequestCount, "thread" + ++nameCounter, get, post);
  }

}
