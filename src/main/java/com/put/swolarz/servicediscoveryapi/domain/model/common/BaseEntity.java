package com.put.swolarz.servicediscoveryapi.domain.model.common;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;


@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    public static final String ID_COLUMN_NAME = "ID";

    @Version
    @Setter(AccessLevel.PROTECTED)
    private Long version;

    public abstract Long getId();
    public abstract void setId(Long id);
}
