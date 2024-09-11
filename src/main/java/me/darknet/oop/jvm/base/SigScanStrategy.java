package me.darknet.oop.jvm.base;

import me.darknet.oop.jvm.BaseObtainStrategy;
import me.darknet.oop.library.Library;
import me.darknet.oop.util.*;

public class SigScanStrategy implements BaseObtainStrategy {

    private static final SigScan jumpSigScan = new SigScan("e9 ?? ?? ?? ??", 0x200);
    private static final SigScan dlsymSigScan = new SigScan("<0100??00> 89 <11???110> <0100??00> 89 <11???111> e8 ?? ?? ?? ??", 0x200);
    private static final SigScan indirectJumpScan = new SigScan("ff 25 ?? ?? ?? ??", 0x200);
    private static final Unsafe unsafe = UnsafeAccessor.getUnsafe();

    private static long base;

    @Override
    public long getBase() {
        if(base != 0) {
            return base;
        }
        try {
            int version = Util.getJavaVersion();
            long jvmBase = new ThreadStrategy().getBase();
            long findLibraryEntry = Libraries.findJvmLib().getExport("JVM_FindLibraryEntry").offset() + jvmBase;
            long dlsym = 0;
            if (version >= 11) {
                // jmp os::dll_lookup
                long jmp = jumpSigScan.scan(findLibraryEntry);
                long osDllLookup = (jmp + 5) + unsafe.getInt(jmp + 1); // pc + ????????

                // jmp dlsym@plt
                long jmpDlsym = jumpSigScan.scan(osDllLookup);
                long dlsymPlt = (jmpDlsym + 5) + unsafe.getInt(jmpDlsym + 1); // pc + ????????

                // jmp *dlsym@got.plt
                long jmpDlsymGotPlt = indirectJumpScan.scan(dlsymPlt);
                long dlsymGotAddr = (jmpDlsymGotPlt + 6) + unsafe.getInt(jmpDlsymGotPlt + 2); // pc + ????????

                // follow indirection
                dlsym = unsafe.getAddress(dlsymGotAddr);
            } else if (version >= 8) {
                // lookup first jump to function
                long jumpRel = jumpSigScan.scan(findLibraryEntry);

                long jumpAbs = jumpRel + 5 + unsafe.getInt(jumpRel + 1);

                long dlsymCallRel = dlsymSigScan.scan(jumpAbs) + 6;

                // abs = rel + <mov r64, r64> + <mov r64, r64> + e8
                long dlsymCallAbs = dlsymCallRel + 5 + unsafe.getInt(dlsymCallRel + 1);

                // this leads us to a jump table

                int jump = unsafe.getInt(dlsymCallAbs + 2); // 0x25ff

                // jmp [rip + jump]
                // so we adjust the jump
                long dlSymJumpAbs = dlsymCallAbs + 6 + jump;

                dlsym = unsafe.getAddress(dlSymJumpAbs);
            }

            Library libc = Libraries.findLibCLib();

            base = dlsym - libc.getExport("dlsym").offset();

            return base;
        } catch (Throwable thr) {
            throw new RuntimeException(thr);
        }
    }
}
