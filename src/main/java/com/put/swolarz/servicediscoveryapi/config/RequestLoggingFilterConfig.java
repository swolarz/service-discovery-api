package com.put.swolarz.servicediscoveryapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;


@Slf4j
@Configuration
public class RequestLoggingFilterConfig {
    private static final int REQUEST_PAYLOAD_MAX_LENGTH = 1024;
    private static final String REQUEST_PAYLOAD_PREFIX = "REQUEST DATA : ";

    @Bean
    public CommonsRequestLoggingFilter getLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();

        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setIncludeHeaders(true);

        loggingFilter.setMaxPayloadLength(REQUEST_PAYLOAD_MAX_LENGTH);
        loggingFilter.setAfterMessagePrefix(REQUEST_PAYLOAD_PREFIX);

        return loggingFilter;
    }
}
