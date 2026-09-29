package org.example.mivar;

/** Supplies a runtime cost for activating a rule in a knowledge state. */
@FunctionalInterface
public interface CostModel {
    long cost(Rule rule, Knowledge knowledge);
}
