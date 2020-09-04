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
@Table(name = DataCenter.TABLE_NAME)
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
class DataCenter extends BaseEntity {

    public static final String TABLE_NAME = "DATA_CENTER";

    public static final String NAME_COLUMN_NAME = "NAME";
    public static final String LOCATION_COLUMN_NAME = "LOCATION";

    private static final String SEQUENCE_GENERATOR_NAME = "DATA_CENTER_SEQ_GEN";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(generator = "DataCenterIdentityAwareGenerator")
    @GenericGenerator(
            name = "DataCenterIdentityAwareGenerator",
            strategy = "com.put.swolarz.servicediscoveryapi.domain.common.data.IdentityAwareGenerator",
            parameters = @Parameter(name = "sequence_name", value = SEQUENCE_GENERATOR_NAME)
    )
    private Long id;

    @Column(name = NAME_COLUMN_NAME, nullable = false, unique = true, length = 128)
    private String name;

    @Column(name = LOCATION_COLUMN_NAME, nullable = false)
    private final String location;

//    @OneToMany(mappedBy = "dataCenter", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<HostNode> hosts = new HashSet<>();


    public DataCenter() {
        this.location = null;
    }

    public DataCenter(String name, String location) {
        this(null, name, location);
    }

    public DataCenter(Long id, String name, String location) {
        this.id = id;
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
