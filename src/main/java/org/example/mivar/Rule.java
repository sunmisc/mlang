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
}
