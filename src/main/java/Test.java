import me.darknet.oop.klass.InstanceKlass;
import me.darknet.oop.types.Types;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test {

	/*public static List<Klass> getLoadedKlasses() {
		List<Klass> klasses = new ArrayList<>();
	    ClassLoaderData walker = new InstanceKlass(Test.class).getClassLoaderData();
		while (walker.getBase() != 0) {
			Klass klassWalker = walker.getKlasses();
			while (klassWalker.getBase() != 0) {
				klasses.add(klassWalker);
				klassWalker = klassWalker.getNextLink();
			}
			walker = walker.getNext();
		}
		return klasses;
	}*/

	public static void main(String[] args) throws IOException {
		Types.initialize();
		InstanceKlass klass = new InstanceKlass(Test.class);
		File file = new File("Test.class");
		klass.dump(new DataOutputStream(new FileOutputStream(file)));
		System.out.println("Wrote class to " + file.getAbsolutePath());
	}

}
