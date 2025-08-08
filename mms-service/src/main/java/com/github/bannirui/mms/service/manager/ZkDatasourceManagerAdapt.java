package com.github.bannirui.mms.service.manager;

import org.springframework.stereotype.Component;

@Component
public class ZkDatasourceManagerAdapt extends DatasourceManagerAdapt<ZkDatasourceManager> {

    public ZkDatasourceManager reload(Integer env, String zkUrl) {
        ZkDatasourceManager zkDatasourceManager = new ZkDatasourceManager(zkUrl);
        return super.reload(env, zkDatasourceManager);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
