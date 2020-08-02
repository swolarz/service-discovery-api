package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.data.BaseEntity;
import com.put.swolarz.servicediscoveryapi.domain.websync.OptimisticVersionHolder;
import lombok.experimental.UtilityClass;

import javax.persistence.OptimisticLockException;


@UtilityClass
class EntityVersionUtils {

    public void validateEntityVersion(BaseEntity entity, String versionToken, OptimisticVersionHolder versionHolder) {

        Long version = versionHolder.restoreVersionForUpdate(versionToken);

        if (version == null || !version.equals(entity.getVersion()))
            throw new OptimisticLockException(String.format("Modified resource with id = %d is in stale state", entity.getId()));
    }
}
