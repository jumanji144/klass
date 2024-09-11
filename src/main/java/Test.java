import me.darknet.oop.Universe;
import me.darknet.oop.jithack.Linker;
import me.darknet.oop.jithack.NativeAccess;
import me.darknet.oop.jithack.NativeInstall;
import me.darknet.oop.jvm.Flag;
import me.darknet.oop.jvm.base.SigScanStrategy;
import me.darknet.oop.jvm.base.ThreadStrategy;
import me.darknet.oop.klass.*;
import me.darknet.oop.library.Library;
import me.darknet.oop.util.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

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

	public static void test() {}

	public static void main(String[] args) throws Throwable {

	}

}
