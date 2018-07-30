package com.javabaas.javasdk.cloud;

/**
 * Created by Codi on 2018/7/18.
 */
public interface HookListener {
    HookResponse onHook(HookRequest hookRequest);
}
