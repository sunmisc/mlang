package org.example.mivar;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/** Plain rule implementation. Most language features should wrap this object. */
public final class BasicRule implements Rule {
    private final String name;
    private final List<String> inputs;
    private final List<String> outputs;
    private final Expr condition;
    private final Map<String, Expr> emissions;
    private final int line;

    public BasicRule(final String name, final List<String> inputs, final List<String> outputs,
        final Expr condition, final Map<String, Expr> emissions, final int line) {
        this.name = name;
        this.inputs = List.copyOf(inputs);
        this.outputs = List.copyOf(outputs);
        this.condition = condition;
        this.emissions = Collections.unmodifiableMap(new LinkedHashMap<>(emissions));
        this.line = line;
    }

    public BasicRule(final String name, final List<String> inputs, final List<String> outputs, final int line) {
        this(name, inputs, outputs, new Expr.Literal(true), Map.of(), line);
    }

    public String name() {
        return this.name;
    }

    public List<String> inputs() {
        return this.inputs;
    }

    public List<String> outputs() {
        return this.outputs;
    }

    public Expr condition() {
        return this.condition;
    }

    public Map<String, Expr> emissions() {
        return this.emissions;
    }

    public int line() {
        return this.line;
    }

    /** Infer dependencies from a condition and emitted expressions. */
    public static List<String> dependencies(final Expr condition, final Map<String, Expr> emissions) {
        final LinkedHashSet<String> refs = new LinkedHashSet<>(condition.references());
        emissions.values().forEach(expr -> refs.addAll(expr.references()));
        emissions.keySet().forEach(refs::remove);
        return List.copyOf(refs);
    }
}
