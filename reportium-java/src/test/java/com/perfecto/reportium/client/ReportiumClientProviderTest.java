package com.perfecto.reportium.client;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.easymock.EasyMock.createMock;

/**
 * Test case for {@link ReportiumClientProvider}
 */
public class ReportiumClientProviderTest {

    @Test
    public void testConstructorIsAccessible() {
        // Exercises the implicit default constructor
        assertNotNull(new ReportiumClientProvider());
    }

    @AfterMethod
    public void teardown() throws Exception {
        // ReportiumClientProvider only exposes get()/set(), so the ThreadLocal it backs must be
        // reset via reflection to avoid leaking state into other tests on this thread.
        Field field = ReportiumClientProvider.class.getDeclaredField("reportiumClient");
        field.setAccessible(true);
        ((ThreadLocal<?>) field.get(null)).remove();
    }

    @Test
    public void testSetAndGet() {
        DigitalZoomClient client = createMock(DigitalZoomClient.class);
        ReportiumClientProvider.set(client);
        assertSame(ReportiumClientProvider.get(), client);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "digitalZoomClient cannot be null")
    public void testSetNull() {
        ReportiumClientProvider.set(null);
    }

    @Test
    public void testGetWithoutSet() {
        // A brand new thread has no value bound to the ThreadLocal
        final DigitalZoomClient[] result = new DigitalZoomClient[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                result[0] = ReportiumClientProvider.get();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertNull(result[0]);
    }
}
