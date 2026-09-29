package org.example.mivar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Tests for parsing, planning and quality diagnostics. */
final class MivarTest {
    @Test
    void findsMinimumPlanWithMultipleInputsAndOutputs() {
        final Program program = new Parser().parse("""
            object a
            object b
            object c
            object x
            object y
            object z
            rule rule1: a, b, c -> x, y
            rule rule2: a, b -> z
            rule rule3: x, y -> z
            """);
        final Plan plan = new Planner().find(Set.of("a", "b", "c"), Set.of("z"), program.rules());
        assertTrue(plan.reached());
        assertEquals(1, plan.rules().size());
        assertEquals("rule2", plan.rules().get(0).name());
        assertTrue(plan.known().contains("z"));
    }

    @Test
    void doesNotActivateRuleUntilAllInputsAreKnown() {
        final Program program = new Parser().parse("""
            object a
            object b
            object x
            rule r: a, b -> x
            """);
        assertTrue(!new Planner().find(Set.of("a"), Set.of("x"), program.rules()).reached());
    }

    @Test
    void evaluatesObjectAttributesMathAndLogic() {
        final Program program = new Parser().parse("""
            object input {
              a = 10
              b = 20
              enabled = true
            }
            rule sum {
              when input.enabled
              emit x = input.a + input.b
              emit y = x * 2
            }
            rule valid {
              when x > 20 && y == 60
              emit result = true
            }
            """);
        final Engine engine = new Engine();
        final Map<String, Object> values = engine.attributes(program, Map.of());
        final Set<String> known = values.keySet();
        final Plan plan = new Planner().find(known, Set.of("result"), program.rules(), values);
        final Map<String, Object> result = engine.execute(program, plan, values);
        assertEquals(true, result.get("result"));
        assertEquals(2, plan.rules().size());
    }

    @Test
    void reportsUnreachableTargets() {
        final Program program = new Parser().parse("""
            object a
            object x
            rule broken: a -> missing
            """);
        final List<Diagnostic> diagnostics = new Analyzer().analyze(program, Set.of("a"), Set.of("x"));
        assertTrue(diagnostics.stream().anyMatch(item -> item.code().equals("MIVAR006")));
    }

    @Test
    void rejectsDuplicateObjectsAndMalformedLines() {
        final Parser parser = new Parser();
        assertThrows(IllegalArgumentException.class, () -> parser.parse("object A\nobject A"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("unknown A"));
    }
}
