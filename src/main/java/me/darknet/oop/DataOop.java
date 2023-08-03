package me.darknet.oop;

import me.darknet.oop.data.Struct;

import java.util.ArrayList;
import java.util.List;

public abstract class DataOop<T> extends Oop {

    protected long dataBase;
    protected int length;

    public DataOop(long base, long dataBase, Struct struct) {
        super(base, struct);
        this.dataBase = dataBase;
        this.length = getLength();
    }

    public abstract int getLength();

    public abstract T get(int index);

    public abstract void set(int index, T value);

    public List<T> getAll() {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add(get(i));
        }
        return list;
    }

    public T[] getArray(T[] array) {
        for (int i = 0; i < length; i++) {
            array[i] = get(i);
        }
        return array;
    }

}
