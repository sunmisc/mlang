package org.example.mivar;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Builds object implementations from the flat parser IR. */
public final class ObjectCatalog {
    /** Create decorated, validated objects from qualified attributes. */
    public List<MivarObject> from(final Program program) {
        final Map<String, Map<String, Expr>> grouped = new LinkedHashMap<>();
        for (final Map.Entry<String, Expr> attribute : program.attributes().entrySet()) {
            final String[] parts = attribute.getKey().split("\\.", 2);
            grouped.computeIfAbsent(parts[0], ignored -> new LinkedHashMap<>())
                .put(attribute.getKey(), attribute.getValue());
        }
        return grouped.entrySet().stream()
            .map(entry -> new ValidatedObject(new BasicObject(entry.getKey(), entry.getValue()),
                new BasicObject(entry.getKey(), Map.of())))
            .map(object -> (MivarObject) object)
            .toList();
    }
}
