package com.optimus.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.optimus.client.EastMoneyDragonApi;
import com.optimus.client.EastMoneyStockApi;
import com.optimus.enums.DateFormatEnum;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.BoardDelay;
import com.optimus.mysql.entity.StockDragonDetail;
import com.optimus.mysql.mapper.StockDragonDetailMapper;
import com.optimus.service.StockDragonDetailService;
import com.optimus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;

import static com.optimus.constant.Constants.LABEL_DATA;
import static com.optimus.constant.Constants.LABEL_RESULT;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockDragonDetailServiceImpl extends MybatisBaseServiceImpl<StockDragonDetailMapper, StockDragonDetail> implements StockDragonDetailService {

    private final StockDragonDetailMapper stockDragonDetailMapper;


    private final EastMoneyDragonApi eastMoneyDragonApi;


    /**
     * 查询当天龙虎榜列表，按游资分类
     */
    public List<StockDragonDetail> queryDragonDetail() {
        return stockDragonDetailMapper.queryDragonDetail();
    }

    /**
     * 龙虎榜个股买卖详情
     */
    public int getStockDragonDetail(LocalDate date, String code, String name) {
        Map<String, StockDragonDetail> map = Maps.newHashMap();
        String tradeDate = DateUtils.format(date, DateFormatEnum.DATE);
        JSONObject buy = eastMoneyDragonApi.getStockDragonListBuy(tradeDate, code).getJSONObject(LABEL_RESULT);
        JSONObject sell = eastMoneyDragonApi.getStockDragonListSell(tradeDate, code).getJSONObject(LABEL_RESULT);
        JSONArray data = new JSONArray();
        if (ObjectUtil.isNotNull(buy) && ObjectUtil.isNotNull(sell) && buy.containsKey(LABEL_DATA) && sell.containsKey(LABEL_DATA)) {
            data = buy.getJSONArray(LABEL_DATA);
            if (!CollectionUtils.isEmpty(data)) {
                data.fluentAddAll(sell.getJSONArray(LABEL_DATA));
                for (int i = 0; i < data.size(); i++) {
                    try {
                        StockDragonDetail d = JSONObject.parseObject(data.getString(i), StockDragonDetail.class);
                        d.setName(name);
                        // 自然人、其他自然人、机构投资者、中小投资者、深股通投资者等，这些code一样，所有要用名称去重
                        if (!map.containsKey(d.getDeptName()) || map.get(d.getDeptName()).getTradeId() < d.getTradeId()) {
                            map.put(d.getDeptName(), d);
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
