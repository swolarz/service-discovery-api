package com.put.swolarz.servicediscoveryapi.domain.discovery;

import org.springframework.data.jpa.repository.JpaRepository;


interface AppServiceRepository extends JpaRepository<AppService, Long> {
}
