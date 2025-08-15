CREATE TABLE `server`
(
    `id`      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `name`    varchar(128) comment '集群名',
    `address` varchar(256) comment 'ip:port or url',
    `type`    INT NOT NULL DEFAULT -1 COMMENT 'mq类型 1=kafka 2=rocket',
    `status`  int not null default -1 comment '集群服务器状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集群';

INSERT INTO `server` (`name`, `address`, `type`, `status`)
VALUES ('Kafka集群A', '192.168.1.100:9092', 1, 1),
       ('Kafka集群B', '192.168.1.101:9092', 1, 1),
       ('RocketMQ集群A', '192.168.1.200:9876', 2, 1);

CREATE TABLE `env`
(
    `id`      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `name`    varchar(256) comment '环境',
    `status`  int not null default -1 comment '状态',
    `sort_id` int not null default 0 comment '排序用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='环境';

CREATE TABLE `topic`
(
    `id`           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `user_id`      BIGINT       not null default -1 COMMENT '申请人',
    `name`         VARCHAR(128) NOT NULL COMMENT '主题名称',
    `cluster_type` INT          NOT NULL DEFAULT -1 COMMENT 'mq类型 1=kafka 2=rocket',
    `app_id`       BIGINT       not null default -1 COMMENT 'topic给哪个应用服务用的',
    `tps`          INT          NOT NULL DEFAULT 0 COMMENT '发送速度 条/秒',
    `msg_sz`       INT          NOT NULL DEFAULT 0 COMMENT '消息体大小 字节',
    `status`       INT          NOT NULL DEFAULT -1 COMMENT '状态',
    `partitions`   INT          NOT NULL DEFAULT 0 COMMENT '分区数',
    `replication`  INT          NOT NULL DEFAULT 0 COMMENT '副本数'
    `remark`         VARCHAR(1024) NOT NULL COMMENT '申请topic的备注',
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主题表';

CREATE TABLE `topic_env_server`
(
    `id`        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `topic_id`  bigint not null default -1 comment 'topic',
    `server_id` bigint NOT NULL DEFAULT -1 COMMENT '集群',
    `env_id`    bigint not null default -1 comment '环境标识'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='topic-集群-环境关联';
