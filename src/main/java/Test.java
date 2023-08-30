import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import me.darknet.oop.Universe;
import me.darknet.oop.jithack.NativeAccess;
import me.darknet.oop.jvm.NativeOopHandle;
import me.darknet.oop.jvm.ProxyOopHandle;
import me.darknet.oop.klass.*;
import me.darknet.oop.library.NativeLibrary;
import me.darknet.oop.util.*;
import sun.nio.fs.UnixFileSystemProvider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentMap;

public class Test {

	public static void dumpAll() throws IOException {
		Universe universe = Universe.obtainFrom(ConstantPool.class);
		for (Klass klass : universe.getKlasses()) {
			if(klass instanceof InstanceKlass) {
				InstanceKlass instanceKlass = (InstanceKlass) klass;
				Path path = Paths.get("dump/" + klass.getName() + ".class");
				Files.createDirectories(path.getParent());
				System.out.println("Dumping " + klass.getName());
				instanceKlass.dump(new DataOutputStream(Files.newOutputStream(path)));
			}
		}
	}

	public static void dump(Class<?> base, String name) throws IOException {
		Universe universe = Universe.obtainFrom(base);
		InstanceKlass klass = (InstanceKlass) universe.findKlass(name);

		Path path = Paths.get("dump/" + klass.getName() + ".class");

		Files.createDirectories(path.getParent());
		System.out.println("Dumping " + klass.getName());

		klass.dump(new DataOutputStream(Files.newOutputStream(path)));
	}

	public static void main(String[] args) throws IOException {

		dumpAll();

	}

}
