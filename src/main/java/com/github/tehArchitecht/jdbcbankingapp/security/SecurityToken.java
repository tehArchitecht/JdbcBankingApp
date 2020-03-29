package com.github.tehArchitecht.jdbcbankingapp.security;

import java.util.Objects;
import java.util.UUID;

/**
 * Used to identify authorised users.
 * @implNote Uses UUIDs as identifiers.
 */
public class SecurityToken {
    private final UUID uuid;

    SecurityToken() {
        uuid = UUID.randomUUID();
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityToken that = (SecurityToken) o;
        return Objects.equals(uuid, that.uuid);
    }
}
