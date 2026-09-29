package org.example.mivar;

import java.util.LinkedHashMap;
import java.util.Map;

/** Evaluates object attributes and the rules selected by the planner. */
public final class Engine {
    /** Execute a plan using initial values. */
    public Map<String, Object> execute(final Program program, final Plan plan,
        final Map<String, Object> initial) {
        final Map<String, Object> values = new LinkedHashMap<>(this.attributes(program, initial));
        for (final Rule rule : plan.rules()) {
            if (rule.condition().eval(values) instanceof Boolean condition && condition) {
                for (final Map.Entry<String, Expr> emission : rule.emissions().entrySet()) {
                    values.put(emission.getKey(), emission.getValue().eval(values));
                }
            }
        }
        return Map.copyOf(values);
    }

    /** Evaluate object attributes before planning. */
    public Map<String, Object> attributes(final Program program, final Map<String, Object> initial) {
        final Map<String, Object> values = new LinkedHashMap<>(initial);
        for (final Map.Entry<String, Expr> attribute : program.attributes().entrySet()) {
            values.put(attribute.getKey(), attribute.getValue().eval(values));
        }
        return Map.copyOf(values);
    }
}
