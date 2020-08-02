package com.put.swolarz.servicediscoveryapi.domain.websync;

public interface OptimisticVersionHolder {

    String storeVersionForUpdate(long version);
    Long restoreVersionForUpdate(String versionToken);
}
