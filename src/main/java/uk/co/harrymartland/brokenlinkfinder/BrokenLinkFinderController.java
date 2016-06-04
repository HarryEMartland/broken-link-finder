package uk.co.harrymartland.brokenlinkfinder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import uk.co.harrymartland.brokenlinkfinder.messages.PageScanRequest;
import uk.co.harrymartland.brokenlinkfinder.messages.ScanResult;
import uk.co.harrymartland.brokenlinkfinder.messages.UrlResult;
import uk.co.harrymartland.brokenlinkfinder.service.urlfinder.UrlFinderService;
import uk.co.harrymartland.brokenlinkfinder.service.urlvalidator.UrlValidatorService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
public class BrokenLinkFinderController {

    @Autowired
    private UrlFinderService urlFinderService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UrlValidatorService urlValidatorService;

    @MessageMapping("/scan")
    public void pageScanRequest(PageScanRequest pageScanRequest, SimpMessageHeaderAccessor accessor) throws ExecutionException, InterruptedException {
        urlFinderService.findUrls(pageScanRequest.getUrl())
                .thenComposeAsync(urls -> convertToScanResult(urls, pageScanRequest))
                .exceptionally(ScanResult::new)
                .thenComposeAsync(scanResult -> sendToUser(scanResult, accessor, "/topic/scanResult"))
                .thenComposeAsync(scanResult -> urlValidatorService.validate(scanResult.getChildUrls().stream().map(this::toUrl).collect(Collectors.toList())))
                .thenComposeAsync(completableFutures -> convertAllToUrlResult(completableFutures, accessor, "/topic/urlResult"))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }

    private URL toUrl(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<List<CompletableFuture<UrlResult>>> convertAllToUrlResult(List<CompletableFuture<UrlResult>> completableFutures, SimpMessageHeaderAccessor accessor, String topic) {
        return CompletableFuture.supplyAsync(() -> completableFutures.stream()
                .map(urlResultCompletableFuture -> urlResultCompletableFuture
                        .thenComposeAsync(urlResult -> sendToUser(urlResult, accessor, topic)).exceptionally(throwable -> {
                            throwable.printStackTrace();
                            return null;
                        }))
                .collect(Collectors.toList()));
    }


    private <Message> CompletableFuture<Message> sendToUser(Message message, SimpMessageHeaderAccessor accessor, String topic) {
        return CompletableFuture.supplyAsync(() -> {
            messagingTemplate.convertAndSendToUser(accessor.getSessionId(), topic, message, accessor.getMessageHeaders());
            return message;
        });
    }

    private CompletableFuture<ScanResult> convertToScanResult(List<URL> urls, PageScanRequest pageScanRequest) {
        return CompletableFuture.supplyAsync(() ->
                new ScanResult(pageScanRequest.getUrl(), urls.stream().map(URL::toString).collect(Collectors.toList())));
    }
}
