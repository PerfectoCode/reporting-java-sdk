package com.perfecto.reportium.imports.client.connection;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 *
 * Created by eitanp on 28/3/16.
 */
public class HttpResponse {

    private final org.apache.http.HttpResponse responseEntity;

    public HttpResponse(org.apache.http.HttpResponse responseEntity) {
        this.responseEntity = responseEntity;
    }

    /**
     * Returns the integer value of the status code.
     *
     * @return Integer value of the status code
     */
    public int getStatus() {
        return responseEntity.getStatusLine().getStatusCode();
    }

    /**
     * Returns the reason for error status.
     *
     * @return String description of the status
     */
    public String getStatusReason() {
        return responseEntity.getStatusLine().getReasonPhrase();
    }

    public String getBody() {
        try {
            return IOUtils.toString(responseEntity.getEntity().getContent());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

