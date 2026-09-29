package org.example.mivar;

import java.util.Set;

/** Lower-bound estimate for the remaining plan cost. */
@FunctionalInterface
public interface Heuristic {
    long estimate(Knowledge knowledge, Set<String> targets);
}
