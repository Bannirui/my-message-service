CREATE TABLE `host`
(
    `id`     BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `name`   varchar(256) comment 'name',
    `host`   varchar(256) comment 'host or ip',
    `env_id` bigint not null default -1 comment '哪个环境',
    `status` int    not null default -1 comment '状态',
    KEY      `idx_status` (`status`),
    KEY      `idx_env_id` (`env_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='主机';
