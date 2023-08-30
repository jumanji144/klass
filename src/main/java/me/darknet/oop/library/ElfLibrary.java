package me.darknet.oop.library;

import net.fornwall.jelf.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElfLibrary extends Library {

    private List<Symbol> cachedImports;
    private List<Symbol> cachedExports;
    private Map<String, ElfSection> sections;

    private final ElfFile elfFile;

    public ElfLibrary(String path) throws IOException {
        this.elfFile = ElfFile.from(new File(path));
    }

    private void readSymbols() {
        this.cachedExports = new ArrayList<>();
        this.cachedImports = new ArrayList<>();
        this.sections = new HashMap<>();

        for (int i = 0; i < this.elfFile.getSectionNameStringTable().numStrings; i++) {
            ElfSection section = this.elfFile.getSection(i);
            this.sections.put(section.header.getName(), section);
        }

        for (ElfSymbol symbol : this.elfFile.getDynamicSymbolTableSection().symbols) {
            SymbolType type;
            switch (symbol.getType()) {
                case ElfSymbol.STT_FUNC:
                    type = SymbolType.FUNCTION;
                    break;
                case ElfSymbol.STT_OBJECT:
                    type = SymbolType.OBJECT;
                    break;
                default:
                    type = SymbolType.UNKNOWN;
                    break;
            }
            if (symbol.st_shndx == 0) {
                this.cachedImports.add(new Symbol(type, symbol.getName(), symbol.st_value));
            } else {
                this.cachedExports.add(new Symbol(type, symbol.getName(), symbol.st_value));
            }
        }

        this.cachedImports.sort((o1, o2) -> (int) (o1.offset() - o2.offset()));
        this.cachedExports.sort((o1, o2) -> (int) (o1.offset() - o2.offset()));
    }
    @Override
    public List<Symbol> getImports() {
        if (cachedImports == null) {
            readSymbols();
        }

        return cachedImports;
    }

    @Override
    public List<Symbol> getExports() {
        if (cachedExports == null) {
            readSymbols();
        }

        return cachedExports;
    }
}
