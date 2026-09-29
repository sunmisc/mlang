package org.example.mivar;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/** Compiles the parsed domain model into indexed executable IR. */
public final class ProgramCompiler {
    public CompiledProgram compile(final Program program) {
        final VariableTable table = new VariableTable(program.objects().stream().distinct().toList());
        final List<CompiledRule> compiled = new ArrayList<>();
        program.rules().forEach(rule -> compiled.add(new CompiledRule(rule,
            this.bits(rule.inputs(), table), this.bits(rule.outputs(), table))));
        return new CompiledProgram(table, compiled);
    }

    private BitSet bits(final List<String> names, final VariableTable table) {
        final BitSet result = new BitSet();
        names.stream().map(table::idOf).flatMap(java.util.Optional::stream).forEach(result::set);
        return result;
    }
}
