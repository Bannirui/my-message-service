package com.github.bannirui.mms.client;

/**
 * mms服务的顶层抽象
 */
public interface LifeCycle {
    /**
     * 开启服务
     */
    void start();
    /**
     * 关闭服务
     */
    void shutdown();
}
