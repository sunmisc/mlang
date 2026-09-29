package org.example.mivar;

import java.util.ArrayList;
import java.util.List;

/** Recursive-descent parser for MIVAR expressions. */
public final class ExpressionParser {
    private List<String> tokens;
    private int position;

    /** Parse one expression. */
    public Expr parse(final String source) {
        this.tokens = tokenize(source);
        this.position = 0;
        final Expr result = or();
        if (this.position != this.tokens.size()) {
            throw new IllegalArgumentException("unexpected token: " + this.tokens.get(this.position));
        }
        return result;
    }

    private Expr or() {
        Expr result = and();
        while (accept("||")) {
            result = new Expr.Binary("||", result, and());
        }
        return result;
    }

    private Expr and() {
        Expr result = equality();
        while (accept("&&")) {
            result = new Expr.Binary("&&", result, equality());
        }
        return result;
    }

    private Expr equality() {
        Expr result = comparison();
        while (peek("==") || peek("!=")) {
            final String operator = take();
            result = new Expr.Binary(operator, result, comparison());
        }
        return result;
    }

    private Expr comparison() {
        Expr result = term();
        while (peek("<") || peek("<=") || peek(">") || peek(">=")) {
            final String operator = take();
            result = new Expr.Binary(operator, result, term());
        }
        return result;
    }

    private Expr term() {
        Expr result = factor();
        while (peek("+") || peek("-")) {
            final String operator = take();
            result = new Expr.Binary(operator, result, factor());
        }
        return result;
    }

    private Expr factor() {
        Expr result = unary();
        while (peek("*") || peek("/") || peek("%")) {
            final String operator = take();
            result = new Expr.Binary(operator, result, unary());
        }
        return result;
    }

    private Expr unary() {
        if (peek("!") || peek("-")) {
            return new Expr.Unary(take(), unary());
        }
        if (accept("(")) {
            final Expr result = or();
            expect(")");
            return result;
        }
        final String token = take();
        if (token.equals("true") || token.equals("false")) {
            return new Expr.Literal(Boolean.valueOf(token));
        }
        try {
            return new Expr.Literal(Double.valueOf(token));
        } catch (final NumberFormatException ignored) {
            return new Expr.Reference(token);
        }
    }

    private boolean accept(final String token) {
        if (peek(token)) {
            ++this.position;
            return true;
        }
        return false;
    }

    private void expect(final String token) {
        if (!accept(token)) {
            throw new IllegalArgumentException("expected '" + token + "'");
        }
    }

    private boolean peek(final String token) {
        return this.position < this.tokens.size() && this.tokens.get(this.position).equals(token);
    }

    private String take() {
        if (this.position >= this.tokens.size()) {
            throw new IllegalArgumentException("unexpected end of expression");
        }
        return this.tokens.get(this.position++);
    }

    private static List<String> tokenize(final String source) {
        final List<String> result = new ArrayList<>();
        int index = 0;
        while (index < source.length()) {
            final char current = source.charAt(index);
            if (Character.isWhitespace(current)) {
                ++index;
            } else if (Character.isLetter(current) || current == '_') {
                final int start = index++;
                while (index < source.length()
                    && (Character.isLetterOrDigit(source.charAt(index)) || source.charAt(index) == '_' || source.charAt(index) == '.')) {
                    ++index;
                }
                result.add(source.substring(start, index));
            } else if (Character.isDigit(current) || current == '.') {
                final int start = index++;
                while (index < source.length() && (Character.isDigit(source.charAt(index)) || source.charAt(index) == '.')) {
                    ++index;
                }
                result.add(source.substring(start, index));
            } else {
                final String two = index + 1 < source.length() ? source.substring(index, index + 2) : "";
                if (List.of("&&", "||", "==", "!=", "<=", ">=").contains(two)) {
                    result.add(two);
                    index += 2;
                } else {
                    result.add(String.valueOf(current));
                    ++index;
                }
            }
        }
        return List.copyOf(result);
    }
}
