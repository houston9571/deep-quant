package com.optimus.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.optimus.base.Result;
import com.optimus.client.EastMoneyDragonApi;
import com.optimus.constants.MarketType;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.DragonDept;
import com.optimus.mysql.entity.DragonStock;
import com.optimus.mysql.entity.OrgDept;
import com.optimus.mysql.mapper.DragonDeptMapper;
import com.optimus.mysql.mapper.DragonStockMapper;
import com.optimus.mysql.vo.DragonStockList;
import com.optimus.service.DragonDeptService;
import com.optimus.service.DragonStockService;
import com.optimus.service.OrgDeptService;
import com.optimus.thread.Threads;
import com.optimus.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.optimus.constant.Constants.*;
import static java.math.RoundingMode.HALF_UP;

@Slf4j
@Service
@RequiredArgsConstructor
public class DragonDeptServiceImpl extends MybatisBaseServiceImpl<DragonDeptMapper, DragonDept> implements DragonDeptService {

    private final DragonDeptMapper dragonDeptMapper;


    private final EastMoneyDragonApi eastMoneyDragonApi;

    private final OrgDeptService orgDeptService;

    /**
     * 查询当天龙虎榜营业部列表
     */
    public List<DragonDept> queryDragonDeptList(String tradeDate) {
        return null;
    }

    /**
     * 龙虎榜个股营业部列表
     */
    public Result<List<DragonDept>> syncDragonDeptList(String date) {
        int total = 0, pageNum = 0, pageSize = 100;
        Set<OrgDept> orgDeptSet = Sets.newHashSet();
        ArrayList<DragonDept> list = Lists.newArrayList();
        JSONArray data = new JSONArray();
        while (true) {
            ++pageNum;
            try {
                data = syncDragonDeptList(date, pageNum, pageSize);
            } catch (Exception e) {
                try {
                    data = syncDragonDeptList(date, pageNum, pageSize);
                } catch (Exception e1) {
                    Threads.sleep(NumberUtils.random(5000));
                    log.error(">>>>>getDragonDeptList request json error. {}", e.getMessage());
                }
            }
            if (CollectionUtils.isEmpty(data)) {
                break;
            }
            log.info(">>>>>getDragonDeptList {} pageNum:{} data:{}", data, pageNum, data.size());
            total += data.size();
            for (int i = 0; i < data.size(); i++) {
                ++total;
                try {
                    DragonDept d = JSONObject.parseObject(data.getString(i), DragonDept.class);
                    String[] sc = d.getBuyStock().split("\\s+");
                    String[] sn = d.getBuyStockName().split("\\s+");
                    JSONObject stocks = new JSONObject();
                    for (int j = 0; j < sc.length; j++) {
                        stocks.put(sc[j].substring(0, 6), sn[j]);
                    }
                    d.setBuyStocks(stocks.toJSONString());
                    d.setName(d.getName().replace("证券营业部", "").replace("营业部", ""));
                    list.add(d);
                    orgDeptSet.add(OrgDept.builder().code(d.getCode()).name(d.getName()).nameFull(d.getNameFull()).build());
                } catch (Exception e) {
                    log.error(">>>>>getDragonDeptList JSONObject.parseObject error. {} {}", data.getString(i), e.getMessage());
                }
            }
            if (data.size() < pageSize) {
                break;
            }
            Threads.sleep(NumberUtils.random(5000));
        }
        log.info(">>>>>getDragonDeptList read finished {} total:{} save:{} ", date, total, list.size());
        try {
            if (!CollectionUtils.isEmpty(list)) {
                saveOrUpdateBatch(list, new String[]{"code", "trade_date"});
                orgDeptService.saveBatch(orgDeptSet);
            }
        } catch (Exception e) {
            log.error(">>>>>getDragonDeptList saveBatch error. {}", e.getMessage());
        }
        return Result.success(list);
    }

    private JSONArray syncDragonDeptList(String date, int pageNum, int pageSize) {
        JSONObject json = eastMoneyDragonApi.syncDragonDeptList(date, pageNum, pageSize);
        JSONObject result = json.getJSONObject(LABEL_RESULT);
        if (ObjectUtil.isEmpty(result) || !result.containsKey(LABEL_DATA)) {
            return null;
        }
        return result.getJSONArray(LABEL_DATA);
    }

}
