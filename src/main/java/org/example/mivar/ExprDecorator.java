package org.example.mivar;

import java.util.Map;
import java.util.Set;

/** Base decorator for expressions. */
public abstract class ExprDecorator implements Expr {
    private final Expr origin;

    protected ExprDecorator(final Expr origin) {
        this.origin = origin;
    }

    protected final Expr origin() {
        return this.origin;
    }

    public Object eval(final Map<String, Object> values) {
        return this.origin.eval(values);
    }

    public Set<String> references() {
        return this.origin.references();
    }
}
