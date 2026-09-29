package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.example.mivar.Analyzer;
import org.example.mivar.Engine;
import org.example.mivar.ImmutableKnowledge;
import org.example.mivar.Knowledge;
import org.example.mivar.Parser;
import org.example.mivar.Plan;
import org.example.mivar.PlanningAgent;
import org.example.mivar.Program;

/** Command line entry point for the MIVAR prototype. */
public final class Main {
    private Main() {
    }

    /** Run: java ... org.example.Main file.mivar known1,known2 target1,target2. */
    public static void main(final String[] args) throws Exception {
        if (args.length < 3) {
            throw new IllegalArgumentException("Usage: mivar <file.mivar> <known1,known2> <target1,target2>");
        }
        final Program program = new Parser().parse(Files.readString(Path.of(args[0])));
        final Engine engine = new Engine();
        final Map<String, Object> initialValues = engine.attributes(program, Map.of());
        final Set<String> initial = new LinkedHashSet<>(variables(args[1]));
        initial.addAll(initialValues.keySet());
        final Set<String> targets = variables(args[2]);
        final Knowledge knowledge = new ImmutableKnowledge(initialValues).add(initial, Map.of());
        final Plan plan = new PlanningAgent("main", program.rules()).solve(knowledge, targets);
        System.out.println("Reached: " + plan.reached());
        System.out.println("Known: " + plan.known());
        System.out.println("Rules: " + plan.rules().stream().map(rule -> rule.name()).toList());
        System.out.println("Values: " + engine.execute(program, plan, initialValues));
        new Analyzer().analyze(program, initial, targets).forEach(System.out::println);
    }

    private static Set<String> variables(final String text) {
        return Set.copyOf(Arrays.stream(text.split(",")).map(String::trim)
            .filter(item -> !item.isEmpty()).toList());
    }
}
