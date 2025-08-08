package com.github.bannirui.mms.service.selector;

import com.github.bannirui.mms.zookeeper.MmsZkClient;

public interface ZkSelector {
    MmsZkClient select();
}
