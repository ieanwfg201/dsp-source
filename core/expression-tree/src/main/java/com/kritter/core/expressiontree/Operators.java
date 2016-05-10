package com.kritter.core.expressiontree;

public enum Operators {
    /**
     * And operator, to be written in the config as "&" (quotes for clarity only)
     */
    AND,
    /**
     * Or operator, to be written in the config as "|" (quotes for clarity only)
     */
    OR,
    /**
     * Less than operator, to be written in the config as "<" (quotes for clarity only)
     */
    L,
    /**
     * Greater than operator, to be written in the config as ">" (quotes for clarity only)
     */
    G,
    /**
     * Less than or equal to operator, to be written in the config as "<=" (quotes for clarity only)
     */
    LE,
    /**
     * Greater than or equal to operator, to be written in the config as ">=" (quotes for clarity only)
     */
    GE,
    /**
     * Equal to operator, to be written in the config as "=" (quotes for clarity only)
     */
    E,
    /**
     * Not equal to operator, to be written in the config as "!=" (quotes for clarity only)
     */
    NE,
    /**
     * Contains operator, to be written in the config as "contains" (quotes for clarity only).
     * Only works for Strings.
     */
    C,
    /**
     * Not contains operator, to be written in the config as "not-contains" (quotes for clarity only).
     * Only works for Strings.
     */
    NC
}
