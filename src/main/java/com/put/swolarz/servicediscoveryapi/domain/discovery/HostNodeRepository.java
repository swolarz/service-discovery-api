package com.put.swolarz.servicediscoveryapi.domain.discovery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;


interface HostNodeRepository extends JpaRepository<HostNode, Long>, HostNodeRepositoryCustom {

    @Query(
            nativeQuery = true,
            value = "select * from host_node h order by (select count(*) from service_instance i where i.host_id = h.id) limit :limit"
    )
    List<HostNode> findLoadBalancedRepositories(@Param("limit") int limit);

    Stream<HostNode> findAllByDataCenterId(long dataCenterId);
}
