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
