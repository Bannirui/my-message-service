package com.github.bannirui.mms.client.crypto;

public interface MMSCrypto {
    byte[] encrypt(byte[] data);

    byte[] decrypt(byte[] data);
}
