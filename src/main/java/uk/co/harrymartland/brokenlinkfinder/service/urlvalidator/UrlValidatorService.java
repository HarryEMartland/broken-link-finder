package uk.co.harrymartland.brokenlinkfinder.service.urlvalidator;

import uk.co.harrymartland.brokenlinkfinder.messages.UrlResult;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UrlValidatorService {
    CompletableFuture<List<CompletableFuture<UrlResult>>> validate(List<URL> urls);
}
