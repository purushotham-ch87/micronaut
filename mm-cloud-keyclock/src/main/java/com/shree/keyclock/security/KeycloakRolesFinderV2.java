package com.shree.keyclock.security;

import io.micronaut.context.annotation.Primary;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Order;
import io.micronaut.security.token.RolesFinder;
import jakarta.inject.Singleton;

import java.util.*;

@Singleton
@Order(100) // Ensure this has a higher order than the default RolesFinder
public class KeycloakRolesFinderV2 implements RolesFinder {

    @Override
    public @NonNull List<String> resolveRoles(@NonNull Map<String, Object> attributes) {

        Object realmAccess = attributes.get("realm_access");

        if (realmAccess instanceof Map<?, ?> realmMap) {
            Object rolesObj = realmMap.get("roles");

            if (rolesObj instanceof Collection<?> roles) {
                return roles.stream()
                        .map(Object::toString)
                        .toList();
            }
        }

        return Collections.emptyList();
    }
}