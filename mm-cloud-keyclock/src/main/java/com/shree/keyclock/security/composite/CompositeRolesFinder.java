package com.shree.keyclock.security.composite;

import io.micronaut.context.annotation.Primary;
import io.micronaut.core.annotation.Order;
import io.micronaut.security.token.RolesFinder;
import jakarta.inject.Singleton;

import java.util.*;

/**
 * CompositeRolesFinder is a RolesFinder implementation that combines multiple RolesFinders.
 * It delegates the role resolution to all registered RolesFinders and merges their results.
 * CompositeRolesFinder (@Primary) → orchestrator
 *
 * DefaultRolesFinder → framework defaults
 * KeycloakRolesFinder → realm/client roles
 * ScopeRolesFinder → OAuth scopes
 * CustomPermissionFinder → database/tenant permissions
 * @Primary - Should be made primary to ensure it is used as the default RolesFinder and should have the lowest order to ensure it is invoked first. However, since we want to combine multiple RolesFinders, we will not make it primary here.
 * @Order - Should be set to 0 for highest order.
 *
 * @Order controls the order of beans in a collection.
 * @Primary controls which bean is selected when a single bean is injected.
 */

//@Primary - Should be made primary to ensure it is used as the default RolesFinder and should have the lowest order to ensure it is invoked first. However, since we want to combine multiple RolesFinders, we will not make it primary here.
@Singleton
//@Order(0)
public class CompositeRolesFinder implements RolesFinder {

    private final List<RolesFinder> delegates;

    public CompositeRolesFinder(List<RolesFinder> delegates) {
        // Remove self to avoid recursion
        this.delegates = delegates.stream()
                .filter(rf -> !(rf instanceof CompositeRolesFinder))
                .toList();
    }

    @Override
    public List<String> resolveRoles(Map<String, Object> attributes) {

        Set<String> merged = new LinkedHashSet<>();

        for (RolesFinder finder : delegates) {
            merged.addAll(finder.resolveRoles(attributes));
        }

        return List.copyOf(merged);
    }
}