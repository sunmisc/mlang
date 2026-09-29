package org.example.mivar;

import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** A rule object: required variables, optional condition and emitted expressions. */
public record Rule(String name, List<String> inputs, List<String> outputs,
                   Expr condition, Map<String, Expr> emissions, int line) {
    public Rule {
        inputs = List.copyOf(inputs);
        outputs = List.copyOf(outputs);
        emissions = Collections.unmodifiableMap(new LinkedHashMap<>(emissions));
    }

    /** Compatibility constructor for `rule r: a, b -> x, y`. */
    public Rule(final String name, final List<String> inputs, final List<String> outputs, final int line) {
        this(name, inputs, outputs, new Expr.Literal(true), Map.of(), line);
    }

    /** Infer all referenced variables used by a condition and emission expressions. */
    public static List<String> dependencies(final Expr condition, final Map<String, Expr> emissions) {
        final LinkedHashSet<String> refs = new LinkedHashSet<>(condition.references());
        emissions.values().forEach(expr -> refs.addAll(expr.references()));
        emissions.keySet().forEach(refs::remove);
        return List.copyOf(refs);
    }
}
