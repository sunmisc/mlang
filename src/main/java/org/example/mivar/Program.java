package org.example.mivar;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Domain object representing a parsed MIVAR program. */
public final class Program {
    private final List<String> objects;
    private final List<Rule> rules;
    private final Map<String, Expr> attributes;

    public Program(final List<String> objects, final List<Rule> rules, final Map<String, Expr> attributes) {
        this.objects = List.copyOf(objects);
        this.rules = List.copyOf(rules);
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
    }

    public Program(final List<String> objects, final List<Rule> rules) {
        this(objects, rules, Map.of());
    }

    public List<String> objects() {
        return this.objects;
    }

    public List<Rule> rules() {
        return this.rules;
    }

    public Map<String, Expr> attributes() {
        return this.attributes;
    }

    /** Find a rule without exposing collection traversal to callers. */
    public Optional<Rule> rule(final String name) {
        return this.rules.stream().filter(candidate -> candidate.name().equals(name)).findFirst();
    }

    /** All variables declared by objects and rules. */
    public Set<String> variables() {
        return Set.copyOf(this.objects);
    }

    /** Build the default planning agent for this program. */
    public Agent agent(final String name) {
        return new PlanningAgent(name, this.rules);
    }
}
