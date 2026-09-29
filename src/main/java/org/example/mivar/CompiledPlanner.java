package org.example.mivar;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** BFS over compiled bitset states. */
public final class CompiledPlanner {
    public CompiledPlan find(final CompiledProgram program, final KnowledgeBits initial,
        final KnowledgeBits targets) {
        final ArrayDeque<KnowledgeBits> queue = new ArrayDeque<>();
        final Set<KnowledgeBits> seen = new HashSet<>();
        final Map<KnowledgeBits, Step> previous = new HashMap<>();
        queue.add(initial);
        seen.add(initial);
        while (!queue.isEmpty()) {
            final KnowledgeBits state = queue.remove();
            if (state.containsAll(targets.bits())) {
                return new CompiledPlan(true, state, this.restore(state, initial, previous));
            }
            program.candidates(state).filter(rule -> rule.applicable(state)).forEach(rule -> {
                if (rule.changes(state)) {
                    final KnowledgeBits next = rule.activate(state);
                    if (seen.add(next)) {
                        previous.put(next, new Step(state, rule));
                        queue.add(next);
                    }
                }
            });
        }
        return new CompiledPlan(false, initial, List.of());
    }

    private List<CompiledRule> restore(final KnowledgeBits state, final KnowledgeBits initial,
        final Map<KnowledgeBits, Step> previous) {
        final java.util.ArrayDeque<CompiledRule> result = new java.util.ArrayDeque<>();
        KnowledgeBits current = state;
        while (!current.equals(initial)) {
            final Step step = previous.get(current);
            result.addFirst(step.rule());
            current = step.previous();
        }
        return List.copyOf(result);
    }

    private record Step(KnowledgeBits previous, CompiledRule rule) {
    }
}
