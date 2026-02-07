package com.optimus.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.optimus.base.Result;
import com.optimus.client.EmPush2delayApi;
import com.optimus.client.EastMoneyH5Api;
import com.optimus.constants.MarketType;
import com.optimus.constants.StockCodeUtils;
import com.optimus.enums.DateFormatEnum;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.StockFundsFlow;
import com.optimus.mysql.entity.StockTradeDelay;
import com.optimus.mysql.entity.StockTradeRealTime;
import com.optimus.mysql.mapper.StockTradeDelayMapper;
import com.optimus.mysql.mapper.StockTradeRealtimeMapper;
import com.optimus.service.StockTradeDelayService;
import com.optimus.service.StockTradeRealtimeService;
import com.optimus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.optimus.constant.Constants.LABEL_DATA;
import static com.optimus.constants.StockConstants.KLINE_1MIN;
import static com.optimus.enums.ErrorCode.NOT_GET_PAGE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockTradeRealtimeServiceImpl extends MybatisBaseServiceImpl<StockTradeRealtimeMapper, StockTradeRealTime> implements StockTradeRealtimeService {

    @Autowired
    EmPush2delayApi eastMoneyApi;
    @Autowired
    EastMoneyH5Api eastMoneyH5Api;

    @Autowired
    EmPush2delayApi emPush2delayApi;


    private final StockTradeRealtimeMapper stockTradeRealtimeMapper;


    /**
     * 获取股票实时交易行情
     *
     * @param code
     * @return
     */
    public Result<StockTradeRealTime> getStockTradeRealtime(String code) {
        String fields = "f80,f43,f44,f45,f46,f47,f48,f49,f50,f51,f52,f57,f58,f60,f116,f117,f161,f162,f163,f164,f167,f168,f169,f170,f171,f178";
        JSONObject json = eastMoneyApi.getStockTradeRealtime(code, MarketType.getMarketCode(code), fields);
        StockTradeRealTime stockTradeRealTime = JSONObject.parseObject(json.getString(LABEL_DATA), StockTradeRealTime.class);
//        String transactionDate = json.getJSONObject(LABEL_DATA).getJSONArray("f80").getJSONObject(0).getString("b");
//        stockTradeRealTime.setTransactionDate(DateUtils.parseLocalDate(transactionDate.substring(0, 8), DateFormatEnum.DATE_SHORT));

        saveOrUpdate(stockTradeRealTime, new String[]{"code", "trade_date"});
        return Result.success(stockTradeRealTime);
    }


    /**
     * 获取实时资金流向 1分钟
     * @param code
     * @return
     */
    public Result<List<StockFundsFlow>> getStockFundsFlow(String code) {
        JSONObject json = emPush2delayApi.getStockFundsFlow(code, MarketType.getMarketCode(code), KLINE_1MIN);
        JSONObject data = json.getJSONObject(LABEL_DATA);
        if (ObjectUtil.isEmpty(data) || !data.containsKey("klines")) {
            return Result.fail(NOT_GET_PAGE_ERROR,"");
        }
        String name = data.getString("name");
        JSONArray lines = data.getJSONArray("klines");
        List<StockFundsFlow> list = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(lines)){
            for (int i = 0; i < lines.size(); i++) {
                String[] line = lines.getString(i).split(COMMA);
                StockFundsFlow stockFundsFlow = StockFundsFlow.builder()
                        .code(code)
                        .name(name)
                        .tradeDate( line[0])
                        .mainNetInflow(line[1])
                        .smallNetInflow(line[2])
                        .mediumNetInflow(line[3])
                        .largeNetInflow(line[4])
                        .superLargeNetInflow(line[5])
                        .build();
                list.add(stockFundsFlow);
            }
        }
        return Result.success(list);
    }

    public Result<JSONObject> getFirstRequest2Data(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("fc", StockCodeUtils.buildSecId(code));
        eastMoneyH5Api.getFirstRequest2Data(params);
        return Result.success();
    }

}
