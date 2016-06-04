package uk.co.harrymartland.brokenlinkfinder.service.urlfinder;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UrlFinderService {
    CompletableFuture<List<URL>> findUrls(String url);
}
