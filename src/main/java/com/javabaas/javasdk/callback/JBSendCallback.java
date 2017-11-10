package com.javabaas.javasdk.callback;

import com.javabaas.javasdk.JBException;
import com.javabaas.javasdk.JBSms;

/**
 * Created by zangyilin on 2017/11/10.
 */
public abstract class JBSendCallback {
    public abstract void done(boolean success, JBSms.JBSmsSendResult result, JBException e);
}
