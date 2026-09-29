package org.example.mivar;

import java.util.Map;

/** Rule decorator that rejects non-boolean conditions early. */
public final class CheckedRule extends RuleDecorator {
    public CheckedRule(final Rule origin) {
        super(origin);
    }

    @Override
    public Expr condition() {
        return new ExprDecorator(super.condition()) {
            @Override
            public Object eval(final Map<String, Object> values) {
                final Object result = this.origin().eval(values);
                if (!(result instanceof Boolean)) {
                    throw new IllegalArgumentException("rule condition must be boolean: " + name());
                }
                return result;
            }
        };
    }
}
