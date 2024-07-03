package com.developerdreamteam.jia.auth.security.matchers;

import lombok.Getter;

@Getter
public enum SecurityMatchers {

    PUBLIC_ENDPOINTS(new String[]{
            "/api/v1/auth/signup", "/api/v1/auth/signup/confirmation", "/api/v1/auth/login"
    }),

    STATIC_RESOURCES(new String[]{

    }),

    ADMIN_GET_ENDPOINTS(new String[]{

    }),

    ADMIN_POST_ENDPOINTS(new String[]{

    }),

    USER_ENDPOINTS(new String[]{

    });

    private final String[] matchers;

    SecurityMatchers(String[] matchers) {
        this.matchers = matchers;
    }

}
