package me.darknet.oop;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Dumpable {

    void dump(DataOutputStream out) throws IOException;

}
