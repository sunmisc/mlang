package org.example.mivar;

import java.util.List;

/** Agent exposing a set of rules over a knowledge state. */
public interface Agent {
    String name();

    List<Rule> rules();

    Plan solve(Knowledge initial, java.util.Set<String> targets);
}
