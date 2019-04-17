package com.perfecto.reportium.imports.model.attachment;

import org.apache.http.entity.ContentType;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;

public class AttachmentTest {
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void build_stream_nullContentType() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        String extension = "txt";
        new Attachment.Builder()
                .withType(ArtifactType.TEXT.name())
                .withFileName("CheckMeOut.txt")
                .withExtension(extension)
                .withInputStream(inputStream)
                .build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void build_stream_notAllowedContentType() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        String extension = "txt";
        ContentType contentType = ContentType.TEXT_PLAIN;
        new ScreenshotAttachment.Builder()
                .withType(ArtifactType.TEXT.name())
                .withFileName("CheckMeOut.txt")
                .withContentType(contentType)
                .withExtension(extension)
                .withInputStream(inputStream)
                .build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void build_stream_emptyArtifactType() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        String extension = "txt";
        ContentType contentType = ContentType.TEXT_PLAIN;
        new Attachment.Builder()
                .withType("")
                .withFileName("CheckMeOut.txt")
                .withContentType(contentType)
                .withExtension(extension)
                .withInputStream(inputStream)
                .build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void build_stream_nullArtifactType() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        String extension = "txt";
        ContentType contentType = ContentType.TEXT_PLAIN;
        new Attachment.Builder()
                .withType(null)
                .withFileName("CheckMeOut.txt")
                .withContentType(contentType)
                .withExtension(extension)
                .withInputStream(inputStream)
                .build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void build_stream_emptyFileName() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        String extension = "txt";
        ContentType contentType = ContentType.TEXT_PLAIN;
        new Attachment.Builder()
                .withType("Hello")
                .withContentType(contentType)
                .withExtension(extension)
                .withInputStream(inputStream)
                .build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void build_stream_emptyExtension() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        ContentType contentType = ContentType.TEXT_PLAIN;
        new Attachment.Builder()
                .withType("Hello")
                .withFileName("CheckMeOut")
                .withContentType(contentType)
                .withInputStream(inputStream)
                .build();
    }
}
