package com.github.bannirui.mms.service.env;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.bannirui.mms.common.EnvStatus;
import com.github.bannirui.mms.dal.mapper.EnvMapper;
import com.github.bannirui.mms.dal.model.Env;
import com.github.bannirui.mms.service.manager.ZkDatasourceManagerAdapt;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class EnvDatasourceService {

    private static final Logger logger = LoggerFactory.getLogger(EnvDatasourceService.class);

    @Autowired
    private EnvMapper envMapper;
    @Autowired
    private ZkDatasourceManagerAdapt zkDatasourceManagerAdapt;

    @PostConstruct
    public void init() {
        List<Env> envs = this.envMapper.selectList(new LambdaQueryWrapper<Env>().eq(Env::getStatus, EnvStatus.ENABLE.getCode()));
        if (CollectionUtils.isEmpty(envs)) {
            logger.info("No enabled envs found");
        }
        for (Env env : envs) {
            this.reloadEnvZkClient(env.getId());
        }
    }

    private void reloadEnvZkClient(Long envId) {
        // todo
        this.zkDatasourceManagerAdapt.reload(envId, "127.0.0.1:2181");
    }
}
