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