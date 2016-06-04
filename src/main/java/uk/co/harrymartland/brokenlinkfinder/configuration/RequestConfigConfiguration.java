package uk.co.harrymartland.brokenlinkfinder.configuration;

import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestConfigConfiguration {
    @Value("${http.validate.head.connectionTimeout}")
    private int validateHeadConnectionTimeout;

    @Value("${http.validate.head.socketTimeout}")
    private int validateHeadSocketTimeout;

    @Value("${http.findurls.connectionTimeout}")
    private int findUrlsConnectionTimeout;

    @Value("${http.findurls.socketTimeout}")
    private int findUrlsSocketTimeout;

    @Bean
    RequestConfig validateHeadHttpConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(validateHeadConnectionTimeout)
                .setSocketTimeout(validateHeadSocketTimeout)
                .build();
    }

    @Bean
    RequestConfig findUrlsHttpConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(findUrlsConnectionTimeout)
                .setSocketTimeout(findUrlsSocketTimeout)
                .build();
    }

}
