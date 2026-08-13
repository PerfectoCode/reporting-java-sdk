package com.perfecto.reportium.imports.client;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfecto.reportium.imports.client.connection.Connection;
import com.perfecto.reportium.imports.client.connection.HttpResponse;
import com.perfecto.reportium.imports.client.connection.ReportingHttpClient;
import com.perfecto.reportium.imports.model.ImportExecutionContext;
import com.perfecto.reportium.imports.model.attachment.ArtifactData;
import com.perfecto.reportium.imports.model.attachment.ScreenshotAttachment;
import com.perfecto.reportium.imports.model.attachment.TextAttachment;
import com.perfecto.reportium.imports.model.command.Command;
import com.perfecto.reportium.imports.model.command.CommandParameter;
import com.perfecto.reportium.imports.model.command.CommandStatus;
import com.perfecto.reportium.imports.model.command.CommandType;
import com.perfecto.reportium.imports.model.event.CommandEvent;
import com.perfecto.reportium.imports.model.event.TestEndEvent;
import com.perfecto.reportium.imports.model.event.TestExecutionStatus;
import com.perfecto.reportium.imports.model.event.TestStartEvent;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.easymock.EasyMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.testng.Assert.*;

public class ReportiumImportClientTest {
    private static String REPORTIUM_URL = "https://tenant.reporting.perfectomobile.com";
    private static String SECURITY_TOKEN = "123456789";

    private IMocksControl mocksControl;
    private ReportiumImportClient tested;
    private ReportingHttpClient reportingHttpClientMock;
    private ExecutorService executorServiceMock;
    private Gson gson = new GsonBuilder().create();
    private String externalId;

    @BeforeMethod
    public void beforeTest() throws URISyntaxException {
        ImportExecutionContext executionContext = new ImportExecutionContext.Builder().build();
        Connection connection = new Connection(new URI(REPORTIUM_URL), SECURITY_TOKEN);
        tested = new ReportiumImportClientFactory().createReportiumImportClient(connection, executionContext);
        externalId = tested.getExternalId();

        mocksControl = EasyMock.createControl();
        reportingHttpClientMock = mocksControl.createMock(ReportingHttpClient.class);
        executorServiceMock = mocksControl.createMock(ExecutorService.class);

        ReflectionTestUtils.setField(tested, "httpClient", reportingHttpClientMock);
        ReflectionTestUtils.setField(tested, "executorService", executorServiceMock);
    }

    @Test
    public void checkDefaults() {
        mocksControl.replay();
        assertFalse(tested.getFailOnUploadFailure());
        assertTrue(tested.isAsyncUpload());
        mocksControl.verify();
    }

    @Test
    public void getExternalId() {
        mocksControl.replay();
        String externalId1 = tested.getExternalId();
        assertNotNull(externalId1);
        String externalId2 = tested.getExternalId();
        assertEquals(externalId1, externalId2);
        mocksControl.verify();
    }

    @Test
    public void getReportUrl() {
        mocksControl.replay();
        String externalId = tested.getExternalId();
        String reportUrl = tested.getReportUrl();
        assertThat(reportUrl, containsString(REPORTIUM_URL));
        assertThat(reportUrl, containsString(externalId));
        assertThat(reportUrl, containsString("library"));
        mocksControl.verify();
    }

    @Test
    public void testStart() throws IOException {
        TestContext testContext = new TestContext.Builder().build();
        String testName = "my-test";

        mockExecutorServiceSubmitPassThrough(executorServiceMock);
        Capture<HttpEntity> httpEntityCapture = mockPost();

        mocksControl.replay();
        tested.testStart(testName, testContext);
        mocksControl.verify();

        String json = IOUtils.toString(httpEntityCapture.getValue().getContent(), StandardCharsets.UTF_8);
        TestStartEvent testStartEvent = gson.fromJson(json, TestStartEvent.class);

        assertEquals(testStartEvent.getExternalId(), externalId);
        assertEquals(testStartEvent.getName(), testName);
        assertEquals(testStartEvent.getOrder(), 0);
    }

    @Test
    public void command() throws IOException {
        String commandName = "command name";
        CommandType commandType = CommandType.API;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 1;
        List<CommandParameter> commandParameters = Arrays.asList(new CommandParameter("name1", "value1"), new CommandParameter("name2", "value2"));
        String message = "some message";
        CommandStatus commandStatus = CommandStatus.FAILURE;
        Command command = new Command.Builder()
                .withName(commandName)
                .withCommandType(commandType)
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withParameters(commandParameters)
                .withMessage(message)
                .withStatus(commandStatus)
                .build();

        mockExecutorServiceSubmitPassThrough(executorServiceMock);
        Capture<HttpEntity> httpEntityCapture = mockPost();

        mocksControl.replay();
        tested.command(command);
        mocksControl.verify();

        String json = IOUtils.toString(httpEntityCapture.getValue().getContent(), StandardCharsets.UTF_8);
        CommandEvent commandEvent = gson.fromJson(json, CommandEvent.class);

        assertEquals(commandEvent.getExternalId(), externalId);
        assertEquals(commandEvent.getCommand().getName(), commandName);
        assertEquals(commandEvent.getCommand().getCommandType(), commandType);
        assertEquals(commandEvent.getCommand().getStartTime(), startTime);
        assertEquals(commandEvent.getCommand().getEndTime(), endTime);
        assertEquals(commandEvent.getCommand().getParameters(), commandParameters);
        assertEquals(commandEvent.getCommand().getMessage(), message);
        assertEquals(commandEvent.getCommand().getStatus(), commandStatus);
    }

    @Test
    public void command_withScreenshot() throws IOException {
        Path tempFile = Files.createTempFile("temp_", ".png");
        String uploadUrl = "https://some.s3.address.com/bucket-name/artifact/tenant/externalId/filename.png?someParam1=someParamValue1";
        String commandName = "name";
        Command command = new Command.Builder()
                .withName(commandName)
                .withScreenshotAttachments(new ScreenshotAttachment.Builder().withAbsolutePath(tempFile.toString()).build())
                .build();

        mockExecutorServiceSubmitPassThrough(executorServiceMock);
        Capture<HttpEntity> postCapture = mockPost();
        mockGet(uploadUrl);

        Capture<URI> putUriCapture = EasyMock.newCapture();
        Capture<Header> headerCapture = EasyMock.newCapture();
        expect(reportingHttpClientMock.put(capture(putUriCapture), anyObject(HttpEntity.class), anyObject(Map.class), capture(headerCapture)))
                .andReturn(getHttpResponse(200, null));

        mocksControl.replay();
        tested.command(command);
        mocksControl.verify();

        String json = IOUtils.toString(postCapture.getValue().getContent(), StandardCharsets.UTF_8);
        CommandEvent commandEvent = gson.fromJson(json, CommandEvent.class);
        assertEquals(commandEvent.getCommand().getName(), commandName);
        assertEquals(commandEvent.getCommand().getScreenshots().size(), 1);
        String screenshot = commandEvent.getCommand().getScreenshots().iterator().next();
        assertTrue(screenshot.endsWith(".png"), "Expected the screenshot '" + screenshot + "' to end with .png");

        URI putUri = putUriCapture.getValue();
        assertEquals(putUri.toString(), uploadUrl);

        Header header = headerCapture.getValue();
        assertEquals(header.getName(), "Content-Disposition");
        assertEquals(header.getValue(), "attachment;filename=" + screenshot);

        Files.delete(tempFile);
    }

    @Test
    public void testEnd() throws IOException {
        mockExecutorServiceSubmitPassThrough(executorServiceMock);
        Capture<HttpEntity> postCapture = mockPost();
        String failureReason = "reason";
        String exceptionMessage = "my error";

        mocksControl.replay();
        tested.testStop(TestResultFactory.createFailure(failureReason, new RuntimeException(exceptionMessage)));
        mocksControl.verify();

        String json = IOUtils.toString(postCapture.getValue().getContent(), StandardCharsets.UTF_8);
        TestEndEvent testEndEvent = gson.fromJson(json, TestEndEvent.class);
        assertEquals(testEndEvent.getExternalId(), externalId);
        assertEquals(testEndEvent.getStatus(), TestExecutionStatus.FAILED);
        assertEquals(testEndEvent.getArtifacts().size(), 0);
        assertThat(testEndEvent.getMessage(), containsString(failureReason));
        assertThat(testEndEvent.getMessage(), containsString(exceptionMessage));
    }

    @Test
    public void testEnd_WithFailureReason() throws IOException {
        mockExecutorServiceSubmitPassThrough(executorServiceMock);
        Capture<HttpEntity> postCapture = mockPost();
        String message = "message";
        String failureReason = "failureReason";
        String exceptionMessage = "my error";

        mocksControl.replay();
        tested.testStop(TestResultFactory.createFailure(message, new RuntimeException(exceptionMessage), failureReason));
        mocksControl.verify();

        String json = IOUtils.toString(postCapture.getValue().getContent(), StandardCharsets.UTF_8);
        TestEndEvent testEndEvent = gson.fromJson(json, TestEndEvent.class);
        assertEquals(testEndEvent.getExternalId(), externalId);
        assertEquals(testEndEvent.getStatus(), TestExecutionStatus.FAILED);
        assertEquals(testEndEvent.getArtifacts().size(), 0);
        assertThat(testEndEvent.getMessage(), containsString(message));
        assertThat(testEndEvent.getMessage(), containsString(exceptionMessage));
        assertEquals(failureReason, testEndEvent.getFailureReasonName());
    }

    @Test
    public void testEnd_withTextAttachment() throws IOException {
        Path tempFile = Files.createTempFile("temp_", ".txt");
        String uploadUrl = "https://some.s3.address.com/bucket-name/artifact/tenant/externalId/filename.png?someParam1=someParamValue1";

        TextAttachment textAttachment = new TextAttachment.Builder().withAbsolutePath(tempFile.toString()).build();

        mockExecutorServiceSubmitPassThrough(executorServiceMock);
        Capture<HttpEntity> postCapture = mockPost();
        mockGet(uploadUrl);

        Capture<URI> putUriCapture = EasyMock.newCapture();
        Capture<Header> headerCapture = EasyMock.newCapture();
        expect(reportingHttpClientMock.put(capture(putUriCapture), anyObject(HttpEntity.class), anyObject(Map.class), capture(headerCapture)))
                .andReturn(getHttpResponse(200, null));

        mocksControl.replay();
        tested.testStop(TestResultFactory.createSuccess(), textAttachment);
        mocksControl.verify();

        String json = IOUtils.toString(postCapture.getValue().getContent(), StandardCharsets.UTF_8);
        TestEndEvent testEndEvent = gson.fromJson(json, TestEndEvent.class);
        assertEquals(testEndEvent.getStatus(), TestExecutionStatus.PASSED);
        assertEquals(testEndEvent.getArtifacts().size(), 1);
        ArtifactData artifactData = testEndEvent.getArtifacts().iterator().next();
        assertEquals(artifactData.getFileName(), tempFile.getFileName().toString());

        URI putUri = putUriCapture.getValue();
        assertEquals(putUri.toString(), uploadUrl);

        Header header = headerCapture.getValue();
        assertEquals(header.getName(), "Content-Disposition");
        assertEquals(header.getValue(), "attachment;filename=" + tempFile.getFileName() + ".zip");

        Files.delete(tempFile);
    }

    @Test
    public void testEnd_withTextAttachment_alreadyZipped() throws IOException {
        Path tempFile = Files.createTempFile("temp_", ".zip");
        String uploadUrl = "https://some.s3.address.com/bucket-name/artifact/tenant/externalId/filename.png?someParam1=someParamValue1";

        TextAttachment textAttachment = new TextAttachment.Builder().withContentType(TextAttachment.TEXT_CSV).withAbsolutePath(tempFile.toString()).build();

        mockExecutorServiceSubmitPassThrough(executorServiceMock);
        Capture<HttpEntity> postCapture = mockPost();
        mockGet(uploadUrl);

        Capture<URI> putUriCapture = EasyMock.newCapture();
        Capture<Header> headerCapture = EasyMock.newCapture();
        expect(reportingHttpClientMock.put(capture(putUriCapture), anyObject(HttpEntity.class), anyObject(Map.class), capture(headerCapture)))
                .andReturn(getHttpResponse(200, null));

        mocksControl.replay();
        tested.testStop(TestResultFactory.createSuccess(), textAttachment);
        mocksControl.verify();

        String json = IOUtils.toString(postCapture.getValue().getContent(), StandardCharsets.UTF_8);
        TestEndEvent testEndEvent = gson.fromJson(json, TestEndEvent.class);
        assertEquals(testEndEvent.getStatus(), TestExecutionStatus.PASSED);
        assertEquals(testEndEvent.getArtifacts().size(), 1);
        ArtifactData artifactData = testEndEvent.getArtifacts().iterator().next();
        assertEquals(artifactData.getFileName(), tempFile.getFileName().toString());

        URI putUri = putUriCapture.getValue();
        assertEquals(putUri.toString(), uploadUrl);

        Header header = headerCapture.getValue();
        assertEquals(header.getName(), "Content-Disposition");
        assertEquals(header.getValue(), "attachment;filename=" + tempFile.getFileName());

        Files.delete(tempFile);
    }


    private Capture<HttpEntity> mockPost() {
        Capture<HttpEntity> httpEntityCapture = EasyMock.newCapture();
        expect(reportingHttpClientMock.post(anyObject(URI.class), capture(httpEntityCapture), anyObject(Map.class), anyObject(Header[].class)))
                .andReturn(getHttpResponse(200, null));

        return httpEntityCapture;
    }

    private void mockGet(String response) {
        expect(reportingHttpClientMock.get(anyObject(URI.class), anyObject(Map.class), anyObject(Header[].class)))
                .andReturn(getHttpResponse(200, new StringEntity(response, Charset.defaultCharset())));
    }

    private HttpResponse getHttpResponse(int code, HttpEntity httpEntity) {
        BasicHttpResponse basicHttpResponse = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("https", 1, 1), code, null));
        basicHttpResponse.setEntity(httpEntity);
        return new HttpResponse(basicHttpResponse);
    }

    private void mockExecutorServiceSubmitPassThrough(ExecutorService executorServiceMock) {
        final Capture<Callable<Object>> capture1 = EasyMock.newCapture();
        expect(executorServiceMock.submit(EasyMock.capture(capture1)))
                .andAnswer(new IAnswer<Future<Object>>() {
                    @Override
                    public Future<Object> answer() throws Throwable {
                        Object result = capture1.getValue().call();
                        return new CompletedFuture<>(result);
                    }
                }).anyTimes();

        final Capture<Runnable> capture2 = EasyMock.newCapture();
        executorServiceMock.submit(EasyMock.capture(capture2));
        expectLastCall()
                .andAnswer(new IAnswer<Object>() {
                    @Override
                    public Object answer() throws Throwable {
                        try {
                            capture2.getValue().run();
                        } catch (Exception e) {
                            // caching all errors like the real ReportingExecutors does
                            e.printStackTrace();
                        }
                        return new CompletedFuture<>(null);
                    }
                }).anyTimes();
    }

    public static class CompletedFuture<T> implements Future<T> {


        private T result;

        public CompletedFuture(T result) {
            this.result = result;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public T get() {
            return this.result;
        }

        @Override
        public T get(long timeout, TimeUnit unit) {
            return this.result;
        }
    }
}
