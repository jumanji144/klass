package me.darknet.oop.library;

import net.fornwall.jelf.ElfSymbol;

public enum SymbolType {

    UNKNOWN,
    FUNCTION,
    SECTION,
    OBJECT;

    boolean isExternal() {
        return this == FUNCTION || this == OBJECT;
    }
}
