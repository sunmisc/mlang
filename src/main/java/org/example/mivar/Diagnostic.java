package org.example.mivar;

/** A static-analysis finding. */
public record Diagnostic(Severity severity, String code, String message, int line) {
    @Override
    public String toString() {
        return severity + " " + code + (line > 0 ? " (line " + line + ")" : "") + ": " + message;
    }

    /** Diagnostic severity. */
    public enum Severity {
        ERROR, WARNING, INFO
    }
}
