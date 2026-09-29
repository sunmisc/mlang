package org.example.mivar;

import java.util.BitSet;

/** Rule compiled to integer ids and bitsets. */
public final class CompiledRule {
    private final Rule source;
    private final BitSet inputs;
    private final BitSet outputs;

    public CompiledRule(final Rule source, final BitSet inputs, final BitSet outputs) {
        this.source = source;
        this.inputs = (BitSet) inputs.clone();
        this.outputs = (BitSet) outputs.clone();
    }

    public Rule source() {
        return this.source;
    }

    public String name() {
        return this.source.name();
    }

    public boolean applicable(final KnowledgeBits knowledge) {
        return knowledge.containsAll(this.inputs);
    }

    public boolean changes(final KnowledgeBits knowledge) {
        return knowledge.changedBy(this.outputs);
    }

    public KnowledgeBits activate(final KnowledgeBits knowledge) {
        return knowledge.add(this.outputs);
    }

    public BitSet inputs() {
        return (BitSet) this.inputs.clone();
    }

    public BitSet outputs() {
        return (BitSet) this.outputs.clone();
    }
}
