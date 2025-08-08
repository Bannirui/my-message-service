package com.github.bannirui.mms.service.manager;

import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ZkDatasourceManager implements DatasourceManager {
    private static final Logger logger = LoggerFactory.getLogger(ZkDatasourceManager.class);
    private String mmsServer;
    private MmsZkClient zkClient;

    public ZkDatasourceManager(String mmsServer) {
        this.mmsServer = mmsServer;
        this.create();
    }

    public MmsZkClient getZkClient() {
        return this.zkClient;
    }

    @Override
    public DatasourceManager create() {
        try {
            this.zkClient = new MmsZkClient(this.mmsServer, 20 * 1_000, null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        assert Objects.nonNull(this.zkClient);
        return this;
    }

    @Override
    public void destroy() {
        if (Objects.nonNull(this.zkClient)) {
            try {
                this.zkClient.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
