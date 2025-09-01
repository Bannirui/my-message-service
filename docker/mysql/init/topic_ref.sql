CREATE TABLE `topic_ref`
(
    `id`        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `topic_id`  bigint not null default -1 comment 'topic',
    `env_id`    bigint not null default -1 comment '环境标识',
    `server_id` bigint NOT NULL DEFAULT -1 COMMENT 'mq服务',
    KEY         `idx_topic_id` (`topic_id`),
    KEY         `idx_server_id` (`server_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='topic-环境-mq服务关联';