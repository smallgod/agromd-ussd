package com.agromarketday.ussd.config;

/**
 *
 * @author smallgod
 */
public class HttpClientPoolConfig {

    private final int readTimeout;
    private final int connTimeout;
    private final int connPerRoute;
    private final int maxConnections;

    public HttpClientPoolConfig(int readTimeout, int connTimeout, int connPerRoute, int maxConnections) {
        this.readTimeout = readTimeout;
        this.connTimeout = connTimeout;
        this.connPerRoute = connPerRoute;
        this.maxConnections = maxConnections;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getConnTimeout() {
        return connTimeout;
    }

    public int getConnPerRoute() {
        return connPerRoute;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

}
