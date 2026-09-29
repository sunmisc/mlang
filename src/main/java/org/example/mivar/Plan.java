package org.example.mivar;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Domain object describing a rule-activation plan. */
public final class Plan {
    private final boolean reached;
    private final Set<String> known;
    private final List<Rule> rules;
    private final long cost;

    public Plan(final boolean reached, final Set<String> known, final List<Rule> rules) {
        this(reached, known, rules, reached ? rules.size() : Long.MAX_VALUE);
    }

    public Plan(final boolean reached, final Set<String> known, final List<Rule> rules, final long cost) {
        this.reached = reached;
        this.known = Set.copyOf(known);
        this.rules = List.copyOf(rules);
        this.cost = cost;
    }

    public boolean reached() {
        return this.reached;
    }

    public Set<String> known() {
        return this.known;
    }

    public List<Rule> rules() {
        return this.rules;
    }

    public long cost() {
        return this.cost;
    }

    /** Number of rule activations in this plan. */
    public int activations() {
        return this.rules.size();
    }

    /** Check whether this plan contains all requested goals. */
    public boolean reaches(final Set<String> targets) {
        return this.reached && this.known.containsAll(targets);
    }

    /** Find a rule in the plan by name. */
    public Optional<Rule> rule(final String name) {
        return this.rules.stream().filter(candidate -> candidate.name().equals(name)).findFirst();
    }

    /** Human-readable activation sequence. */
    public String explain() {
        return this.rules.stream().map(Rule::name).reduce((left, right) -> left + " -> " + right).orElse("<empty>");
    }
}
