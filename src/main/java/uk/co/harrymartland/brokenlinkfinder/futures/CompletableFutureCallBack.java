package uk.co.harrymartland.brokenlinkfinder.futures;

import org.apache.http.concurrent.FutureCallback;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureCallBack<T> implements FutureCallback<T> {

    private final CompletableFuture<T> completableFuture;

    public CompletableFutureCallBack(CompletableFuture<T> completableFuture) {
        this.completableFuture = completableFuture;
    }

    @Override
    public void completed(T o) {
        completableFuture.complete(o);
    }

    @Override
    public void failed(Exception e) {
        completableFuture.completeExceptionally(e);
    }

    @Override
    public void cancelled() {
        completableFuture.cancel(true);
    }
}
