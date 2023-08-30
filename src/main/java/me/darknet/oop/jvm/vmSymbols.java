package me.darknet.oop.jvm;

import me.darknet.oop.data.Field;
import me.darknet.oop.data.Struct;
import me.darknet.oop.klass.Symbol;
import me.darknet.oop.types.Types;
import me.darknet.oop.util.UnsafeAccessor;

public class vmSymbols {

    private static final Struct vmSymbols = new Struct("vmSymbols");

    private static final Field symbols0 = vmSymbols.getStaticField("_symbols[0]"); // Symbol*

    public static Symbol getSymbol(int index) {
        return Symbol.of(UnsafeAccessor.getUnsafe().getAddress(
                symbols0.address() + ((long) index * Types.getSize("Symbol*"))));
    }

}
