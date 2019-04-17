package com.perfecto.reportium.imports.model.attachment;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class Attachment {
    private final String type;
    private InputStream inputStream;
    private String absolutePath;
    private ContentType contentType;
    private String extension;
    private String fileName;
    private boolean zipped;
    private Path tempFile = null;

    protected Attachment(Builder builder) {
        type = builder.type;
        inputStream = builder.inputStream;
        absolutePath = builder.absolutePath;
        contentType = builder.contentType;
        extension = builder.extension;
        fileName = builder.fileName;
        zipped = builder.zipped;
    }

    public String getType() {
        return type;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isZipped() {
        return zipped;
    }

    public boolean shouldZip() {
        return false;
    }

    public Path getTempFile() {
        return tempFile;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "type=" + type +
                ", inputStream=" + inputStream +
                ", absolutePath='" + absolutePath + '\'' +
                ", contentType=" + contentType +
                ", extension='" + extension + '\'' +
                ", fileName='" + fileName + '\'' +
                ", zipped='" + zipped + '\'' +
                '}';
    }

    public void setTempFile(Path tempFile) {
        this.tempFile = tempFile;
    }

    public static class Builder<T extends Builder<T>> {
        private String type;
        private InputStream inputStream;
        private String absolutePath;
        private ContentType contentType;
        private String extension;
        private String fileName;
        private boolean zipped = false;

        protected List<String> getAllowedContentTypes() {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        public T withType(String type) {
            this.type = type;
            return (T) this;
        }

        /**
         * An input stream that represents the attachment. This method will not close this stream
         *
         * @param inputStream
         * @return
         */
        @SuppressWarnings("unchecked")
        public T withInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return (T) this;
        }

        protected InputStream getInputStream() {
            return inputStream;
        }

        @SuppressWarnings("unchecked")
        public T withAbsolutePath(String absolutePath) {
            this.absolutePath = absolutePath;
            return (T) this;
        }

        public String getAbsolutePath() {
            return absolutePath;
        }

        @SuppressWarnings("unchecked")
        public T withContentType(ContentType contentType) {
            this.contentType = contentType;
            return (T) this;
        }

        protected ContentType getContentType() {
            return contentType;
        }

        @SuppressWarnings("unchecked")
        public T withExtension(String extension) {
            this.extension = extension;
            return (T) this;
        }

        protected String getExtension() {
            return extension;
        }

        protected void guessExtensionFromPath() {
            if (StringUtils.isBlank(getExtension()) && StringUtils.isNotBlank(getAbsolutePath())) {
                String extension = FilenameUtils.getExtension(getAbsolutePath()).toLowerCase();
                if (StringUtils.isNotBlank(extension)) {
                    withExtension(extension);
                }
            }
        }

        @SuppressWarnings("unchecked")
        public T withZipped(boolean zipped) {
            this.zipped = zipped;
            return (T) this;
        }

        protected boolean isZipped() {
            return zipped;
        }

        protected void guessZippedFromPath() {
            if (StringUtils.isNotBlank(getAbsolutePath()) && getAbsolutePath().toLowerCase().endsWith(".zip")) {
                zipped = true;
            }
        }

        public String getFileName() {
            return fileName;
        }

        @SuppressWarnings("unchecked")
        public T withFileName(String fileName) {
            this.fileName = fileName;
            return (T) this;
        }

        protected void guessFileNameFromAbsolutePath() {
            if (StringUtils.isNotBlank(getAbsolutePath()) && StringUtils.isBlank(getFileName())) {
                fileName = FilenameUtils.getName(getAbsolutePath());
            }
        }

        protected void validateAttachment() {
            if (this.contentType == null) {
                throw new IllegalArgumentException("Content type cannot be null");
            }
            if (StringUtils.isEmpty(this.type)) {
                throw new IllegalArgumentException("Artifact type cannot be empty");
            }
            if (!isContentTypeAllowed(this.contentType)) {
                throw new IllegalArgumentException("Content type '" + this.contentType + "' is not allowed");
            }
            if (this.extension == null) {
                throw new IllegalArgumentException("Extension cannot be null");
            }
            if (this.inputStream == null && this.absolutePath == null) {
                throw new IllegalArgumentException("Content was not provided");
            }
            if (this.absolutePath != null) {
                if (!Files.exists(Paths.get(absolutePath))) {
                    throw new RuntimeException("Could not find file: " + absolutePath);
                }

                if (absolutePath.endsWith(".zip")) {
                    withZipped(true);
                }
            }
        }

        private boolean isContentTypeAllowed(ContentType contentType) {
            // Check if content type allowed
            List<String> allowedContentTypes = getAllowedContentTypes();
            return allowedContentTypes.isEmpty() || allowedContentTypes.contains(contentType.getMimeType());
        }

        public Attachment build() {
            guessExtensionFromPath();

            guessZippedFromPath();

            guessFileNameFromAbsolutePath();

            if (getFileName() == null) {
                throw new IllegalArgumentException("File name cannot be null");
            }

            validateAttachment();

            return new Attachment(this);
        }
    }
}
