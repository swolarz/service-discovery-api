package com.put.swolarz.servicediscoveryapi.api.dto.hostnode;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class HostNodesPageDto {
    private int page;
    private int perPage;
    private int allFound;

    private List<HostNodeDto> hostNodes;
}
