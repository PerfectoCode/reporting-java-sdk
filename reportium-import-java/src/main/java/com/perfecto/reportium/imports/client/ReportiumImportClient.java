package com.perfecto.reportium.imports.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perfecto.reportium.client.DigitalZoomClient;
import com.perfecto.reportium.exception.ReportiumException;
import com.perfecto.reportium.imports.client.connection.Connection;
import com.perfecto.reportium.imports.client.connection.HttpResponse;
import com.perfecto.reportium.imports.client.connection.ReportingHttpClient;
import com.perfecto.reportium.imports.model.ImportExecutionContext;
import com.perfecto.reportium.imports.model.attachment.ArtifactData;
import com.perfecto.reportium.imports.model.attachment.Attachment;
import com.perfecto.reportium.imports.model.attachment.ScreenshotAttachment;
import com.perfecto.reportium.imports.model.attachment.TextAttachment;
import com.perfecto.reportium.imports.model.command.Command;
import com.perfecto.reportium.imports.model.command.InternalCommand;
import com.perfecto.reportium.imports.model.event.*;
import com.perfecto.reportium.model.CustomField;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Reportium client with configurable backend servers for Reporting and SSO
 */
public class ReportiumImportClient implements DigitalZoomClient, Closeable {
    private static final Logger LOGGER = Logger.getLogger(ReportiumImportClient.class.getName());

    private static final String EVENTS_PATH = InternalConstants.Url.V1.eventsResource;
    private static final String ARTIFACTS_PATH = InternalConstants.Url.V1.artifactsUrlResource;
    private static final String AUTHORIZATION_HEADER_NAME = "PERFECTO_AUTHORIZATION";
    private static final String ARTIFACT_ID_PARAM = "artifactId";
    private static final String EXTERNAL_ID_PARAM = "externalId";
    private static final String TYPE_PARAM = "type";
    private static final Gson gson = new GsonBuilder().create();
    private static final String SDK_PROPERTIES_FILE_NAME = "sdk.properties";
    private static final String SDK_VERSION_PROPERTY_NAME = "version";
    private static final String SDK_VERSION_PROPERTY_PLACEHOLDER = "${pom.version}";
    private final ImportExecutionContext executionContext;
    private final ReportingHttpClient httpClient;
    private final Connection connection;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private int order;
    private int eventsCount;
    private String lastTestId;
    private TestContext testContext;
    private String externalId;
    private String sdkVersion = null;

    // flags
    private boolean failOnUploadFailure;
    private boolean asyncUpload;

    /**
     * Creates a new instance
     *
     * @param connection       Connection details to Reportium server
     * @param executionContext Test execution context to assist test fitering and grouping in Reportium UI
     */
    public ReportiumImportClient(Connection connection,
                                 ImportExecutionContext executionContext) {
        Validate.notNull(connection, "Connection can't be null");
        Validate.notNull(executionContext, "executionContext can't be null");
        this.connection = connection;
        this.executionContext = executionContext;
        this.order = 0;
        this.eventsCount = 0;
        this.externalId = UUID.randomUUID().toString();
        this.failOnUploadFailure = false;
        this.asyncUpload = true;
        httpClient = new ReportingHttpClient(connection);
    }

    public String getExternalId() {
        return externalId;
    }

    @Override
    public void testStart(String name, TestContext testContext) {
        this.testContext = testContext;
        TestStartEvent testStartEvent = new TestStartEvent(this.getExternalId(), UUID.randomUUID().toString(), order++, getSdkVersion());
        testStartEvent.setName(name);
        testStartEvent.setStartTime(System.currentTimeMillis());
        testStartEvent.setPlatforms(executionContext.getPlatforms());
        testStartEvent.setJob(executionContext.getJob());
        testStartEvent.setProject(executionContext.getProject());
        testStartEvent.setAutomationFramework(executionContext.getAutomationFramework());
        Set<String> tags = new HashSet<>(executionContext.getContextTags());
        tags.addAll(testContext.getTestExecutionTags());
        testStartEvent.setTags(tags);
        Set<CustomField> customFields = mergeCustomFields(executionContext.getCustomFields(), testContext.getCustomFields());
        testStartEvent.setCustomFields(customFields);

        Map<String, String> queryParams = getQueryParams(testStartEvent, String.valueOf(order), EventType.TEST_START.name());
        executePost(connection.getReportingServer().resolve(EVENTS_PATH), queryParams, testStartEvent);
        this.lastTestId = testStartEvent.getTestId();
        eventsCount = 1;
    }

    private Set<CustomField> mergeCustomFields(Set<CustomField> executionCustomFields, Set<CustomField> populatedCustomFields) {
        Set<CustomField> result = new HashSet<>(populatedCustomFields);

        if (!executionCustomFields.isEmpty()) {
            Set<String> customFieldsNames = new HashSet<>();

            for (CustomField customField : result) {
                customFieldsNames.add(customField.getName());
            }

            for (CustomField customField : executionCustomFields) {
                if (!customFieldsNames.contains(customField.getName())) {
                    result.add(customField);
                }
            }
        }
        return result;
    }

    @Override
    public void stepStart(String description) {
        StepStartEvent stepStartEvent = new StepStartEvent(this.getExternalId(), lastTestId, order++);
        stepStartEvent.setDescription(description);
        stepStartEvent.setStartTime(System.currentTimeMillis());

        Map<String, String> queryParams = getQueryParams(stepStartEvent, String.valueOf(order), EventType.STEP_START.name());
        executePost(connection.getReportingServer().resolve(EVENTS_PATH), queryParams, stepStartEvent);
        this.eventsCount++;
    }

    public void command(Command command) {
        CommandEvent commandEvent = new CommandEvent(this.getExternalId(), lastTestId, order++);

        // upload all attachments and get the IDs
        List<String> artifactIds = new ArrayList<>();
        for (ScreenshotAttachment screenshotAttachment : command.getScreenshots()) {
            try {
                artifactIds.add(uploadAttachment(screenshotAttachment).getPath());
            } catch (Exception e) {
                String msg = "Failed to upload attachment : " + screenshotAttachment + ". Reason: " + e.getMessage();
                LOGGER.log(Level.SEVERE, msg, e);
                if (failOnUploadFailure) {
                    throw new RuntimeException(msg, e);
                }
            }
        }

        // upload command
        InternalCommand internalCommand = new InternalCommand(command);
        internalCommand.setScreenshots(artifactIds);
        commandEvent.setCommand(internalCommand);
        Map<String, String> queryParams = getQueryParams(commandEvent, String.valueOf(order), EventType.COMMAND_EVENT.name());
        executePost(connection.getReportingServer().resolve(EVENTS_PATH), queryParams, commandEvent);
        this.eventsCount++;
    }

    private ArtifactData uploadAttachment(final Attachment attachment) throws IOException {
        final ArtifactData artifactData = new ArtifactData();
        final boolean zipped = attachment.isZipped() || attachment.shouldZip();
        final String extension = zipped ? "zip" : attachment.getExtension();
        String artifactId = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        final ContentType contentType = attachment.getContentType();

        artifactData.setContentType(contentType.getMimeType());
        artifactData.setPath(artifactId);
        artifactData.setZipped(zipped);
        artifactData.setType(attachment.getType());
        artifactData.setFileName(attachment.getFileName());

        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put(ARTIFACT_ID_PARAM, artifactId);
        queryParams.put(EXTERNAL_ID_PARAM, this.getExternalId());
        queryParams.put(TYPE_PARAM, attachment.getType());

        Path tempFile;
        if (attachment.shouldZip()) {
            tempFile = zipToTempFile(attachment);
        } else {
            tempFile = copyToTempFile(attachment);
        }
        attachment.setTempFile(tempFile);

        if (asyncUpload) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    uploadArtifact(attachment, artifactData, queryParams, zipped);
                }
            });
        } else {
            uploadArtifact(attachment, artifactData, queryParams, zipped);
        }
        return artifactData;
    }

    private Path copyToTempFile(Attachment attachment) throws IOException {
        Path tempFile;
        tempFile = Files.createTempFile("perfecto_" + attachment.getType() + "_attachment_", "." + attachment.getExtension());
        if (attachment.getInputStream() != null) {
            FileUtils.copyInputStreamToFile(attachment.getInputStream(), tempFile.toFile());
        } else {
            FileUtils.copyFile(new File(attachment.getAbsolutePath()), tempFile.toFile());
        }
        return tempFile;
    }

    private void uploadArtifact(Attachment attachment, ArtifactData artifactData, Map<String, String> queryParams, boolean zipped) {
        ContentType contentType = zipped ? ContentType.create("application/zip") : attachment.getContentType();
        Path tempFile = attachment.getTempFile();
        String fileName = getFileName(attachment, artifactData, zipped);

        HttpEntity httpEntity = new FileEntity(tempFile.toFile(), contentType);
        try {
            URI uploadUri = new URI(get(connection.getReportingServer().resolve(ARTIFACTS_PATH), queryParams));
            put(uploadUri, Collections.<String, String>emptyMap(), httpEntity, fileName);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed parsing upload URL. Reason: " + e.getMessage());
        } finally {
            try {
                Files.delete(tempFile);
            } catch (Exception e) {
                LOGGER.warning("Failed to delete temp file: " + tempFile + ". Reason: " + e.getMessage());
            }
        }
    }

    private String getFileName(Attachment attachment, ArtifactData artifactData, boolean zipped) {
        String fileName = attachment.getFileName();
        if (StringUtils.isBlank(fileName)) {
            fileName = FilenameUtils.getName(artifactData.getPath());
        }
        if (zipped && !fileName.endsWith(".zip")) {
            fileName = fileName.concat(".zip");
        }
        return fileName;
    }

    @Override
    public void stepEnd() {
        stepEnd(null);
    }

    @Override
    public void stepEnd(String message) {
        StepEndEvent stepEndEvent = new StepEndEvent(this.getExternalId(), this.lastTestId, order++);
        stepEndEvent.setEndTime(System.currentTimeMillis());
        stepEndEvent.setMessage(message);

        Map<String, String> queryParams = getQueryParams(stepEndEvent, String.valueOf(order), EventType.STEP_END.name());
        executePost(connection.getReportingServer().resolve(EVENTS_PATH), queryParams, stepEndEvent);
        this.eventsCount++;
    }

    @Override
    public void testStop(TestResult testResult) {
        testStop(testResult, Collections.<TextAttachment>emptyList(), new TestContext());
    }

    @Override
    public void testStop(TestResult testResult, TestContext testContext) {
        testStop(testResult, Collections.<TextAttachment>emptyList(), testContext);
    }

    public void testStop(TestResult testResult, TextAttachment... textAttachments) {
        if (textAttachments != null) {
            testStop(testResult, Arrays.asList(textAttachments), new TestContext());
        } else {
            testStop(testResult);
        }
    }

    public void testStop(TestResult testResult, Collection<TextAttachment> textAttachments, TestContext testContext) {
        TestEndEvent testEndEvent = new TestEndEvent(this.getExternalId(), this.lastTestId, order++);
        testEndEvent.setEndTime(System.currentTimeMillis());
        testEndEvent.setTestStartEventId(this.lastTestId);
        testEndEvent.setEventsCount(eventsCount);

        testResult.visit(new TestEndEventVisitor(testEndEvent));

        if (testContext != null) {
            testEndEvent.setTags(testContext.getTestExecutionTags());
            testEndEvent.setCustomFields(testContext.getCustomFields());
        }
        this.eventsCount = 0;
        this.lastTestId = null;
        this.testContext = null;

        for (TextAttachment textAttachment : textAttachments) {
            try {
                ArtifactData artifactData = uploadAttachment(textAttachment);
                testEndEvent.getArtifacts().add(artifactData);
            } catch (Exception e) {
                String msg = "Failed to upload attachment : " + textAttachment + ". Reason: " + e.getMessage();
                LOGGER.log(Level.SEVERE, msg, e);
                if (failOnUploadFailure) {
                    throw new RuntimeException(msg, e);
                }
            }
        }

        Map<String, String> queryParams = getQueryParams(testEndEvent, String.valueOf(order), EventType.TEST_END.name());
        executePost(connection.getReportingServer().resolve(EVENTS_PATH), queryParams, testEndEvent);
    }

    public void addArtifacts(Attachment... attachments) {
        // Verify the attachments
        if (attachments != null) {
            addArtifacts(Arrays.asList(attachments));
        }
    }

    public void addArtifacts(Collection<Attachment> attachments) {
        // Verify the attachments
        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        // Create the event
        AddArtifactsEvent addArtifactsEvent = new AddArtifactsEvent(this.getExternalId(), this.lastTestId);

        // Upload the attachments
        for (Attachment attachment : attachments) {
            try {
                // Upload the attachment
                ArtifactData artifactData = uploadAttachment(attachment);

                // Add the artifact data to the event
                addArtifactsEvent.getArtifacts().add(artifactData);
            } catch (Exception e) {
                String msg = "Failed to upload attachment : " + attachment + ". Reason: " + e.getMessage();
                LOGGER.log(Level.SEVERE, msg, e);
                if (failOnUploadFailure) {
                    throw new RuntimeException(msg, e);
                }
            }
        }

        // Send the event
        Map<String, String> queryParams = getQueryParams(addArtifactsEvent, String.valueOf(order), EventType.ADD_ARTIFACTS.name());
        executePost(connection.getReportingServer().resolve(EVENTS_PATH), queryParams, addArtifactsEvent);
    }

    private Map<String, String> getQueryParams(TestEvent event, String order, String name) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("externalId", event.getExternalId());
        queryParams.put("testId", event.getTestId());
        queryParams.put("order", order);
        queryParams.put("type", name);
        String sdkVersion = getSdkVersion();
        if (StringUtils.isNotBlank(sdkVersion)) {
            queryParams.put("engineVersion", sdkVersion);
        }
        return queryParams;
    }

    private String getSdkVersion() {
        if (StringUtils.isBlank(sdkVersion)) {
            final Properties properties = new Properties();
            try (final InputStream stream = getInputStream(SDK_PROPERTIES_FILE_NAME)) {
                properties.load(stream);
                String version = properties.getProperty(SDK_VERSION_PROPERTY_NAME);
                if (!SDK_VERSION_PROPERTY_PLACEHOLDER.equals(version)) {
                    sdkVersion = version;
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error while loading SDK version", e);
            }
        }
        return sdkVersion;
    }

    private InputStream getInputStream(String path) throws IOException {
        InputStream is;
        is = this.getClass().getResourceAsStream(path);
        if (is == null) {
            is = this.getClass().getClassLoader().getResourceAsStream(path);
        }
        if (is == null) {
            is = ClassLoader.getSystemResourceAsStream(path);
        }
        if (is == null) {
            throw new FileNotFoundException("Could not find file in path: " + path);
        }
        return is;
    }

    @Override
    public void close() {
        LOGGER.info("Terminating events thread pool");
        try {
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.MINUTES);
            LOGGER.info("Terminated events thread pool successfully");
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Interrupted while awaiting upload executor termination", e);
        }
        if (!executorService.isTerminated()) {
            LOGGER.log(Level.SEVERE, "Some upload tasks haven't finished yet. Exiting anyway");
        }
    }

    @Override
    public String getReportUrl() {
        URIBuilder reportUrlBuilder = new URIBuilder(connection.getReportingServer());
        reportUrlBuilder.setPath("library");

        List<NameValuePair> parameters = new ArrayList<>();

        String externalId = this.getExternalId();
        if (externalId != null) {
            parameters.add(new BasicNameValuePair(
                    createParamName(InternalConstants.Url.QueryParameterNames.externalId, 0),
                    externalId));
        }

        reportUrlBuilder.addParameters(parameters);

        try {
            return reportUrlBuilder.build().toString();
        } catch (URISyntaxException e) {
            throw new ReportiumException("Failed to create report URL", e);
        }
    }

    public void quit() {
        close();
    }

    public boolean getFailOnUploadFailure() {
        return failOnUploadFailure;
    }

    public void setFailOnUploadFailure(boolean value) {
        this.failOnUploadFailure = value;
    }

    public boolean isAsyncUpload() {
        return asyncUpload;
    }

    public void setAsyncUpload(boolean asyncUpload) {
        this.asyncUpload = asyncUpload;
    }

    public ImportExecutionContext getExecutionContext() {
        return executionContext;
    }

    /**
     * Create a param name Reportium backend can use by appending the index for its value with square brackets.
     * <p>
     * E.g. if the param name is "tags" and index is 3 then this method will return "tags[3]"
     *
     * @param paramName Name of query parameter without square brackets
     * @return Query parameter name with "[{index}]" appended to it
     */
    private String createParamName(String paramName, int index) {
        return paramName + "[" + index + "]";
    }

    private Header createSsoHeader() {
        return new BasicHeader(AUTHORIZATION_HEADER_NAME, connection.getSecurityToken());
    }

    private Header createContentDispositionHeader(String fileName) {
        return new BasicHeader("Content-Disposition", "attachment;filename=" + fileName);
    }

    private void executePost(URI uri, Map<String, String> queryParams, TestEvent event) {
        StringEntity entity = new StringEntity(gson.toJson(event), ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8));
        executePost(uri, queryParams, entity);
    }

    private void executePost(final URI uri, final Map<String, String> queryParams, final HttpEntity entity) {
        if (asyncUpload) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        post(uri, queryParams, entity);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error while executing POST request", e);
                    }
                }
            });
        } else {
            post(uri, queryParams, entity);
        }
    }

    private String get(URI uri, Map<String, String> queryParams) {

        HttpResponse httpResponse = httpClient.get(uri, queryParams, getHeaders().toArray(new Header[0]));
        validateResponse(uri, httpResponse);
        return httpResponse.getBody();
    }

    private void post(URI uri, Map<String, String> queryParams, HttpEntity entity) {
        HttpResponse httpResponse = httpClient.post(uri, entity, queryParams, getHeaders().toArray(new Header[0]));
        validateResponse(uri, httpResponse);
    }

    private List<Header> getHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(createSsoHeader());
        for (Map.Entry<String, String> entry : connection.getHeaders().entrySet()) {
            headers.add(new BasicHeader(entry.getKey(), entry.getValue()));
        }
        return headers;
    }

    private void put(URI uri, Map<String, String> queryParams, HttpEntity entity, String fileName) {
        HttpResponse httpResponse = httpClient.put(uri, entity, queryParams, createContentDispositionHeader(fileName));
        validateResponse(uri, httpResponse);
    }

    private void validateResponse(URI uri, HttpResponse httpResponse) {
        int httpResponseStatus = httpResponse.getStatus();
        if (400 <= httpResponseStatus && httpResponseStatus < 500) {
            // We don't want tests of SDK consumers to fail on failures to send data to Reportium backend
            throw new BadRequestException("Call to  " + uri + " failed with status " + httpResponseStatus + " " + httpResponse.getBody());
        } else if (httpResponseStatus > 300) {
            // All other non 2x HTTP error codes
            throw new ReportiumException("Call to  " + uri + " failed with status " + httpResponseStatus + " " + httpResponse.getBody());
        }
    }

    private Path zipToTempFile(Attachment attachment) throws IOException {
        if (attachment.getInputStream() != null) {
            return zipToTempFile(attachment.getInputStream(), attachment);
        } else {
            try (FileInputStream fin = new FileInputStream(attachment.getAbsolutePath())) {
                return zipToTempFile(fin, attachment);
            }
        }
    }

    private Path zipToTempFile(InputStream in, Attachment attachment) throws IOException {
        Path tempFile = Files.createTempFile("perfecto_" + attachment.getType() + "_attachment_", ".zip");

        try (FileOutputStream fos = new FileOutputStream(tempFile.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            ZipEntry ze = new ZipEntry(attachment.getFileName());
            zos.putNextEntry(ze);
            IOUtils.copy(in, zos);
        }
        return tempFile;
    }
}
