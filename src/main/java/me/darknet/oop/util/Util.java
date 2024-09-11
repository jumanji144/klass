package me.darknet.oop.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Util {

    private static int cachedVersion = -1;
    private static String cachedVmName = null;

    public static int getJavaVersion() {
        if(cachedVersion != -1) {
            return cachedVersion;
        }
        String javaVersion = System.getProperty("java.version");
        if(javaVersion.contains(".")) {
            int first = Integer.parseInt(javaVersion.substring(0, javaVersion.indexOf(".")));
            if(first == 1) {
                javaVersion = javaVersion.substring(javaVersion.indexOf(".") + 1);
            }
            cachedVersion = Integer.parseInt(javaVersion.substring(0, javaVersion.indexOf(".")));
            return cachedVersion;
        }
        cachedVersion = Integer.parseInt(javaVersion);
        return cachedVersion;
    }

    public static String getVMType() {
        if(cachedVmName != null) {
            return cachedVmName;
        }
        String vmName = System.getProperty("java.vm.name");
        if(vmName.contains(" ")) {
            vmName = vmName.substring(0, vmName.indexOf(" "));
        }
        cachedVmName = vmName;
        return cachedVmName.toLowerCase();
    }


    public static String readStream(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        StringBuilder builder = new StringBuilder();
        while((read = in.read(buffer)) != -1) {
            builder.append(new String(buffer, 0, read));
        }
        return builder.toString();
    }

    public static byte[] readStreamBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    public static byte[] memoryView(long pointer, int size) {
        byte[] bytes = new byte[size];
        for(int i = 0; i < bytes.length; i++) {
            bytes[i] = UnsafeAccessor.getUnsafe().getByte(pointer + i);
        }
        return bytes;
    }

    public static void memoryDump(Path path, long pointer, int size) throws IOException {
        byte[] bytes = memoryView(pointer, size);
        Files.write(path, bytes);
    }

    public static String readCString(long base) {
        // read until null terminator
        StringBuilder builder = new StringBuilder();
        byte b;
        while((b = UnsafeAccessor.getUnsafe().getByte(base++)) != 0) {
            builder.append((char) b);
        }

        return builder.toString();
    }

    public static Os getOs() {
        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("win")) {
            return Os.WINDOWS;
        } else if(os.contains("mac")) {
            return Os.MAC;
        } else if(os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return Os.LINUX;
        }
        return Os.LINUX;
    }

    public static boolean is64Bit() {
        return System.getProperty("sun.arch.data.model").equals("64");
    }

    public static boolean isLittleEndian() {
        return System.getProperty("sun.cpu.endian").equals("little");
    }
}
