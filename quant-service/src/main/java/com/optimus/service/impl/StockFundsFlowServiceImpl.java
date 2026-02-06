package com.optimus.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.optimus.base.Result;
import com.optimus.client.EastMoneyH5Api;
import com.optimus.client.EmDatacenterApi;
import com.optimus.client.EmPush2Api;
import com.optimus.client.EmPush2delayApi;
import com.optimus.core.MarketType;
import com.optimus.enums.DateFormatEnum;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.StockFundsFlow;
import com.optimus.mysql.mapper.StockFundsFlowMapper;
import com.optimus.service.StockFundsFlowService;
import com.optimus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.optimus.constant.Constants.LABEL_DATA;
import static com.optimus.core.StockConstants.KLINE_DAILY;
import static com.optimus.enums.ErrorCode.NOT_GET_PAGE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockFundsFlowServiceImpl extends MybatisBaseServiceImpl<StockFundsFlowMapper, StockFundsFlow> implements StockFundsFlowService {

    private final StockFundsFlowMapper stockFundsFlowMapper;


    @Autowired
    EmPush2delayApi emPush2delayApi;



    public Result<Void> getStockFundsFlow(String code) {
        JSONObject json = emPush2delayApi.getStockFundsFlow(code, MarketType.getMarketCode(code), KLINE_DAILY);
        JSONObject data = json.getJSONObject(LABEL_DATA);
        if (ObjectUtil.isEmpty(data) || !data.containsKey("klines")) {
            return Result.fail(NOT_GET_PAGE_ERROR,"");
        }
        JSONArray lines = data.getJSONArray("klines");
        if(!CollectionUtils.isEmpty(lines)){
            for (int i = 0; i < lines.size(); i++) {
                String[] line = lines.getString(i).split(COMMA);
                StockFundsFlow stockFundsFlow = StockFundsFlow.builder()
                        .code(code)
                        .transactionDate(DateUtils.parseLocalDate(line[0], DateFormatEnum.DATE))
                        .mainNetInflow(line[1])
                        .smallNetInflow(line[2])
                        .mediumNetInflow(line[3])
                        .largeNetInflow(line[4])
                        .superLargeNetInflow(line[5])
                        .build();
                LambdaQueryWrapper<StockFundsFlow> wrapper = new LambdaQueryWrapper<StockFundsFlow>()
                        .eq(StockFundsFlow::getCode, code)
                        .eq(StockFundsFlow::getTransactionDate, stockFundsFlow.getTransactionDate());
                if (exist(wrapper)) {
                    update(stockFundsFlow, wrapper);
                }else {
                    save(stockFundsFlow);
                }
            }
        }
        return Result.success();
    }

}
