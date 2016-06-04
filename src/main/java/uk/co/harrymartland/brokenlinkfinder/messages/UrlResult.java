package uk.co.harrymartland.brokenlinkfinder.messages;

import org.apache.http.HttpStatus;

public class UrlResult {
    private String url;
    private boolean isValid;
    private Throwable throwable;
    private int statusCode;

    public UrlResult(String url, int statusCode) {
        this.url = url;
        this.isValid = HttpStatus.SC_OK == statusCode;
        this.statusCode = statusCode;
    }

    public UrlResult(String url, Throwable throwable) {
        this.url = url;
        this.isValid = false;
        this.throwable = throwable;
    }

    public String getUrl() {
        return url;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isValid() {
        return isValid;
    }
}
