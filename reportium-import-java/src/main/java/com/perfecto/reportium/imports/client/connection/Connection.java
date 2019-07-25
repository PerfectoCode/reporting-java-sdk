package com.perfecto.reportium.imports.client.connection;

import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Class denoting connection data to Reportium server
 */
public class Connection {

    private URI reportingServer;
    private String securityToken;
    private HttpHost proxy;
    private CredentialsProvider credentialsProvider;
    private SSLConnectionSocketFactory sslSocketFactory;
    private Map<String, String> headers;

    /**
     * Create a new connection to Reportium backend
     *
     * @param securityToken   security token (offline token) to use for authentication with reporting
     * @param reportingServer the reportium server URL
     */
    public Connection(URI reportingServer, String securityToken) {
        this.securityToken = securityToken;
        this.reportingServer = reportingServer;
        this.headers = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    /**
     * @return the reportium server URL
     */
    public URI getReportingServer() {
        return reportingServer;
    }

    /**
     * @return security token (offline token) to use for authentication with reporting
     */
    public String getSecurityToken() {
        return securityToken;
    }

    /**
     * @return Proxy server to be used when establishing the connection
     */
    public HttpHost getProxy() {
        return proxy;
    }

    /**
     * @param proxy Proxy server to be used when establishing the connection
     */
    public void setProxy(HttpHost proxy) {
        this.proxy = proxy;
    }

    /**
     * @return Credentials provider to be used when establishing the connection to the proxy server
     */
    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    /**
     * @param credentialsProvider Credentials provider to be used when establishing the connection to the proxy server
     */
    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public SSLConnectionSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public void setSslSocketFactory(SSLConnectionSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }
}
