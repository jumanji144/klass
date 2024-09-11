package me.darknet.oop.jithack;

import me.darknet.oop.library.*;
import me.darknet.oop.util.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Linker {

    private static final List<NativeLibrary> resolvingLibraries;
    private static final Unsafe unsafe = UnsafeAccessor.getUnsafe();

    static {
        try {
            resolvingLibraries = Arrays.asList(
                    Libraries.findJvm(),
                    Libraries.findLibC());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private final Library binary;
    private Map<String, Long> loadedSections = new HashMap<>();
    private Map<String, Long> loadedSymbols = new HashMap<>();

    public Linker(Library binary) {
        this.binary = binary;
    }

    long findSymbol(String name) {

        // resolve the symbol in the library itself
        Symbol symbol = binary.getExport(name); // is export?
        if(symbol != null) {
            if (symbol.type() == SymbolType.SECTION) {
                return loadSection(binary.getSection(symbol.name()));
            }
            return loadSection(symbol.section()) + symbol.offset();
        }

        // resolve import

        for (NativeLibrary resolvingLibrary : resolvingLibraries) {
            long addr = resolvingLibrary.findEntry(name);
            if(addr != 0) {
                return addr;
            }
        }
        return 0;
    }

    long loadSection(Section section) {
        if(section == null) {
            return 0;
        }

        long address = loadedSections.getOrDefault(section.name(), 0L);
        if(address != 0) {
            return address;
        }

        try {
            address = NativeAccess.mmap(0, section.size(),
                    NativeAccess.PROT_EXEC | NativeAccess.PROT_READ | NativeAccess.PROT_WRITE,
                    NativeAccess.MAP_PRIVATE | NativeAccess.MAP_ANONYMOUS, -1, 0);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        unsafe.writeBytes(section.data(), address);

        loadedSections.put(section.name(), address);

        return address;
    }


    public Map<String, Long> load() {
        Map<String, Long> functions = new HashMap<>();

        // first we load all relevant sections into memory
        // check for .rodata, .data, .text

        // now we need to solve all relocations
        for (Relocation relocation : binary.getRelocations()) {
            long target = loadSection(binary.getSection(relocation.target()));
            long targetAddress = target + relocation.address();
            long symbolAddress;

            if(relocation.symbol().type() == SymbolType.SECTION) {
                symbolAddress = loadSection(binary.getSection(relocation.symbol().name()));
            } else {
                symbolAddress = findSymbol(relocation.symbol().name());
            }

            long relocAddr = 0;

            switch (relocation.type()) {
                case PC:
                case PLT:
                    relocAddr = (symbolAddress + relocation.addend() - targetAddress);
                    break;
            }

            switch (relocation.size()) {
                case Relocation.REL8:
                    unsafe.putByte(targetAddress, (byte) relocAddr);
                    break;
                case Relocation.REL16:
                    unsafe.putShort(targetAddress, (short) relocAddr);
                    break;
                case Relocation.REL32:
                    unsafe.putInt(targetAddress, (int) relocAddr);
                    break;
                case Relocation.REL64:
                    unsafe.putLong(targetAddress, relocAddr);
                    break;
            }
        }

        for (Symbol export : binary.getExports()) {
            if(export.type() == SymbolType.FUNCTION) {
                functions.put(export.name(), findSymbol(export.name()));
            }
        }

        return functions;
    }

}
