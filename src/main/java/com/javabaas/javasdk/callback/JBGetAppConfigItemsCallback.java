package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBException;

import java.util.List;
import java.util.Map;

/**
 * Created by zangyilin on 2018/8/24.
 */
public abstract class JBGetAppConfigItemsCallback {
    public abstract void done(boolean success, List<Map<String, String>> list, JBException e);
}
