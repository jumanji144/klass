package me.darknet.oop.klass;

import me.darknet.oop.Oop;
import me.darknet.oop.Structs;
import me.darknet.oop.data.Struct;

public class InvocationCounter extends Oop {

    private static final int _number_of_state_bits = 2;
    private static final int _number_of_states = 1 << _number_of_state_bits;
    private static final int _number_of_carry_bits = 1;
    private static final int _number_of_counter_bits = 32 - _number_of_state_bits - _number_of_carry_bits;
    // right_n_bits
    private static final int status_mask = (1 << _number_of_state_bits + _number_of_carry_bits) - 1;

    private static final int counter_mask = ~status_mask;
    private static final int carry_mask = (1 << _number_of_carry_bits) - 1;
    // nth_bit(number_of_state_bits + number_of_carry_bits)
    private static final int counter_increment = 1 << (_number_of_state_bits + _number_of_carry_bits);

    public InvocationCounter(long base) {
        super(base, Structs.invocationCounter);
    }

    private int getCount() {
        return struct.getInt(base, "_count");
    }

    public int count() {
        return getCount() & counter_mask;
    }

    public int state() {
        return getCount() & status_mask;
    }

    public int carry() {
        return getCount() & carry_mask;
    }

    public void putCount(int value) {
        struct.putInt(base, "_count", value);
    }

    public void putCount(int count, int state, int carry) {
        putCount((count & counter_mask) | (state & status_mask) | (carry & carry_mask));
    }

    public void increment() {
        putCount(getCount() + counter_increment);
    }

    public void decrement() {
        putCount(getCount() - counter_increment);
    }

}
