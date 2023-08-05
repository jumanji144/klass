import me.darknet.oop.VMStructs;
import me.darknet.oop.jvm.OopHandle;
import me.darknet.oop.jvm.ProxyOopHandle;
import me.darknet.oop.klass.ConstantPool;
import me.darknet.oop.klass.ConstantPoolCache;
import me.darknet.oop.klass.InstanceKlass;
import me.darknet.oop.klass.Method;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {

	public static void main(String[] args) throws IOException {
		OopHandle oop = new ProxyOopHandle("Hello");
		InstanceKlass klass = (InstanceKlass) oop.getKlass();
		klass.dump(new DataOutputStream(Files.newOutputStream(Paths.get("String.class"))));
	}

}
