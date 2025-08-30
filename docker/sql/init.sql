CREATE TABLE `env`
(
    `id`      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `name`    varchar(256) comment '环境',
    `status`  int    not null default -1 comment '状态',
    `sort_id` int    not null default 0 comment '排序用',
    `zk_id`   bigint not null default 0 comment '环境的zk数据源',
    KEY       `idx_status` (`status`),
    KEY       `idx_zk_id` (`zk_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='环境';

CREATE TABLE `host`
(
    `id`     BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `name`   varchar(256) comment 'name',
    `host`   varchar(256) comment 'host or ip',
    `env_id` bigint not null default -1 comment '哪个环境',
    `status` int    not null default -1 comment '状态',
    KEY      `idx_status` (`status`),
    KEY      `idx_env_id` (`env_id`),
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='主机';

CREATE TABLE `server`
(
    `id`      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `name`    varchar(128) comment '服务名',
    `type`    int    not null default 0 comment '服务类型',
    `port`    INT    NOT NULL DEFAULT -1 COMMENT '服务在主机上的端口',
    `status`  int    not null default -1 comment '状态',
    `host_id` bigint NOT NULL DEFAULT -1 COMMENT '关联主机',
    KEY       `idx_status` (`status`),
    KEY       `idx_host_id` (`host_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='服务';

CREATE TABLE `topic`
(
    `id`           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id`      BIGINT        not null default -1 COMMENT '申请人',
    `name`         VARCHAR(128)  NOT NULL COMMENT '主题名称',
    `cluster_type` INT           NOT NULL DEFAULT -1 COMMENT 'mq类型 1=kafka 2=rocket',
    `app_id`       BIGINT        not null default -1 COMMENT 'topic给哪个应用服务用的',
    `tps`          INT           NOT NULL DEFAULT 0 COMMENT '发送速度 条/秒',
    `msg_sz`       INT           NOT NULL DEFAULT 0 COMMENT '消息体大小 字节',
    `status`       INT           NOT NULL DEFAULT -1 COMMENT '状态',
    `partitions`   INT           NOT NULL DEFAULT 0 COMMENT '分区数',
    `replication`  INT           NOT NULL DEFAULT 0 COMMENT '副本数',
    `remark`       VARCHAR(1024) NOT NULL COMMENT '申请topic的备注',
    KEY            `idx_status` (`status`),
    KEY            `idx_user_id` (`user_id`),
    KEY            `idx_app_id` (`app_id`),
    KEY            `idx_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='主题表';

CREATE TABLE `topic_ref`
(
    `id`        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `topic_id`  bigint not null default -1 comment 'topic',
    `env_id`    bigint not null default -1 comment '环境标识',
    `server_id` bigint NOT NULL DEFAULT -1 COMMENT 'mq服务',
    KEY         `idx_topic_id` (`topic_id`),
    KEY         `idx_server_id` (`server_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='topic-环境-mq服务关联';

CREATE TABLE `consumer`
(
    `id`                 BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id`            BIGINT        not null default 0 COMMENT '申请人',
    `name`               VARCHAR(128)  NOT NULL COMMENT '消费组名',
    `topic_id`           BIGINT        not null default 0 COMMENT '订阅哪个topic',
    `app_id`             BIGINT        not null default 0 COMMENT '给哪个应用服务用的',
    `status`             INT           NOT NULL DEFAULT -1 COMMENT '状态',
    `remark`             VARCHAR(1024) NOT NULL COMMENT '申请时的备注信息',
    `consumer_broadcast` INT           NOT NULL DEFAULT -1 COMMENT '广播消费 0-不支持 1-支持',
    `consumer_from_min`  INT           NOT NULL DEFAULT -1 COMMENT '最早消费 0-不支持 1-支持',
    KEY                  `idx_name` (`name`),
    KEY                  `idx_status` (`status`),
    KEY                  `idx_user_id` (`user_id`),
    KEY                  `idx_topic_id` (`topic_id`),
    KEY                  `idx_app_id` (`app_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='消费组';
