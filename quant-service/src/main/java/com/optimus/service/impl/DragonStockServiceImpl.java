package com.optimus.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.optimus.base.Result;
import com.optimus.client.EastMoneyDragonApi;
import com.optimus.constants.MarketType;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.BoardDelay;
import com.optimus.mysql.entity.DragonStock;
import com.optimus.mysql.mapper.DragonStockMapper;
import com.optimus.mysql.vo.DragonStockList;
import com.optimus.service.DragonStockService;
import com.optimus.thread.Threads;
import com.optimus.utils.DateUtils;
import com.optimus.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.optimus.constant.Constants.*;
import static java.math.RoundingMode.HALF_UP;

@Slf4j
@Service
@RequiredArgsConstructor
public class DragonStockServiceImpl extends MybatisBaseServiceImpl<DragonStockMapper, DragonStock> implements DragonStockService {

    private final DragonStockMapper dragonStockMapper;


    private final EastMoneyDragonApi eastMoneyDragonApi;


    /**
     * 查询当天龙虎榜列表，按游资分类
     */
    public List<DragonStockList> queryDragonPartnerList(String tradeDate) {
        return dragonStockMapper.queryDragonPartnerList(tradeDate);
    }


    /**
     * 龙虎榜个股列表
     */
    public Result<List<DragonStock>> syncDragonStockList(String date) {
        int total = 0, pageNum = 0, pageSize = 100;
        Map<String, DragonStock> map = Maps.newHashMap();
        JSONArray data = new JSONArray();
        while (true) {
            ++pageNum;
            try {
                data = syncDragonStockList(date, pageNum, pageSize);
            } catch (Exception e) {
                try {
                    data = syncDragonStockList(date, pageNum, pageSize);
                } catch (Exception e1) {
                    Threads.sleep(NumberUtils.random(5000));
                    log.error(">>>>>getDragonStockList request json error. {}", e.getMessage());
                }
            }
            if (CollectionUtils.isEmpty(data)) {
                break;
            }
            log.info(">>>>>getDragonStockList {} pageNum:{} data:{}", data, pageNum, data.size());
            total += data.size();
            for (int i = 0; i < data.size(); i++) {
                ++total;
                try {
                    DragonStock d = JSONObject.parseObject(data.getString(i), DragonStock.class);
                    if (MarketType.contains(d.getCode())) {
                        d.setBuyAmountRatio(BigDecimal.valueOf(d.getBuyAmount()).divide(BigDecimal.valueOf(d.getAccumAmount()), new MathContext(4, HALF_UP)).multiply(HUNDRED));
                        d.setSellAmountRatio(BigDecimal.valueOf(d.getSellAmount()).divide(BigDecimal.valueOf(d.getAccumAmount()), new MathContext(4, HALF_UP)).multiply(HUNDRED));
                        if (!map.containsKey(d.getCode())) {
                            map.put(d.getCode(), d);
                        } else {
                            DragonStock o = map.get(d.getCode());
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
                    log.error(">>>>>getDragonStockList JSONObject.parseObject error. {} {}", data.getString(i), e.getMessage());
                }
            }
            if (data.size() < pageSize) {
                break;
            }
        }
        log.info(">>>>>getDragonStockList read finished {} total:{} save:{} ", date, total, map.size());
        ArrayList<DragonStock> list = new ArrayList<>(map.values());
        try {
            if (!CollectionUtils.isEmpty(map)) {
                delete(new LambdaQueryWrapper<DragonStock>().eq(DragonStock::getTradeDate, date));
                saveBatch(list);
            }
        } catch (Exception e) {
            log.error(">>>>>getDragonStockList saveBatch error. {}", e.getMessage());
        }
        return Result.success(list);
    }

    private JSONArray syncDragonStockList(String date, int pageNum, int pageSize) {
        JSONObject json = eastMoneyDragonApi.syncDragonStockList(date, pageNum, pageSize);
        JSONObject result = json.getJSONObject(LABEL_RESULT);
        if (ObjectUtil.isEmpty(result) || !result.containsKey(LABEL_DATA)) {
            return null;
        }
        return result.getJSONArray(LABEL_DATA);
    }


}
