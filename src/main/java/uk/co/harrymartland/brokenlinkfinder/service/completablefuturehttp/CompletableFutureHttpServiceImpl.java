package uk.co.harrymartland.brokenlinkfinder.service.completablefuturehttp;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.harrymartland.brokenlinkfinder.futures.CompletableFutureCallBack;

import java.util.concurrent.CompletableFuture;

@Service
public class CompletableFutureHttpServiceImpl implements CompletableFutureHttpService {

    @Autowired
    private CloseableHttpAsyncClient httpclient;

    @Override
    public CompletableFuture<HttpResponse> http(HttpUriRequest httpRequest, CompletableFuture<HttpResponse> listCompletableFuture) {
        httpclient.execute(httpRequest, new CompletableFutureCallBack<>(listCompletableFuture));
        return listCompletableFuture;
    }

    @Override
    public CompletableFuture<HttpResponse> get(String url, RequestConfig requestConfig) {
        CompletableFuture<HttpResponse> listCompletableFuture = new CompletableFuture<>();
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);
            return http(httpGet, listCompletableFuture);
        } catch (Exception e) {
            listCompletableFuture.completeExceptionally(e);
        }
        return listCompletableFuture;
    }

    @Override
    public CompletableFuture<HttpResponse> head(String url, RequestConfig requestConfig) {
        CompletableFuture<HttpResponse> listCompletableFuture = new CompletableFuture<>();
        try {
            HttpHead httpHead = new HttpHead(url);
            httpHead.setConfig(requestConfig);
            return http(httpHead, listCompletableFuture);
        } catch (Exception e) {
            listCompletableFuture.completeExceptionally(e);
        }
        return listCompletableFuture;
    }


}
