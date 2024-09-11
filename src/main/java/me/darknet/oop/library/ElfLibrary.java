package me.darknet.oop.library;

import net.fornwall.jelf.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ElfLibrary extends Library {

    private List<Symbol> cachedImports;
    private List<Symbol> cachedExports;
    private List<Section> cachedSections;
    private List<Relocation> cachedRelocations;
    private Map<String, Section> sections;

    private final ElfFile elfFile;

    public ElfLibrary(String path) throws IOException {
        this.elfFile = ElfFile.from(new File(path));
    }

    public ElfLibrary(byte[] data) throws IOException {
        this.elfFile = ElfFile.from(data);
    }

    Section getSection(int index) {
        if(index < 0) return null;
        return sections.get(this.elfFile.getSection(index).header.getName());
    }

    Symbol buildSymbol(ElfSymbol symbol) {
        SymbolType type;
        String name = symbol.getName();
        switch (symbol.getType()) {
            case ElfSymbol.STT_FUNC:
                type = SymbolType.FUNCTION;
                break;
            case ElfSymbol.STT_SECTION:
                type = SymbolType.SECTION;
                name = this.elfFile.getSection(symbol.st_shndx).header.getName();
                break;
            case ElfSymbol.STT_OBJECT:
                type = SymbolType.OBJECT;
                break;
            default:
                type = SymbolType.UNKNOWN;
                break;
        }
        return new Symbol(type, name, getSection(symbol.st_shndx), symbol.st_size, symbol.st_value);
    }

    RelocationType getType(int relType) {
        switch (relType) {
            case ElfRelocationTypes.R_X86_64_GOT32:
            case ElfRelocationTypes.R_X86_64_GOTPC32_TLSDESC:
            case ElfRelocationTypes.R_X86_64_GOTPC32:
            case ElfRelocationTypes.R_X86_64_GOTPCREL:
            case ElfRelocationTypes.R_X86_64_GOTPLT64:
            case ElfRelocationTypes.R_X86_64_GOTPCREL64:
            case ElfRelocationTypes.R_X86_64_GOTPC64:
            case ElfRelocationTypes.R_X86_64_GOT64:
            case ElfRelocationTypes.R_X86_64_GOTPCRELX:
            case ElfRelocationTypes.R_X86_64_REX_GOTPCRELX:
            case ElfRelocationTypes.R_X86_64_GOTTPOFF:
                return RelocationType.GOT;
            case ElfRelocationTypes.R_X86_64_PLT32:
            case ElfRelocationTypes.R_X86_64_PLTOFF64:
                return RelocationType.PLT;
            case ElfRelocationTypes.R_X86_64_PC32:
            case ElfRelocationTypes.R_X86_64_PC64:
            case ElfRelocationTypes.R_X86_64_PC8:
            case ElfRelocationTypes.R_X86_64_PC16:
                return RelocationType.PC;
            case ElfRelocationTypes.R_X86_64_RELATIVE:
                return RelocationType.RELATIVE;
            case ElfRelocationTypes.R_X86_64_IRELATIVE:
                return RelocationType.IRELATIVE;
            default:
                return RelocationType.NONE;
        }
    }

    int getSize(int relType) {
        switch (relType) {
            case ElfRelocationTypes.R_X86_64_8:
            case ElfRelocationTypes.R_X86_64_PC8:
                return Relocation.REL8;
            case ElfRelocationTypes.R_X86_64_16:
            case ElfRelocationTypes.R_X86_64_PC16:
                return Relocation.REL16;
            case ElfRelocationTypes.R_X86_64_64:
            case ElfRelocationTypes.R_X86_64_PC64:
            case ElfRelocationTypes.R_X86_64_GOTPLT64:
            case ElfRelocationTypes.R_X86_64_GOTPCREL64:
            case ElfRelocationTypes.R_X86_64_GOTPC64:
            case ElfRelocationTypes.R_X86_64_GOT64:
            case ElfRelocationTypes.R_X86_64_PLTOFF64:
            case ElfRelocationTypes.R_X86_64_IRELATIVE:
                return Relocation.REL64;
            default:
                return Relocation.REL32;
        }
    }

    private void readSymbols() {
        this.cachedExports = new ArrayList<>();
        this.cachedImports = new ArrayList<>();
        this.cachedSections = new ArrayList<>();
        List<Symbol> cachedDynamicSymbols = new ArrayList<>();
        List<Symbol> cachedSymbols = new ArrayList<>();
        this.cachedRelocations = new ArrayList<>();
        this.sections = new HashMap<>();

        for (int i = 0; i < this.elfFile.getSectionNameStringTable().numStrings; i++) {
            ElfSection section = this.elfFile.getSection(i);
            if (section == null) continue;
            Section sec = new Section(section.header.getName(), section.header.sh_addr, section.header.sh_size,
                    section.header.sh_offset, section.getData());
            this.cachedSections.add(sec);
            this.sections.put(section.header.getName(), sec);
        }

        if(this.elfFile.getSymbolTableSection() != null) {
            for (ElfSymbol symbol : this.elfFile.getSymbolTableSection().symbols) {
                Symbol sym = buildSymbol(symbol);
                if(sym.type().isExternal()) {
                    if (symbol.st_shndx == 0) {
                        this.cachedImports.add(sym);
                    } else {
                        this.cachedExports.add(sym);
                    }
                }
                cachedSymbols.add(sym);
            }
        }

        if(this.elfFile.getDynamicSymbolTableSection() != null) {
            for (ElfSymbol symbol : this.elfFile.getDynamicSymbolTableSection().symbols) {
                Symbol sym = buildSymbol(symbol);
                if(sym.type().isExternal()) {
                    if (symbol.st_shndx == 0) {
                        this.cachedImports.add(sym);
                    } else {
                        this.cachedExports.add(sym);
                    }
                }
                cachedDynamicSymbols.add(sym);
            }
        }

        for (ElfSection elfSection : this.elfFile.sectionsOfType(ElfSectionHeader.SHT_REL)) {
            ElfRelocationSection relocationSection = (ElfRelocationSection) elfSection;
            List<Symbol> eff = cachedSymbols;
            if(eff.isEmpty()) {
                eff = cachedDynamicSymbols;
            }
            String target = elfSection.header.getName().substring(5); // .rela
            for (ElfRelocation relocation : relocationSection.relocations) {
                Symbol sym = eff.get(relocation.getSymbolIndex());
                if(sym.name() == null) continue;
                RelocationType type = getType((int) relocation.getType());
                int size = getSize((int) relocation.getType());

                this.cachedRelocations.add(new Relocation(type, size, sym, target, relocation.r_offset, 0));
            }
        }

        for (ElfSection elfSection : this.elfFile.sectionsOfType(ElfSectionHeader.SHT_RELA)) {
            ElfRelocationAddendSection relocationSection = (ElfRelocationAddendSection) elfSection;
            List<Symbol> eff = cachedSymbols;
            if(eff.isEmpty()) {
                eff = cachedDynamicSymbols;
            }
            String target = elfSection.header.getName().substring(5); // .rela
            for (ElfRelocationAddend relocation : relocationSection.relocations) {
                Symbol sym = eff.get(relocation.getSymbolIndex());
                if(sym.name() == null) continue;
                RelocationType type = getType((int) relocation.getType());
                int size = getSize((int) relocation.getType());

                this.cachedRelocations.add(new Relocation(type, size, sym, target, relocation.r_offset,
                        relocation.r_addend));
            }
        }
        this.cachedImports.sort((o1, o2) -> (int) (o1.offset() - o2.offset()));
        this.cachedExports.sort((o1, o2) -> (int) (o1.offset() - o2.offset()));

        this.cachedRelocations.sort((o1, o2) -> (int) (o1.address() - o2.address()));
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

    @Override
    public List<Relocation> getRelocations() {
        if (cachedRelocations == null) {
            readSymbols();
        }

        return cachedRelocations;
    }

    @Override
    public List<Section> getSections() {
        if (cachedSections == null) {
            readSymbols();
        }

        return cachedSections;
    }

    @Override
    public boolean is64Bit() {
        return !elfFile.is32Bits();
    }
}
