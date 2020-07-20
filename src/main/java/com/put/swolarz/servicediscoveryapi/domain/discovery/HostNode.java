package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.data.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Table(name = HostNode.TABLE_NAME)
@DynamicUpdate
@Getter
@EqualsAndHashCode(callSuper = true)
class HostNode extends BaseEntity {

    public static final String TABLE_NAME = "HOST_NODE";

    public static final String NAME_COLUMN_NAME = "NAME";
    public static final String STATUS_COLUMN_NAME = "STATUS";
    public static final String LAUNCHED_AT_COLUMN_NAME = "LAUNCHED_AT";
    public static final String DATA_CENTER_COLUMN_NAME = "DATA_CENTER_ID";
    public static final String OS_COLUMN_NAME = "OS";

    private static final String SEQUENCE_GENERATOR_NAME = "HOST_NODE_SEQ_GEN";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = SEQUENCE_GENERATOR_NAME)
    @SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME)
    @Setter
    private Long id;

    @Column(name = NAME_COLUMN_NAME, nullable = false, length = 128)
    @Setter
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = STATUS_COLUMN_NAME, nullable = false, length = 32)
    private HostStatus status;

    @Column(name = LAUNCHED_AT_COLUMN_NAME)
    private LocalDateTime launchedAt;

    @ManyToOne
    @JoinColumn(name = DATA_CENTER_COLUMN_NAME)
    @Setter
    private DataCenter dataCenter;

    @Column(name = OS_COLUMN_NAME, nullable = false, length = 64)
    @Setter
    private String os;

    @OneToMany(mappedBy = "host")
    private Set<ServiceInstance> instances;

    @Builder
    public HostNode(Long id, String name, HostStatus status, DataCenter dataCenter, String os) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.dataCenter = dataCenter;
        this.os = os;

        this.launchedAt = resolveLaunchedTime(status);
    }

    public void setStatus(HostStatus status) {
        this.status = status;
        this.launchedAt = resolveLaunchedTime(status);
    }

    private static LocalDateTime resolveLaunchedTime(HostStatus hostStatus) {
        return (hostStatus.equals(HostStatus.UP) ? LocalDateTime.now() : null);
    }
}
