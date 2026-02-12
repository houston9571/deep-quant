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
import com.optimus.mysql.mapper.StockDragonMapper;
import com.optimus.service.StockDragonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.optimus.constant.Constants.LABEL_DATA;
import static com.optimus.constant.Constants.LABEL_RESULT;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockDragonServiceImpl extends MybatisBaseServiceImpl<StockDragonMapper, StockDragon> implements StockDragonService {

    private final StockDragonMapper stockDragonMapper;


    private final EastMoneyDragonApi eastMoneyDragonApi;



    /**
     * 股票基本信息
     *
     * @param code
     * @return
     */
    public Result<Void> getStockInfo(String code) {

        return Result.success();
    }

    /**
     * 龙虎榜个股列表
     */
    public Result<List<StockDragon>> getStockDragonList(String date) {
        int total = 0, pageNum = 0, pageSize = 100;
        Map<String, StockDragon> map = Maps.newHashMap();
        while (true) {
            JSONObject json = eastMoneyDragonApi.getStockDragonList(date, ++pageNum, pageSize);
            JSONObject result = json.getJSONObject(LABEL_RESULT);
            if (ObjectUtil.isEmpty(result) || !result.containsKey(LABEL_DATA)) {
                break;
            }
            JSONArray data = result.getJSONArray(LABEL_DATA);
            if (CollectionUtils.isEmpty(data)) {
                break;
            }
            log.info(">>>>>getStockDragonList {} pageNum:{} data:{}", data, pageNum, data.size());
            for (int i = 0; i < data.size(); i++) {
                ++total;
                try {
                    StockDragon d = JSONObject.parseObject(data.getString(i), StockDragon.class);
                    if (MarketType.contains(d.getCode())) {
                        if (!map.containsKey(d.getCode())) {
                            map.put(d.getCode(), d);
                        } else {
                            StockDragon o = map.get(d.getCode());
                            if (o.getAccumAmount() >= d.getAccumAmount()) {
                                o.setExplains(d.getExplains() + "\n" + o.getExplains());
                                o.setExplanation(d.getExplanation() + "\n" + o.getExplanation());
                            }else {
                                d.setExplains(o.getExplains() + "\n" + d.getExplains());
                                d.setExplanation(o.getExplanation() + "\n" + d.getExplanation());
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
