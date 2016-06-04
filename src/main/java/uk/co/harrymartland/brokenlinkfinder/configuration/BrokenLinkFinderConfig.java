package uk.co.harrymartland.brokenlinkfinder.configuration;

import org.apache.http.client.RedirectStrategy;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "uk.co.harrymartland.brokenlinkfinder")
public class BrokenLinkFinderConfig extends SpringBootServletInitializer {

    @Value("${http.client.connections.maxPerRoute}")
    private int httpClientConnectionsMaxPerRoute;
    @Value("${http.client.connections.maxTotal}")
    private int httpClientConnectionsMaxTotal;

    @Bean
    ConnectingIOReactor connectingIOReactor() throws IOReactorException {
        return new DefaultConnectingIOReactor();
    }

    @Bean
    public NHttpClientConnectionManager nHttpClientConnectionManager(ConnectingIOReactor connectingIOReactor) {
        PoolingNHttpClientConnectionManager nHttpClientConnectionManager = new PoolingNHttpClientConnectionManager(connectingIOReactor);
        nHttpClientConnectionManager.setDefaultMaxPerRoute(httpClientConnectionsMaxPerRoute);
        nHttpClientConnectionManager.setMaxTotal(httpClientConnectionsMaxTotal);
        return nHttpClientConnectionManager;
    }

    @Bean
    public RedirectStrategy redirectStrategy() {
        return new LaxRedirectStrategy();
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpAsyncClient httpAsyncClient(NHttpClientConnectionManager nHttpClientConnectionManager,
                                                    RedirectStrategy redirectStrategy) throws IOReactorException {
        CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setConnectionManager(nHttpClientConnectionManager)
                .setRedirectStrategy(redirectStrategy)
                .disableCookieManagement()
                .build();
        client.start();
        return client;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.out.println("configure broken link...");
        return application.sources(BrokenLinkFinderConfig.class);
    }
}
