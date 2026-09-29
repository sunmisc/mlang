package org.example.mivar;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Compatibility view: variables connected by at least one hyperedge. */
public final class Graph {
    private final Program program;

    public Graph(final Program program) {
        this.program = program;
    }

    /** Return variables reachable through rules, ignoring activation requirements. */
    public List<String> bfs(final String start) {
        final Set<String> seen = new LinkedHashSet<>();
        final List<String> result = new ArrayList<>();
        seen.add(start);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (final Rule rule : this.program.rules()) {
                if (rule.inputs().stream().anyMatch(seen::contains)
                    || rule.outputs().stream().anyMatch(seen::contains)) {
                    for (final String variable : rule.inputs()) {
                        changed |= seen.add(variable);
                    }
                    for (final String variable : rule.outputs()) {
                        changed |= seen.add(variable);
                    }
                }
            }
        }
        seen.remove(start);
        result.addAll(seen);
        return List.copyOf(result);
    }

    /** Return variables used by a rule, for diagnostics. */
    public Set<String> neighbors(final String variable) {
        final Set<String> result = new LinkedHashSet<>();
        for (final Rule rule : this.program.rules()) {
            if (rule.inputs().contains(variable) || rule.outputs().contains(variable)) {
                result.addAll(rule.inputs());
                result.addAll(rule.outputs());
            }
        }
        result.remove(variable);
        return Set.copyOf(result);
    }
}
