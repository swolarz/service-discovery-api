package com.put.swolarz.servicediscoveryapi.domain.websync;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
class PostRequestsCacheImpl implements PostRequestsCache {
    private static final String POE_KEY_PREFIX = "POE@";

    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public boolean acceptRequest(@NonNull String postOnceToken) {
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(
                resolveKey(postOnceToken), "1", Duration.ofHours(1)
        );

        if (absent == null)
            throw new RuntimeException("Unexpected result value of POE put operation");

        return absent;
    }

    private String resolveKey(String poeToken) {
        return POE_KEY_PREFIX + poeToken;
    }
}
