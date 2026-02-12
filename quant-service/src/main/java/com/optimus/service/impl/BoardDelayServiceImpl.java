package com.optimus.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.optimus.client.EastMoneyBoardApi;
import com.optimus.client.EastMoneyStockApi;
import com.optimus.constants.MarketType;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.BoardDelay;
import com.optimus.mysql.mapper.BoardDelayMapper;
import com.optimus.service.BoardDelayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.optimus.constant.Constants.*;
import static java.math.RoundingMode.HALF_UP;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardDelayServiceImpl extends MybatisBaseServiceImpl<BoardDelayMapper, BoardDelay> implements BoardDelayService {

    private final BoardDelayMapper boardDelayMapper;

    private final EastMoneyBoardApi eastMoneyBoardApi;

    /**
     * 查询每日排名前10的板块，竖型列表，第一行是表头信息
     */
    public List<List<BoardDelay>> queryBoardTradeList(int days, int top) {
        List<BoardDelay> title = boardDelayMapper.queryBoardTradeDate(days);
        Map<LocalDate, List<BoardDelay>> map = Maps.newLinkedHashMap();
        for (BoardDelay boardDelay : title) {
            map.put(boardDelay.getTradeDate(), boardDelayMapper.queryBoardTop(boardDelay.getTradeDate(), top));
        }

        List<List<BoardDelay>> grid = Lists.newArrayList();
        grid.add(title);
        for (int j = 0; j < top; j++) {
            List<BoardDelay> data = Lists.newArrayList();
            for (BoardDelay boardDelay : title) {
                data.add(map.get(boardDelay.getTradeDate()).get(j));
            }
            grid.add(data);
        }
        return grid;
    }

    /**
     * 概念板块列表，按涨跌幅排序
     */
    public void syncBoardTradeList() {
        int total = 0, pageNum = 0, pageSize = 100;
        List<BoardDelay> list = new ArrayList<>();
        while (true) {
            JSONObject json = eastMoneyBoardApi.getBoardTradeList(++pageNum, pageSize, System.currentTimeMillis());
            JSONObject data = json.getJSONObject(LABEL_DATA);
            if (Objects.isNull(data) || !data.containsKey("diff")) {
                break;
            }
            JSONArray array = data.getJSONArray("diff");
            total = data.getInteger(LABEL_TOTAL);
            log.info(">>>>>getBoardTradeList pageNum:{} data:{} total:{}", pageNum, array.size(), total);
            for (int i = 0; i < array.size(); i++) {
                try {
                    BoardDelay boardDelay = JSONObject.parseObject(array.getString(i), BoardDelay.class);
                    syncBoardFundsFlow(boardDelay);
                    saveOrUpdate(boardDelay, new String[]{"code", "trade_date"});
                    list.add(boardDelay);
                } catch (Exception e) {
                    log.error(">>>>>syncBoardTradeList JSONObject.parseObject error. {}", e.getMessage());
                }
            }
            if (array.size() < pageSize) {
                break;
            }
        }
        log.info(">>>>>syncBoardTradeList finished total:{} list:{} ", total, list.size());
    }

    /**
     * 获取最新资金流向
     */
    private void syncBoardFundsFlow(BoardDelay boardDelay) {
        try {
            JSONObject json = eastMoneyBoardApi.getFundsFlow(boardDelay.getCode(), MarketType.getMarketCode(boardDelay.getCode()), System.currentTimeMillis());
            BoardDelay d = JSONObject.parseObject(json.getString(LABEL_DATA), BoardDelay.class);
            BigDecimal acc = BigDecimal.valueOf(boardDelay.getAccumAmount());
            MathContext mc = new MathContext(4, HALF_UP);
            d.setSuperLargeNetRatio(BigDecimal.valueOf(d.getSuperLargeNetIn()).divide(acc, mc).multiply(HUNDRED));
            d.setLargeNetRatio(BigDecimal.valueOf(d.getLargeNetIn()).divide(acc, mc).multiply(HUNDRED));
            d.setMediumNetRatio(BigDecimal.valueOf(d.getMediumNetIn()).divide(acc, mc).multiply(HUNDRED));
            d.setSmallNetRatio(BigDecimal.valueOf(d.getSmallNetIn()).divide(acc, mc).multiply(HUNDRED));

            d.setMainIn(d.getSuperLargeIn() + d.getLargeIn());
            d.setMainOut(d.getSuperLargeOut() + d.getLargeOut());
            d.setMainNetIn(d.getSuperLargeNetIn() + d.getLargeNetIn());
            d.setMainNetRatio(d.getSuperLargeNetRatio().add(d.getLargeNetRatio()));

            d.setRetailIn(d.getMediumIn() + d.getSmallIn());
            d.setRetailOut(d.getMediumOut() + d.getSmallOut());
            d.setRetailNetIn(d.getMediumNetIn() + d.getSmallNetIn());
            d.setRetailNetRatio(d.getMediumNetRatio().add(d.getSmallNetRatio()));
            BeanUtil.copyProperties(d, boardDelay, CopyOptions.create().setIgnoreNullValue(true));
        } catch (Exception e) {
            log.error(">>>>>syncBoardFundsFlow {} {} {}", boardDelay.getCode(), boardDelay.getName(), e.getMessage());
        }
    }
}

