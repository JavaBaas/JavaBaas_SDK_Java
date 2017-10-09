package com.javabaas.javasdk;

/**
 * Created by zangyilin on 2017/9/4.
 */
public final class JBExceptionHolder {
    private static final ThreadLocal<JBException> LOCAL = ThreadLocal.withInitial(() -> null);

    public final static void add(JBException e) {
        LOCAL.set(e);
    }

    public final static boolean exists() {
        return LOCAL.get() != null;
    }

    public final static JBException remove() {
        JBException e = LOCAL.get();
        LOCAL.remove();
        return e;
    }
}
