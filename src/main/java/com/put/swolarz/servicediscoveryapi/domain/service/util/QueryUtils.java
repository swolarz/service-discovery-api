package com.put.swolarz.servicediscoveryapi.domain.service.util;

import com.put.swolarz.servicediscoveryapi.domain.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.exception.ErrorCode;
import com.put.swolarz.servicediscoveryapi.domain.model.common.BaseEntity;
import com.put.swolarz.servicediscoveryapi.domain.model.common.EntitiesPage;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@UtilityClass
public class QueryUtils {

    public Pageable createPageRequest(int page, int perPage) throws BusinessException {
        try {
            return PageRequest.of(page, perPage);
        }
        catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_PAGE_REQUESTED, e);
        }
    }

    public <T extends BaseEntity> EntitiesPage<T> wrapEntitiesPage(Page<T> resultsPage) {
        return new EntitiesPage<>(resultsPage.getContent(), resultsPage.getTotalElements());
    }
}
