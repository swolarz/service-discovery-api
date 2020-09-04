package com.put.swolarz.servicediscoveryapi.domain.discovery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


interface ServiceInstanceRepository extends JpaRepository<ServiceInstance, Long> {

    Page<ServiceInstance> findByServiceId(long appServiceId, Pageable pageable);
    Optional<ServiceInstance> findByIdAndServiceId(long id, long appServiceId);

    List<ServiceInstancePort> findUsedPortsByHostId(long hostId);

    Stream<ServiceInstance> findAllByHostId(long hostId);
    Stream<ServiceInstance> findAllByServiceId(long serviceId);
    long countByServiceId(long serviceId);

    boolean existsByHostIdAndPort(long hostId, int port);

    int countByHostId(long hostId);

    void deleteByServiceId(long serviceId);
}
