package me.darknet.oop.jvm;

import me.darknet.oop.jvm.base.ThreadStrategy;

public interface BaseObtainStrategy {

    static BaseObtainStrategy getStrategy() {
        return new ThreadStrategy();
    }

    long getBase();

}
