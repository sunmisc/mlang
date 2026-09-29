package org.example.mivar;

/** Uses the rule's declared/default dynamic cost. */
public final class RuleCost implements CostModel {
    public long cost(final Rule rule, final Knowledge knowledge) {
        final long result = rule.cost(knowledge);
        if (result < 0L) {
            throw new IllegalArgumentException("rule cost must be non-negative: " + rule.name());
        }
        return result;
    }
}
