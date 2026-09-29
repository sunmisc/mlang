package org.example.mivar;

import java.util.List;
import java.util.Set;

/** Result of a shortest rule-activation search. */
public record Plan(boolean reached, Set<String> known, List<Rule> rules, long cost) {
    public Plan(final boolean reached, final Set<String> known, final List<Rule> rules) {
        this(reached, known, rules, reached ? rules.size() : Long.MAX_VALUE);
    }

    public Plan {
        known = Set.copyOf(known);
        rules = List.copyOf(rules);
    }
}
