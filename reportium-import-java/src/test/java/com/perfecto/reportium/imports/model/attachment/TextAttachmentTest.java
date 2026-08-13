package com.perfecto.reportium.imports.model.attachment;

import org.apache.http.entity.ContentType;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TextAttachmentTest {

    @Test
    public void build_stream_explicitEverything() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        TextAttachment attachment = new TextAttachment.Builder()
                .withFileName("CheckMeOut.txt")
                .withContentType(TextAttachment.TEXT_PLAIN)
                .withExtension("txt")
                .withInputStream(inputStream)
                .build();

        assertEquals(attachment.getFileName(), "CheckMeOut.txt");
        assertEquals(attachment.getContentType(), TextAttachment.TEXT_PLAIN);
        assertEquals(attachment.getExtension(), "txt");
        assertFalse(attachment.isZipped());
        assertTrue(attachment.shouldZip());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void build_stream_noFileName_throws() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        new TextAttachment.Builder()
                .withContentType(TextAttachment.TEXT_PLAIN)
                .withExtension("txt")
                .withInputStream(inputStream)
                .build();
    }

    @Test
    public void build_stream_guessExtensionFromContentType_textPlain() {
        assertGuessedExtension(TextAttachment.TEXT_PLAIN, "txt");
    }

    @Test
    public void build_stream_guessExtensionFromContentType_richText() {
        assertGuessedExtension(TextAttachment.TEXT_RICHTEXT, "rtf");
    }

    @Test
    public void build_stream_guessExtensionFromContentType_html() {
        assertGuessedExtension(TextAttachment.TEXT_HTML, "html");
    }

    @Test
    public void build_stream_guessExtensionFromContentType_csv() {
        assertGuessedExtension(TextAttachment.TEXT_CSV, "csv");
    }

    @Test
    public void build_stream_guessExtensionFromContentType_xml() {
        assertGuessedExtension(TextAttachment.APPLICATION_XML, "xml");
    }

    @Test
    public void build_stream_guessExtensionFromContentType_json() {
        assertGuessedExtension(TextAttachment.APPLICATION_JSON, "json");
    }

    private void assertGuessedExtension(ContentType contentType, String expectedExtension) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        TextAttachment attachment = new TextAttachment.Builder()
                .withFileName("CheckMeOut")
                .withContentType(contentType)
                .withInputStream(inputStream)
                .build();

        assertEquals(attachment.getExtension(), expectedExtension);
        assertEquals(attachment.getContentType(), contentType);
    }

    @Test
    public void build_stream_guessContentTypeFromExtension_txt() {
        assertGuessedContentType("txt", TextAttachment.TEXT_PLAIN);
    }

    @Test
    public void build_stream_guessContentTypeFromExtension_log() {
        assertGuessedContentType("log", TextAttachment.TEXT_PLAIN);
    }

    @Test
    public void build_stream_guessContentTypeFromExtension_rt() {
        assertGuessedContentType("rt", TextAttachment.TEXT_RICHTEXT);
    }

    @Test
    public void build_stream_guessContentTypeFromExtension_rtf() {
        assertGuessedContentType("rtf", TextAttachment.TEXT_RICHTEXT);
    }

    @Test
    public void build_stream_guessContentTypeFromExtension_html() {
        assertGuessedContentType("html", TextAttachment.TEXT_HTML);
    }

    @Test
    public void build_stream_guessContentTypeFromExtension_csv() {
        assertGuessedContentType("csv", TextAttachment.TEXT_CSV);
    }

    @Test
    public void build_stream_guessContentTypeFromExtension_xml() {
        assertGuessedContentType("xml", TextAttachment.APPLICATION_XML);
    }

    @Test
    public void build_stream_guessContentTypeFromExtension_json() {
        assertGuessedContentType("json", TextAttachment.APPLICATION_JSON);
    }

    @Test
    public void build_stream_guessContentTypeFromExtension_uppercase() {
        assertGuessedContentType("TXT", TextAttachment.TEXT_PLAIN);
    }

    private void assertGuessedContentType(String extension, ContentType expectedContentType) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        TextAttachment attachment = new TextAttachment.Builder()
                .withFileName("CheckMeOut")
                .withExtension(extension)
                .withInputStream(inputStream)
                .build();

        assertEquals(attachment.getContentType(), expectedContentType);
        assertEquals(attachment.getExtension(), extension);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void build_stream_contentTypeNotAllowed_throws() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        new TextAttachment.Builder()
                .withFileName("CheckMeOut.bin")
                .withContentType(ContentType.APPLICATION_OCTET_STREAM)
                .withExtension("bin")
                .withInputStream(inputStream)
                .build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void build_stream_noContentTypeNoExtension_throws() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        new TextAttachment.Builder()
                .withFileName("CheckMeOut")
                .withInputStream(inputStream)
                .build();
    }

    @Test
    public void build_file_guessExtensionZippedAndFileNameFromPath() throws IOException {
        Path tempFile = Files.createTempFile("test", ".zip");
        try {
            TextAttachment attachment = new TextAttachment.Builder()
                    .withContentType(TextAttachment.TEXT_PLAIN)
                    .withAbsolutePath(tempFile.toString())
                    .build();

            assertEquals(attachment.getAbsolutePath(), tempFile.toString());
            assertEquals(attachment.getExtension(), "zip");
            assertEquals(attachment.getFileName(), tempFile.getFileName().toString());
            assertTrue(attachment.isZipped());
            assertFalse(attachment.shouldZip());
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test
    public void build_file_guessEverythingFromPath() throws IOException {
        Path tempFile = Files.createTempFile("test", ".csv");
        try {
            TextAttachment attachment = new TextAttachment.Builder()
                    .withAbsolutePath(tempFile.toString())
                    .build();

            assertEquals(attachment.getExtension(), "csv");
            assertEquals(attachment.getContentType(), TextAttachment.TEXT_CSV);
            assertEquals(attachment.getFileName(), tempFile.getFileName().toString());
            assertFalse(attachment.isZipped());
            assertTrue(attachment.shouldZip());
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void build_file_fileNotFound_throws() {
        new TextAttachment.Builder()
                .withContentType(TextAttachment.TEXT_PLAIN)
                .withExtension("txt")
                .withAbsolutePath("/no/such/file-" + System.nanoTime() + ".txt")
                .build();
    }
}
