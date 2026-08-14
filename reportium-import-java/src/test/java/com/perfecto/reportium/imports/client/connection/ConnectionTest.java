package com.perfecto.reportium.imports.client.connection;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class ConnectionTest {

    private static final URI REPORTING_SERVER = create("https://tenant.reporting.perfectomobile.com");
    private static final String SECURITY_TOKEN = "token-123";

    private static URI create(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void constructor_setsReportingServerAndSecurityToken() {
        Connection tested = new Connection(REPORTING_SERVER, SECURITY_TOKEN);

        assertEquals(tested.getReportingServer(), REPORTING_SERVER);
        assertEquals(tested.getSecurityToken(), SECURITY_TOKEN);
        assertNull(tested.getProxy());
        assertNull(tested.getCredentialsProvider());
        assertNull(tested.getSslSocketFactory());
        assertTrue(tested.getHeaders().isEmpty());
    }

    @Test
    public void addHeader_addsToHeadersMap() {
        Connection tested = new Connection(REPORTING_SERVER, SECURITY_TOKEN);
        tested.addHeader("X-Custom", "value1");
        tested.addHeader("X-Other", "value2");

        Map<String, String> headers = tested.getHeaders();
        assertEquals(headers.size(), 2);
        assertEquals(headers.get("X-Custom"), "value1");
        assertEquals(headers.get("X-Other"), "value2");
    }

    @Test
    public void getHeaders_returnsDefensiveCopy() {
        Connection tested = new Connection(REPORTING_SERVER, SECURITY_TOKEN);
        tested.addHeader("X-Custom", "value1");

        Map<String, String> headers = tested.getHeaders();
        headers.put("X-Injected", "hacked");

        assertEquals(tested.getHeaders().size(), 1);
    }

    @Test
    public void setProxy_getProxy() {
        Connection tested = new Connection(REPORTING_SERVER, SECURITY_TOKEN);
        HttpHost proxy = new HttpHost("proxy.example.com", 8080);
        tested.setProxy(proxy);

        assertEquals(tested.getProxy(), proxy);
    }

    @Test
    public void setCredentialsProvider_getCredentialsProvider() {
        Connection tested = new Connection(REPORTING_SERVER, SECURITY_TOKEN);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("user", "pass"));
        tested.setCredentialsProvider(credentialsProvider);

        assertEquals(tested.getCredentialsProvider(), credentialsProvider);
    }

    @Test
    public void setSslSocketFactory_getSslSocketFactory() {
        Connection tested = new Connection(REPORTING_SERVER, SECURITY_TOKEN);
        SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
        tested.setSslSocketFactory(sslSocketFactory);

        assertEquals(tested.getSslSocketFactory(), sslSocketFactory);
    }
}
