package com.optimus.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.optimus.base.Result;
import com.optimus.client.EmPush2delayApi;
import com.optimus.client.EastMoneyH5Api;
import com.optimus.core.MarketType;
import com.optimus.core.StockCodeUtils;
import com.optimus.enums.DateFormatEnum;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.StockTrade;
import com.optimus.mysql.mapper.StockTradeMapper;
import com.optimus.service.StockTradeService;
import com.optimus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.optimus.constant.Constants.LABEL_DATA;
import static com.optimus.core.StockConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockTradeServiceImpl extends MybatisBaseServiceImpl<StockTradeMapper, StockTrade> implements StockTradeService {

    @Autowired
    EmPush2delayApi eastMoneyApi;
    @Autowired
    EastMoneyH5Api eastMoneyH5Api;

    private final StockTradeMapper stockTradeMapper;


    /**
     * 获取股票实时交易行情
     * @param code
     * @return
     */
    public Result<Void> getStockTrade(String code) {
        String fields = "f80,f43,f44,f45,f46,f47,f48,f49,f50,f51,f52,f57,f58,f60,f116,f117,f161,f162,f163,f164,f167,f168,f169,f170,f171,f178";
        JSONObject json = eastMoneyApi.getStockTrade(code, MarketType.getMarketCode(code),fields);
        StockTrade stockTrade =  JSONObject.parseObject(json.getString(LABEL_DATA), StockTrade.class);
        String transactionDate = json.getJSONObject(LABEL_DATA).getJSONArray("f80").getJSONObject(0).getString("b");
        stockTrade.setTransactionDate(DateUtils.parseLocalDate(transactionDate.substring(0,8), DateFormatEnum.DATE_SHORT));

        LambdaQueryWrapper<StockTrade> wrapper = new LambdaQueryWrapper<StockTrade>()
                .eq(StockTrade::getCode, code)
                .eq(StockTrade::getTransactionDate, stockTrade.getTransactionDate());
        if (exist(wrapper)) {
            update(stockTrade, wrapper);
        }else {
            save(stockTrade);
        }
        return Result.success();
    }


    public Result<JSONObject> getFirstRequest2Data(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("fc", StockCodeUtils.buildSecId(code));
        eastMoneyH5Api.getFirstRequest2Data(params);
        return Result.success();
    }

}
