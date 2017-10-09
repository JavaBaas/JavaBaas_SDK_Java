package com.javabaas.javasdk.callback;


import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBObject;

import java.util.List;

/**
 * Created by zangyilin on 2017/9/7.
 */
public abstract class JBFindCallBack<T extends JBObject> {
    public abstract void done(boolean success, List<T> objects, JBException e);
}
