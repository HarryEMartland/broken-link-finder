package uk.co.harrymartland.brokenlinkfinder.messages;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public class ScanResult {
    private String parentUrl = StringUtils.EMPTY;
    private List<String> childUrls = Collections.emptyList();
    private Throwable throwable = null;

    public ScanResult(Throwable throwable) {
        this.throwable = throwable;
    }

    public ScanResult(String parentUrl, List<String> childUrls) {
        this.parentUrl = parentUrl;
        this.childUrls = childUrls;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public List<String> getChildUrls() {
        return childUrls;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
