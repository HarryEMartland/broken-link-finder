package uk.co.harrymartland.brokenlinkfinder.service.completablefuturehttp;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.concurrent.CompletableFuture;

public interface CompletableFutureHttpService {
    CompletableFuture<HttpResponse> http(HttpUriRequest httpRequest, CompletableFuture<HttpResponse> listCompletableFuture);

    CompletableFuture<HttpResponse> get(String url, RequestConfig requestConfig);

    CompletableFuture<HttpResponse> head(String url, RequestConfig requestConfig);
}
