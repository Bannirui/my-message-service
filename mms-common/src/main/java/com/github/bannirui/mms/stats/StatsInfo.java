package com.github.bannirui.mms.stats;

public abstract class StatsInfo {
    private ClientInfo clientInfo;

    private Long timestamp = System.currentTimeMillis();

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

