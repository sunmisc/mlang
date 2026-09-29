package org.example.mivar;

import java.util.List;
import java.util.Map;

/** Parsed MIVAR intermediate representation. */
public record Program(List<String> objects, List<Rule> rules, Map<String, Expr> attributes) {
    public Program {
        objects = List.copyOf(objects);
        rules = List.copyOf(rules);
        attributes = Map.copyOf(attributes);
    }

    /** Compatibility constructor for the compact rule syntax. */
    public Program(final List<String> objects, final List<Rule> rules) {
        this(objects, rules, Map.of());
    }
}
