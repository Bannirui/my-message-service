package com.github.bannirui.mms.client.producer;

public class SendResult {

    private int code;
    private long offset;
    private String msgId;
    private String topic;
    private int queueOrPartition;

    private String msg;

    public SendResult(int code, long offset, String msgId, String topic, int queueOrPartition) {

        this.code = code;
        this.offset = offset;
        this.msgId = msgId;
        this.topic = topic;
        this.queueOrPartition = queueOrPartition;
    }

    public SendResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public long getOffset() {
        return offset;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getTopic() {
        return topic;
    }

    public int getQueueOrPartition() {
        return queueOrPartition;
    }

    public String getMsg() {
        return msg;
    }

    static SendResult buildSuccessResult(long offset, String msgId, String topic, int queueOrPartition) {
        return new SendResult(200, offset, msgId, topic, queueOrPartition);
    }

    public static SendResult SUCCESS = new SendResult(200, null);
    public static SendResult FAILURE_NOTRUNNING = new SendResult(401, "client状态不是running");
    public static SendResult FAILURE_TIMEOUT = new SendResult(402, "客户端发送超时");
    public static SendResult FAILURE_INTERRUPTED = new SendResult(403, "等待线程被中断");

    public static SendResult buildErrorResult(String msg) {
        return new SendResult(404, msg);
    }

    public static SendResult buildErrorResult(int code, String msg) {
        return new SendResult(code, msg);
    }

    public static SendResult FAILURE_SLAVE = new SendResult(405, "slave节点不存在");


    public boolean isSucceed() {
        return this.code == 200;
    }
}