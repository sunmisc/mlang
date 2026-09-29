package org.example.mivar;

import java.util.List;

/** Plan result produced by the bitset runtime. */
public final class CompiledPlan {
    private final boolean reached;
    private final KnowledgeBits known;
    private final List<CompiledRule> rules;

    public CompiledPlan(final boolean reached, final KnowledgeBits known, final List<CompiledRule> rules) {
        this.reached = reached;
        this.known = known;
        this.rules = List.copyOf(rules);
    }

    public boolean reached() {
        return this.reached;
    }

    public KnowledgeBits known() {
        return this.known;
    }

    public List<CompiledRule> rules() {
        return this.rules;
    }
}
