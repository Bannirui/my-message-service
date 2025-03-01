package com.github.bannirui.mms.client.crypto;

public enum MMSCryptoType {
    AES_128("AES", 128),
    ;

    private String alg;
    private int length;

    MMSCryptoType(String alg, int length) {
        this.alg = alg;
        this.length = length;
    }

    public String getAlg() {
        return this.alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
