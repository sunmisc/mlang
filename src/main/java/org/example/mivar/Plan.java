package org.example.mivar;

import java.util.List;
import java.util.Set;

/** Result of a shortest rule-activation search. */
public record Plan(boolean reached, Set<String> known, List<Rule> rules) {
    public Plan {
        known = Set.copyOf(known);
        rules = List.copyOf(rules);
    }
}
