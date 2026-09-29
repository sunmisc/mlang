package org.example.mivar;

import java.util.List;
import java.util.Map;

/** Base decorator for adding rule behavior without changing the rule contract. */
public abstract class RuleDecorator implements Rule {
    private final Rule origin;

    protected RuleDecorator(final Rule origin) {
        this.origin = origin;
    }

    protected final Rule origin() {
        return this.origin;
    }

    public String name() {
        return this.origin.name();
    }

    public List<String> inputs() {
        return this.origin.inputs();
    }

    public List<String> outputs() {
        return this.origin.outputs();
    }

    public Expr condition() {
        return this.origin.condition();
    }

    public Map<String, Expr> emissions() {
        return this.origin.emissions();
    }

    public int line() {
        return this.origin.line();
    }
}
