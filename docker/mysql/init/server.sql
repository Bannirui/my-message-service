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
