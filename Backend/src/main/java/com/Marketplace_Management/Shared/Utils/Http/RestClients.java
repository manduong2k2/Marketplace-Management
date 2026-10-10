package com.Marketplace_Management.Shared.Utils.Http;

import java.time.Duration;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/** RestClient setup shared by the clients of external APIs (Gemini, Facebook Graph...). */
public final class RestClients {
    private RestClients() {
    }

    /** A builder for baseUrl whose calls fail after the given connect / read timeouts. */
    public static RestClient.Builder withTimeouts(RestClient.Builder builder, String baseUrl, Duration connectTimeout, Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        return builder.baseUrl(baseUrl).requestFactory(requestFactory);
    }
}
