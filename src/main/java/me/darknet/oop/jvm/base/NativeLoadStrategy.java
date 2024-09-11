package me.darknet.oop.jvm.base;

import me.darknet.oop.jvm.BaseObtainStrategy;
import me.darknet.oop.util.Libraries;

public class NativeLoadStrategy implements BaseObtainStrategy {
    @Override
    public long getBase() {
        return Libraries.getLibJvmBaseAddress();
    }
}
