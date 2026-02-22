package com.optimus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.StockKlineDaily;
import com.optimus.mysql.entity.StockTechDaily;
import com.optimus.mysql.mapper.StockTechDailyMapper;
import com.optimus.service.StockKlineDailyService;
import com.optimus.service.StockTechDailyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.optimus.service.impl.StockIndicatorDailyCalculator.*;
import static com.optimus.constant.Constants.ROUND_MODE;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockTechDailyServiceImpl extends MybatisBaseServiceImpl<StockTechDailyMapper, StockTechDaily> implements StockTechDailyService {

    private final StockTechDailyMapper stockTechDailyMapper;

    private final StockKlineDailyService stockKlineDailyService;


    /**
     * 全流程超短线量化体系 数据读取→指标计算→背离判断→共振筛选→结果入库
     * <p>
     * 多指标共振规则（可直接修改代码中的规则）
     * 短线买入共振（匹配≥5 条触发）
     * 1.MA5 上穿 MA10（均线趋势）
     * 2.MACD 金叉（价格趋势）
     * 3.RSI14 在 30~70 且向上（强弱适中）
     * 4.KDJ 金叉且 J<80（未超买）
     * 5.CCI 上穿 100 或 >-100 向上（趋势转强）
     * 6.VMACD 金叉（量能同步）
     * 7.OBV 突破 MA20（资金进场）
     * 8.股价站 60 日平均成本（成本支撑）
     * 短线卖出共振（匹配≥3 条触发）
     * 1.MA5 下穿 MA10（均线走弱）
     * 2.MACD 死叉（价格趋势反转）
     * 3.RSI>80 或 KDJ J>90（超买）
     * 4.任意顶背离（趋势反转信号）
     * 实战优化建议
     * 1.信号过滤：共振评分 < 60 的信号可过滤（避免弱共振）；
     * 2.时间窗口：仅在 9:30-10:30、14:00-14:30（A 股波动关键时段）触发信号；
     * 3.仓位控制：买入信号评分 80+（强共振）可满仓，60-80 分半仓，<60 分轻仓；
     * 4.规则迭代：根据实盘结果调整规则数量和阈值（如熊市可提高买入规则匹配数）。
     * 规则迭代建议
     * 每周回测：统计信号胜率，淘汰胜率 <50% 的规则（如熊市可去掉 WR10>80 规则）；
     * 参数优化：不同板块参数不同（创业板 WR10 阈值改为 75，主板保持 80）；
     * 新增维度：加入「涨停基因」「龙虎榜」数据，提升信号胜率。
     * <p>
     * 价格跌破5日线=强制止损
     */
    public void calculateTechAndSave(String stockCode) {
        // 1. 从数据库读取日线数据（按时间升序）
        List<StockKlineDaily> barList = stockKlineDailyService.queryList(null, new QueryWrapper<StockKlineDaily>().eq("stock_code", stockCode).ge("trade_date", "2025-10-01"));
        if (barList.size() < 60) { // 至少60天数据才能计算全量指标
            System.out.println("股票" + stockCode + "日线数据不足，跳过计算");
            return;
        }

        // 2. 计算各指标
        List<BigDecimal> ma3List = calculateMA(barList, 3);
        List<BigDecimal> ma5List = calculateMA(barList, 5);
        List<BigDecimal> ma10List = calculateMA(barList, 10);
        List<BigDecimal> ma20List = calculateMA(barList, 20);
        List<BigDecimal[]> macdList = calculateMACD(barList);
        List<BigDecimal> rsi3List = calculateRSI(barList, 3);   // 原 6/14 → 3/9
        List<BigDecimal> rsi9List = calculateRSI(barList, 9);
        List<BigDecimal[]> kdjList = calculateKDJ(barList);
        List<BigDecimal[]> bollList = calculateBOLL(barList);
        List<BigDecimal> atrList = calculateATR(barList);
        List<Long> obvList = calculateOBV(barList);
        List<Long> obvMa10List = calculateOBVMA(obvList);
        List<BigDecimal> wr6List = calculateWR(barList);
        List<BigDecimal> cciList = calculateCCI(barList);
        List<BigDecimal> mfiList = calculateMFI(barList);
        List<BigDecimal[]> vmacdList = calculateVMACD(barList);
        List<BigDecimal> avgCostList = calculateAvgCost(barList);
        List<BigDecimal> costConcentrationList = calculateCostConcentration(barList, avgCostList);

        // 3. 组装指标数据
        List<StockTechDaily> techList = new ArrayList<>();
        for (int i = 0; i < barList.size(); i++) {
            StockKlineDaily bar = barList.get(i);
            StockTechDaily tech = new StockTechDaily();
            tech.setStockCode(stockCode);
            tech.setTradeDate(bar.getTradeDate());

            // 均线
            tech.setMa3(ma3List.get(i));
            tech.setMa5(ma5List.get(i));
            tech.setMa10(ma10List.get(i));
            tech.setMa20(ma20List.get(i));

            // MACD
            if (macdList.get(i) != null) {
                tech.setMacdDif(macdList.get(i)[0]);
                tech.setMacdDea(macdList.get(i)[1]);
                tech.setMacdBar(macdList.get(i)[2]);
                tech.setMacdDiff(tech.getMacdDif().subtract(tech.getMacdDea()));
            }

            // RSI
            tech.setRsi3(rsi3List.get(i));
            tech.setRsi9(rsi9List.get(i));

            // KDJ
            if (kdjList.get(i) != null) {
                tech.setKdjK(kdjList.get(i)[0]);
                tech.setKdjD(kdjList.get(i)[1]);
                tech.setKdjJ(kdjList.get(i)[2]);
            }

            // 布林带
            if (bollList.get(i) != null) {
                tech.setBollMid(bollList.get(i)[0]);
                tech.setBollUpper(bollList.get(i)[1]);
                tech.setBollLower(bollList.get(i)[2]);
                // 计算布林带状态（1=收口,2=开口,3=正常）
                BigDecimal width = bollList.get(i)[1].subtract(bollList.get(i)[2]);
                if (i > 0 && bollList.get(i - 1) != null) {
                    BigDecimal prevWidth = bollList.get(i - 1)[1].subtract(bollList.get(i - 1)[2]);
                    if (width.compareTo(prevWidth.multiply(BigDecimal.valueOf(0.9))) < 0) {
                        tech.setBollStatus(1); // 收口
                    } else if (width.compareTo(prevWidth.multiply(BigDecimal.valueOf(1.1))) > 0) {
                        tech.setBollStatus(2); // 开口
                    } else {
                        tech.setBollStatus(3); // 正常
                    }
                }
            }

            // ATR
            tech.setAtr(atrList.get(i));
            // ATRRatio（当日ATR/昨日ATR）
            if (i > 0 && atrList.get(i) != null && atrList.get(i - 1) != null && atrList.get(i - 1).compareTo(BigDecimal.ZERO) > 0) {
                tech.setAtrRatio(atrList.get(i).divide(atrList.get(i - 1), 4, ROUND_MODE));
            }

            tech.setObv(obvList.get(i));
            tech.setObvMa10(obvMa10List.get(i));
            tech.setObvDiff(tech.getObv() - tech.getObvMa10());
            tech.setWr6(wr6List.get(i));
            tech.setCci(cciList.get(i));
            tech.setMfi(mfiList.get(i));

            // VMACD
            if (vmacdList.get(i) != null) {
                tech.setVmacdDif(vmacdList.get(i)[0]);
                tech.setVmacdDea(vmacdList.get(i)[1]);
                tech.setVmacdBar(vmacdList.get(i)[2]);
                tech.setVmacdDiff(tech.getVmacdDif().subtract(tech.getVmacdDea()));
            }
            // 筹码指标
            tech.setAvgCost(avgCostList.get(i));
            tech.setCostConcentration(costConcentrationList.get(i));
            techList.add(tech);
        }

        // ===================== 计算顶底背离 =====================
        List<Object[]> divergenceResult = batchJudgeDivergence(barList, macdList, rsi9List, kdjList, cciList);
        // 给每个指标对象赋值背离信息
        for (int i = 0; i < techList.size(); i++) {
            if (i < divergenceResult.size()) {
                techList.get(i).setDivergenceType((Integer) divergenceResult.get(i)[0]);
                techList.get(i).setDivergenceStrength((BigDecimal) divergenceResult.get(i)[1]);
            } else {
                techList.get(i).setDivergenceType(DIVERGENCE_NONE);
                techList.get(i).setDivergenceStrength(BigDecimal.ZERO);
            }
        }

        // ===================== 多指标共振筛选 =====================
        batchJudgeResonance(stockCode, barList, techList);

        // 4. 批量写入数据库
        saveOrUpdateBatch(techList, new String[]{"stock_code", "trade_date"});
        log.info("股票" + stockCode + "指标计算完成，共写入" + techList.size() + "条数据");


        // 7. 策略回测
        BigDecimal[] backTestResult = backTestResonanceSignal(stockCode, barList, techList);
        System.out.println("===== " + stockCode + " 回测结果 =====");
        System.out.println("买入信号总数：" + backTestResult[0]);
        System.out.println("盈利信号数：" + backTestResult[1]);
        System.out.println("胜率：" + backTestResult[2] + "%");
        System.out.println("平均盈亏比：" + backTestResult[3]);
    }

}

