package org.example.mivar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Cheap admissible heuristic from a reverse relaxed hypergraph. */
public final class ReverseDistance implements Heuristic {
    private final Map<String, Long> distances;

    public ReverseDistance(final List<Rule> rules, final CostModel costs) {
        this.distances = distances(rules, costs);
    }

    public long estimate(final Knowledge knowledge, final Set<String> targets) {
        long result = 0L;
        for (final String target : targets) {
            if (!knowledge.knows(target)) {
                result = Math.max(result, this.distances.getOrDefault(target, Long.MAX_VALUE / 4));
            }
        }
        return result;
    }

    private static Map<String, Long> distances(final List<Rule> rules, final CostModel costs) {
        final Map<String, Long> result = new HashMap<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (final Rule rule : rules) {
                long input = 0L;
                for (final String variable : rule.inputs()) {
                    input = Math.max(input, result.getOrDefault(variable, 0L));
                }
                final long value = input + costs.cost(rule, new ImmutableKnowledge(Set.of()));
                for (final String output : rule.outputs()) {
                    if (value < result.getOrDefault(output, Long.MAX_VALUE)) {
                        result.put(output, value);
                        changed = true;
                    }
                }
            }
        }
        return Map.copyOf(result);
    }
}
