package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBException;

import java.util.List;

/**
 * Created by zangyilin on 2017/10/13.
 */
public abstract class JBApiStatListCallback {
    public abstract void done(boolean success, List<Long> list, JBException e);
}
