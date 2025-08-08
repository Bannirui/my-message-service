package com.github.bannirui.mms.selector;

import com.github.bannirui.mms.service.manager.MmsContextManager;
import com.github.bannirui.mms.service.manager.ZkDatasourceManager;
import com.github.bannirui.mms.service.manager.ZkDatasourceManagerAdapt;
import com.github.bannirui.mms.service.selector.ZkSelector;
import com.github.bannirui.mms.util.Assert;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class WebZkSelector implements ZkSelector {

    @Autowired
    ZkDatasourceManagerAdapt zkDatasourceManagerAdapt;

    @Override
    public MmsZkClient select() {
        Long env = MmsContextManager.getEnv();
        Assert.that(Objects.nonNull(env) && !Objects.equals(0L, env), "The current environment is not specified");
        ZkDatasourceManager zkDatasourceManager = this.zkDatasourceManagerAdapt.getDatasource(env);
        return Objects.isNull(zkDatasourceManager) ? null : zkDatasourceManager.getZkClient();
    }
}
