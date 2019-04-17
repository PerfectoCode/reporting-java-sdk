package com.perfecto.reportium.imports.model.attachment;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;

import java.util.Arrays;
import java.util.List;

public class TextAttachment extends Attachment {

    public static final ContentType TEXT_PLAIN = ContentType.create("text/plain");
    public static final ContentType TEXT_RICHTEXT = ContentType.create("text/richtext");
    public static final ContentType TEXT_HTML = ContentType.create("text/html");
    public static final ContentType TEXT_CSV = ContentType.create("text/csv");
    public static final ContentType APPLICATION_XML = ContentType.create("application/xml");
    public static final ContentType APPLICATION_JSON = ContentType.create("application/json");

    private TextAttachment(Builder builder) {
        super(builder);
    }

    @Override
    public boolean shouldZip() {
        return !isZipped();
    }

    public static class Builder extends Attachment.Builder<TextAttachment.Builder> {
        public Builder() {
            super.withType(ArtifactType.TEXT.name());
        }

        @Override
        protected List<String> getAllowedContentTypes() {
            return Arrays.asList(TEXT_PLAIN.getMimeType(), TEXT_RICHTEXT.getMimeType(), TEXT_HTML.getMimeType(),
                    TEXT_CSV.getMimeType(), APPLICATION_XML.getMimeType(), APPLICATION_JSON.getMimeType());
        }

        public TextAttachment build() {
            guessExtensionFromPath();

            guessZippedFromPath();

            guessFileNameFromAbsolutePath();

            if (getFileName() == null) {
                throw new IllegalArgumentException("File name cannot be null");
            }

            // guess extension from content type
            if (StringUtils.isBlank(getExtension()) && getContentType() != null) {
                if (TEXT_PLAIN.getMimeType().equals(getContentType().getMimeType())) {
                    withExtension("txt");
                } else if (TEXT_RICHTEXT.getMimeType().equals(getContentType().getMimeType())) {
                    withExtension("rtf");
                } else if (TEXT_HTML.getMimeType().equals(getContentType().getMimeType())) {
                    withExtension("html");
                } else if (TEXT_CSV.getMimeType().equals(getContentType().getMimeType())) {
                    withExtension("csv");
                } else if (APPLICATION_XML.getMimeType().equals(getContentType().getMimeType())) {
                    withExtension("xml");
                } else if (APPLICATION_JSON.getMimeType().equals(getContentType().getMimeType())) {
                    withExtension("json");
                }
            }

            // guess content type from extension
            if (getContentType() == null && StringUtils.isNotBlank(getExtension())) {
                String extension = getExtension().toLowerCase();
                if ("txt".equals(extension) || "log".equals(getExtension())) {
                    super.withContentType(TEXT_PLAIN);
                } else if ("rt".equals(extension) || "rtf".equals(getExtension())) {
                    super.withContentType(TEXT_RICHTEXT);
                } else if ("html".equals(getExtension())) {
                    super.withContentType(TEXT_HTML);
                } else if ("csv".equals(getExtension())) {
                    super.withContentType(TEXT_CSV);
                } else if ("xml".equals(getExtension())) {
                    super.withContentType(APPLICATION_XML);
                } else if ("json".equals(getExtension())) {
                    super.withContentType(APPLICATION_JSON);
                }
            }

            validateAttachment();

            return new TextAttachment(this);
        }
    }
}
