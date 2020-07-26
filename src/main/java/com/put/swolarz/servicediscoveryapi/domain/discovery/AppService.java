package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.data.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@Entity
@Table(name = AppService.TABLE_NAME)
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
class AppService extends BaseEntity {

    public static final String TABLE_NAME = "APP_SERVICE";

    public static final String NAME_COLUMN_NAME = "NAME";
    public static final String SERVICE_VERSION_COLUMN_NAME = "SERVICE_VERSION";

    private static final String SEQUENCE_GENERATOR_NAME = "APP_SERVICE_SEQ_GEN";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = SEQUENCE_GENERATOR_NAME)
    @SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME)
    private Long id;

    @Column(name = NAME_COLUMN_NAME, nullable = false, unique = true, length = 128)
    @NonNull
    private String name;

    @Column(name = SERVICE_VERSION_COLUMN_NAME, nullable = false, length = 32)
    @NonNull
    private String serviceVersion;


    public AppService(String name, String serviceVersion) {
        this.name = name;
        this.serviceVersion = serviceVersion;
    }
}
