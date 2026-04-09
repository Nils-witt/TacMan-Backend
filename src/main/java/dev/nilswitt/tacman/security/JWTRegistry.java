package dev.nilswitt.tacman.security;

import java.util.concurrent.ConcurrentHashMap;

public class JWTRegistry {

    private final ConcurrentHashMap.KeySetView<String, Boolean> validTokens = ConcurrentHashMap.newKeySet();

    public JWTRegistry() {}
}
