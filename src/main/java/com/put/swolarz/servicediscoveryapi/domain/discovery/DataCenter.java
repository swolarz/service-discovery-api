package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.data.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@Entity
@Table(name = DataCenter.TABLE_NAME)
@DynamicUpdate
@Getter
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
    @NonNull
    private String name;

    @Column(name = LOCATION_COLUMN_NAME, nullable = false)
    @NonNull
    private final String location;


    public DataCenter(String name, String location) {
        this.id = null;
        this.name = name;
        this.location = location;
    }

    public void setLocation(String location) {
        if (!sameLocation(location))
            throw new IllegalArgumentException("Data center location is unmodifiable");
    }

    private boolean sameLocation(String location) {
        return this.location.equals(location);
    }
}
