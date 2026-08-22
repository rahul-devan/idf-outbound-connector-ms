package com.ndash.idsphere.integrations.cache;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IntegrationMetadataCache {

    private final ConcurrentHashMap<String, List<?>> cache = new ConcurrentHashMap<>();

    public Optional<List<?>> get(String key) {
        return Optional.ofNullable(cache.get(key));
    }

    public void put(String key, List<?> value) {
        cache.put(key, value);
    }

    public void evict(String key) {
        cache.remove(key);
    }
}
