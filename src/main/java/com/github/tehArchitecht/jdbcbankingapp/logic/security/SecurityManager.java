package com.github.tehArchitecht.jdbcbankingapp.logic.security;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for authorisation operations. Tracks authorised users by their
 * token.
 */
public class SecurityManager {
    private final Map<SecurityToken, Long> tokenToUserId;

    public SecurityManager() {
        tokenToUserId = new HashMap<>();
    }

    public SecurityToken signIn(Long userId) {
        SecurityToken userToken = new SecurityToken();
        tokenToUserId.put(userToken, userId);
        return  userToken;
    }

    public void signOut(SecurityToken token) {
        tokenToUserId.remove(token);
    }

    public boolean isTokenInvalid(SecurityToken token) {
        return !tokenToUserId.containsKey(token);
    }

    public Long getUserId(SecurityToken token) {
        return tokenToUserId.get(token);
    }
}
