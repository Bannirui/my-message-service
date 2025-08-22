package com.github.bannirui.mms.service.env;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.dal.mapper.EnvMapper;
import com.github.bannirui.mms.dal.model.Env;
import com.github.bannirui.mms.dal.model.EnvHostServerExt;
import com.github.bannirui.mms.service.manager.ZkDatasourceManagerAdapt;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

@Service
public class EnvDatasourceService {

    private static final Logger logger = LoggerFactory.getLogger(EnvDatasourceService.class);

    @Autowired
    private EnvMapper envMapper;
    @Autowired
    private ZkDatasourceManagerAdapt zkDatasourceManagerAdapt;

    @PostConstruct
    public void init() {
        List<Env> envs = this.envMapper.selectList(new LambdaQueryWrapper<Env>().eq(Env::getStatus, ResourceStatus.ENABLE.getCode()));
        if (CollectionUtils.isEmpty(envs)) {
            logger.info("No enabled envs found");
        }
        logger.info("Found {} enabled envs", envs.size());
        for (Env env : envs) {
            this.reloadEnvZkClient(env.getId());
        }
    }

    /**
     * 每个环境都分配了zk作为元数据注册中心
     * @param envId 哪个环境
     */
    public void reloadEnvZkClient(Long envId) {
        EnvHostServerExt env = this.envMapper.selectByEnvId(envId);
        if(Objects.isNull(env)) {
            return;
        }
        String host = env.getZkHost();
        Integer port = env.getZkPort();
        if(StringUtils.isEmpty(host) || Objects.isNull(port)) {
            return;
        }
        // todo zk集群
        String zkUrl = host + ":" + port;
        logger.info("reload zk {} for env {}", zkUrl, env.getEnvName());
        this.zkDatasourceManagerAdapt.reload(envId, zkUrl);
    }
}
