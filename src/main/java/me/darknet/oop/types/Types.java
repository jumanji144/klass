package me.darknet.oop.types;

import me.darknet.oop.VMStructs;
import me.darknet.oop.util.Util;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Types {

    private static boolean initialized = false;

    private static final Map<String, Type> types = new HashMap<>();
    private static final Map<String, Field> fields = new HashMap<>();
    private static final Map<String, Constant> constants = new HashMap<>();

    public static Type getType(String name) {
        return types.get(name);
    }

    public static int getSize(String name) {
        Type type = getType(name);
        if(type == null)
            return 0;
        return type.size;
    }

    public static Field getField(String name) {
        return fields.get(name);
    }

    public static long getOffset(String name) {
        Field field = getField(name);
        if(field == null)
            return -1L;
        return field.offset;
    }

    public static Constant getConstant(String name) {
        return constants.get(name);
    }

    public static long getValue(String name) {
        Constant constant = getConstant(name);
        if(constant == null)
            return -1L;
        return constant.value;
    }

    public static void initialize() throws IOException {
        int version = Util.getJavaVersion();
        String vmType = Util.getVMType();

        String structDef = "vmstructs_" + vmType + "_" + version + ".structs";

        InputStream in = Types.class.getResourceAsStream("/vmstructs/" + structDef);
        if (in == null) {
            throw new RuntimeException("Could not find " + structDef);
        }

        parseStructDef(in);
    }

    public static void parseStructDef(InputStream stream) throws IOException {
        StreamTokenizer t = new StreamTokenizer(new BufferedReader(new InputStreamReader(stream)));
        t.resetSyntax();
        t.wordChars('\u0000', '\uFFFF');
        t.whitespaceChars(' ', ' ');
        t.whitespaceChars('\t', '\t');
        t.whitespaceChars('\n', '\n');
        t.quoteChar('"');
        t.eolIsSignificant(true);
        while (t.nextToken() != StreamTokenizer.TT_EOF) {
            if (t.ttype == StreamTokenizer.TT_EOL) {
                continue;
            }

            String type = t.sval;

            switch (type) {
                case "field": {
                    t.nextToken();
                    String containingType = t.sval;
                    t.nextToken();
                    String fieldName = t.sval;

                    // The field's Type must already be in the database -- no exceptions
                    t.nextToken();
                    String fieldType = t.sval;
                    t.nextToken();
                    boolean isStatic = Boolean.parseBoolean(t.sval);
                    t.nextToken();
                    long offset = Long.parseLong(t.sval);
                    t.nextToken();
                    long staticAddress = Long.parseLong(t.sval.substring(2), 16);

                    // lookup field type
                    Type typeObj = types.get(fieldType);

                    // create field object
                    Field field = new Field();
                    field.name = containingType + "::" + fieldName;
                    field.type = typeObj;
                    field.isStatic = isStatic;
                    field.offset = isStatic ? staticAddress : offset;

                    // add field to database
                    fields.put(field.name, field);
                    break;
                }
                case "type": {
                    t.nextToken();
                    String typeName = t.sval;
                    t.nextToken();
                    String superclassName = t.sval;
                    if (superclassName.equals("null")) {
                        superclassName = null;
                    }

                    t.nextToken();
                    boolean isOop = Boolean.parseBoolean(t.sval);
                    t.nextToken();
                    boolean isInteger = Boolean.parseBoolean(t.sval);
                    t.nextToken();
                    boolean isUnsigned = Boolean.parseBoolean(t.sval);
                    t.nextToken();
                    long size = Long.parseLong(t.sval);

                    // create type object
                    Type typeObj = new Type();
                    typeObj.name = typeName;
                    typeObj.superType = superclassName == null ? null : types.get(superclassName);
                    typeObj.isOop = isOop;
                    typeObj.isInteger = isInteger;
                    typeObj.isUnsigned = isUnsigned;
                    typeObj.size = (int) size;

                    // add type to database
                    types.put(typeName, typeObj);
                    break;
                }
                case "constant": {
                    t.nextToken();
                    String superClassName = t.sval;
                    t.nextToken();
                    String constantName = t.sval;
                    t.nextToken();
                    String constantType = t.sval;
                    t.nextToken();
                    long value = new BigInteger(t.sval.substring(2), 16).longValue();

                    // lookup constant type
                    Type typeObj = types.get(constantType);

                    // create constant object
                    Constant constant = new Constant();
                    constant.name = constantName;
                    if (Objects.equals(superClassName, "null")) {
                        superClassName = null;
                    } else constant.superType = types.get(superClassName);
                    constant.type = typeObj;
                    constant.value = value;

                    // add constant to database
                    String keyName = superClassName == null ? constantName : superClassName + "::" + constantName;
                    constants.put(keyName, constant);
                    break;
                }
                default:
                    throw new InternalError("\"" + t.sval + "\"");
            }
        }
    }

    public static void resolveRuntime() throws IOException {
        String s = VMStructs.dumpVMStructs();
        parseStructDef(new ByteArrayInputStream(s.getBytes()));
        initialized = true;
    }

    public static void resolvePreResolved() throws IOException {
        initialize();
        initialized = true;
    }

    public static void resolveFromFile(String path) throws IOException {
        parseStructDef(new FileInputStream(path));
        initialized = true;
    }
}
