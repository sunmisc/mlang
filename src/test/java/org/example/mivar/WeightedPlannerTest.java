package org.example.mivar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Tests for weighted shortest-path planning. */
final class WeightedPlannerTest {
    @Test
    void choosesFewestRulesWithUnitCost() {
        final Rule direct = new BasicRule("direct", List.of("a"), List.of("target"), 1);
        final Rule first = new BasicRule("first", List.of("a"), List.of("x"), 2);
        final Rule second = new BasicRule("second", List.of("x"), List.of("target"), 3);
        final Plan plan = planner(List.of(direct, first, second), new UnitCost()).find(
            new ImmutableKnowledge(Set.of("a")), Set.of("target"), List.of(direct, first, second),
            new UnitCost(), new ZeroHeuristic());
        assertTrue(plan.reached());
        assertEquals(List.of("direct"), names(plan));
        assertEquals(1L, plan.cost());
    }

    @Test
    void choosesCheaperPlanEvenWhenItUsesMoreRules() {
        final Rule expensive = new BasicRule("expensive", List.of("a"), List.of("target"), 1);
        final Rule first = new BasicRule("cheap1", List.of("a"), List.of("x"), 2);
        final Rule second = new BasicRule("cheap2", List.of("x"), List.of("target"), 3);
        final CostModel costs = (rule, state) -> rule.name().equals("expensive") ? 10L : 2L;
        final Plan plan = planner(List.of(expensive, first, second), costs).find(
            new ImmutableKnowledge(Set.of("a")), Set.of("target"), List.of(expensive, first, second),
            costs, new ZeroHeuristic());
        assertEquals(List.of("cheap1", "cheap2"), names(plan));
        assertEquals(4L, plan.cost());
    }

    @Test
    void supportsSeveralOutputsFromOneRule() {
        final Rule rule = new BasicRule("split", List.of("a"), List.of("x", "y"), 1);
        final Plan plan = planner(List.of(rule), new UnitCost()).find(
            new ImmutableKnowledge(Set.of("a")), Set.of("x", "y"), List.of(rule),
            new UnitCost(), new ZeroHeuristic());
        assertTrue(plan.reached());
        assertTrue(plan.known().containsAll(Set.of("x", "y")));
    }

    @Test
    void requiresAllInputs() {
        final Rule rule = new BasicRule("join", List.of("a", "b"), List.of("x"), 1);
        final Plan plan = planner(List.of(rule), new UnitCost()).find(
            new ImmutableKnowledge(Set.of("a")), Set.of("x"), List.of(rule),
            new UnitCost(), new ZeroHeuristic());
        assertFalse(plan.reached());
    }

    @Test
    void handlesRuleWithoutInputs() {
        final Rule rule = new BasicRule("constant", List.of(), List.of("x"), 1);
        final Plan plan = planner(List.of(rule), new UnitCost()).find(
            new ImmutableKnowledge(Set.of()), Set.of("x"), List.of(rule),
            new UnitCost(), new ZeroHeuristic());
        assertTrue(plan.reached());
    }

    @Test
    void reverseDistanceIsZeroForKnownTarget() {
        final Rule rule = new BasicRule("make", List.of("a"), List.of("x"), 1);
        final ReverseDistance heuristic = new ReverseDistance(List.of(rule), new UnitCost());
        assertEquals(0L, heuristic.estimate(new ImmutableKnowledge(Set.of("x")), Set.of("x")));
    }

    @Test
    void reverseDistanceIsPositiveForUnknownTarget() {
        final Rule rule = new BasicRule("make", List.of("a"), List.of("x"), 1);
        final ReverseDistance heuristic = new ReverseDistance(List.of(rule), new UnitCost());
        assertTrue(heuristic.estimate(new ImmutableKnowledge(Set.of("a")), Set.of("x")) > 0L);
    }

    @Test
    void returnsUnreachablePlan() {
        final Plan plan = planner(List.of(), new UnitCost()).find(
            new ImmutableKnowledge(Set.of("a")), Set.of("x"), List.of(),
            new UnitCost(), new ZeroHeuristic());
        assertFalse(plan.reached());
        assertEquals(Long.MAX_VALUE, plan.cost());
    }

    @Test
    void evaluatesDynamicEmissionValues() {
        final Rule rule = new BasicRule("calculate", List.of("a"), List.of("x"),
            new Expr.Literal(true), Map.of("x", new Expr.Binary("+", new Expr.Reference("a"), new Expr.Literal(2))), 1);
        final Plan plan = planner(List.of(rule), new UnitCost()).find(
            new ImmutableKnowledge(Map.of("a", 3.0)), Set.of("x"), List.of(rule),
            new UnitCost(), new ZeroHeuristic());
        assertTrue(plan.reached());
    }

    private static WeightedPlanner planner(final List<Rule> rules, final CostModel costs) {
        return new WeightedPlanner();
    }

    private static List<String> names(final Plan plan) {
        return plan.rules().stream().map(Rule::name).toList();
    }
}
