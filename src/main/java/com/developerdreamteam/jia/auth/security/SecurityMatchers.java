package com.developerdreamteam.jia.auth.security;

import lombok.Getter;

@Getter
public enum SecurityMatchers {

    PUBLIC_ENDPOINTS(new String[]{
            "/api/v1/users/signup", "/api/v1/users/signup/confirmation"
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
