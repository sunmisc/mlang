package org.example.mivar;

import java.util.Set;

/** Zero heuristic: Dijkstra's algorithm. */
public final class ZeroHeuristic implements Heuristic {
    public long estimate(final Knowledge knowledge, final Set<String> targets) {
        return 0L;
    }
}
