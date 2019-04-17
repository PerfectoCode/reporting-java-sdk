package com.perfecto.reportium.imports.model.attachment;

import org.apache.http.entity.ContentType;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ScreenshotAttachmentTest {

    @Test(expectedExceptions = RuntimeException.class)
    public void build_file_fileNotFound() {
        File file = new File(UUID.randomUUID().toString());
        String extension = "jpg";
        ContentType contentType = ScreenshotAttachment.IMAGE_JPEG;
        new ScreenshotAttachment.Builder()
                .withContentType(contentType)
                .withExtension(extension)
                .withAbsolutePath(file.getAbsolutePath())
                .build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void build_file_noPath() {
        String extension = "jpg";
        ContentType contentType = ScreenshotAttachment.IMAGE_JPEG;
        new ScreenshotAttachment.Builder()
                .withContentType(contentType)
                .withExtension(extension)
                .build();
    }


    @Test
    public void build_file() throws IOException {
        Path tempFile = Files.createTempFile("test", ".jpg");
        try {
            String extension = "jpg";
            ContentType contentType = ScreenshotAttachment.IMAGE_JPEG;
            ScreenshotAttachment attachment = new ScreenshotAttachment.Builder()
                    .withContentType(contentType)
                    .withExtension(extension)
                    .withAbsolutePath(tempFile.toString())
                    .build();
            assertEquals(tempFile.toString(), attachment.getAbsolutePath());
            assertEquals(extension, attachment.getExtension());
            assertEquals(contentType, attachment.getContentType());
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test
    public void build_file_guessExtensionAndContentType_jpg() throws IOException {
        Path tempFile = Files.createTempFile("test", ".jpg");
        try {
            String extension = "jpg";
            ContentType contentType = ScreenshotAttachment.IMAGE_JPEG;
            ScreenshotAttachment attachment = new ScreenshotAttachment.Builder()
                    .withAbsolutePath(tempFile.toString())
                    .build();
            assertEquals(tempFile.toString(), attachment.getAbsolutePath());
            assertEquals(extension, attachment.getExtension());
            assertEquals(contentType, attachment.getContentType());
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test
    public void build_file_guessExtensionAndContentType_jpeg() throws IOException {
        Path tempFile = Files.createTempFile("test", ".jpeg");
        try {
            String extension = "jpeg";
            ContentType contentType = ScreenshotAttachment.IMAGE_JPEG;
            ScreenshotAttachment attachment = new ScreenshotAttachment.Builder()
                    .withAbsolutePath(tempFile.toString())
                    .build();
            assertEquals(tempFile.toString(), attachment.getAbsolutePath());
            assertEquals(extension, attachment.getExtension());
            assertEquals(contentType, attachment.getContentType());
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test
    public void build_file_guessExtensionAndContentType_png() throws IOException {
        Path tempFile = Files.createTempFile("test", ".png");
        try {
            String extension = "png";
            ContentType contentType = ScreenshotAttachment.IMAGE_PNG;
            ScreenshotAttachment attachment = new ScreenshotAttachment.Builder()
                    .withAbsolutePath(tempFile.toString())
                    .build();
            assertEquals(tempFile.toString(), attachment.getAbsolutePath());
            assertEquals(extension, attachment.getExtension());
            assertEquals(contentType, attachment.getContentType());
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void build_stream_notAllowedContentType() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        String extension = "txt";
        ContentType contentType = ContentType.TEXT_PLAIN;
        ScreenshotAttachment attachment = new ScreenshotAttachment.Builder()
                .withContentType(contentType)
                .withExtension(extension)
                .withInputStream(inputStream)
                .build();
        assertNotNull(attachment.getInputStream());
        assertEquals(extension, attachment.getExtension());
        assertEquals(contentType, attachment.getContentType());
    }

    @Test
    public void build_stream() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        String extension = "jpg";
        ContentType contentType = ScreenshotAttachment.IMAGE_JPEG;
        ScreenshotAttachment attachment = new ScreenshotAttachment.Builder()
                .withContentType(contentType)
                .withExtension(extension)
                .withInputStream(inputStream)
                .build();
        assertNotNull(attachment.getInputStream());
        assertEquals(extension, attachment.getExtension());
        assertEquals(contentType, attachment.getContentType());
    }

    @Test
    public void build_stream_guessContentType() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        String extension = "jpg";
        ContentType contentType = ScreenshotAttachment.IMAGE_JPEG;
        ScreenshotAttachment attachment = new ScreenshotAttachment.Builder()
                .withExtension(extension)
                .withInputStream(inputStream)
                .build();
        assertNotNull(attachment.getInputStream());
        assertEquals(extension, attachment.getExtension());
        assertEquals(contentType, attachment.getContentType());
    }

    @Test
    public void build_stream_guessExtension() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        String extension = "jpg";
        ContentType contentType = ScreenshotAttachment.IMAGE_JPEG;
        ScreenshotAttachment attachment = new ScreenshotAttachment.Builder()
                .withContentType(contentType)
                .withInputStream(inputStream)
                .build();
        assertNotNull(attachment.getInputStream());
        assertEquals(extension, attachment.getExtension());
        assertEquals(contentType, attachment.getContentType());
    }

}
