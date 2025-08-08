package com.github.bannirui.mms.common;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public enum BrokerType {
    KAFKA(1, "kafka"),
    ROCKETMQ(1 << 1, "rocketmq");

    private final Integer code;
    public final String desc;

    BrokerType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static final Map<Integer, BrokerType> by_code = new HashMap<>();

    static {
        for (BrokerType e : BrokerType.values()) {
            by_code.put(e.code, e);
        }
    }

    public static BrokerType getByCode(Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return by_code.get(code);
    }
}
