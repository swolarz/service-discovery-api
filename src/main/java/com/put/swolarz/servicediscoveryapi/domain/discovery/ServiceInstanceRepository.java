package com.put.swolarz.servicediscoveryapi.domain.discovery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


interface ServiceInstanceRepository extends JpaRepository<ServiceInstance, Long> {

    Page<ServiceInstance> findByServiceId(long appServiceId, Pageable pageable);

    Optional<ServiceInstance> findByIdAndServiceId(long id, long appServiceId);
}
