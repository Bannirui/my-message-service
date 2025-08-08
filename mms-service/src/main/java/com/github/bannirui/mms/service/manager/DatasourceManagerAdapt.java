package com.github.bannirui.mms.service.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatasourceManagerAdapt<T extends DatasourceManager> {

    private final Map<Integer, T> envDatasourceMap = new ConcurrentHashMap<>();

    public T getDatasource(int env) {
        return this.envDatasourceMap.get(env);
    }

    protected T reload(Integer env, T dataSourceManager) {
        synchronized (this.envDatasourceMap) {
            this.rm(env);
            this.envDatasourceMap.put(env, dataSourceManager);
        }
        return this.getDatasource(env);
    }

    private void rm(Integer env) {
        synchronized (this.envDatasourceMap) {
            if (this.envDatasourceMap.containsKey(env)) {
                this.envDatasourceMap.remove(env).destroy();
            }
        }
    }

    public void destroy() {
        this.envDatasourceMap.forEach((k, v) -> v.destroy());
    }
}
