package com.github.bannirui.mms.service.manager;

import com.alibaba.ttl.TransmittableThreadLocal;

public class MmsContextManager {
    private static final TransmittableThreadLocal<Integer> ZMS_CONTEXT = new TransmittableThreadLocal<>();

    public static void setEnv(Integer env) {
        ZMS_CONTEXT.set(env);
    }

    public static Integer getEnv() {
        return ZMS_CONTEXT.get();
    }
}
