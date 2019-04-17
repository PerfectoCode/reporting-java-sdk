package com.perfecto.reportium.imports.client;


import com.perfecto.reportium.imports.client.connection.Connection;
import com.perfecto.reportium.imports.model.ImportExecutionContext;

/**
 * Factory for creating the ReportiumClient instances for internal uses
 */
public class ReportiumImportClientFactory {

    /**
     * Creates an internal Reportium client
     *
     * @param connection       Connection details for this customer
     * @param executionContext Test execution context
     * @return
     */
    public ReportiumImportClient createReportiumImportClient(Connection connection,
                                                             ImportExecutionContext executionContext) {
        return new ReportiumImportClient(connection, executionContext);
    }
}