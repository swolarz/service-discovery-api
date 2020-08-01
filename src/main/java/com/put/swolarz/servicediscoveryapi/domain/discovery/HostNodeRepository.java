package com.put.swolarz.servicediscoveryapi.domain.discovery;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


interface HostNodeRepository extends JpaRepository<HostNode, Long> {

    @Query("select h from HostNode h order by (count(i) from ServiceInstance i where i.host.id = h.id)")
    List<HostNode> findLoadBalancedRepositories(Pageable pageable);
}
