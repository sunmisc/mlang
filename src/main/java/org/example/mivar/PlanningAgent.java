package org.example.mivar;

import java.util.List;
import java.util.Set;

/** Default agent implementation using the shortest-rule BFS planner. */
public final class PlanningAgent implements Agent {
    private final String name;
    private final List<Rule> rules;

    public PlanningAgent(final String name, final List<Rule> rules) {
        this.name = name;
        this.rules = List.copyOf(rules);
    }

    public String name() {
        return this.name;
    }

    public List<Rule> rules() {
        return this.rules;
    }

    public Plan solve(final Knowledge initial, final Set<String> targets) {
        return new Planner().find(initial, targets, this.rules);
    }
}
