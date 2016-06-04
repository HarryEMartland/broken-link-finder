package uk.co.harrymartland.brokenlinkfinder.service.urlfinder;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.co.harrymartland.brokenlinkfinder.service.completablefuturehttp.CompletableFutureHttpService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
class UrlFinderServiceImpl implements UrlFinderService {

    @Autowired
    private CompletableFutureHttpService completableFutureHttpService;

    @Autowired
    @Qualifier("findUrlsHttpConfig")
    private RequestConfig requestConfig;

    @Override
    public CompletableFuture<List<URL>> findUrls(String url) {
        return completableFutureHttpService.get(url, requestConfig)
                .thenComposeAsync(this::toString)
                .thenComposeAsync(content -> findUrlsInString(content, url));
    }

    private URL create(URL context, String url) {
        try {
            return new URL(context, url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private URL create(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<List<URL>> findUrlsInString(String content, String baseUrlStr) {
        return CompletableFuture.supplyAsync(() -> {
            URL baseUrl = create(baseUrlStr);
            Pattern compile = Pattern.compile("href=\"([^\"]+)\"");
            Matcher matcher = compile.matcher(content);
            List<String> results = new ArrayList<>();
            while (matcher.find()) {
                results.add(matcher.group(1));
            }
            return results.stream()
                    .filter(s -> !s.startsWith("#"))
                    .filter(s -> !s.startsWith("//"))
                    .filter(s -> !s.toUpperCase().startsWith("MAILTO:"))
                    .map(s -> formatUrl(s, baseUrl))
                    .collect(Collectors.toList());
        });
    }

    private URL formatUrl(String url, URL baseUrl) {
        url = url.replace("|", "%7C").replace("?", "%3F").replace("\n", "%0A");
        if (!url.startsWith("http")) {
            return create(baseUrl, url);
        } else {
            return create(url);
        }
    }

    private CompletableFuture<String> toString(HttpResponse httpResponse) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}