package org.example.mivar;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Expression AST with arithmetic and boolean operations. */
public interface Expr {
    Object eval(Map<String, Object> values);

    Set<String> references();

    record Literal(Object value) implements Expr {
        public Object eval(final Map<String, Object> values) {
            return this.value;
        }

        public Set<String> references() {
            return Set.of();
        }
    }

    record Reference(String name) implements Expr {
        public Object eval(final Map<String, Object> values) {
            if (!values.containsKey(this.name)) {
                throw new IllegalArgumentException("unknown value: " + this.name);
            }
            return values.get(this.name);
        }

        public Set<String> references() {
            return Set.of(this.name);
        }
    }

    record Unary(String operator, Expr value) implements Expr {
        public Object eval(final Map<String, Object> values) {
            final Object operand = this.value.eval(values);
            return switch (this.operator) {
                case "!" -> !bool(operand);
                case "-" -> -number(operand);
                default -> throw new IllegalArgumentException("unknown unary operator: " + this.operator);
            };
        }

        public Set<String> references() {
            return this.value.references();
        }
    }

    record Binary(String operator, Expr left, Expr right) implements Expr {
        public Object eval(final Map<String, Object> values) {
            final Object lhs = this.left.eval(values);
            final Object rhs = this.right.eval(values);
            return switch (this.operator) {
                case "+" -> number(lhs) + number(rhs);
                case "-" -> number(lhs) - number(rhs);
                case "*" -> number(lhs) * number(rhs);
                case "/" -> number(lhs) / number(rhs);
                case "%" -> number(lhs) % number(rhs);
                case "==" -> lhs.equals(rhs);
                case "!=" -> !lhs.equals(rhs);
                case "<" -> number(lhs) < number(rhs);
                case "<=" -> number(lhs) <= number(rhs);
                case ">" -> number(lhs) > number(rhs);
                case ">=" -> number(lhs) >= number(rhs);
                case "&&" -> bool(lhs) && bool(rhs);
                case "||" -> bool(lhs) || bool(rhs);
                default -> throw new IllegalArgumentException("unknown binary operator: " + this.operator);
            };
        }

        public Set<String> references() {
            final Set<String> refs = new LinkedHashSet<>(this.left.references());
            refs.addAll(this.right.references());
            return Set.copyOf(refs);
        }
    }

    private static boolean bool(final Object value) {
        if (value instanceof Boolean result) {
            return result;
        }
        throw new IllegalArgumentException("expected boolean, got: " + value);
    }

    private static double number(final Object value) {
        if (value instanceof Number result) {
            return result.doubleValue();
        }
        throw new IllegalArgumentException("expected number, got: " + value);
    }
}
