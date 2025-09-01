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
