package me.darknet.oop.jithack;

public class Blob {

    public String name;
    public String signature;
    public byte[] data;

    public Blob(String name, String signature, byte[] data) {
        this.name = name;
        this.signature = signature;
        this.data = data;
    }

    public String name() {
        return name;
    }

    public String signature() {
        return signature;
    }

    public byte[] data() {
        return data;
    }

}
