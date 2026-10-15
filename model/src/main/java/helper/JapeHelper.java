package helper;

import br.com.sankhya.jape.core.JapeSession;

public class JapeHelper {

    public static void executeWithTX(JapeRunnable runnable) throws Exception {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            hnd.execWithTX(runnable::run);
        } finally {
            if (hnd != null) {
                JapeSession.close(hnd);
            }
        }
    }

    public static void executeWithoutTX(JapeRunnable runnable) throws Exception {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            runnable.run();
        } finally {
            if (hnd != null) {
                JapeSession.close(hnd);
            }
        }
    }

    public static <T> T executeWithoutTX(JapeCallable<T> callable) throws Exception {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            return callable.call();
        } finally {
            if (hnd != null) {
                JapeSession.close(hnd);
            }
        }
    }

    @FunctionalInterface
    public interface JapeRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    public interface JapeCallable<T> {
        T call() throws Exception;
    }
}
