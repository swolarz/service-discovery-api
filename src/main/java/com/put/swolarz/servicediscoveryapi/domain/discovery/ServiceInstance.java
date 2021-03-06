package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.data.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(
        name = ServiceInstance.TABLE_NAME,
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                ServiceInstance.HOST_COLUMN_NAME,
                                ServiceInstance.PORT_COLUMN_NAME
                        }
                )
        }
)
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
class ServiceInstance extends BaseEntity {

    public static final String TABLE_NAME = "SERVICE_INSTANCE";

    public static final String SERVICE_COLUMN_NAME = "SERVICE_ID";
    public static final String HOST_COLUMN_NAME = "HOST_ID";
    public static final String PORT_COLUMN_NAME = "PORT";
    public static final String STATUS_COLUMN_NAME = "STATUS";
    public static final String STARTED_AT_COLUMN_NAME = "STARTED_AT";

    private static final String SEQUENCE_GENERATOR_NAME = "SERVICE_INSTANCE_SEQ_GEN";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(generator = "ServiceInstanceIdentityAwareGenerator")
    @GenericGenerator(
            name = "ServiceInstanceIdentityAwareGenerator",
            strategy = "com.put.swolarz.servicediscoveryapi.domain.common.data.IdentityAwareGenerator",
            parameters = @Parameter(name = "sequence_name", value = SEQUENCE_GENERATOR_NAME)
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = SERVICE_COLUMN_NAME, nullable = false)
    private AppService service;

    @ManyToOne
    @JoinColumn(name = HOST_COLUMN_NAME, nullable = false)
    private HostNode host;

    @Column(name = PORT_COLUMN_NAME, nullable = false)
    private int port;

    @Enumerated(EnumType.STRING)
    @Column(name = STATUS_COLUMN_NAME, nullable = false, length = 32)
    private InstanceStatus status;

    @Column(name = STARTED_AT_COLUMN_NAME)
    private LocalDateTime startedAt;


    public ServiceInstance(AppService service, HostNode host, int port) {
        this.id = null;

        this.service = service;
        this.host = host;
        this.port = port;

        this.status = resolveByHostStatus(host.getStatus());
        this.startedAt = resolveStartTime(status, InstanceStatus.STOPPED);

        this.status = InstanceStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    public void onHostStatusChanged(HostStatus newHostStatus) {
        setStatus(resolveByHostStatus(newHostStatus));
    }

    public void setStatus(InstanceStatus status) {
        InstanceStatus prevStatus = this.status;

        this.status = status;
        this.startedAt = resolveStartTime(status, prevStatus);
    }

    private LocalDateTime resolveStartTime(InstanceStatus status, InstanceStatus prevStatus) {
        if (status.equals(InstanceStatus.STOPPED))
            return null;

        return LocalDateTime.now();
    }

    private InstanceStatus resolveByHostStatus(@NonNull HostStatus hostStatus) {
        switch (hostStatus) {
            case UP:
                return InstanceStatus.RUNNING;

            case DOWN:
                return InstanceStatus.STOPPED;

            case RESTARTING:
                return InstanceStatus.STARTING;

            default:
                throw new IllegalArgumentException(String.format("Invalid host status: %s", hostStatus));
        }
    }
}
