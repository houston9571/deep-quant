package com.optimus.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.optimus.base.Result;
import com.optimus.client.EmPush2delayApi;
import com.optimus.client.EastMoneyH5Api;
import com.optimus.client.EmDatacenterApi;
import com.optimus.core.MarketType;
import com.optimus.core.StockCodeUtils;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.BoardInfo;
import com.optimus.mysql.entity.StockBoard;
import com.optimus.mysql.entity.StockInfo;
import com.optimus.mysql.mapper.StockInfoMapper;
import com.optimus.service.BoardInfoService;
import com.optimus.service.StockBoardService;
import com.optimus.service.StockInfoService;
import com.optimus.sprider.SpriderTemplateParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.optimus.core.MarketType.codeMap;
import static com.optimus.core.MarketType.getMarket;
import static com.optimus.core.StockConstants.*;
import static com.optimus.enums.ErrorCode.NOT_GET_PAGE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockInfoServiceImpl extends MybatisBaseServiceImpl<StockInfoMapper, StockInfo> implements StockInfoService {

    private final StockInfoMapper stockInfoMapper;

    private final BoardInfoService boardInfoService;

    private final StockBoardService stockBoardService;

    @Autowired
    EmPush2delayApi eastMoneyApi;
    @Autowired
    EastMoneyH5Api eastMoneyH5Api;

    @Autowired
    EmDatacenterApi emDatacenterApi;

    @Autowired
    SpriderTemplateParser spiderTemplateParser;


    /**
     * 股票基本信息
     *
     * @param code
     * @return
     */
    public Result<Void> getStockInfo(String code) {
        String tpl = code.length() == 6 ? "S01-overview.json" : "S01-overview-hk.json";
        List<Map<String, String>[]> factors = spiderTemplateParser.parserAsMap(tpl, codeMap(code));
        if (CollectionUtils.isEmpty(factors)) {
            return Result.fail(NOT_GET_PAGE_ERROR, "tlp:" + tpl);
        }
        Map<String, String>[] maps = factors.get(0);
        if (ArrayUtils.isEmpty(maps)) {
            return Result.fail(NOT_GET_PAGE_ERROR, "factors:" + JSONObject.toJSONString(factors));
        }

        StockInfo stockInfo = BeanUtil.fillBeanWithMap(maps[0], new StockInfo(), true);
        if (exist(new LambdaQueryWrapper<StockInfo>().eq(StockInfo::getCode, stockInfo.getCode()))) {
            updateById(stockInfo);
        } else {
            save(stockInfo);
        }
        return Result.success();
    }

    /**
     * 所属概念
     *
     * @param code
     * @return
     */
    public Result<Void> getStockBoards(String code) {
        JSONObject json = emDatacenterApi.getBoards(code, getMarket(code));
        JSONObject result = json.getJSONObject("result");
        if (ObjectUtil.isEmpty(result) || !result.containsKey("data")) {
            return Result.fail(NOT_GET_PAGE_ERROR, "getBoards result is null");
        }
        JSONArray data = result.getJSONArray("data");
        if (CollectionUtils.isEmpty(data)) {
            return Result.fail(NOT_GET_PAGE_ERROR, "getBoards data is null");
        }
        JSONObject d;
        List<StockBoard> stockBoardList = Lists.newArrayList();
        for (int i = 0; i < data.size(); i++) {
            d = data.getJSONObject(i);
            String bcode = d.getString("NEW_BOARD_CODE");
            if (!boardInfoService.exist(new LambdaQueryWrapper<BoardInfo>().eq(BoardInfo::getCode, bcode))) {
                boardInfoService.save(BoardInfo.builder().code(bcode).name(d.getString("BOARD_NAME")).type(d.getString("BOARD_TYPE")).level(d.getString("BOARD_LEVEL")).build());
            }
            stockBoardList.add(StockBoard.builder().scode(code).bcode(bcode).build());
        }
        stockBoardService.delete(new LambdaQueryWrapper<StockBoard>().eq(StockBoard::getScode, code));
        stockBoardService.saveBatch(stockBoardList);
        return Result.success();
    }


}
