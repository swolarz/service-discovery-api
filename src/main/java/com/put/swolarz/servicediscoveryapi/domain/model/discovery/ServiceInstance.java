package com.put.swolarz.servicediscoveryapi.domain.model.discovery;

import com.put.swolarz.servicediscoveryapi.domain.model.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = ServiceInstance.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceInstance extends BaseEntity {

    public static final String TABLE_NAME = "SERVICE_INSTANCE";

    public static final String SERVICE_COLUMN_NAME = "SERVICE_FK";
    public static final String HOST_COLUMN_NAME = "HOST_FK";
    public static final String PORT_COLUMN_NAME = "PORT";
    public static final String STATUS_COLUMN_NAME = "STATUS";
    public static final String STARTED_AT_COLUMN_NAME = "STARTED_AT";

    private static final String SEQUENCE_GENERATOR_NAME = "SERVICE_INSTANCE_SEQ_GEN";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = SEQUENCE_GENERATOR_NAME)
    @SequenceGenerator(name = SEQUENCE_GENERATOR_NAME, sequenceName = SEQUENCE_GENERATOR_NAME)
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

    @Temporal(TemporalType.TIME)
    @Column(name = STARTED_AT_COLUMN_NAME)
    private LocalDateTime startedAt;
}
