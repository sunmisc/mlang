package org.example.mivar;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Immutable knowledge state. Values are optional; planning only needs names. */
public interface Knowledge {
    Set<String> variables();

    Map<String, Object> values();

    default Optional<Object> value(final String variable) {
        return Optional.ofNullable(this.values().get(variable));
    }

    default boolean knows(final String variable) {
        return this.variables().contains(variable);
    }

    Knowledge add(Set<String> variables, Map<String, Object> values);
}
