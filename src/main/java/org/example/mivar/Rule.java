package org.example.mivar;

import java.util.List;
import java.util.Map;

/** A rule object in the MIVAR object model. */
public interface Rule {
    String name();

    List<String> inputs();

    List<String> outputs();

    Expr condition();

    Map<String, Expr> emissions();

    int line();

    /** Dynamic activation price; custom rules may override it. */
    default long cost(final Knowledge knowledge) {
        return 1L;
    }

    /** Check the AND-input condition and optional expression condition. */
    default boolean applicable(final Knowledge knowledge) {
        if (!knowledge.variables().containsAll(this.inputs())) {
            return false;
        }
        final Object result = this.condition().eval(knowledge.values());
        return result instanceof Boolean && (Boolean) result;
    }
}
