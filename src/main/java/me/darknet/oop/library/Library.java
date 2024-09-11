package me.darknet.oop.library;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Library {

    private long base;

    public abstract List<Symbol> getImports();
    public abstract List<Symbol> getExports();
    public abstract List<Relocation> getRelocations();
    public abstract List<Section> getSections();

    public abstract boolean is64Bit();

    public Symbol getExport(String name) {
        for (Symbol symbol : getExports()) {
            if (symbol.name().equals(name)) {
                return symbol;
            }
        }
        return null;
    }

    public Symbol getImport(String name) {
        for (Symbol symbol : getImports()) {
            if (symbol.name().equals(name)) {
                return symbol;
            }
        }
        return null;
    }

    public Relocation getRelocation(long address) {
        for (Relocation relocation : getRelocations()) {
            if (relocation.address() == address) {
                return relocation;
            }
        }
        return null;
    }

    public Relocation getRelocation(String symbol) {
        for (Relocation relocation : getRelocations()) {
            if (relocation.symbol().name().equals(symbol)) {
                return relocation;
            }
        }
        return null;
    }

    public Section getSection(String name) {
        for (Section section : getSections()) {
            if(section.name() == null) {
                continue;
            }
            if (section.name().equals(name)) {
                return section;
            }
        }
        return null;
    }

    public long getExportAddr(String name) {
        Symbol symbol = getExport(name);
        if (symbol == null) {
            return 0;
        }
        return base + symbol.offset();
    }

    public void rebase(long base) {
        this.base = base;
    }

    private static final Map<String, Library> cache = new HashMap<>();

    public static Library of(String path) throws IOException {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }
        // read header
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist");
        }

        InputStream is = Files.newInputStream(file.toPath());

        byte[] magic = new byte[4];
        if(is.read(magic) != 4) {
            throw new IllegalArgumentException("File is too small");
        }

        Library lib = null;

        switch (magic[0]) {
            case 0x7f:
                if (magic[1] == 'E' && magic[2] == 'L' && magic[3] == 'F') {
                    lib = new ElfLibrary(path);
                }
                break;
            case 'M':
                if (magic[1] == 'Z') {
                    lib = new PeLibrary(path);
                }
                break;
        }

        if(lib != null) {
            cache.put(path, lib);
            return lib;
        }

        throw new IllegalArgumentException("Unknown file format");
    }

    public static Library of(byte[] data) throws IOException {

        switch (data[0]) {
            case 0x7f:
                if (data[1] == 'E' && data[2] == 'L' && data[3] == 'F') {
                    return new ElfLibrary(data);
                }
                break;
            case 'M':
                if (data[1] == 'Z') {
                    return new PeLibrary(data);
                }
                break;
        }

        throw new IllegalArgumentException("Unknown file format");
    }

}
