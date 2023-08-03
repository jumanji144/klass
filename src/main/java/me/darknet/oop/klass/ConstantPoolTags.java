package me.darknet.oop.klass;

public interface ConstantPoolTags {

     int JVM_CONSTANT_Utf8                    = 1;
     int JVM_CONSTANT_Unicode                 = 2; // unused
     int JVM_CONSTANT_Integer                 = 3;
     int JVM_CONSTANT_Float                   = 4;
     int JVM_CONSTANT_Long                    = 5;
     int JVM_CONSTANT_Double                  = 6;
     int JVM_CONSTANT_Class                   = 7;
     int JVM_CONSTANT_String                  = 8;
     int JVM_CONSTANT_Fieldref                = 9;
     int JVM_CONSTANT_Methodref               = 10;
     int JVM_CONSTANT_InterfaceMethodref      = 11;
     int JVM_CONSTANT_NameAndType             = 12;
     int JVM_CONSTANT_MethodHandle            = 15;  // JSR 292
     int JVM_CONSTANT_MethodType              = 16;  // JSR 292
     int JVM_CONSTANT_Dynamic                 = 17;  // JSR 292
     int JVM_CONSTANT_InvokeDynamic           = 18;  // JSR 292
     int JVM_CONSTANT_Invalid                 = 0;   // For bad value initialization
     int JVM_CONSTANT_UnresolvedClass         = 100; // Temporary tag until actual use
     int JVM_CONSTANT_ClassIndex              = 101; // Temporary tag while constructing constant pool
     int JVM_CONSTANT_StringIndex             = 102; // Temporary tag while constructing constant pool
     int JVM_CONSTANT_UnresolvedClassInError  = 103; // Resolution failed
     int JVM_CONSTANT_MethodHandleInError     = 104; // Error tag due to resolution error
     int JVM_CONSTANT_MethodTypeInError       = 105; // Error tag due to resolution error

}
