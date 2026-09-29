package org.example.mivar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Static quality checks for the MIVAR intermediate representation. */
public final class Analyzer {
    /** Analyze rules and variables. */
    public List<Diagnostic> analyze(final Program program, final Set<String> initial, final Set<String> targets) {
        final List<Diagnostic> findings = new ArrayList<>();
        final Set<String> declared = new HashSet<>(program.objects());
        final Set<String> ruleNames = new HashSet<>();
        for (final Rule rule : program.rules()) {
            if (!ruleNames.add(rule.name())) {
                findings.add(new Diagnostic(Diagnostic.Severity.ERROR, "MIVAR002",
                    "duplicate rule: " + rule.name(), rule.line()));
            }
            final List<String> variables = new ArrayList<>(rule.inputs());
            variables.addAll(rule.outputs());
            for (final String variable : variables) {
                if (!declared.contains(variable)) {
                    findings.add(new Diagnostic(Diagnostic.Severity.ERROR, "MIVAR003",
                        "rule references unknown variable: " + variable, rule.line()));
                }
            }
            if (new HashSet<>(rule.outputs()).size() < rule.outputs().size()) {
                findings.add(new Diagnostic(Diagnostic.Severity.WARNING, "MIVAR004",
                    "rule emits the same variable more than once: " + rule.name(), rule.line()));
            }
            if (rule.inputs().stream().anyMatch(rule.outputs()::contains)) {
                findings.add(new Diagnostic(Diagnostic.Severity.WARNING, "MIVAR005",
                    "rule repeats an input in its outputs: " + rule.name(), rule.line()));
            }
        }
        final Plan plan = new Planner().find(initial, targets, program.rules());
        if (!plan.reached()) {
            findings.add(new Diagnostic(Diagnostic.Severity.WARNING, "MIVAR006",
                "target variables cannot be reached from the initial variables", 0));
        }
        return List.copyOf(findings);
    }
}
