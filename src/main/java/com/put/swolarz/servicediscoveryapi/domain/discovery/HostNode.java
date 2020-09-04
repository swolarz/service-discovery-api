package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.data.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = HostNode.TABLE_NAME)
@DynamicUpdate
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
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
    @GenericGenerator(
            name = "HostNodeIdentityAwareGenerator",
            strategy = "com.put.swolarz.servicediscoveryapi.domain.common.data.IdentityAwareGenerator",
            parameters = @Parameter(name = "sequence_name", value = SEQUENCE_GENERATOR_NAME)
    )
    @GeneratedValue(generator = "HostNodeIdentityAwareGenerator")
    private Long id;

    @Column(name = NAME_COLUMN_NAME, nullable = false, length = 128)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = STATUS_COLUMN_NAME, nullable = false, length = 32)
    private HostStatus status;

    @Column(name = LAUNCHED_AT_COLUMN_NAME)
    private LocalDateTime launchedAt;

    @ManyToOne
    @JoinColumn(name = DATA_CENTER_COLUMN_NAME, nullable = false)
    private DataCenter dataCenter;

    @Column(name = OS_COLUMN_NAME, nullable = false, length = 64)
    private String os;

//    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<ServiceInstance> instances = new HashSet<>();


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

//        updateInstancesStatus(status);
    }

//    private void updateInstancesStatus(HostStatus newStatus) {
//        getInstances().forEach(instance -> instance.onHostStatusChanged(newStatus));
//    }

    private static LocalDateTime resolveLaunchedTime(HostStatus hostStatus) {
        return (hostStatus.equals(HostStatus.UP) ? LocalDateTime.now() : null);
    }

//    public List<Integer> getUsedPorts() {
//        return getInstances().stream().map(ServiceInstance::getPort).collect(Collectors.toList());
//    }
}
