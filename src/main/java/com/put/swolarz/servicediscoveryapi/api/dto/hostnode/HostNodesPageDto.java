package com.put.swolarz.servicediscoveryapi.api.dto.hostnode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
public class HostNodesPageDto {
    private int page;
    private int perPage;
    private long allFound;

    private List<HostNodeDto> hostNodes;
}
