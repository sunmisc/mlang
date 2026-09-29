package org.example.mivar;

import java.util.Map;

/** A named MIVAR object with lazy attribute expressions. */
public interface MivarObject {
    String name();

    Map<String, Expr> attributes();

    default MivarObject decorate(final MivarObject base) {
        return new ObjectDecorator(this, base);
    }
}
