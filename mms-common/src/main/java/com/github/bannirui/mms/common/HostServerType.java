package com.github.bannirui.mms.common;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 主机服务类型
 */
@Getter
public enum HostServerType {
    KAFKA(1, "kafka"),
    ROCKETMQ(1 << 1, "rocketmq"),
    ZK(1 << 2, "zk"),
    ;

    private final Integer code;
    public final String desc;

    HostServerType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static final Map<Integer, HostServerType> by_code = new HashMap<>();

    static {
        for (HostServerType e : HostServerType.values()) {
            by_code.put(e.code, e);
        }
    }

    public static HostServerType getByCode(Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return by_code.get(code);
    }
}
