package me.darknet.oop.types;

public class Field {

    public String name;
    public Type type;
    public boolean isStatic;
    public long offset;

    public long getOffset() {
        return offset;
    }

}
