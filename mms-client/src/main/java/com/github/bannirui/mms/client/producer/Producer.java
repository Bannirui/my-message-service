package com.github.bannirui.mms.client.producer;

import com.github.bannirui.mms.client.MmsService;
import com.github.bannirui.mms.client.common.MmsMessage;

public interface Producer extends MmsService {

    SendResult syncSend(MmsMessage mmsMessage);

    void asyncSend(MmsMessage mmsMessage, SendCallback mmsCallBack);

    void oneway(MmsMessage mmsMessage);

    void statistics();
}

