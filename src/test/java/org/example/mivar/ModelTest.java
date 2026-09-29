package org.example.mivar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Unit tests for the object, knowledge and decorator contracts. */
final class ModelTest {
    @Test
    void variableRejectsInvalidNames() {
        assertThrows(IllegalArgumentException.class, () -> new Variable("bad-name"));
        assertEquals("amount", new Variable("amount").name());
    }

    @Test
    void knowledgeIsImmutable() {
        final Knowledge first = new ImmutableKnowledge(Set.of("a"));
        final Knowledge second = first.add(Set.of("b"), Map.of("b", 2));
        assertTrue(first.knows("a"));
        assertTrue(!first.knows("b"));
        assertTrue(second.knows("b"));
        assertEquals(2, second.values().get("b"));
    }

    @Test
    void objectDecoratorMergesAttributes() {
        final MivarObject base = new BasicObject("base", Map.of("base.x", new Expr.Literal(1)));
        final MivarObject own = new BasicObject("child", Map.of("child.y", new Expr.Literal(2)));
        final MivarObject decorated = own.decorate(base);
        assertEquals(2, decorated.attributes().size());
        assertEquals("child", decorated.name());
    }

    @Test
    void tracingExpressionPreservesValue() {
        final Expr expression = new TracingExpr(new Expr.Literal(42), (ignored, value) -> assertEquals(42, value));
        assertEquals(42, expression.eval(Map.of()));
    }

    @Test
    void checkedRuleDelegatesRuleContract() {
        final Rule rule = new CheckedRule(new BasicRule("ok", Set.of("a").stream().toList(),
            Set.of("x").stream().toList(), 1));
        assertEquals("ok", rule.name());
        assertEquals(List.of("a"), rule.inputs());
    }
}
