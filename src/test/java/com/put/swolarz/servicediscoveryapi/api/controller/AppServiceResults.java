package com.put.swolarz.servicediscoveryapi.api.controller;

import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.AppServiceDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
class AppServiceResults {

    private int page;
    private int perPage;
    private long totalNumber;

    private List<AppServiceDetails> results;
}
