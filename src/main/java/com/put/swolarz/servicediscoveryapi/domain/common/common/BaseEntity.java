package com.put.swolarz.servicediscoveryapi.domain.common.common;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;


@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    public static final String ID_COLUMN_NAME = "ID";
    public static final String VERSION_COLUMN_NAME = "OPTLOCK";

    @Version
    @Column(name = VERSION_COLUMN_NAME)
    @Setter(AccessLevel.PROTECTED)
    private long version;

    public abstract Long getId();
    public abstract void setId(Long id);
}
