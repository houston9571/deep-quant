package com.optimus.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.optimus.base.Result;
import com.optimus.client.EastMoneyH5Api;
import com.optimus.client.EmDatacenterApi;
import com.optimus.client.EmPush2delayApi;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.BoardInfo;
import com.optimus.mysql.entity.StockBoard;
import com.optimus.mysql.entity.StockInfo;
import com.optimus.mysql.mapper.StockInfoMapper;
import com.optimus.service.BoardInfoService;
import com.optimus.service.DragonListService;
import com.optimus.service.StockBoardService;
import com.optimus.service.StockInfoService;
import com.optimus.sprider.SpriderTemplateParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.optimus.constants.MarketType.codeMap;
import static com.optimus.constants.MarketType.getMarket;
import static com.optimus.enums.ErrorCode.NOT_GET_PAGE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class DragonListServiceImpl extends MybatisBaseServiceImpl<StockInfoMapper, StockInfo> implements DragonListService {

    private final StockInfoMapper stockInfoMapper;

    private final BoardInfoService boardInfoService;

    private final StockBoardService stockBoardService;

    @Autowired
    EmPush2delayApi eastMoneyApi;
    @Autowired
    EastMoneyH5Api eastMoneyH5Api;

    @Autowired
    EmDatacenterApi emDatacenterApi;


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
     * 个股龙虎榜列表
     *
     * @param code
     * @return
     */
    public Result<Void> getStockDragon(String code) {
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
            stockBoardList.add(StockBoard.builder().code(code).bcode(bcode).build());
        }
        stockBoardService.delete(new LambdaQueryWrapper<StockBoard>().eq(StockBoard::getCode, code));
        stockBoardService.saveBatch(stockBoardList);
        return Result.success();
    }


}
