package com.optimus.rest;

import com.google.common.collect.Lists;
import com.optimus.base.Result;
import com.optimus.mysql.entity.StockFundsFlow;
import com.optimus.mysql.entity.StockTradeRealTime;
import com.optimus.service.StockTradeDelayService;
import com.optimus.service.StockTradeRealtimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping(value = "stock/trade", produces = APPLICATION_JSON)
public class StockTradeRest {



    private final StockTradeDelayService stockTradeService;

    private final StockTradeRealtimeService stockTradeRealtimeService;


    /**
     * 获取所有股票今天交易行情
     */
    @GetMapping("delay")
    public Result<Void> syncStockTradeList() {
        return stockTradeService.syncStockTradeList();
    }

    /**
     * 获取股票实时交易行情
     * @param codes
     * @return
     */
    @GetMapping("realtime/{codes}")
    public Result<List<StockTradeRealTime>> getStockTradeRealtime(@PathVariable String codes) {
        List<StockTradeRealTime> list = Lists.newArrayList();
        String[] codeArray = codes.split(COMMA);
        for (String code : codeArray) {
            Result<StockTradeRealTime> result = stockTradeRealtimeService.getStockTradeRealtime(code);
            if (result.isSuccess()) {
                list.add(result.getData());
            }
        }
        return Result.success(list);
    }

    /**
     * 获取实时资金流向，按分钟返回列表
     * @param code
     * @return
     */
    @GetMapping("flow/{code}")
    public Result<List<StockFundsFlow>> getStockFundsFlow(@PathVariable String code) {
        return stockTradeRealtimeService.getStockFundsFlow(code);
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
