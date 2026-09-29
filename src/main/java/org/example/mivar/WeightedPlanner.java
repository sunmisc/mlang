package org.example.mivar;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/** Dijkstra/A* planner for dynamic rule costs. */
public final class WeightedPlanner {
    /** Find the least-cost derivation. */
    public Plan find(final Knowledge initial, final Set<String> targets, final List<Rule> rules,
        final CostModel costs, final Heuristic heuristic) {
        final Set<String> start = Set.copyOf(initial.variables());
        final Map<String, List<Rule>> index = index(rules);
        final PriorityQueue<Entry> queue = new PriorityQueue<>(Comparator.comparingLong(Entry::priority));
        final Map<Set<String>, Long> best = new HashMap<>();
        final Map<Set<String>, Step> previous = new HashMap<>();
        final Map<Set<String>, Map<String, Object>> values = new HashMap<>();
        queue.add(new Entry(start, 0L, heuristic.estimate(initial, targets)));
        best.put(start, 0L);
        values.put(start, initial.values());
        while (!queue.isEmpty()) {
            final Entry entry = queue.remove();
            final Set<String> known = entry.state();
            if (entry.cost() != best.getOrDefault(known, Long.MAX_VALUE)) {
                continue;
            }
            if (known.containsAll(targets)) {
                return new Plan(true, known, restore(known, start, previous), entry.cost());
            }
            final Knowledge state = ImmutableKnowledge.of(values.get(known)).add(known, Map.of());
            for (final Rule rule : candidates(known, index)) {
                if (!applicable(rule, state)) {
                    continue;
                }
                final Set<String> next = new LinkedHashSet<>(known);
                next.addAll(rule.outputs());
                final Set<String> frozen = Set.copyOf(next);
                if (frozen.equals(known)) {
                    continue;
                }
                final long nextCost = entry.cost() + costs.cost(rule, state);
                if (nextCost < best.getOrDefault(frozen, Long.MAX_VALUE)) {
                    final Map<String, Object> nextValues = new HashMap<>(values.get(known));
                    for (final Map.Entry<String, Expr> emission : rule.emissions().entrySet()) {
                        nextValues.put(emission.getKey(), emission.getValue().eval(nextValues));
                    }
                    best.put(frozen, nextCost);
                    values.put(frozen, Map.copyOf(nextValues));
                    previous.put(frozen, new Step(known, rule));
                    final Knowledge nextKnowledge = ImmutableKnowledge.of(nextValues).add(frozen, Map.of());
                    queue.add(new Entry(frozen, nextCost, nextCost + heuristic.estimate(nextKnowledge, targets)));
                }
            }
        }
        return new Plan(false, start, List.of(), Long.MAX_VALUE);
    }

    private static boolean applicable(final Rule rule, final Knowledge state) {
        try {
            return rule.applicable(state);
        } catch (final IllegalArgumentException missingValue) {
            return state.values().isEmpty();
        }
    }

    private static Map<String, List<Rule>> index(final List<Rule> rules) {
        final Map<String, List<Rule>> result = new HashMap<>();
        for (final Rule rule : rules) {
            if (rule.inputs().isEmpty()) {
                result.computeIfAbsent("", ignored -> new ArrayList<>()).add(rule);
            }
            for (final String input : rule.inputs()) {
                result.computeIfAbsent(input, ignored -> new ArrayList<>()).add(rule);
            }
        }
        return result;
    }

    private static Set<Rule> candidates(final Set<String> known, final Map<String, List<Rule>> index) {
        final Set<Rule> result = new LinkedHashSet<>(index.getOrDefault("", List.of()));
        for (final String variable : known) {
            result.addAll(index.getOrDefault(variable, List.of()));
        }
        return result;
    }

    private static List<Rule> restore(final Set<String> state, final Set<String> start,
        final Map<Set<String>, Step> previous) {
        final ArrayDeque<Rule> result = new ArrayDeque<>();
        Set<String> current = state;
        while (!current.equals(start)) {
            final Step step = previous.get(current);
            result.addFirst(step.rule());
            current = step.previous();
        }
        return List.copyOf(result);
    }

    private record Entry(Set<String> state, long cost, long priority) {
    }

    private record Step(Set<String> previous, Rule rule) {
    }
}
