package com.put.swolarz.servicediscoveryapi.domain.repository;

import com.put.swolarz.servicediscoveryapi.domain.model.discovery.HostNode;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HostNodeRepository extends JpaRepository<HostNode, Long> {
}
