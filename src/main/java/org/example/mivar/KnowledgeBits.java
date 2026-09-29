package org.example.mivar;

import java.util.BitSet;

/** Compact compiled knowledge state. */
public final class KnowledgeBits {
    private final BitSet bits;

    public KnowledgeBits(final BitSet bits) {
        this.bits = (BitSet) bits.clone();
    }

    public boolean knows(final int variable) {
        return this.bits.get(variable);
    }

    public boolean containsAll(final BitSet required) {
        final BitSet missing = (BitSet) required.clone();
        missing.andNot(this.bits);
        return missing.isEmpty();
    }

    public KnowledgeBits add(final BitSet outputs) {
        final BitSet next = (BitSet) this.bits.clone();
        next.or(outputs);
        return new KnowledgeBits(next);
    }

    public boolean changedBy(final BitSet outputs) {
        final BitSet next = (BitSet) outputs.clone();
        next.andNot(this.bits);
        return !next.isEmpty();
    }

    public BitSet bits() {
        return (BitSet) this.bits.clone();
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof KnowledgeBits state && this.bits.equals(state.bits);
    }

    @Override
    public int hashCode() {
        return this.bits.hashCode();
    }
}
