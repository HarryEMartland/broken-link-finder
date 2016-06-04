package uk.co.harrymartland.brokenlinkfinder;

import org.springframework.boot.SpringApplication;
import uk.co.harrymartland.brokenlinkfinder.configuration.BrokenLinkFinderConfig;

public class BrokenLinkFinderApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BrokenLinkFinderConfig.class, args);
    }
}
