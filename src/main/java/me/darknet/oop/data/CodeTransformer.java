package me.darknet.oop.data;

import me.darknet.oop.jvm.Array;
import me.darknet.oop.klass.ConstantPool;

import java.util.Arrays;
import java.util.Map;

public class CodeTransformer implements Opcodes, ReservedOpcodes {

    public static short readShort(byte[] code, int index, boolean bigEndian) {
        if(bigEndian) {
            return (short) (((code[index] & 0xff) << 8) | (code[index + 1] & 0xff));
        } else {
            return (short) (((code[index + 1] & 0xff) << 8) | (code[index] & 0xff));
        }
    }

    public static int readInt(byte[] code, int index, boolean bigEndian) {
        if(bigEndian) {
            return readShort(code, index, true) << 16 | readShort(code, index + 2, true);
        }
        return readShort(code, index + 2, false) << 16 | readShort(code, index, false);
    }

    public static void writeShort(byte[] code, int index, short value, boolean bigEndian) {
        if(bigEndian) {
            code[index] = (byte) (value >> 8);
            code[index + 1] = (byte) value;
        } else {
            code[index] = (byte) value;
            code[index + 1] = (byte) (value >> 8);
        }
    }

    public static void writeInt(byte[] code, int index, int value, boolean bigEndian) {
        if(bigEndian) {
            writeShort(code, index, (short) (value >> 16), true);
            writeShort(code, index + 2, (short) value, true);
        } else {
            writeShort(code, index, (short) value, false);
            writeShort(code, index + 2, (short) (value >> 16), false);
        }
    }

    /**
     * Remap rewritten instructions
     */
    public static void transform(ConstantPool pool, byte[] code) {
        if(code.length > 65565) throw new IllegalArgumentException("Code too large");
        for (int i = 0; i < code.length; i++) {
            int opcode = code[i] & 0xff;
            System.out.println(i + ": " + opcode);
            switch (opcode) {
                // zero operand
                case NOP:
                case ACONST_NULL:
                case ICONST_M1:
                case ICONST_0:
                case ICONST_1:
                case ICONST_2:
                case ICONST_3:
                case ICONST_4:
                case ICONST_5:
                case LCONST_0:
                case LCONST_1:
                case FCONST_0:
                case FCONST_1:
                case FCONST_2:
                case DCONST_0:
                case DCONST_1:
                case ILOAD_0:
                case ILOAD_1:
                case ILOAD_2:
                case ILOAD_3:
                case LLOAD_0:
                case LLOAD_1:
                case LLOAD_2:
                case LLOAD_3:
                case FLOAD_0:
                case FLOAD_1:
                case FLOAD_2:
                case FLOAD_3:
                case DLOAD_0:
                case DLOAD_1:
                case DLOAD_2:
                case DLOAD_3:
                case ALOAD_0:
                case ALOAD_1:
                case ALOAD_2:
                case ALOAD_3:
                case IALOAD:
                case LALOAD:
                case FALOAD:
                case DALOAD:
                case AALOAD:
                case BALOAD:
                case CALOAD:
                case SALOAD:
                case RET:
                case ISTORE_0:
                case ISTORE_1:
                case ISTORE_2:
                case ISTORE_3:
                case LSTORE_0:
                case LSTORE_1:
                case LSTORE_2:
                case LSTORE_3:
                case FSTORE_0:
                case FSTORE_1:
                case FSTORE_2:
                case FSTORE_3:
                case DSTORE_0:
                case DSTORE_1:
                case DSTORE_2:
                case DSTORE_3:
                case ASTORE_0:
                case ASTORE_1:
                case ASTORE_2:
                case ASTORE_3:
                case IASTORE:
                case LASTORE:
                case FASTORE:
                case DASTORE:
                case AASTORE:
                case BASTORE:
                case CASTORE:
                case SASTORE:
                case POP:
                case POP2:
                case DUP:
                case DUP_X1:
                case DUP_X2:
                case DUP2:
                case DUP2_X1:
                case DUP2_X2:
                case SWAP:
                case IADD:
                case LADD:
                case FADD:
                case DADD:
                case ISUB:
                case LSUB:
                case FSUB:
                case DSUB:
                case IMUL:
                case LMUL:
                case FMUL:
                case DMUL:
                case IDIV:
                case LDIV:
                case FDIV:
                case DDIV:
                case IREM:
                case LREM:
                case FREM:
                case DREM:
                case INEG:
                case LNEG:
                case FNEG:
                case DNEG:
                case ISHL:
                case LSHL:
                case ISHR:
                case LSHR:
                case IUSHR:
                case LUSHR:
                case IAND:
                case LAND:
                case IOR:
                case LOR:
                case IXOR:
                case LXOR:
                case I2L:
                case I2F:
                case I2D:
                case L2I:
                case L2F:
                case L2D:
                case F2I:
                case F2L:
                case F2D:
                case D2I:
                case D2L:
                case D2F:
                case I2B:
                case I2C:
                case I2S:
                case LCMP:
                case FCMPL:
                case FCMPG:
                case DCMPL:
                case DCMPG:
                case IRETURN:
                case LRETURN:
                case FRETURN:
                case DRETURN:
                case ARETURN:
                case RETURN:
                case ARRAYLENGTH:
                case ATHROW:
                case MONITORENTER:
                case MONITOREXIT:
                case breakpoint:
                    break;
                // one operand
                case ILOAD:
                case LLOAD:
                case FLOAD:
                case DLOAD:
                case ALOAD:
                case ISTORE:
                case LSTORE:
                case FSTORE:
                case DSTORE:
                case ASTORE:
                case BIPUSH:
                case NEWARRAY:
                case LDC:
                    i++;
                    break;
                // two operands
                case ANEWARRAY:
                case CHECKCAST:
                case INSTANCEOF:
                case NEW:
                case GOTO:
                case JSR:
                case IFNULL:
                case IFNONNULL:
                case IFEQ:
                case IFNE:
                case IFLT:
                case IFGE:
                case IFGT:
                case IFLE:
                case IF_ICMPEQ:
                case IF_ICMPNE:
                case IF_ICMPLT:
                case IF_ICMPGE:
                case IF_ICMPGT:
                case IF_ICMPLE:
                case IF_ACMPEQ:
                case IF_ACMPNE:
                case SIPUSH:
                case IINC:
                case LDC_W:
                case LDC2_W:
                    i++;
                    i++;
                    break;
                // four operands
                case GOTO_W:
                case JSR_W:
                    i++;
                    i++;
                    i++;
                    i++;
                    break;
                // call instructions
                case INVOKEVIRTUAL:
                case INVOKESTATIC:
                case INVOKEINTERFACE:
                case GETFIELD:
                case GETSTATIC:
                case PUTFIELD:
                case PUTSTATIC:
                case INVOKESPECIAL: {
                    // rewrite
                    short index = readShort(code, i + 1, false);
                    short refIndex = pool.getRefIndex(index);
                    writeShort(code, i + 1, refIndex, true);
                    i += 2;
                    break;
                }
                // lookupswitch
                case fast_linearswitch:
                case fast_binaryswitch:
                    code[i] = (byte) LOOKUPSWITCH;
                case LOOKUPSWITCH: {
                    // padding
                    i += 4 - (i & 3);
                    // default
                    i+=4;
                    // npairs
                    int npairs = readInt(code, i, true);
                    i += 4;
                    // pairs
                    i += npairs * 8;
                    break;
                }
                // tableswitch
                case TABLESWITCH: {
                    // padding
                    i += 4 - (i & 3);
                    // default
                    i+=4;
                    // low
                    i+=4;
                    // high
                    i+=4;
                    // offsets
                    int high = readInt(code, i - 4, true);
                    i += high * 4;
                    break;
                }
                case INVOKEDYNAMIC: {
                    i += 4;
                    break;
                }
                // multianewarray
                case MULTIANEWARRAY: {
                    i += 3;
                    break;
                }
                case fast_aload_0: {
                    code[i] = ALOAD_0;
                    break;
                }
                case fast_aaccess_0:
                case fast_iaccess_0: {
                    code[i] = (byte) (opcode == fast_aaccess_0 ? ALOAD_0 : ILOAD_0);
                    code[i + 1] = (byte) GETFIELD;
                    short index = readShort(code, i + 2, false);
                    short refIndex = pool.getRefIndex(index);
                    writeShort(code, i + 2, refIndex, true);
                    i+=3;
                    break;
                }
                case fast_iload: {
                    code[i] = ILOAD;
                    i++;
                    break;
                }
                case fast_iload2: {
                    code[i] = ILOAD;
                    code[i + 2] = ILOAD;
                    i += 3;
                    break;
                }
                case fast_icaload: {
                    code[i] = ILOAD;
                    code[i + 2] = CALOAD;
                    i += 3;
                    break;
                }
                case fast_aldc: {
                    code[i] = LDC;
                    code[i + 1] = (byte) pool.getStringIndex(code[i + 1]);
                    i++;
                    break;
                }
                case fast_aldc_w: {
                    code[i] = LDC_W;
                    short index = readShort(code, i + 1, false);
                    short refIndex = pool.getStringIndex(index);
                    writeShort(code, i + 1, refIndex, true);
                    i += 2;
                    break;
                }
                case fast_agetfield:
                case fast_bgetfield:
                case fast_cgetfield:
                case fast_dgetfield:
                case fast_fgetfield:
                case fast_igetfield:
                case fast_lgetfield:
                case fast_aputfield:
                case fast_zputfield:
                case fast_bputfield:
                case fast_cputfield:
                case fast_dputfield:
                case fast_fputfield:
                case fast_iputfield:
                case fast_lputfield: {
                    code[i] = (byte) (opcode >= fast_aputfield ? PUTFIELD : GETFIELD);
                    short index = readShort(code, i + 1, false);
                    short i2 = pool.getRefIndex(index);
                    writeShort(code, i + 1, i2, true);
                    i += 2;
                    break;
                }

                case invokehandle: {
                    code[i] = (byte) INVOKEVIRTUAL;
                    short index = readShort(code, i + 1, false);
                    short i2 = pool.getRefIndex(index);
                    writeShort(code, i + 1, i2, true);
                    i += 2;
                    break;
                }
            }
        }
    }

}
