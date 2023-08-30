package me.darknet.oop.jvm;

import me.darknet.oop.data.Field;
import me.darknet.oop.data.Struct;
import me.darknet.oop.types.Types;
import me.darknet.oop.util.Util;

import java.util.HashMap;
import java.util.Map;

public class Flag {

    private static final Struct flag = new Struct("Flag");
    private static final Field flags = flag.getStaticField("flags"); // Flag*
    private static final Field numFlags = flag.getStaticField("numFlags"); // int

    private static final Map<String, Flag> flagMap = new HashMap<>();

    private final Field name;
    private final Field type;
    private final Field addr;

    public Flag(long base) {
        this.name = flag.getField(base, "_name");
        this.type = flag.getField(base, "_type");
        this.addr = flag.getField(base, "_addr");
    }

    public String getName() {
        if(name.getAddress() == 0) {
            return null;
        }
        return Util.readCString(name.getAddress());
    }

    public String getType() {
        if(type.getAddress() == 0) {
            return null;
        }
        return Util.readCString(type.getAddress());
    }

    public Field getValue() {
        return new Field(addr.getAddress(), "");
    }

    private static void readFlags() {

        Flag[] flagArray = new Flag[numFlags.getInt()-1];

        long address = flags.getAddress();

        for(int i = 0; i < flagArray.length; i++) {
            flagArray[i] = new Flag(address);
            address += Types.getSize("Flag");
        }

        for(Flag flag : flagArray) {
            flagMap.put(flag.getName(), flag);
        }

    }

    public static Flag getFlag(String name) {
        if(flagMap.isEmpty()) {
            readFlags();
        }
        return flagMap.get(name);
    }



}
