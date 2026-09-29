package org.example.mivar;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Finds a plan with the minimum number of activated rules. */
public final class Planner {
    /** Breadth-first search over known-variable states. */
    public Plan find(final Set<String> initial, final Set<String> targets, final List<Rule> rules) {
        return this.find(initial, targets, rules, Map.of());
    }

    /** BFS variant that also validates rule conditions and computes emitted values. */
    public Plan find(final Set<String> initial, final Set<String> targets,
        final List<Rule> rules, final Map<String, Object> initialValues) {
        final Set<String> start = Set.copyOf(initial);
        final Map<String, List<Rule>> byInput = index(rules);
        final List<Rule> withoutInputs = rules.stream().filter(rule -> rule.inputs().isEmpty()).toList();
        final ArrayDeque<Set<String>> queue = new ArrayDeque<>();
        final Set<Set<String>> seen = new HashSet<>();
        final Map<Set<String>, Step> previous = new HashMap<>();
        final Map<Set<String>, Map<String, Object>> values = new HashMap<>();
        queue.add(start);
        seen.add(start);
        values.put(start, Map.copyOf(initialValues));
        while (!queue.isEmpty()) {
            final Set<String> known = queue.remove();
            if (known.containsAll(targets)) {
                return new Plan(true, known, restore(known, start, previous));
            }
            for (final Rule rule : candidates(known, byInput, withoutInputs)) {
                final Map<String, Object> currentValues = values.get(known);
                if (known.containsAll(rule.inputs()) && condition(rule, currentValues)) {
                    final Set<String> next = new LinkedHashSet<>(known);
                    next.addAll(rule.outputs());
                    final Set<String> frozen = Set.copyOf(next);
                    if (!frozen.equals(known) && seen.add(frozen)) {
                        previous.put(frozen, new Step(known, rule));
                        final Map<String, Object> nextValues = new LinkedHashMap<>(currentValues);
                        if (!currentValues.isEmpty()) {
                            for (final Map.Entry<String, Expr> emission : rule.emissions().entrySet()) {
                                nextValues.put(emission.getKey(), emission.getValue().eval(nextValues));
                            }
                        }
                        values.put(frozen, Map.copyOf(nextValues));
                        queue.add(frozen);
                    }
                }
            }
        }
        return new Plan(false, start, List.of());
    }

    private static boolean condition(final Rule rule, final Map<String, Object> values) {
        try {
            final Object result = rule.condition().eval(values);
            return result instanceof Boolean && (Boolean) result;
        } catch (final IllegalArgumentException missingValue) {
            // Structural analysis may not have runtime values yet.
            return values.isEmpty();
        }
    }

    private static Map<String, List<Rule>> index(final List<Rule> rules) {
        final Map<String, List<Rule>> result = new HashMap<>();
        for (final Rule rule : rules) {
            for (final String input : rule.inputs()) {
                result.computeIfAbsent(input, ignored -> new ArrayList<>()).add(rule);
            }
        }
        return result;
    }

    private static Set<Rule> candidates(final Set<String> known,
        final Map<String, List<Rule>> byInput, final List<Rule> withoutInputs) {
        final Set<Rule> result = new LinkedHashSet<>(withoutInputs);
        for (final String variable : known) {
            result.addAll(byInput.getOrDefault(variable, List.of()));
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

    private record Step(Set<String> previous, Rule rule) {
    }
}
