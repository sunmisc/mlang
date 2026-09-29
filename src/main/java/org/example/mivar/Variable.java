package org.example.mivar;

/** A named unit of knowledge passed between rules and agents. */
public record Variable(String name) {
    public Variable {
        if (!name.matches("[A-Za-z][A-Za-z0-9_.]*")) {
            throw new IllegalArgumentException("invalid variable: " + name);
        }
    }
}
