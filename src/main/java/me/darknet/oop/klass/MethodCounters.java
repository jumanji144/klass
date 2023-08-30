package me.darknet.oop.klass;

import me.darknet.oop.Oop;
import me.darknet.oop.Structs;

public class MethodCounters extends Oop {

    public MethodCounters(long base) {
        super(base, Structs.methodCounters);
    }

    public int getInterpreterInvocationCount() {
        return struct.getInt(base, "_interpreter_invocation_count");
    }

    public int getInterpreterBackedgeCount() {
        return struct.getInt(base, "_interpreter_backedge_count");
    }

    public InvocationCounter getInvocationCounter() {
        return new InvocationCounter(base + struct.getOffset("_invocation_counter"));
    }

    public InvocationCounter getBackedgeCounter() {
        return new InvocationCounter(base + struct.getOffset("_backedge_counter"));
    }

    public void incrementInterpreterInvocationCount() {
        struct.putInt(base, "_interpreter_invocation_count", getInterpreterInvocationCount() + 1);
    }

    public void incrementInterpreterBackedgeCount() {
        struct.putInt(base, "_interpreter_backedge_count", getInterpreterBackedgeCount() + 1);
    }

}
