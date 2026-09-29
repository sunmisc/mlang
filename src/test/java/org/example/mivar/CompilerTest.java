package org.example.mivar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Tests for the compact compiled IR. */
final class CompilerTest {
    @Test
    void variableTableMapsNamesBothWays() {
        final VariableTable table = new VariableTable(List.of("a", "b", "a"));
        assertEquals(2, table.size());
        assertEquals(0, table.idOf("a").orElseThrow());
        assertEquals("b", table.nameOf(1).orElseThrow());
        assertTrue(table.idOf("missing").isEmpty());
        assertTrue(table.nameOf(99).isEmpty());
    }

    @Test
    void compilerCreatesIndexedRules() {
        final Rule rule = new BasicRule("join", List.of("a", "b"), List.of("x", "y"), 1);
        final Program program = new Program(List.of("a", "b", "x", "y"), List.of(rule));
        final CompiledProgram compiled = new ProgramCompiler().compile(program);
        assertEquals(4, compiled.variables().size());
        assertEquals(List.of("join"), compiled.candidates(compiled.compileKnowledge(Set.of("a")))
            .map(CompiledRule::name).toList());
    }

    @Test
    void compiledRuleRequiresAllInputsAndAddsAllOutputs() {
        final Rule source = new BasicRule("join", List.of("a", "b"), List.of("x", "y"), 1);
        final CompiledProgram program = new ProgramCompiler().compile(
            new Program(List.of("a", "b", "x", "y"), List.of(source)));
        final CompiledRule rule = program.rules().get(0);
        final KnowledgeBits incomplete = program.compileKnowledge(Set.of("a"));
        final KnowledgeBits complete = program.compileKnowledge(Set.of("a", "b"));
        assertFalse(rule.applicable(incomplete));
        assertTrue(rule.applicable(complete));
        final KnowledgeBits result = rule.activate(complete);
        assertTrue(result.knows(program.variables().idOf("x").orElseThrow()));
        assertTrue(result.knows(program.variables().idOf("y").orElseThrow()));
    }

    @Test
    void compiledKnowledgeIsValueLikeAndDoesNotShareBitSets() {
        final KnowledgeBits first = new KnowledgeBits(new java.util.BitSet());
        final java.util.BitSet output = new java.util.BitSet();
        output.set(2);
        final KnowledgeBits second = first.add(output);
        output.set(3);
        assertFalse(first.knows(2));
        assertTrue(second.knows(2));
        assertFalse(second.knows(3));
        assertTrue(second.changedBy(output));
    }

    @Test
    void compiledPlannerFindsShortestBitsetPath() {
        final Rule first = new BasicRule("first", List.of("a"), List.of("x"), 1);
        final Rule second = new BasicRule("second", List.of("x"), List.of("result"), 2);
        final Rule direct = new BasicRule("direct", List.of("a"), List.of("result"), 3);
        final CompiledProgram program = new ProgramCompiler().compile(
            new Program(List.of("a", "x", "result"), List.of(first, second, direct)));
        final KnowledgeBits initial = program.compileKnowledge(Set.of("a"));
        final KnowledgeBits target = program.compileKnowledge(Set.of("result"));
        final CompiledPlan plan = new CompiledPlanner().find(program, initial, target);
        assertTrue(plan.reached());
        assertEquals("direct", plan.rules().get(0).name());
    }

    @Test
    void compiledPlannerReportsMissingTarget() {
        final CompiledProgram program = new ProgramCompiler().compile(
            new Program(List.of("a", "result"), List.of()));
        final CompiledPlan plan = new CompiledPlanner().find(program,
            program.compileKnowledge(Set.of("a")), program.compileKnowledge(Set.of("result")));
        assertFalse(plan.reached());
        assertTrue(plan.rules().isEmpty());
    }
}
