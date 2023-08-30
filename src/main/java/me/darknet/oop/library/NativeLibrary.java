package me.darknet.oop.library;

public interface NativeLibrary {

    long findEntry(String entry);

    Library getLibrary();
}