package com.github.bannirui.mms.service.mq.rocket;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.common.message.MessageClientIDSetter;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.RemotingClient;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExtImpl;
import org.jooq.tools.reflect.Reflect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MQAdminExtImpl extends DefaultMQAdminExt {

    private static final Logger logger = LoggerFactory.getLogger(MQAdminExtImpl.class);

    private RemotingClient remotingClient;
    private final Object lock = new Object();

    public MQAdminExtImpl() {
    }

    private RemotingClient getRemotingClient() {
        if (Objects.isNull(this.remotingClient)) {
            synchronized (this.lock) {
                if (Objects.isNull(this.remotingClient)) {
                    DefaultMQAdminExtImpl defaultMqAdminExtImpl = Reflect.on(this).get("defaultMQAdminExtImpl");
                    MQClientInstance mqClientInstance = Reflect.on(defaultMqAdminExtImpl).get("mqClientInstance");
                    MQClientAPIImpl mqClientApiImpl = Reflect.on(mqClientInstance).get("mQClientAPIImpl");
                    this.remotingClient = Reflect.on(mqClientApiImpl).get("remotingClient");
                }
            }
        }
        return remotingClient;
    }

    @Override
    public MessageExt viewMessage(String topic, String msgId) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        logger.info("MessageClientIDSetter.getNearlyTimeFromID(msgId)={} msgId={}", MessageClientIDSetter.getNearlyTimeFromID(msgId), msgId);
        try {
            return super.viewMessage(topic, msgId);
        } catch (Exception e) {
            logger.error("viewMessage error, msgId={}, err", msgId, e);
        }
        return null;
    }
}
