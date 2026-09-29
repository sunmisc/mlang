package org.example.mivar;

import java.util.Map;
import java.util.function.BiConsumer;

/** Expression decorator for diagnostics and future IDE tracing. */
public final class TracingExpr extends ExprDecorator {
    private final BiConsumer<Expr, Object> sink;

    public TracingExpr(final Expr origin, final BiConsumer<Expr, Object> sink) {
        super(origin);
        this.sink = sink;
    }

    @Override
    public Object eval(final Map<String, Object> values) {
        final Object result = super.eval(values);
        this.sink.accept(this.origin(), result);
        return result;
    }
}
