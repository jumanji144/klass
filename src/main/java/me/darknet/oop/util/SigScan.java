package me.darknet.oop.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class SigScan {

    private static final Unsafe unsafe = UnsafeAccessor.getUnsafe();

    private final int maxOffset;
    private final List<Entry> entries;

    public SigScan(String pattern, int maxOffset) {
        this.maxOffset = maxOffset;
        this.entries = compile(pattern);
    }

    public long scan(long base) {
        long offset = base;
        for (int i = 0; i < maxOffset; i++) {
            if (match(offset)) {
                return offset;
            }
            offset++;
        }
        return 0;
    }

    boolean match(long offset) {
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            byte value = unsafe.getByte(offset + i);
            if (!entry.execute(value)) {
                return false;
            }
        }
        return true;
    }

    enum Type {
        KNOWN_BYTE,
        ANY_BYTE,
        BINARY_PATTERN
    }

    List<Entry> compile(String pattern) {
        String[] parts = pattern.split(" ");
        List<Entry> entries = new ArrayList<>();
        for (String part : parts) {
            Entry current = new Entry();
            char first = part.charAt(0);
            switch (first) {
                case '?': current.type = Type.ANY_BYTE; break;
                case '<': { // pattern is like <110??010>
                    // binary pattern
                    String binary = part.substring(1, part.length() - 1);
                    byte mask = 0;
                    byte compare = 0;
                    for (int i = 0; i < binary.length(); i++) {
                        char c = binary.charAt(i);
                        mask <<= 1;
                        compare <<= 1;
                        if (c == '1') {
                            mask |= 1;
                            compare |= 1;
                        } else if (c == '0') {
                            mask |= 1;
                        }
                    }
                    current.type = Type.BINARY_PATTERN;
                    current.bitMask = mask;
                    current.compare = compare;
                    break;
                }
                default: {
                    current.type = Type.KNOWN_BYTE;
                    current.value = (byte) Integer.parseInt(part, 16);
                    break;
                }
            }
            entries.add(current);
        }
        return entries;
    }

    static class Entry {

        Type type;
        byte value;
        byte bitMask;
        byte compare;

        boolean execute(byte match) {
            switch (type) {
                case ANY_BYTE: return true;
                case KNOWN_BYTE: return value == match;
                case BINARY_PATTERN: {
                    return (match & bitMask) == compare;
                }
                default: return false;
            }
        }

    }

}
