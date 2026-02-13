package com.optimus.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.optimus.base.Result;
import com.optimus.client.EastMoneyDragonApi;
import com.optimus.client.EastMoneyStockApi;
import com.optimus.constants.MarketType;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.StockDragon;
import com.optimus.mysql.entity.StockDragonDetail;
import com.optimus.mysql.mapper.StockDragonMapper;
import com.optimus.mysql.vo.StockDragonList;
import com.optimus.service.StockDragonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.optimus.constant.Constants.*;
import static java.math.RoundingMode.HALF_UP;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockDragonServiceImpl extends MybatisBaseServiceImpl<StockDragonMapper, StockDragon> implements StockDragonService {

    private final StockDragonMapper stockDragonMapper;


    private final EastMoneyDragonApi eastMoneyDragonApi;


    /**
     * 查询当天龙虎榜列表，按游资分类
     */
    public List<StockDragonList> queryPartnerDragonList(String tradeDate) {
        return stockDragonMapper.queryPartnerDragonList(tradeDate);
    }

    /**
     * 龙虎榜个股列表
     */
    public Result<List<StockDragon>> getStockDragonList(String date) {
        int total = 0, pageNum = 0, pageSize = 100;
        Map<String, StockDragon> map = Maps.newHashMap();
        JSONArray data;
        while (true) {
            data = new JSONArray();
            try {
                JSONObject json = eastMoneyDragonApi.getStockDragonList(date, ++pageNum, pageSize);
                JSONObject result = json.getJSONObject(LABEL_RESULT);
                if (ObjectUtil.isEmpty(result) || !result.containsKey(LABEL_DATA)) {
                    break;
                }
                data = result.getJSONArray(LABEL_DATA);
                if (CollectionUtils.isEmpty(data)) {
                    break;
                }
            } catch (Exception e) {
                log.error(">>>>>getStockDragonList request json error. {}", e.getMessage());
            }
            log.info(">>>>>getStockDragonList {} pageNum:{} data:{}", data, pageNum, data.size());
            for (int i = 0; i < data.size(); i++) {
                ++total;
                try {
                    StockDragon d = JSONObject.parseObject(data.getString(i), StockDragon.class);
                    if (MarketType.contains(d.getCode())) {
                        d.setBuyAmountRatio(BigDecimal.valueOf(d.getBuyAmount()).divide(BigDecimal.valueOf(d.getAccumAmount()), new MathContext(4, HALF_UP)).multiply(HUNDRED));
                        d.setSellAmountRatio(BigDecimal.valueOf(d.getSellAmount()).divide(BigDecimal.valueOf(d.getAccumAmount()), new MathContext(4, HALF_UP)).multiply(HUNDRED));
                        if (!map.containsKey(d.getCode())) {
                            map.put(d.getCode(), d);
                        } else {
                            StockDragon o = map.get(d.getCode());
                            if (o.getAccumAmount() >= d.getAccumAmount()) {
                                o.setExplains(d.getExplains() + " | " + o.getExplains());
                                o.setExplanation(d.getExplanation() + " | " + o.getExplanation());
                            } else {
                                d.setExplains(o.getExplains() + " | " + d.getExplains());
                                d.setExplanation(o.getExplanation() + " | " + d.getExplanation());
                                map.put(d.getCode(), d);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(">>>>>getStockDragonList JSONObject.parseObject error. {}", e.getMessage());
                }
            }
            if (data.size() < pageSize) {
                break;
            }
        }
        log.info(">>>>>getStockDragonList read finished {} total:{} save:{} ", date, total, map.size());
        ArrayList<StockDragon> list = new ArrayList<>(map.values());
        try {
            if (!CollectionUtils.isEmpty(map)) {
                delete(new LambdaQueryWrapper<StockDragon>().eq(StockDragon::getTradeDate, date));
                saveBatch(list);
            }
        } catch (Exception e) {
            log.error(">>>>>getStockDragonList saveBatch error. {}", e.getMessage());
        }
        return Result.success(list);
    }


}
