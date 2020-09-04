package com.put.swolarz.servicediscoveryapi.domain.websync;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;


@Service
@RequiredArgsConstructor
class OptimisticVersionHolderImpl implements OptimisticVersionHolder {
    private static final String VERSION_KEY_PREFIX = "VER@";

    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public String storeVersionForUpdate(long version) {
        String versionToken = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                resolveKey(versionToken),
                Long.toString(version),
                Duration.ofMinutes(15)
        );

        return versionToken;
    }

    @Override
    public Long restoreVersionForUpdate(@NonNull String versionToken) {
        if (versionToken == null)
            throw new IllegalArgumentException("Version token not passed");

        String versionValue = redisTemplate.opsForValue().get(resolveKey(versionToken));

        if (versionValue == null)
            return null;

        return Long.parseLong(versionValue);
    }

    private String resolveKey(String versionToken) {
        return VERSION_KEY_PREFIX + versionToken;
    }
}
