package com.put.swolarz.servicediscoveryapi.domain.common.util;

import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;


@UtilityClass
public class DtoUtils {
    public <T, D> ResultsPage<D> toDtoResultsPage(Page<T> resultsPage, int page, int perPage, DtoMapper<T, D> dtoMapper, Class<D> dtoClass) {
        return ResultsPage.builder(dtoClass)
                .totalNumber(resultsPage.getTotalElements())
                .page(page)
                .perPage(perPage)
                .results(toDtoList(resultsPage, dtoMapper))
                .build();
    }

    private <T, D> List<D> toDtoList(Page<T> resultsPage, DtoMapper<T, D> dtoMapper) {
        return resultsPage.stream()
                .map(dtoMapper::convert)
                .collect(Collectors.toList());
    }
}
