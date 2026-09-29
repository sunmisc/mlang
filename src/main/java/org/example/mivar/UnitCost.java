package org.example.mivar;

/** Cost model for ordinary BFS semantics. */
public final class UnitCost implements CostModel {
    public long cost(final Rule rule, final Knowledge knowledge) {
        return 1L;
    }
}
