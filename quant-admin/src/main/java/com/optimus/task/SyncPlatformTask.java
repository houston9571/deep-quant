package com.optimus.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
//@Component
@RequiredArgsConstructor
public class SyncPlatformTask {



    @Scheduled(cron = "0 0/1 * * * ? ")
    public void execSyncBetOrders() {

            log.info(" --> 同步 Task:syncBetOrders");

    }

}
