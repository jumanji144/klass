package me.darknet.oop.data;

public class MarkOop {

    private final static byte age_bits = 4;
    private final static byte lock_bits = 2;
    private final static byte biased_lock_bits = 1;
    private final static byte max_hash_bits = 31 - age_bits - lock_bits - biased_lock_bits;
    private final static byte cms_bits = 1;
    private final static byte epoch_bits = 2;
    // shift
    private final static byte lock_shift = 0;
    private final static byte biased_lock_shift = lock_bits;
    private final static byte age_shift = lock_bits + biased_lock_bits;
    private final static byte cms_shift = age_shift + age_bits;
    private final static byte hash_shift = cms_shift + cms_bits;
    private final static byte epoch_shift = hash_shift;
    // masks
    private final static long lock_mask = ((1 << lock_bits) - 1) << lock_shift;
    private final static long lock_mask_in_place = lock_mask << lock_shift;
    private final static long biased_lock_mask = ((1 << biased_lock_bits) - 1) << biased_lock_shift;
    private final static long biased_lock_mask_in_place = biased_lock_mask << biased_lock_shift;
    private final static long biased_lock_bit_in_place = 1 << biased_lock_shift;
    private final static long age_mask = ((1 << age_bits) - 1) << age_shift;
    private final static long age_mask_in_place = age_mask << age_shift;
    private final static long epoch_mask = ((1 << epoch_bits) - 1) << epoch_shift;
    private final static long epoch_mask_in_place = epoch_mask << epoch_shift;
    private final static long cms_mask = ((1 << cms_bits) - 1) << cms_shift;
    private final static long cms_mask_in_place = cms_mask << cms_shift;

}
