package com.put.swolarz.servicediscoveryapi.api.controller;

import com.put.swolarz.servicediscoveryapi.domain.websync.PostRequestsCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
class PostOnceExactlyHandler {
    public static final String POE_HEADER = "POE-Token";

    private final PostRequestsCache postRequestsCache;

    public void ensurePostOnceExactly(String poeToken) throws PostOnceExactlyException {
        if (!postRequestsCache.acceptRequest(poeToken))
            throw new PostOnceExactlyException(poeToken);
    }
}
