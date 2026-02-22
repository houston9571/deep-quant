package com.optimus.rest;

import com.optimus.base.PageInfo;
import com.optimus.base.PageResult;
import com.optimus.mysql.entity.StockInfo;
import com.optimus.service.StockKlineDailyService;
import com.optimus.service.StockInfoService;
import com.optimus.service.StockKlineMinuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "stock", produces = APPLICATION_JSON)
public class StockRest {

    private final StockInfoService stockInfoService;

    private final StockKlineDailyService stockKlineDailyService;

    private final StockKlineMinuteService stockKlineMinuteService;



    /**
     * 同步更新股票基本信息，所属概念
     */
    @PostMapping("")
    public PageResult<StockInfo> stockList(@RequestBody PageInfo<StockInfo> pageInfo) {
        List<StockInfo> list = stockInfoService.queryPage(pageInfo);
        return PageResult.success(list);
    }


}
