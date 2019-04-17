package com.perfecto.reportium.imports.client.connection;

import com.perfecto.reportium.imports.client.InternalConstants;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Generic HTTP client
 */
public class ReportingHttpClient {
    private static int TIMEOUT_MILLIS = Integer.parseInt((System.getProperty(InternalConstants.Url.Timing.httpTimeoutMillis, "30000")));

    private Connection connection;

    public ReportingHttpClient(Connection connection) {
        this.connection = connection;
    }

    public HttpResponse get(URI uri, Map<String, String> queryParams, Header... headers) {
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                uriBuilder = uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());

            for (Header header : headers) {
                httpGet.addHeader(header);
            }
            org.apache.http.HttpResponse httpResponse = sendRequest(httpGet);
            return new HttpResponse(httpResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public HttpResponse post(URI uri, HttpEntity entity, Map<String, String> queryParams, Header... headers) {
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                uriBuilder = uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            HttpPost httpPost = new HttpPost(uriBuilder.build());

            httpPost.setEntity(entity);
            for (Header header : headers) {
                httpPost.addHeader(header);
            }
            org.apache.http.HttpResponse httpResponse = sendRequest(httpPost);
            return new HttpResponse(httpResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public HttpResponse put(URI uri, HttpEntity entity, Map<String, String> queryParams, Header... headers) {
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                uriBuilder = uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            HttpPut httpPut = new HttpPut(uriBuilder.build());

            httpPut.setEntity(entity);
            for (Header header : headers) {
                httpPut.addHeader(header);
            }
            org.apache.http.HttpResponse httpResponse = sendRequest(httpPut);
            return new HttpResponse(httpResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private org.apache.http.HttpResponse sendRequest(HttpRequestBase httpRequest) throws IOException {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        if (this.connection.getProxy() != null) {
            httpClientBuilder.setProxy(this.connection.getProxy());
        }
        if (this.connection.getCredentialsProvider() != null) {
            httpClientBuilder.setDefaultCredentialsProvider(this.connection.getCredentialsProvider());
        }
        if (this.connection.getSslSocketFactory() != null) {
            httpClientBuilder.setSSLSocketFactory(this.connection.getSslSocketFactory());
        }
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(3, true));
        httpClientBuilder.setDefaultRequestConfig(RequestConfig.custom()
                .setSocketTimeout(TIMEOUT_MILLIS)
                .setConnectTimeout(TIMEOUT_MILLIS)
                .setConnectionRequestTimeout(TIMEOUT_MILLIS)
                .build());
        return httpClientBuilder.build().execute(httpRequest);
    }
}
