package me.darknet.oop.data;

import me.darknet.oop.klass.ConstantPool;

public class CodeTransformer implements Opcodes, ReservedOpcodes {

    /**
     * Remap rewritten instructions
     */
    public static byte[] transform(ConstantPool pool, byte[] code) {
        for (int i = 0; i < code.length; i++) {
            int opcode = code[i] & 0xff;
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
                case GOTO_W:
                case JSR_W:
                case NEWARRAY:
                case ANEWARRAY:
                case CHECKCAST:
                case INSTANCEOF:
                case NEW:
                case LDC:
                    i++;
                    break;
                // two operands
                case BIPUSH:
                case SIPUSH:
                case IINC:
                case LDC_W:
                case LDC2_W:
                    i++;
                    i++;
                    break;
                // call instructions
                case INVOKEVIRTUAL:
                case INVOKESPECIAL:
                case INVOKESTATIC:
                case INVOKEINTERFACE:
                case GETFIELD:
                case GETSTATIC:
                case PUTFIELD:
                case PUTSTATIC: {
                    // rewrite
                    short index = (short) ((code[i + 1] & 0xff) << 8 | (code[i + 2] & 0xff));
                    short refIndex = (short) pool.getRefIndex(index);
                    code[i + 1] = (byte) (refIndex >> 8);
                    code[i + 2] = (byte) (refIndex & 0xff);
                    i += 2;
                    break;
                }
                // lookupswitch
                case LOOKUPSWITCH: {
                    // padding
                    int padding = (i + 1) % 4;
                    if (padding != 0) {
                        padding = 4 - padding;
                    }
                    i += padding;
                    // default
                    i+=4;
                    // npairs
                    int npairs = (code[i + 1] & 0xff) << 24 | (code[i + 2] & 0xff) << 16 | (code[i + 3] & 0xff) << 8 | (code[i + 4] & 0xff);
                    i += 4;
                    // pairs
                    i += npairs * 8;
                    break;
                }
                // tableswitch
                case TABLESWITCH: {
                    // padding
                    int padding = (i + 1) % 4;
                    if (padding != 0) {
                        padding = 4 - padding;
                    }
                    i += padding;
                    // default
                    i+=4;
                    // low
                    i+=4;
                    // high
                    i+=4;
                    // offsets
                    int high = (code[i + 1] & 0xff) << 24 | (code[i + 2] & 0xff) << 16 | (code[i + 3] & 0xff) << 8 | (code[i + 4] & 0xff);
                    i += 4;
                    i += (high + 1) * 4;
                    break;
                }
                // multianewarray
                case MULTIANEWARRAY: {
                    // rewrite
                    short index = (short) ((code[i + 1] & 0xff) << 8 | (code[i + 2] & 0xff));
                    short refIndex = (short) pool.getRefIndex(index);
                    code[i + 1] = (byte) (refIndex >> 8);
                    code[i + 2] = (byte) (refIndex & 0xff);
                    // dimensions
                    i += 3;
                    break;
                }
                case fast_aldc: {
                    code[i] = LDC;
                    code[i + 1] = (byte) pool.getStringIndex(code[i + 1]);
                    i++;
                    break;
                }
            }
        }
        return code;
    }

}
