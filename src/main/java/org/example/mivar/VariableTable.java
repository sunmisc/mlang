package org.example.mivar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Bidirectional symbol table used by the compiler. */
public final class VariableTable {
    private final Map<String, Integer> ids;
    private final List<String> names;

    public VariableTable(final List<String> variables) {
        final Map<String, Integer> indexed = new LinkedHashMap<>();
        variables.forEach(variable -> indexed.putIfAbsent(variable, indexed.size()));
        this.ids = Map.copyOf(indexed);
        this.names = List.copyOf(indexed.keySet());
    }

    public Optional<Integer> idOf(final String name) {
        return Optional.ofNullable(this.ids.get(name));
    }

    public Optional<String> nameOf(final int id) {
        return id >= 0 && id < this.names.size() ? Optional.of(this.names.get(id)) : Optional.empty();
    }

    public int size() {
        return this.names.size();
    }

    public List<String> names() {
        return this.names;
    }
}
