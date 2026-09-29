package org.example.mivar;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;

/** Object decorator: own attributes override decorated attributes. */
public class ObjectDecorator implements MivarObject {
    private final MivarObject own;
    private final MivarObject base;

    public ObjectDecorator(final MivarObject own, final MivarObject base) {
        this.own = own;
        this.base = base;
    }

    public String name() {
        return this.own.name();
    }

    public Map<String, Expr> attributes() {
        final Map<String, Expr> result = new LinkedHashMap<>(this.base.attributes());
        result.putAll(this.own.attributes());
        return Collections.unmodifiableMap(result);
    }
}
