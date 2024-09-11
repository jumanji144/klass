package me.darknet.oop.jvm;

import me.darknet.oop.Oop;
import me.darknet.oop.Structs;
import me.darknet.oop.data.Struct;

public class JavaThread extends Oop {
    public JavaThread(long base) {
        super(base, Structs.javaThread);
    }

    public long getJNIEnv() {
        return struct.getAddress(base, "_jni_environment");
    }
}
