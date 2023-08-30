package me.darknet.oop.library;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public abstract class Library {

    public abstract List<Symbol> getImports();
    public abstract List<Symbol> getExports();

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

    public static Library of(String path) throws IOException {
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

        switch (magic[0]) {
            case 0x7f:
                if (magic[1] == 'E' && magic[2] == 'L' && magic[3] == 'F') {
                    return new ElfLibrary(path);
                }
                break;
            case 'M':
                if (magic[1] == 'Z') {
                    return new PeLibrary(path);
                }
                break;
        }

        throw new IllegalArgumentException("Unknown file format");
    }

}
