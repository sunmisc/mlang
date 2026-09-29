package org.example.mivar;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/** Persistent knowledge implementation used by the planner. */
public final class ImmutableKnowledge implements Knowledge {
    private final Set<String> variables;
    private final Map<String, Object> values;

    private ImmutableKnowledge(final Set<String> variables, final Map<String, Object> values) {
        this.variables = Set.copyOf(variables);
        this.values = Map.copyOf(values);
    }

    public static Knowledge of(final Set<String> variables) {
        return new ImmutableKnowledge(variables, Map.of());
    }

    public static Knowledge of(final Map<String, Object> values) {
        return new ImmutableKnowledge(values.keySet(), values);
    }

    public Set<String> variables() {
        return this.variables;
    }

    public Map<String, Object> values() {
        return this.values;
    }

    public Knowledge add(final Set<String> added, final Map<String, Object> additions) {
        final Set<String> next = new LinkedHashSet<>(this.variables);
        next.addAll(added);
        final Map<String, Object> values = new LinkedHashMap<>(this.values);
        values.putAll(additions);
        return new ImmutableKnowledge(next, values);
    }
}
