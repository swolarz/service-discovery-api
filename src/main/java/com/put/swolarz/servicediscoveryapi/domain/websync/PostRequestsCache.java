package com.put.swolarz.servicediscoveryapi.domain.websync;

public interface PostRequestsCache {

    boolean acceptRequest(String postOnceToken);
}
