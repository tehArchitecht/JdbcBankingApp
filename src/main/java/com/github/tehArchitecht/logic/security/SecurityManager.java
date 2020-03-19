package com.github.tehArchitecht.logic.security;

import java.util.HashMap;
import java.util.Map;

public class SecurityManager {
    private Map<SecurityToken, Long> tokenToUserId;

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
