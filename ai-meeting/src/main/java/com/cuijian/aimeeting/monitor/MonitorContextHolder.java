package com.cuijian.aimeeting.monitor;

import lombok.extern.slf4j.Slf4j;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/30 13:49
 **/
@Slf4j
public class MonitorContextHolder {
    public final static ThreadLocal<MonitorContext> MONITOR_CONTEXT = new ThreadLocal<>();

    public static void set(MonitorContext context) {
        MONITOR_CONTEXT.set(context);
    }

    public static MonitorContext get() {
        return MONITOR_CONTEXT.get();
    }

    public static void clear() {
        MONITOR_CONTEXT.remove();
    }
}
