package org.example.mivar;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Plain object implementation. */
public final class BasicObject implements MivarObject {
    private final String name;
    private final Map<String, Expr> attributes;

    public BasicObject(final String name, final Map<String, Expr> attributes) {
        this.name = name;
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
    }

    public String name() {
        return this.name;
    }

    public Map<String, Expr> attributes() {
        return this.attributes;
    }
}
