package com.optimus.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.optimus.base.Result;
import com.optimus.client.EastMoneyStockApi;
import com.optimus.client.EastMoneyH5Api;
import com.optimus.components.MarketType;
import com.optimus.components.StockCodeUtils;
import com.optimus.enums.DateFormatEnum;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.StockKlineMinute;
import com.optimus.mysql.mapper.StockKlineMinuteMapper;
import com.optimus.service.StockKlineMinuteService;
import com.optimus.service.StockTechMinuteService;
import com.optimus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.optimus.constant.Constants.LABEL_DATA;
import static com.optimus.components.StockConstants.KLINE_1MIN;
import static com.optimus.enums.ErrorCode.NOT_GET_PAGE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockKlineMinuteServiceImpl extends MybatisBaseServiceImpl<StockKlineMinuteMapper, StockKlineMinute> implements StockKlineMinuteService {

    private final StockKlineMinuteMapper stockKlineMinuteMapper;

    private final EastMoneyStockApi eastMoneyStockApi;

    private final EastMoneyH5Api eastMoneyH5Api;

    private final StockTechMinuteService stockTechMinuteService;

    /**
     * 获取股票实时交易行情和资金流向 1分钟
     */
    public Result<StockKlineMinute> getStockRealtime(String stockCode) {
        String fields = "f80,f43,f44,f45,f46,f47,f48,f49,f50,f51,f52,f57,f58,f60,f116,f117,f161,f162,f163,f164,f167,f168,f169,f170,f171,f178";
        JSONObject kline = eastMoneyStockApi.getStockTradeRealtime(stockCode, MarketType.getMarketCode(stockCode), fields);
        StockKlineMinute stockKlineMinute = JSONObject.parseObject(kline.getString(LABEL_DATA), StockKlineMinute.class);
        String transactionDate = kline.getJSONObject(LABEL_DATA).getJSONArray("f80").getJSONObject(1).getString("e");
//        if(DateUtils.now().isBefore(stockTechMin.getTradeDate())){
//            stockTechMin.setTradeDate(DateUtils.now());
//        }

        JSONObject flow = eastMoneyStockApi.getFundsFlowLines(stockCode, MarketType.getMarketCode(stockCode), KLINE_1MIN, 1);
        JSONObject data = flow.getJSONObject(LABEL_DATA);
        if (ObjectUtil.isEmpty(data) || !data.containsKey("klines")) {
            return Result.fail(NOT_GET_PAGE_ERROR, "");
        }

        JSONArray lines = data.getJSONArray("klines");

        String[] line = lines.getString(0).split(COMMA);
        String[] t = line[0].split("  ");
        stockKlineMinute.setTradeDate(DateUtils.parseLocalDate(t[0], DateFormatEnum.DATE));
        stockKlineMinute.setTradeTime(DateUtils.parseLocalTime(t[1] + ":00", DateFormatEnum.TIME));
        stockKlineMinute.setMainNetInflow(line[1]);
        stockKlineMinute.setSmallNetInflow(line[2]);
        stockKlineMinute.setMediumNetInflow(line[3]);
        stockKlineMinute.setLargeNetInflow(line[4]);
        stockKlineMinute.setSuperLargeNetInflow(line[5]);

        saveOrUpdate(stockKlineMinute, new String[]{"stock_code", "trade_date"});

        stockTechMinuteService.calcMinuteIndicatorAndSave(stockCode);
        return Result.success(stockKlineMinute);
    }



    public Result<JSONObject> getFirstRequest2Data(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("fc", StockCodeUtils.buildSecId(code));
        eastMoneyH5Api.getFirstRequest2Data(params);
        return Result.success();
    }

}
