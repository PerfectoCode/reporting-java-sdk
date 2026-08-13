package com.perfecto.reportium.imports.client.connection;

import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class HttpResponseTest {

    private IMocksControl mocksControl;
    private org.apache.http.HttpResponse responseEntityMock;
    private StatusLine statusLineMock;

    @BeforeMethod
    public void beforeTest() {
        mocksControl = EasyMock.createControl();
        responseEntityMock = mocksControl.createMock(org.apache.http.HttpResponse.class);
        statusLineMock = mocksControl.createMock(StatusLine.class);
    }

    @Test
    public void getStatus() {
        EasyMock.expect(responseEntityMock.getStatusLine()).andReturn(statusLineMock);
        EasyMock.expect(statusLineMock.getStatusCode()).andReturn(204);

        mocksControl.replay();
        HttpResponse tested = new HttpResponse(responseEntityMock);
        assertEquals(tested.getStatus(), 204);
        mocksControl.verify();
    }

    @Test
    public void getStatusReason() {
        EasyMock.expect(responseEntityMock.getStatusLine()).andReturn(statusLineMock);
        EasyMock.expect(statusLineMock.getReasonPhrase()).andReturn("No Content");

        mocksControl.replay();
        HttpResponse tested = new HttpResponse(responseEntityMock);
        assertEquals(tested.getStatusReason(), "No Content");
        mocksControl.verify();
    }

    @Test
    public void getBody_success() throws IOException {
        HttpEntity httpEntityMock = mocksControl.createMock(HttpEntity.class);
        EasyMock.expect(responseEntityMock.getEntity()).andReturn(httpEntityMock);
        EasyMock.expect(httpEntityMock.getContent()).andReturn(new ByteArrayInputStream("hello".getBytes()));

        mocksControl.replay();
        HttpResponse tested = new HttpResponse(responseEntityMock);
        assertEquals(tested.getBody(), "hello");
        mocksControl.verify();
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void getBody_ioException() throws IOException {
        HttpEntity httpEntityMock = mocksControl.createMock(HttpEntity.class);
        EasyMock.expect(responseEntityMock.getEntity()).andReturn(httpEntityMock);
        EasyMock.expect(httpEntityMock.getContent()).andThrow(new IOException("boom"));

        mocksControl.replay();
        HttpResponse tested = new HttpResponse(responseEntityMock);
        try {
            tested.getBody();
        } finally {
            mocksControl.verify();
        }
    }
}
