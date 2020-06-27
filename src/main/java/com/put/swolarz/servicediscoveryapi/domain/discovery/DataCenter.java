package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.common.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@Entity
@Table(name = DataCenter.TABLE_NAME)
@DynamicUpdate
@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
class DataCenter extends BaseEntity {

    public static final String TABLE_NAME = "DATA_CENTER";

    public static final String NAME_COLUMN_NAME = "NAME";
    public static final String LOCATION_COLUMN_NAME = "LOCATION";

    private static final String SEQUENCE_GENERATOR_NAME = "DATA_CENTER_SEQ_GEN";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = SEQUENCE_GENERATOR_NAME)
    @SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME)
    @Setter
    private Long id;

    @Column(name = NAME_COLUMN_NAME, nullable = false, unique = true, length = 128)
    @Setter
    private String name;

    @Column(name = LOCATION_COLUMN_NAME)
    @Setter
    private String location;


    public DataCenter(Long id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }
}
