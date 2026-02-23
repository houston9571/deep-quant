package com.optimus.task;

import com.optimus.base.Result;
import com.optimus.components.MarketType;
import com.optimus.constant.Constants;
import com.optimus.mysql.mapper.TradeCalendarMapper;
import com.optimus.service.ConceptDelayService;
import com.optimus.service.StockInfoService;
import com.optimus.service.StockKlineDailyService;
import com.optimus.service.TradeCalendarService;
import com.optimus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Lazy
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncTask {

    private final TradeCalendarService tradeCalendarService;

    private final StockInfoService stockInfoService;

    private final StockKlineDailyService stockKlineDailyService;

    private final ConceptDelayService conceptDelayService;

    /**
     * 所有股票基本信息及所属概念，不包含920
     * 每周六早上5点
     */
    @Scheduled(cron = "0 0 5 ? * 6 ")
    public void syncStockInfo() {
        log.info(" --> 每周六早上5点，同步【stock_info】开始");
        Result<Integer> result = stockInfoService.syncStockInfoAll();
        log.info(" --> 每周六早上5点，同步【stock_info】结束: {}", result);
    }

    /**
     * 获取股票实时交易列表，不包含688 920 ST
     * 10:00:30 10:30:30 11:00:30 11:30:30
     * 13:30:30 14:00:30 14:30:30 15:00:30
     */
    @Scheduled(cron = "30 0/30 10-11,13-15 ? * 1-5 ")
    public void syncStockKlineDaily() {
        if (tradeCalendarService.isTradeTime()) {
            log.info(" --> 同步股票实时交易列表【stock_kline_daily】开始");
            stockKlineDailyService.syncStockTradeList();
            log.info(" --> 同步股票实时交易列表【stock_kline_daily】结束");
        }
    }

    /**
     * 获取概念板块列表，按涨跌幅排序，只更新涨幅前25的板块
     * 每10分钟执行一次
     */
    @Scheduled(cron = "20 0/10 9-12,13-15 ? * 1-5 ")
    void syncConceptDaily(){
        if (tradeCalendarService.isTradeTime()) {
            log.info(" --> 同步概念板块列表【concept_daily】开始 top25");
            conceptDelayService.syncConceptTradeList(false, 25);
            log.info(" --> 同步概念板块列表【concept_daily】结束 top25");
        }
    }

    /**
     * 获取概念板块列表，按涨跌幅排序，只更新涨幅前25的板块
     * 15:05 执行一次
     */
    @Scheduled(cron = "20 5 15 ? * 1-5")
    void syncConceptDailyAll(){
        if (tradeCalendarService.isTradeTime()) {
            log.info(" --> 同步概念板块列表【concept_daily】开始 全量");
            conceptDelayService.syncConceptTradeList(true, 100);
            log.info(" --> 同步概念板块列表【concept_daily】结束 全量");
        }
    }
}
