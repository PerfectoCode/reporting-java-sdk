package com.perfecto.reportium.imports.client;

/**
 * Reportium client constants
 */
public interface InternalConstants {

    String baseReportiumPath = "/import/api";

    interface Url {
        /**
         * Perfecto's HTTP client runtime configuration
         */
        interface Timing {
            /**
             * JVM parameter name for controlling the default HTTP timeout used by Perfecto's internal HTTP client
             */
            String httpTimeoutMillis = "perfecto-http-timeout";
        }

        /**
         * Query parameter names resolvable by Reportium backend
         */
        interface QueryParameterNames {
            String tags = "tags";
            String externalId = "externalId";
        }

        /**
         * Relative URLs to REST resources
         */
        interface V1 {
            String v1Path = baseReportiumPath + "/v1";
            String eventsResource = v1Path + "/events";
            String artifactsUrlResource = v1Path + "/artifacts/url";
        }
    }
}
