package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.data.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = AppService.TABLE_NAME)
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
class AppService extends BaseEntity {

    public static final String TABLE_NAME = "APP_SERVICE";

    public static final String NAME_COLUMN_NAME = "NAME";
    public static final String SERVICE_VERSION_COLUMN_NAME = "SERVICE_VERSION";

    private static final String SEQUENCE_GENERATOR_NAME = "APP_SERVICE_SEQ_GEN";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(generator = "AppServiceIdentityAwareGenerator")
    @GenericGenerator(
            name = "AppServiceIdentityAwareGenerator",
            strategy = "com.put.swolarz.servicediscoveryapi.domain.common.data.IdentityAwareGenerator",
            parameters = @Parameter(name = "sequence_name", value = SEQUENCE_GENERATOR_NAME)
    )
    private Long id;

    @Column(name = NAME_COLUMN_NAME, nullable = false, unique = true, length = 128)
    @NonNull
    private String name;

    @Column(name = SERVICE_VERSION_COLUMN_NAME, nullable = false, length = 32)
    @NonNull
    private String serviceVersion;

//    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<ServiceInstance> instances = new HashSet<>();


    public AppService(String name, String serviceVersion) {
        this(null, name, serviceVersion);
    }

    public AppService(Long id, String name, String serviceVersion) {
        this.id = id;
        this.name = name;
        this.serviceVersion = serviceVersion;
    }
}
