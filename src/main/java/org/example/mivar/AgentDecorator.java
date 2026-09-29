package org.example.mivar;

import java.util.List;
import java.util.Set;

/** Base decorator for agent policies. */
public abstract class AgentDecorator implements Agent {
    private final Agent origin;

    protected AgentDecorator(final Agent origin) {
        this.origin = origin;
    }

    protected final Agent origin() {
        return this.origin;
    }

    public String name() {
        return this.origin.name();
    }

    public List<Rule> rules() {
        return this.origin.rules();
    }

    public Plan solve(final Knowledge initial, final Set<String> targets) {
        return this.origin.solve(initial, targets);
    }
}
