package com.perfecto.reportium.imports.model.attachment;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;


import java.util.Arrays;
import java.util.List;

public class ScreenshotAttachment extends Attachment {

    public static final ContentType IMAGE_JPEG = ContentType.create("image/jpeg");
    public static final ContentType IMAGE_PNG = ContentType.create("image/png");

    private ScreenshotAttachment(Builder builder) {
        super(builder);
    }

    public static class Builder extends Attachment.Builder<Builder> {
        public Builder() {
            super.withType(ArtifactType.IMAGE.name());
        }

        @Override
        protected List<String> getAllowedContentTypes() {
            return Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType());
        }

        public ScreenshotAttachment build() {
            guessExtensionFromPath();

            // guess extension from content type
            if (StringUtils.isBlank(getExtension()) && getContentType() != null) {
                if (IMAGE_JPEG.getMimeType().equals(getContentType().getMimeType())) {
                    withExtension("jpg");
                } else if (IMAGE_PNG.getMimeType().equals(getContentType().getMimeType())) {
                    withExtension("png");
                }
            }

            // guess content type from extension
            if (getContentType() == null && StringUtils.isNotBlank(getExtension())) {
                String extension = getExtension().toLowerCase();
                if ("jpg".equals(extension) || "jpeg".equals(getExtension())) {
                    super.withContentType(IMAGE_JPEG);
                } else if ("png".equals(getExtension())) {
                    super.withContentType(IMAGE_PNG);
                }
            }

            validateAttachment();
            return new ScreenshotAttachment(this);
        }
    }
}
