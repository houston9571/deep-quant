package com.optimus.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.optimus.base.Result;
import com.optimus.client.EastMoneyStockApi;
import com.optimus.client.EastMoneyH5Api;
import com.optimus.constants.MarketType;
import com.optimus.constants.StockCodeUtils;
import com.optimus.enums.DateFormatEnum;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.vo.FundsFlowLine;
import com.optimus.mysql.entity.StockRealTime;
import com.optimus.mysql.mapper.StockRealtimeMapper;
import com.optimus.service.StockRealtimeService;
import com.optimus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
public class StockRealtimeServiceImpl extends MybatisBaseServiceImpl<StockRealtimeMapper, StockRealTime> implements StockRealtimeService {

    private final StockRealtimeMapper stockRealtimeMapper;

    private final EastMoneyStockApi eastMoneyStockApi;

    private final EastMoneyH5Api eastMoneyH5Api;

    /**
     * 获取股票实时交易行情
     *
     * @param code
     * @return
     */
    public Result<StockRealTime> getStockRealtime(String code) {
        String fields = "f80,f43,f44,f45,f46,f47,f48,f49,f50,f51,f52,f57,f58,f60,f116,f117,f161,f162,f163,f164,f167,f168,f169,f170,f171,f178";
        JSONObject json = eastMoneyStockApi.getStockTradeRealtime(code, MarketType.getMarketCode(code), fields);
        StockRealTime stockTradeRealTime = JSONObject.parseObject(json.getString(LABEL_DATA), StockRealTime.class);
        String transactionDate = json.getJSONObject(LABEL_DATA).getJSONArray("f80").getJSONObject(1).getString("e");
        stockTradeRealTime.setTradeDate(DateUtils.parse(transactionDate+"00", DateFormatEnum.DATETIME_SHORT));
        if(DateUtils.now().isBefore(stockTradeRealTime.getTradeDate())){
            stockTradeRealTime.setTradeDate(DateUtils.now());
        }

        saveOrUpdate(stockTradeRealTime, new String[]{"code", "trade_date"});
        return Result.success(stockTradeRealTime);
    }


    /**
     * 获取实时资金流向 1分钟
     * @param code
     * @return
     */
    public Result<List<FundsFlowLine>> getFundsFlowLines(String code) {
        JSONObject json = eastMoneyStockApi.getFundsFlowLines(code, MarketType.getMarketCode(code), KLINE_1MIN, 10);
        JSONObject data = json.getJSONObject(LABEL_DATA);
        if (ObjectUtil.isEmpty(data) || !data.containsKey("klines")) {
            return Result.fail(NOT_GET_PAGE_ERROR,"");
        }
        String name = data.getString("name");
        JSONArray lines = data.getJSONArray("klines");
        List<FundsFlowLine> list = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(lines)){
            for (int i = 0; i < lines.size(); i++) {
                String[] line = lines.getString(i).split(COMMA);
                FundsFlowLine stockFundsFlow = FundsFlowLine.builder()
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
