package com.put.swolarz.servicediscoveryapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;


@Slf4j
@Configuration
public class RequestLoggingFilterConfig {
    private static final int REQUEST_PAYLOAD_MAX_LENGTH = 1024;


    @Bean
    public CommonsRequestLoggingFilter getLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();

        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludeHeaders(true);
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(REQUEST_PAYLOAD_MAX_LENGTH);

        return loggingFilter;
    }
}
