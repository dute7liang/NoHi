package com.nohi.framework.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异步任务执行
 * <p>created on 2022/2/18 16:05</p>
 * @author dute7liang
 */
@Slf4j(topic = "sys-user")
@Async
@Component
public class AsyncService {
    public void recordLog(final StringBuilder logBuild) {
        log.info(logBuild.toString());
    }
}
