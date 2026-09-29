package org.example.mivar;

import java.util.Map;

/** Object decorator for validating attribute names. */
public final class ValidatedObject extends ObjectDecorator {
    public ValidatedObject(final MivarObject own, final MivarObject base) {
        super(own, base);
        for (final String attribute : attributes().keySet()) {
            if (!attribute.matches("[A-Za-z][A-Za-z0-9_.]*")) {
                throw new IllegalArgumentException("invalid object attribute: " + attribute);
            }
        }
    }
}
