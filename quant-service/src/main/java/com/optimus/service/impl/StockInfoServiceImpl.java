package com.optimus.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.optimus.base.Result;
import com.optimus.client.EastMoneyStockApi;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.ConceptInfo;
import com.optimus.mysql.entity.ConceptStock;
import com.optimus.mysql.entity.StockInfo;
import com.optimus.mysql.mapper.StockInfoMapper;
import com.optimus.service.ConceptInfoService;
import com.optimus.service.ConceptStockService;
import com.optimus.service.StockInfoService;
import com.optimus.sprider.SpriderTemplateParser;
import com.optimus.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.optimus.constant.Constants.LABEL_DATA;
import static com.optimus.constant.Constants.LABEL_RESULT;
import static com.optimus.components.MarketType.*;
import static com.optimus.enums.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockInfoServiceImpl extends MybatisBaseServiceImpl<StockInfoMapper, StockInfo> implements StockInfoService {

    private final StockInfoMapper stockInfoMapper;

    private final ConceptInfoService conceptInfoService;

    private final ConceptStockService conceptStockService;

    private final EastMoneyStockApi eastMoneyStockApi;


    @Autowired
    SpriderTemplateParser spiderTemplateParser;


    /**
     * 股票基本信息
     */
    public Result<StockInfo> syncStockInfo(String stockCode) {
        try {
            if (exist(new LambdaQueryWrapper<StockInfo>().eq(StockInfo::getStockCode, stockCode).gt(StockInfo::getUpdateTime, LocalDateTime.of(LocalDate.now(), LocalTime.MIN)))) {
                return Result.fail(DATA_UPDATED, "getStockInfo", stockCode);
            }

            String tpl = stockCode.length() == 6 ? "S01-overview.json" : "S01-overview-hk.json";
            if (StringUtil.equals(getMarket(stockCode), MARKET_BJ)) {
                return Result.fail(REQUEST_UNSUPPORTED);
            }
            List<Map<String, String>[]> factors = spiderTemplateParser.parserAsMap(tpl, stockCodeMap(stockCode));
            if (CollectionUtils.isEmpty(factors)) {
                return Result.fail(NOT_GET_PAGE_ERROR, "tlp:" + tpl);
            }
            Map<String, String>[] maps = factors.get(0);
            if (ArrayUtils.isEmpty(maps)) {
                return Result.fail(NOT_GET_PAGE_ERROR, "factors:" + JSONObject.toJSONString(factors));
            }

            StockInfo stockInfo = BeanUtil.fillBeanWithMap(maps[0], new StockInfo(), true);
            saveOrUpdate(stockInfo, new String[]{"stock_code"});
            return Result.success(stockInfo);
        } catch (Exception e) {
            log.error(">>>>>getStockInfo error. {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 个股所属概念
     */
    public Result<Void> syncStockConceptList(String stockCode) {
        try {
            JSONObject json = eastMoneyStockApi.syncStockConcepts(stockCode, getMarket(stockCode));
            JSONObject result = json.getJSONObject(LABEL_RESULT);
            if (ObjectUtil.isEmpty(result) || !result.containsKey(LABEL_DATA)) {
                return Result.fail(NOT_GET_PAGE_ERROR, "getStockConceptList result is null");
            }
            JSONArray data = result.getJSONArray(LABEL_DATA);
            if (CollectionUtils.isEmpty(data)) {
                return Result.fail(NOT_GET_PAGE_ERROR, "getStockConceptList data is null");
            }
            JSONObject d;
            List<ConceptStock> conceptStockList = Lists.newArrayList();
            for (int i = 0; i < data.size(); i++) {
                d = data.getJSONObject(i);
                String conceptCode = d.getString("NEW_BOARD_CODE");
                if (!conceptInfoService.exist(new LambdaQueryWrapper<ConceptInfo>().eq(ConceptInfo::getConceptCode, conceptCode))) {
                    // 添加不存在的概念名称
                    conceptInfoService.save(ConceptInfo.builder().conceptCode(conceptCode).conceptName(d.getString("BOARD_NAME")).type(d.getString("BOARD_TYPE")).level(d.getString("BOARD_LEVEL")).build());
                }
                conceptStockList.add(ConceptStock.builder().stockCode(stockCode).conceptCode(conceptCode).build());
            }
            conceptStockService.delete(new LambdaQueryWrapper<ConceptStock>().eq(ConceptStock::getStockCode, stockCode));
            conceptStockService.saveBatch(conceptStockList);
        } catch (Exception e) {
            log.error(">>>>>getStockConceptList error. {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
        return Result.success();
    }


}
