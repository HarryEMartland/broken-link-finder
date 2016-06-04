package uk.co.harrymartland.brokenlinkfinder.service.urlvalidator;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.co.harrymartland.brokenlinkfinder.messages.UrlResult;
import uk.co.harrymartland.brokenlinkfinder.service.completablefuturehttp.CompletableFutureHttpService;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
class UrlValidatorServiceImpl implements UrlValidatorService {

    @Autowired
    private CompletableFutureHttpService completableFutureHttpService;

    @Autowired
    @Qualifier("validateHeadHttpConfig")
    private RequestConfig validateHeadHttpConfig;

    @Override
    public CompletableFuture<List<CompletableFuture<UrlResult>>> validate(List<URL> urls) {
        return CompletableFuture.supplyAsync(() -> urls
                .stream().map(url -> completableFutureHttpService.head(url.toString(), validateHeadHttpConfig)
                        .thenComposeAsync(httpResponse -> createUrlResult(httpResponse, url))
                        .exceptionally((throwable) -> new UrlResult(url.toString(), throwable)))
                .collect(Collectors.toList()));
    }

    private CompletableFuture<UrlResult> createUrlResult(HttpResponse httpResponse, URL url) {
        return CompletableFuture.supplyAsync(() -> new UrlResult(url.toString(), httpResponse.getStatusLine().getStatusCode()));
    }
}