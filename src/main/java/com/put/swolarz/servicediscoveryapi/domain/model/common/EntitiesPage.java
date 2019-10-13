package com.put.swolarz.servicediscoveryapi.domain.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class EntitiesPage<Entity extends BaseEntity> {

    private List<Entity> entities;
    private long totalNumber;
}
