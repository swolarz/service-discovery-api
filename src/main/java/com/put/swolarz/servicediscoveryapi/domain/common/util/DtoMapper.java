package com.put.swolarz.servicediscoveryapi.domain.common.util;

@FunctionalInterface
public interface DtoMapper<T, D> {
    D convert(T dataObject);
}
