package com.github.bannirui.mms.client.producer;

public interface SendCallback {

    void onException(Throwable exception);

    void onResult(SendResult response);
}

