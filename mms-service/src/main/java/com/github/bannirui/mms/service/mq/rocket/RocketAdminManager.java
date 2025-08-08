package com.github.bannirui.mms.service.mq.rocket;

import com.github.bannirui.mms.service.manager.MessageAdminManagerAdapt;
import com.github.bannirui.mms.service.manager.rocket.RocketMqMiddlewareManager;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RocketAdminManager {
    @Autowired
    MessageAdminManagerAdapt messageAdminManagerAdapt;

    public DefaultMQAdminExt getAdmin(String clusterName) {
        return ((RocketMqMiddlewareManager) this.messageAdminManagerAdapt.getOrCreateAdmin(clusterName)).getAdmin();
    }
}
