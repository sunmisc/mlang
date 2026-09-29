package org.example.mivar;

import java.util.Map;
import java.util.Set;

/** Immutable knowledge state. Values are optional; planning only needs names. */
public interface Knowledge {
    Set<String> variables();

    Map<String, Object> values();

    default boolean knows(final String variable) {
        return this.variables().contains(variable);
    }

    Knowledge add(Set<String> variables, Map<String, Object> values);
}
