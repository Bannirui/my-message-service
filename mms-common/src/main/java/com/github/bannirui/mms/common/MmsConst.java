package com.github.bannirui.mms.common;

import org.springside.modules.utils.net.NetUtil;

public class MmsConst {

    public static final String DEFAULT_NAME = "DefaultName";
    public static final String DEFAULT_PRODUCER = "DEFAULT_PRODUCER";
    public static final String DEFAULT_CONSUMER = "DEFAULT_CONSUMER";
    public final static String CHAR_ENCODING = "UTF-8";
    public final static String MMS = "MMS";
    public static final String MMS_PORTAL = "mms.portal";
    public static final String MMS_TOKEN = "mms.token";
    public static final String INFLUXDB = "influxDB";

    public static final String MMS_VERSION = "1.0.0";
    public static final String MMS_IP = NetUtil.getLocalHost();

    private MmsConst() {
    }

    public static class STATISTICS {
        public static final String STATISTICS_CLUSTER_NAME = "statistic_cluster";
        public static final String STATISTICS_METADATA_NAME = "statisticsLogger";
        public static final String STATISTICS_TOPIC_PRODUCER_INFO = "statistic_topic_producer_info";
        public static final String STATISTICS_TOPIC_CONSUMER_INFO = "statistic_topic_consumer_info";
        public static final String CHECK_STATUS_TOPIC_NAME = "check_status_topic";
        public static final String STATISTICS_CONSUMER_PRODUCER_INFO = "statistic_consumer_producer_info";
        public static final String STATISTICS_CONSUMER_CONSUMER_INFO = "statistic_consumer_consumer_info";
        public static final String PING_TOPIC_NAME = "statistic_ping_topic";
        public static final String PING_CONSUMER_NAME = "statistic_ping_consumer";
    }

    public static class ZK {
        /**
         * mms用zk作注册中心 持久化关于mq的cluster consumer和topic的元数据
         * <ul>
         *     <li>zk单机 ip:port</li>
         *     <li>zk集群 ip1:port1,ip2:port2</li>
         * </ul>
         */
        public static final String MMS_STARTUP_PARAM = "mms_zookeeper_register";
        public static final String ENV = "env";
        public static final String ROOT_ZK_PATH = "/mms";
        public static final String CLUSTER_ZK_PATH = ROOT_ZK_PATH + "/cluster";
        public static final String TOPIC_ZK_PATH = ROOT_ZK_PATH + "/topic";
        public static final String CONSUMER_GROUP_ZK_PATH = ROOT_ZK_PATH + "/consumerGroup";
        public static final String ZK_FIELD_CLUSTERNAME = "clusterName";
        public static final String ZK_FIELD_BOOTADDDR = "bootAddr";
        public static final String ZK_FIELD_BROKERTYPE = "brokerType";
        public static final String ZK_FIELD_BINDING_TOPIC = "bindingTopic";
        public static final String ZK_FIELD_BROADCAST = "broadcast";
        public static final String ZK_FIELD_CONSUME_FROM = "consumeFrom";
        public static final String ZK_FIELD_TYPE = "type";
        public static final String ZK_FIELD_SERVERIPS = "serverIps";
        public static final String ZK_FIELD_SUSPEND = "suspend";
        public static final String ZK_FIELD_GATED_IP = "gatedIps";
        public static final String ZK_FIELD_GATED_CLUSTER = "gatedCluster";
    }


    public static class CLIENT_CONFIG {
        public static final String CONSUME_THREAD_MIN = "consumeThreadMin";
        public static final String CONSUME_THREAD_MAX = "consumeThreadMax";
        public static final String CONSUME_MESSAGES_SIZE = "consumeMessagesSize";
        public static final String ROCKETMQ_CONSUME_BATCH = "rocketmqConsumeBatchSize";
        public static final String CONSUME_ORDERLY = "isOrderly";
    }

    public static class Measurement {
        public static final String CLUSTER_NUMBER_INFO = "cluster_number_info";
        public static final String CLUSTER_STRING_INFO = "cluster_string_info";
        public static final String MQ_BROKER_NUMBER_INFO = "mq_broker_number_info";
        public static final String MQ_BROKER_STRING_INFO = "mq_broker_string_info";
        public static final String MQ_CONSUMER_NUMBER_INFO = "mq_consumer_number_info";
        public static final String MQ_CONSUMER_STRING_INFO = "mq_consumer_string_info";
        public static final String MQ_TOPIC_INFO = "mq_topic_info";
        public static final String STATISTIC_TOPIC_PRODUCER_INFO = "statistic_topic_producer_info";
        public static final String STATISTIC_TOPIC_CONSUMER_INFO = "statistic_topic_consumer_info";
        public static final String STATISTIC_TOPIC_OFFSETS_INFO = "statistic_topic_offsets_info";
        public static final String STATISTIC_TOPIC_DAILY_OFFSETS_INFO = "statistic_topic_daily_offsets_info";
        public static final String STATISTIC_CLUSTER_DAILY_OFFSETS_INFO = "statistic_cluster_daily_offsets_info";
        public static final String STATISTIC_DLQ_TOPIC_OFFSETS_INFO = "statistic_dlq_topic_offsets_info";
        public static final String KAFKA_CONSUMER_NUMBER_INFO = "kafka_consumer_number_info";
        public static final String KAFKA_BROKER_INFO = "kafka_broker_info";
        public static final String KAFKA_TOPIC_INFO = "kafka_topic_info";
        public static final String KAFKA_ENV_INFO = "kafka_env_info";
        public static final String TOPIC_TOP_INFO = "topic_top_info";
        public static final String CONSUMER_TOP_INFO = "consumer_top_info";
        public static final String RT_TIME = "rt_time";
        public static final String DLQ_ALERT_MSG_INFO = "dlq_alert_msg_info";
        public static final String TRIGGER_RULES = "trigger_rules";
    }
}

