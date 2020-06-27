package com.put.swolarz.servicediscoveryapi.domain.discovery;

import org.springframework.data.jpa.repository.JpaRepository;


interface HostNodeRepository extends JpaRepository<HostNode, Long> {
}
