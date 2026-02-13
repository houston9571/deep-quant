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
import com.optimus.mysql.entity.BoardInfo;
import com.optimus.mysql.entity.BoardStock;
import com.optimus.mysql.entity.StockInfo;
import com.optimus.mysql.mapper.StockInfoMapper;
import com.optimus.service.BoardInfoService;
import com.optimus.service.BoardStockService;
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
import static com.optimus.constants.MarketType.*;
import static com.optimus.enums.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockInfoServiceImpl extends MybatisBaseServiceImpl<StockInfoMapper, StockInfo> implements StockInfoService {

    private final StockInfoMapper stockInfoMapper;

    private final BoardInfoService boardInfoService;

    private final BoardStockService boardStockService;

    private final EastMoneyStockApi eastMoneyStockApi;


    @Autowired
    SpriderTemplateParser spiderTemplateParser;


    /**
     * 股票基本信息
     *
     * @param code
     * @return
     */
    public Result<StockInfo> getStockInfo(String code) {
        try {
            if (exist(new LambdaQueryWrapper<StockInfo>().eq(StockInfo::getCode, code).gt(StockInfo::getUpdateTime, LocalDateTime.of(LocalDate.now(), LocalTime.MIN)))) {
                return Result.fail(DATA_UPDATED, "getStockInfo", code);
            }

            String tpl = code.length() == 6 ? "S01-overview.json" : "S01-overview-hk.json";
            if (StringUtil.equals(getMarket(code), MARKET_BJ)) {
                return Result.fail(REQUEST_UNSUPPORTED);
            }
            List<Map<String, String>[]> factors = spiderTemplateParser.parserAsMap(tpl, codeMap(code));
            if (CollectionUtils.isEmpty(factors)) {
                return Result.fail(NOT_GET_PAGE_ERROR, "tlp:" + tpl);
            }
            Map<String, String>[] maps = factors.get(0);
            if (ArrayUtils.isEmpty(maps)) {
                return Result.fail(NOT_GET_PAGE_ERROR, "factors:" + JSONObject.toJSONString(factors));
            }

            StockInfo stockInfo = BeanUtil.fillBeanWithMap(maps[0], new StockInfo(), true);
            saveOrUpdate(stockInfo, new String[]{"code"});
            return Result.success(stockInfo);
        } catch (Exception e) {
            log.error(">>>>>getStockInfo error. {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 所属概念
     *
     * @param code
     * @return
     */
    public Result<Void> getStockBoardList(String code) {
        try {
            JSONObject json = eastMoneyStockApi.getBoards(code, getMarket(code));
            JSONObject result = json.getJSONObject(LABEL_RESULT);
            if (ObjectUtil.isEmpty(result) || !result.containsKey(LABEL_DATA)) {
                return Result.fail(NOT_GET_PAGE_ERROR, "getStockBoardList result is null");
            }
            JSONArray data = result.getJSONArray(LABEL_DATA);
            if (CollectionUtils.isEmpty(data)) {
                return Result.fail(NOT_GET_PAGE_ERROR, "getStockBoardList data is null");
            }
            JSONObject d;
            List<BoardStock> boardStockList = Lists.newArrayList();
            for (int i = 0; i < data.size(); i++) {
                d = data.getJSONObject(i);
                String bcode = d.getString("NEW_BOARD_CODE");
                if (!boardInfoService.exist(new LambdaQueryWrapper<BoardInfo>().eq(BoardInfo::getCode, bcode))) {
                    // 添加不存在的概念名称
                    boardInfoService.save(BoardInfo.builder().code(bcode).name(d.getString("BOARD_NAME")).type(d.getString("BOARD_TYPE")).level(d.getString("BOARD_LEVEL")).build());
                }
                boardStockList.add(BoardStock.builder().code(code).bcode(bcode).build());
            }
            boardStockService.delete(new LambdaQueryWrapper<BoardStock>().eq(BoardStock::getCode, code));
            boardStockService.saveBatch(boardStockList);
        } catch (Exception e) {
            log.error(">>>>>getStockBoardList error. {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
        return Result.success();
    }


}
