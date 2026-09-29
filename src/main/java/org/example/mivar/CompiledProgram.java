package org.example.mivar;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/** Executable, indexed IR produced by the MIVAR compiler. */
public final class CompiledProgram {
    private final VariableTable variables;
    private final List<CompiledRule> rules;
    private final Map<Integer, List<CompiledRule>> byInput;

    public CompiledProgram(final VariableTable variables, final List<CompiledRule> rules) {
        this.variables = variables;
        this.rules = List.copyOf(rules);
        final Map<Integer, List<CompiledRule>> index = new LinkedHashMap<>();
        this.rules.forEach(rule -> {
            final BitSet inputs = rule.inputs();
            if (inputs.isEmpty()) {
                index.computeIfAbsent(-1, ignored -> new ArrayList<>()).add(rule);
            }
            inputs.stream().forEach(input -> index.computeIfAbsent(input, ignored -> new ArrayList<>()).add(rule));
        });
        final Map<Integer, List<CompiledRule>> frozen = new LinkedHashMap<>();
        index.forEach((key, value) -> frozen.put(key, List.copyOf(value)));
        this.byInput = Collections.unmodifiableMap(frozen);
    }

    public VariableTable variables() {
        return this.variables;
    }

    public List<CompiledRule> rules() {
        return this.rules;
    }

    /** Return only rules touched by variables in the current state. */
    public Stream<CompiledRule> candidates(final KnowledgeBits knowledge) {
        final Set<CompiledRule> result = new LinkedHashSet<>(this.byInput.getOrDefault(-1, List.of()));
        knowledge.bits().stream().forEach(variable -> result.addAll(this.byInput.getOrDefault(variable, List.of())));
        return result.stream();
    }

    public KnowledgeBits compileKnowledge(final Set<String> names) {
        final BitSet bits = new BitSet();
        names.stream().map(this.variables::idOf).flatMap(java.util.Optional::stream)
            .forEach(bits::set);
        return new KnowledgeBits(bits);
    }
}
