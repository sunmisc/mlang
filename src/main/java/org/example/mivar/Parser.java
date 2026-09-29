package org.example.mivar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parser for compact rules and object-oriented MIVAR blocks. */
public final class Parser {
    private static final Pattern OBJECT = Pattern.compile("object\\s+([A-Za-z][A-Za-z0-9_]*)(?:\\s*\\{)?");
    private static final Pattern RULE = Pattern.compile("rule\\s+([A-Za-z][A-Za-z0-9_]*)\\s*:\\s*([^->]+?)\\s*->\\s*(.+)");
    private static final Pattern BLOCK_RULE = Pattern.compile("rule\\s+([A-Za-z][A-Za-z0-9_]*)\\s*\\{");
    private static final Pattern AGENT = Pattern.compile("agent\\s+([A-Za-z][A-Za-z0-9_]*)\\s*\\{");

    /** Parse source. */
    public Program parse(final String source) {
        final List<String> variables = new ArrayList<>();
        final List<Rule> rules = new ArrayList<>();
        final Map<String, Expr> attributes = new LinkedHashMap<>();
        final Set<String> names = new LinkedHashSet<>();
        final String[] lines = source.split("\\R", -1);
        int index = 0;
        while (index < lines.length) {
            final int line = index + 1;
            final String text = clean(lines[index]);
            if (text.isEmpty()) {
                ++index;
            } else {
                final Matcher object = OBJECT.matcher(text);
                final Matcher rule = RULE.matcher(text);
                final Matcher block = BLOCK_RULE.matcher(text);
                if (AGENT.matcher(text).matches() || text.equals("}")) {
                    ++index;
                } else if (object.matches()) {
                    final String name = object.group(1);
                    addName(names, variables, name, line);
                    if (text.endsWith("{")) {
                        index = parseObject(lines, index + 1, name, attributes, names, variables);
                    } else {
                        ++index;
                    }
                } else if (rule.matches()) {
                    final List<String> inputs = vars(rule.group(2), line);
                    final List<String> outputs = vars(rule.group(3), line);
                    rules.add(new CheckedRule(new BasicRule(rule.group(1), inputs, outputs, line)));
                    inputs.forEach(variable -> addIfMissing(names, variables, variable));
                    outputs.forEach(variable -> addIfMissing(names, variables, variable));
                    ++index;
                } else if (block.matches()) {
                    final Block parsed = parseRule(lines, index + 1, line, block.group(1));
                    rules.add(new CheckedRule(parsed.rule()));
                    parsed.rule().inputs().forEach(input -> addIfMissing(names, variables, input));
                    parsed.rule().outputs().forEach(output -> addIfMissing(names, variables, output));
                    ++index;
                    index = parsed.next();
                } else {
                    throw new IllegalArgumentException("line " + line + ": invalid MIVAR declaration");
                }
            }
        }
        return new Program(variables, rules, attributes);
    }

    private static int parseObject(final String[] lines, int index, final String object,
        final Map<String, Expr> attributes, final Set<String> names, final List<String> variables) {
        while (index < lines.length) {
            final int line = index + 1;
            final String text = clean(lines[index]);
            if (text.equals("}")) {
                return index + 1;
            }
            final String[] assignment = text.split("=", 2);
            if (assignment.length != 2) {
                throw new IllegalArgumentException("line " + line + ": expected attribute = expression");
            }
            final String name = object + "." + assignment[0].trim();
            addName(names, variables, name, line);
            attributes.put(name, new ExpressionParser().parse(assignment[1].trim()));
            ++index;
        }
        throw new IllegalArgumentException("object " + object + " is not closed");
    }

    private static Block parseRule(final String[] lines, int index, final int start, final String name) {
        Expr condition = new Expr.Literal(true);
        final Map<String, Expr> emissions = new LinkedHashMap<>();
        while (index < lines.length) {
            final String text = clean(lines[index]);
            if (text.equals("}")) {
                return new Block(new BasicRule(name, BasicRule.dependencies(condition, emissions),
                    List.copyOf(emissions.keySet()), condition, emissions, start), index + 1);
            }
            if (text.startsWith("when ")) {
                condition = new ExpressionParser().parse(text.substring(5).trim());
            } else if (text.startsWith("emit ")) {
                final String[] assignment = text.substring(5).split("=", 2);
                if (assignment.length != 2) {
                    throw new IllegalArgumentException("line " + (index + 1) + ": expected emit name = expression");
                }
                emissions.put(assignment[0].trim(), new ExpressionParser().parse(assignment[1].trim()));
            } else {
                throw new IllegalArgumentException("line " + (index + 1) + ": expected when, emit or }");
            }
            ++index;
        }
        throw new IllegalArgumentException("rule is not closed");
    }

    private static List<String> vars(final String text, final int line) {
        final List<String> result = new ArrayList<>();
        for (final String raw : text.split(",")) {
            final String variable = raw.trim();
            if (!variable.matches("[A-Za-z][A-Za-z0-9_.]*")) {
                throw new IllegalArgumentException("line " + line + ": invalid variable " + variable);
            }
            result.add(variable);
        }
        return List.copyOf(result);
    }

    private static String clean(final String line) {
        return line.split("#", 2)[0].trim();
    }

    private static void addName(final Set<String> names, final List<String> variables,
        final String name, final int line) {
        if (!names.add(name)) {
            throw new IllegalArgumentException("line " + line + ": duplicate name " + name);
        }
        variables.add(name);
    }

    private static void addIfMissing(final Set<String> names, final List<String> variables, final String name) {
        if (names.add(name)) {
            variables.add(name);
        }
    }

    private record Block(Rule rule, int next) {
    }
}
