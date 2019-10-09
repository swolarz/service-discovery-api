package com.put.swolarz.servicediscoveryapi.domain.model.discovery;

import com.put.swolarz.servicediscoveryapi.domain.model.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;


@Entity
@Table(name = DataCenter.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
public class DataCenter extends BaseEntity {

    public static final String TABLE_NAME = "DATA_CENTER";

    public static final String NAME_COLUMN_NAME = "NAME";
    public static final String LOCATION_COLUMN_NAME = "LOCATION";

    private static final String SEQUENCE_GENERATOR_NAME = "DATA_CENTER_SEQ_GEN";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = SEQUENCE_GENERATOR_NAME)
    @SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME)
    private Long id;

    @Column(name = NAME_COLUMN_NAME, nullable = false, unique = true, length = 128)
    private String name;

    @Column(name = LOCATION_COLUMN_NAME)
    private String location;
}
