package me.darknet.oop.library;

import me.martinez.pe.ExportEntry;
import me.martinez.pe.ImportEntry;
import me.martinez.pe.LibraryImports;
import me.martinez.pe.PeImage;
import me.martinez.pe.io.CadesBufferStream;
import me.martinez.pe.io.CadesFileStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
public class PeLibrary extends Library {

    private final PeImage image;
    private List<Symbol> cachedImports = new ArrayList<>();
    private List<Symbol> cachedExports = new ArrayList<>();

    public PeLibrary(String path) throws FileNotFoundException {
        this.image = PeImage.read(new CadesFileStream(new File(path))).ifErr(System.err::println).getOkOrDefault(null);
    }

    public PeLibrary(byte[] data) {
        this.image = PeImage.read(new CadesBufferStream(data)).ifErr(System.err::println).getOkOrDefault(null);
    }

    void readSymbols() {
        image.imports.ifOk(libraryImports -> {
            for (LibraryImports libraryImport : libraryImports) {
                for (ImportEntry entry : libraryImport.entries) {
                    cachedImports.add(new Symbol(SymbolType.FUNCTION, entry.name, null, 0, entry.ordinal));
                }
            }
        });

        image.exports.ifOk(exports -> {
            for (ExportEntry entry : exports.entries) {
                cachedExports.add(new Symbol(SymbolType.FUNCTION, entry.name, null, 0, entry.address));
            }
        });
    }

    @Override
    public List<Symbol> getImports() {
        return cachedImports;
    }

    @Override
    public List<Symbol> getExports() {
        return cachedExports;
    }

    @Override
    public List<Relocation> getRelocations() {
        return null;
    }

    @Override
    public List<Section> getSections() {
        return null;
    }

    @Override
    public boolean is64Bit() {
        return image.is64bit();
    }
}
