package com.shree.keyclock.security;

import io.micronaut.context.annotation.Primary;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.security.token.RolesFinder;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.*;

@Singleton
@Primary
public class KeycloakRolesFinder implements RolesFinder {

    private final RolesFinder defaultRolesFinder;

    public KeycloakRolesFinder(
            @Named("defaultRolesFinder") RolesFinder defaultRolesFinder) {
        this.defaultRolesFinder = defaultRolesFinder;
    }

    @Override
    public @NonNull List<String> resolveRoles(@NonNull Map<String, Object> attributes) {

        System.out.println("KeycloakRolesFinder invoked");
        System.out.println("JWT attributes: " + attributes);

        // 1. Get roles from Micronaut default implementation
        Set<String> roles = new LinkedHashSet<>(
                defaultRolesFinder.resolveRoles(attributes)
        );

        Object realmAccess = attributes.get("realm_access");

        if (realmAccess instanceof Map<?, ?> realmMap) {
            Object rolesObj = realmMap.get("roles");

            if (rolesObj instanceof Collection<?> realmRoles) {
                realmRoles.stream()
                        .map(Object::toString)
                        .forEach(roles::add);


            }
        }

        System.out.println("Extracted roles: " + roles);

        return List.copyOf(roles);

        //return Collections.emptyList();
    }
}