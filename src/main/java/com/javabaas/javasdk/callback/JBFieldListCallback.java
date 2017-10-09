package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBField;

import java.util.List;

/**
 * Created by zangyilin on 2017/9/21.
 */
public abstract class JBFieldListCallback {
    public abstract void done(boolean success, List<JBField> list, JBException e);
}
