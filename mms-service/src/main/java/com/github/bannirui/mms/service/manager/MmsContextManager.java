package com.github.bannirui.mms.service.manager;

import com.alibaba.ttl.TransmittableThreadLocal;

public class MmsContextManager {
    private static final TransmittableThreadLocal<Long> ZMS_CONTEXT = new TransmittableThreadLocal<>();

    /**
     * @param envId {@link com.github.bannirui.mms.dal.model.Env#id}
     */
    public static void setEnv(Long envId) {
        ZMS_CONTEXT.set(envId);
    }

    public static Long getEnv() {
        return ZMS_CONTEXT.get();
    }
}
