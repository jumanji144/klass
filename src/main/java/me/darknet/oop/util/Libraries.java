package me.darknet.oop.util;

import me.darknet.oop.library.Library;
import me.darknet.oop.library.NativeLibrary;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class Libraries {

    public static final MethodHandles.Lookup LOOKUP;
    private static final NativeLibraryLoader NATIVE_LIBRARY_LOADER;

    private static Library libc;
    private static Library libjvm;

    private Libraries() {
    }

    public static Library getLibC() {
        if(libc == null) {
            try {
                libc = findLibCLib();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return libc;
    }

    public static Library getLibJvm() {
        if(libjvm == null) {
            try {
                libjvm = findJvmLib();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return libjvm;
    }

    public static NativeLibrary findJvm() throws Throwable {
        return new NativeLibrary() {
            @Override
            public long findEntry(String entry) {
                return getLibJvm().getExportAddr(entry);
            }

            @Override
            public Library getLibrary() {
                return getLibJvm();
            }
        };
    }

    public static Library findJvmLib() throws IOException {
        Path jvmDir = Paths.get(System.getProperty("java.home"));
        Path maybeJre = jvmDir.resolve("jre");
        if (Files.isDirectory(maybeJre)) {
            jvmDir = maybeJre;
        }
        jvmDir = jvmDir.resolve("bin");
        if(!Files.exists(jvmDir.resolve("lib"))) {
            jvmDir = jvmDir.getParent();
        }
        String os = System.getProperty("os.name").toLowerCase();
        Path pathToJvm;
        if (os.contains("win")) {
            pathToJvm = findFirstFile(jvmDir, "server/jvm.dll", "client/jvm.dll");
        } else if (os.contains("nix") || os.contains("nux")) {
            pathToJvm = findFirstFile(jvmDir, "lib/amd64/server/libjvm.so", "lib/i386/server/libjvm.so", "lib/server/libjvm.so");
        } else {
            throw new RuntimeException("Unsupported OS (probably MacOS X): " + os);
        }
        return Library.of(pathToJvm.normalize().toString());
    }

    public static NativeLibrary findLibC() {
        return new NativeLibrary() {
            @Override
            public long findEntry(String entry) {
                return getLibC().getExportAddr(entry);
            }

            @Override
            public Library getLibrary() {
                return getLibC();
            }
        };
    }

    public static Library findLibCLib() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        Path pathToLibC;
        if(os.contains("win")) {
            pathToLibC = Paths.get("C:\\Windows\\System32\\msvcrt.dll");
        } else if(os.contains("nix") || os.contains("nux")) {
            String arch = System.getProperty("os.arch");
            switch (arch) {
                case "amd64":
                case "x86_64":
                    arch = "x86_64";
                    break;
                case "i386":
                case "x86":
                    arch = "i386";
                    break;
            }
            pathToLibC = Paths.get("/usr/lib/" + arch + "-linux-gnu/");
            pathToLibC = findFirstFile(pathToLibC, "libc.so.6", "libc.so", "libgcc_s.so.1");
        } else {
            throw new RuntimeException("Unsupported OS (probably MacOS X): " + os);
        }
        return Library.of(pathToLibC.normalize().toString());
    }

    public static NativeLibrary findLibrary(String path) throws Throwable {
        return NATIVE_LIBRARY_LOADER.loadLibrary(path);
    }

    public static Library loadResource(String resourcePath) {
        InputStream is = Libraries.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }

        try {
            int available = is.available();
            byte[] data = new byte[available];
            if(available != is.read(data)) {
                throw new RuntimeException("Failed to read all bytes from resource: " + resourcePath);
            }
            is.close();
            return Library.of(data);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static Library loadFile(String path) throws IOException {
        return Library.of(path);
    }

    private static Path findFirstFile(Path directory, String... files) {
        for (String file : files) {
            Path path = directory.resolve(file);
            if (Files.exists(path)) return path;
        }
        throw new RuntimeException("Failed to find one of the required paths!: " + directory.toAbsolutePath() + Arrays.toString(files));
    }

    static {
        try {
            Unsafe unsafe = UnsafeAccessor.getUnsafe();
            MethodHandles.publicLookup();
            Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            LOOKUP = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(field),
                    unsafe.staticFieldOffset(field));
            NATIVE_LIBRARY_LOADER = null;
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    private static abstract class NativeLibraryLoader {

        protected static final Class<?> CL_NATIVE_LIBRARY;
        protected static final MethodHandle CNSTR_NATIVE_LIBRARY;

        abstract NativeLibrary loadLibrary(String path) throws Throwable;

        static {
            try {
                CL_NATIVE_LIBRARY = Class.forName("java.lang.ClassLoader$NativeLibrary", true, null);
                CNSTR_NATIVE_LIBRARY = LOOKUP.findConstructor(CL_NATIVE_LIBRARY, MethodType.methodType(Void.TYPE, Class.class, String.class, Boolean.TYPE));
            } catch (Throwable t) {
                throw new ExceptionInInitializerError(t);
            }
        }
    }

    private static long baseAddress = 0;

    public static long getLibJvmBaseAddress() {
        if(baseAddress == 0) {
            try {
                NativeLibrary libjvm = Libraries.findJvm();
                baseAddress =  libjvm.findEntry("JVM_DefineClassWithSource")
                        - libjvm.getLibrary().getExport("JVM_DefineClassWithSource").offset();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return 0;
            }
        }
        return baseAddress;
    }
}