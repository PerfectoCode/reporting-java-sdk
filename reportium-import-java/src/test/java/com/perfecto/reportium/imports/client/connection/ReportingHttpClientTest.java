package com.perfecto.reportium.imports.client.connection;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class ReportingHttpClientTest {

    private HttpServer httpServer;
    private int port;
    private RecordingHandler handler;

    @BeforeMethod
    public void setUp() throws IOException {
        handler = new RecordingHandler();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        httpServer.createContext("/", handler);
        httpServer.start();
        port = httpServer.getAddress().getPort();
    }

    @AfterMethod
    public void tearDown() {
        httpServer.stop(0);
    }

    private Connection connectionToTestServer() throws Exception {
        return new Connection(new URI("http://localhost:" + port + "/"), "token");
    }

    @Test
    public void get_noParamsNoHeaders() throws Exception {
        ReportingHttpClient tested = new ReportingHttpClient(connectionToTestServer());
        HttpResponse response = tested.get(new URI("http://localhost:" + port + "/path"), Collections.<String, String>emptyMap());

        assertEquals(response.getStatus(), 200);
        assertEquals(handler.method, "GET");
        assertEquals(handler.path, "/path");
    }

    @Test
    public void get_withParamsAndHeaders() throws Exception {
        ReportingHttpClient tested = new ReportingHttpClient(connectionToTestServer());
        Map<String, String> params = new HashMap<>();
        params.put("a", "1");
        params.put("b", "2");

        HttpResponse response = tested.get(new URI("http://localhost:" + port + "/path"), params, new BasicHeader("X-Test", "hello"));

        assertEquals(response.getStatus(), 200);
        assertEquals(handler.method, "GET");
        assertTrue(handler.query.contains("a=1"));
        assertTrue(handler.query.contains("b=2"));
        assertTrue(handler.requestHeaders.containsKey("X-Test"));
        assertEquals(handler.requestHeaders.get("X-Test").get(0), "hello");
    }

    @Test
    public void post_withEntityAndHeaders() throws Exception {
        ReportingHttpClient tested = new ReportingHttpClient(connectionToTestServer());
        Map<String, String> params = new HashMap<>();
        params.put("p", "v");
        StringEntity entity = new StringEntity("post-body-content", StandardCharsets.UTF_8);

        HttpResponse response = tested.post(new URI("http://localhost:" + port + "/postpath"), entity, params, new BasicHeader("X-One", "1"), new BasicHeader("X-Two", "2"));

        assertEquals(response.getStatus(), 200);
        assertEquals(handler.method, "POST");
        assertEquals(handler.path, "/postpath");
        assertTrue(handler.query.contains("p=v"));
        assertEquals(handler.body, "post-body-content");
        assertTrue(handler.requestHeaders.containsKey("X-One"));
        assertTrue(handler.requestHeaders.containsKey("X-Two"));
    }

    @Test
    public void post_noParamsNoHeaders() throws Exception {
        ReportingHttpClient tested = new ReportingHttpClient(connectionToTestServer());
        StringEntity entity = new StringEntity("body", StandardCharsets.UTF_8);

        HttpResponse response = tested.post(new URI("http://localhost:" + port + "/postpath"), entity, Collections.<String, String>emptyMap());

        assertEquals(response.getStatus(), 200);
        assertEquals(handler.method, "POST");
        assertEquals(handler.body, "body");
    }

    @Test
    public void put_withEntityAndHeaders() throws Exception {
        ReportingHttpClient tested = new ReportingHttpClient(connectionToTestServer());
        Map<String, String> params = new HashMap<>();
        params.put("q", "w");
        StringEntity entity = new StringEntity("put-body-content", StandardCharsets.UTF_8);

        HttpResponse response = tested.put(new URI("http://localhost:" + port + "/putpath"), entity, params, new BasicHeader("X-Put", "yes"));

        assertEquals(response.getStatus(), 200);
        assertEquals(handler.method, "PUT");
        assertEquals(handler.path, "/putpath");
        assertTrue(handler.query.contains("q=w"));
        assertEquals(handler.body, "put-body-content");
        assertTrue(handler.requestHeaders.containsKey("X-Put"));
    }

    @Test
    public void put_noParamsNoHeaders() throws Exception {
        ReportingHttpClient tested = new ReportingHttpClient(connectionToTestServer());
        StringEntity entity = new StringEntity("body", StandardCharsets.UTF_8);

        HttpResponse response = tested.put(new URI("http://localhost:" + port + "/putpath"), entity, Collections.<String, String>emptyMap());

        assertEquals(response.getStatus(), 200);
        assertEquals(handler.method, "PUT");
    }

    @Test
    public void get_withCredentialsProviderAndSslSocketFactory_success() throws Exception {
        Connection connection = connectionToTestServer();
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("user", "pass"));
        connection.setCredentialsProvider(credentialsProvider);
        connection.setSslSocketFactory(SSLConnectionSocketFactory.getSocketFactory());

        ReportingHttpClient tested = new ReportingHttpClient(connection);
        HttpResponse response = tested.get(new URI("http://localhost:" + port + "/path"), Collections.<String, String>emptyMap());

        assertEquals(response.getStatus(), 200);
        assertEquals(handler.method, "GET");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void get_withUnreachableProxy_throwsRuntimeException() throws Exception {
        Connection connection = connectionToTestServer();
        connection.setProxy(new HttpHost("localhost", 1));

        ReportingHttpClient tested = new ReportingHttpClient(connection);
        tested.get(new URI("http://localhost:" + port + "/path"), Collections.<String, String>emptyMap());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void post_withUnreachableProxy_throwsRuntimeException() throws Exception {
        Connection connection = connectionToTestServer();
        connection.setProxy(new HttpHost("localhost", 1));

        ReportingHttpClient tested = new ReportingHttpClient(connection);
        tested.post(new URI("http://localhost:" + port + "/path"), new StringEntity("body", StandardCharsets.UTF_8), Collections.<String, String>emptyMap());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void put_withUnreachableProxy_throwsRuntimeException() throws Exception {
        Connection connection = connectionToTestServer();
        connection.setProxy(new HttpHost("localhost", 1));

        ReportingHttpClient tested = new ReportingHttpClient(connection);
        tested.put(new URI("http://localhost:" + port + "/path"), new StringEntity("body", StandardCharsets.UTF_8), Collections.<String, String>emptyMap());
    }

    private static class RecordingHandler implements HttpHandler {
        private volatile String method;
        private volatile String path;
        private volatile String query;
        private volatile Map<String, List<String>> requestHeaders;
        private volatile String body;

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            method = exchange.getRequestMethod();
            path = exchange.getRequestURI().getPath();
            query = exchange.getRequestURI().getRawQuery();
            requestHeaders = exchange.getRequestHeaders();
            body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

            byte[] responseBytes = "OK".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}
