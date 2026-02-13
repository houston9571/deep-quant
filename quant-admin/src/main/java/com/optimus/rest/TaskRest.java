package com.optimus.rest;

import com.google.common.collect.Lists;
import com.optimus.base.Result;
import com.optimus.mysql.entity.DragonStock;
import com.optimus.mysql.entity.StockInfo;
import com.optimus.mysql.entity.StockDelay;
import com.optimus.mysql.entity.StockRealTime;
import com.optimus.mysql.vo.FundsFlowLine;
import com.optimus.service.*;
import com.optimus.thread.Threads;
import com.optimus.utils.DateUtils;
import com.optimus.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "task", produces = APPLICATION_JSON)
public class TaskRest {


    private final BoardDelayService boardDelayService;

    private final StockInfoService stockInfoService;

    private final StockDelayService stockDelayService;

    private final StockRealtimeService stockRealtimeService;

    private final TradeCalendarService tradeCalendarService;

    private final DragonStockService dragonStockService;

    private final DragonStockDetailService dragonStockDetailService;


    /**
     * 同步概念板块列表
     */
    @GetMapping("board/delay")
    public Result<Void> syncBoardTradeList() {
        Threads.asyncExecute(boardDelayService::syncBoardTradeList);
        return Result.success();
    }


    /**
     * 同步单个股票基本信息，所属概念
     */
    @GetMapping("stock/{code}}")
    public Result<StockInfo> stock(@PathVariable String code) {
        Result<StockInfo> result = stockInfoService.getStockInfo(code);
        if (result.isSuccess()) {
            stockInfoService.getStockBoardList(code);
        }
        return Result.success();
    }

    /**
     * 同步更新所有股票基本信息，所属概念
     */
    @GetMapping("stock/overview")
    public Result<Void> overview() {
        StockDelay stockDelay = StockDelay.builder().tradeDate(DateUtils.now().toLocalDate()).build();
        List<StockDelay> list = stockDelayService.queryList(stockDelay);
        if (!CollectionUtils.isEmpty(list)) {
            Threads.asyncExecute(() -> {
                Result<StockInfo> result;
                for (StockDelay delay : list) {
                    result = stockInfoService.getStockInfo(delay.getCode());
                    if (result.isSuccess()) {
                        stockInfoService.getStockBoardList(delay.getCode());
                    }
                }
            });
        }
        return Result.success();
    }


    /**
     * 获取所有股票当天交易行情
     */
    @GetMapping("stock/delay")
    public Result<Void> syncStockTradeList() {
        Threads.asyncExecute(() -> {
            stockDelayService.syncStockTradeList();
        });
        return Result.success();

    }

    /**
     * 龙虎榜个股列表
     */
    @GetMapping("dragon/{date}")
    public Result<Integer> getStockDragonList(@PathVariable String date) {
        Result<List<DragonStock>> result = dragonStockService.getDragonStockList(date);
        if (result.hasData()) {
            List<DragonStock> list = result.getData();
            Threads.asyncExecute(() -> {
                int count = 0;
                for (DragonStock d : list) {
                    int cc = dragonStockDetailService.getDragonStockDetail(d.getTradeDate(), d.getCode(), d.getName());
                    if(cc == 0){
                        // 连续请求容易超时，重试一次
                        Threads.sleep(NumberUtils.random(2000));
                        cc = dragonStockDetailService.getDragonStockDetail(d.getTradeDate(), d.getCode(), d.getName());
                    }
                    count += cc;
                }
                log.info(">>>>>getStockDragonDetail: {} total_save_size:{}", date, count);
            });
        }
        return Result.success(result.getData().size());
    }


    /**
     * 获取股票实时交易行情
     *
     * @param codes
     * @return
     */
    @GetMapping("stock/realtime/{codes}")
    public Result<List<StockRealTime>> getStockTradeRealtime(@PathVariable String codes) {
        List<StockRealTime> list = Lists.newArrayList();
        String[] codeArray = codes.split(COMMA);
        for (String code : codeArray) {
            Result<StockRealTime> result = stockRealtimeService.getStockRealtime(code);
            if (result.isSuccess()) {
                list.add(result.getData());
            }
        }
        return Result.success(list);
    }

    /**
     * 获取实时资金流向，按分钟返回列表, kline
     *
     * @param code
     * @return
     */
    @GetMapping("/flow/kline/{code}")
    public Result<List<FundsFlowLine>> getFundsFlowLines(@PathVariable String code) {
        return stockRealtimeService.getFundsFlowLines(code);
    }


    @GetMapping("genYearCalendar")
    public Result<Integer> genYearCalendar() {
        return Result.success(tradeCalendarService.genYearCalendar());
    }


   /* @GetMapping("fundHoldInfo/{scode}")
    public JSONResult fundHoldInfo(@PathVariable String scode) {
        List<Map<String, String>[]> factors = spiderTemplateParser.parserAsMap("S02-fundHoldInfo.json", createMap(scode));
        if (CollectionUtils.isEmpty(factors)) {
            return JSONResult.failed("未获取到页面信息");
        }
        stockFundHoldService.save(scode, factors.get(0)[0]);
        return fundHoldDetail(scode, "", 1);
    }

    private JSONResult fundHoldDetail(String scode, String exDate, int page) {
        int pageSize = 50;
        Map<String, String> param = createMap(scode);
        param.put("pageNo", page + "");
        param.put("pageSize", pageSize + "");
        List<Map<String, String>[]> factors = spiderTemplateParser.parserAsMap("S02-fundHoldDetail.json", param);
        if (CollectionUtils.isEmpty(factors)) {
            return JSONResult.failed("未获取到页面信息");
        }
        Map<String, String>[] fundHoldDetailData = factors.get(0);
        log.info("---->fundHoldDetailData pageNo:{} size:{} ", page, fundHoldDetailData.length);
        if (fundHoldDetailData.length > 0) {
            stockFundHoldDetailService.batchSave(fundHoldDetailData);
            String expirationDate = fundHoldDetailData[0].get("expirationDate");
            if (StringUtils.isEmpty(exDate) || exDate.equals(expirationDate)) {  // 该接口会返回往年所有的明细，遇到时间改变时停止
                fundHoldDetail(scode, expirationDate, page + 1);
            }
        }
        return JSONResult.success();
    }*/

  /*  @GetMapping("moneyFlow/{scode}")
    public JSONResult moneyFlow(@PathVariable String scode) {
        List<Map<String, String>[]> factors = spiderTemplateParser.parserAsMap("S03-moneyFlow.json", createMap(scode));
        if (CollectionUtils.isEmpty(factors)) {
            return JSONResult.failed("未获取到页面信息");
        }
        Map<String, String>[] maps = factors.get(0);
        if (ArrayUtils.isNotEmpty(maps)) {
            return stockMoneyFlowService.save(scode, maps[0]);
        }
        return JSONResult.success();
    }*/


    /*

     */
/**
 * 执行股票表和基金持有的股票
 *//*

    @GetMapping("moneyFlowTask")
    public JSONResult moneyFlowTask() {
        JSONResult result = stockOverviewService.allScode();
        if (result.isSuccess()) {
            JSONArray array = result.getData();
            for (int i = 0; i < array.size(); i++) {
                moneyFlow(array.getJSONObject(i).getString("scode"));
            }
        }
        return JSONResult.success("");
    }

    @GetMapping("overviewTask")
    public JSONResult overviewTask() {
        JSONResult result = stockOverviewService.allScode();
        if (result.isSuccess()) {
            JSONArray array = result.getData();
            for (int i = 0; i < array.size(); i++) {
                overview(array.getJSONObject(i).getString("scode"));
            }
        }
        return JSONResult.success("");
    }
*/

}
