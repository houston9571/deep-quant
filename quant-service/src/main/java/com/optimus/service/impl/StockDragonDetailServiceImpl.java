package com.optimus.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.optimus.base.Result;
import com.optimus.client.EastMoneyApi;
import com.optimus.constants.MarketType;
import com.optimus.enums.DateFormatEnum;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.StockDragon;
import com.optimus.mysql.entity.StockDragonDetail;
import com.optimus.mysql.mapper.StockDragonDetailMapper;
import com.optimus.mysql.mapper.StockDragonMapper;
import com.optimus.service.StockDragonDetailService;
import com.optimus.service.StockDragonService;
import com.optimus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.optimus.constant.Constants.LABEL_DATA;
import static com.optimus.constant.Constants.LABEL_RESULT;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockDragonDetailServiceImpl extends MybatisBaseServiceImpl<StockDragonDetailMapper, StockDragonDetail> implements StockDragonDetailService {

    private final StockDragonDetailMapper stockDragonDetailMapper;


    @Autowired
    EastMoneyApi eastMoneyApi;


    /**
     * 个股龙虎榜买卖详情
     */
    public int getStockDragonDetail(LocalDate date, String code, String name) {
        Map<String, StockDragonDetail> map = Maps.newHashMap();
        String tradeDate = DateUtils.format(date, DateFormatEnum.DATE);
        JSONObject buy = eastMoneyApi.getStockDragonListBuy(tradeDate, code).getJSONObject(LABEL_RESULT);
        JSONObject sell = eastMoneyApi.getStockDragonListSell(tradeDate, code).getJSONObject(LABEL_RESULT);
        JSONArray data = new JSONArray();
        if (ObjectUtil.isNotNull(buy) && ObjectUtil.isNotNull(sell) && buy.containsKey(LABEL_DATA) && sell.containsKey(LABEL_DATA)) {
            data = buy.getJSONArray(LABEL_DATA);
            if (!CollectionUtils.isEmpty(data)) {
                data.fluentAddAll(sell.getJSONArray(LABEL_DATA));
                for (int i = 0; i < data.size(); i++) {
                    try {
                        StockDragonDetail d = JSONObject.parseObject(data.getString(i), StockDragonDetail.class);
                        d.setName(name);
                        if (!map.containsKey(d.getDeptCode()) || map.get(d.getDeptCode()).getTradeId() < d.getTradeId()) {
                            map.put(d.getDeptCode(), d);
                        }
                    } catch (Exception e) {
                        log.error(">>>>>getStockDragonDetail JSONObject.parseObject error. {}", e.getMessage());
                    }
                }
            }
        }
        log.info(">>>>>getStockDragonDetail: {} {} {} total:{} save:{}", date, code, name, data.size(), map.size());
        try {
            if (!CollectionUtils.isEmpty(map)) {
                ArrayList<StockDragonDetail> list = new ArrayList<>(map.values());
                list.sort(Comparator.comparingLong(StockDragonDetail::getNetBuyAmount).reversed());
                saveOrUpdateBatch(list, new String[]{"code", "trade_date", "dept_code"});
            }
        } catch (Exception e) {
            log.error(">>>>>getStockDragonDetail saveBatch error. {}", e.getMessage());
        }
        return map.size();
    }

}
