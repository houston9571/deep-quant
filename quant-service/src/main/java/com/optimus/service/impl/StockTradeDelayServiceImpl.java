package com.optimus.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.optimus.base.Result;
import com.optimus.client.EastMoneyH5Api;
import com.optimus.client.EmPush2delayApi;
import com.optimus.constants.MarketType;
import com.optimus.constants.StockCodeUtils;
import com.optimus.enums.DateFormatEnum;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.StockTradeDelay;
import com.optimus.mysql.entity.StockTradeRealTime;
import com.optimus.mysql.mapper.StockTradeDelayMapper;
import com.optimus.service.StockTradeDelayService;
import com.optimus.thread.Threads;
import com.optimus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.optimus.constant.Constants.LABEL_DATA;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockTradeDelayServiceImpl extends MybatisBaseServiceImpl<StockTradeDelayMapper, StockTradeDelay> implements StockTradeDelayService {

    @Autowired
    EmPush2delayApi eastMoneyApi;
    @Autowired
    EastMoneyH5Api eastMoneyH5Api;

    private final StockTradeDelayMapper stockTradeDelayMapper;


    /**
     * 获取股票实时交易列表
     */
    public Result<Void> syncStockTradeList() {
        String fields = "f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f14,f15,f16,f17,f18,f20,f21,f23,f24,f34,f35,f37,f40,f41,f45,f46,f48,f49,f57,f64,f65,f66,f69,f70,f71,f72,f75,f76,f77,f78,f81,f82,f83,f84,f87,f109,f129,f297";
        Threads.asyncExecute(() -> {
            int total = 0, pageNum = 0, pageSize = 100;
            List<StockTradeDelay> list = new ArrayList<>(5800);
            while (true) {
                JSONObject json = eastMoneyApi.getStockTradeList(fields, System.currentTimeMillis(), ++pageNum, pageSize);
                JSONObject data = json.getJSONObject(LABEL_DATA);
                if (Objects.isNull(data) || !data.containsKey("diff")) {
                    break;
                }
                JSONArray array = data.getJSONArray("diff");
                log.info(">>>>>syncStockTradeList pageNum:{} pageSize:{} data:{}", pageNum, pageSize, array.size());
                for (int i = 0; i < array.size(); i++) {
                    ++total;
                    try {
                        StockTradeDelay d = JSONObject.parseObject(array.getString(i), StockTradeDelay.class);
                        if (MarketType.contains(d.getCode())) {
                            d.setLimitUp(d.getOpenPrice().add(d.getOpenPrice().multiply(MarketType.getChangeLimit(d.getCode()))));
                            d.setLimitDown(d.getOpenPrice().subtract(d.getOpenPrice().multiply(MarketType.getChangeLimit(d.getCode()))));
                            d.setMainIn(d.getSuperLargeIn() + d.getLargeIn());
                            d.setMainOut(d.getSuperLargeOut() + d.getLargeOut());
                            d.setMainNetIn(d.getSuperLargeNetIn() + d.getLargeNetIn());
                            d.setMainNetRatio(d.getSuperLargeNetRatio().add(d.getLargeNetRatio()));
                            d.setRetailIn(d.getMediumIn() + d.getSmallIn());
                            d.setRetailOut(d.getMediumOut() + d.getSmallOut());
                            d.setRetailNetIn(d.getMediumNetIn() + d.getSmallNetIn());
                            d.setRetailNetRatio(d.getMediumNetRatio().add(d.getSmallNetRatio()));
                            list.add(d);
                        }
                    } catch (Exception e) {
                        log.error(">>>>>syncStockTradeList JSONObject.parseObject error. {}", e.getMessage());
                    }
                }
            }
            try {
                log.info(">>>>>syncStockTradeList read finished total:{} list:{} ", total, list.size());
                LambdaQueryWrapper<StockTradeDelay> wrapper = new LambdaQueryWrapper<StockTradeDelay>().eq(StockTradeDelay::getTransactionDate, list.get(0).getTransactionDate());
                delete(wrapper);
                saveBatch(list);
            } catch (Exception e) {
                log.error(">>>>>syncStockTradeList saveBatch error. {}", e.getMessage());
            }
        });
        return Result.success();
    }
}

