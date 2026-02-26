package com.optimus.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import com.optimus.constant.Constants;
import com.optimus.mysql.entity.StockKlineMinute;
import com.optimus.mysql.entity.StockTechMinute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.WilliamsRIndicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import static com.optimus.constant.Constants.*;
import static java.math.BigDecimal.ZERO;

@Slf4j
@Service
@RequiredArgsConstructor
public class Ta4jMinuteIndicatorCalculator {

    public StockTechMinute convertToTimeSeries(List<StockKlineMinute> barList) {
        int size = barList.size();
        StockKlineMinute curr = barList.get(size - 1);
        StockTechMinute tech = new StockTechMinute();
        tech.setStockCode(curr.getStockCode());
        tech.setStockName(curr.getStockName());
        tech.setTradeDate(curr.getTradeDate());
        tech.setTradeTime(curr.getTradeTime());
        tech.setPrice(curr.getPrice());
        tech.setHigh(curr.getHigh());
        tech.setLow(curr.getLow());
        tech.setOpen(curr.getOpen());
        tech.setClose(curr.getClose());
        tech.setVolume(curr.getVolume());
        tech.setVolumeRatio(curr.getVolumeRatio());
        tech.setVolumeRising(calcVolumeAvgRising(barList));

        BaseBarSeries series = new BaseBarSeriesBuilder().withName(curr.getStockCode() + curr.getStockName()).build();
        Instant begin = barList.get(0).getTradeDate().atTime(barList.get(0).getTradeTime()).atZone(ZoneId.of(ZONE_ID)).toInstant();
        Instant end = curr.getTradeDate().atTime(curr.getTradeTime()).atZone(ZoneId.of(ZONE_ID)).toInstant();
        for (StockKlineMinute stockKlineMinute : barList) {
            series.addBar(new BaseBar(Duration.ofMinutes(size), begin, end,
                    DecimalNum.valueOf(stockKlineMinute.getOpen()),
                    DecimalNum.valueOf(stockKlineMinute.getHigh()),
                    DecimalNum.valueOf(stockKlineMinute.getLow()),
                    DecimalNum.valueOf(stockKlineMinute.getClose()),
                    DecimalNum.valueOf(stockKlineMinute.getVolume()),
                    DecimalNum.valueOf(stockKlineMinute.getAmount()), 1));
        }

        int lastIndex = series.getEndIndex();
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        HighPriceIndicator highPrice = new HighPriceIndicator(series);
        LowPriceIndicator lowPrice = new LowPriceIndicator(series);
        VolumeIndicator volume = new VolumeIndicator(series);

        int buyScore = 0;
        int sellScore = 0;
        List<String> buyReasons = new ArrayList<>();
        List<String> sellReasons = new ArrayList<>();

        // 1. EMA（指数移动平均） 短线参数：3 5 10   确定当前波段的多空基调     -- 隔夜条件：价格站上 EMA5/EMA10 → 隔夜安全；跌破 EMA10 → 不隔夜。
        EMAIndicator ema3 = new EMAIndicator(closePrice, 3);
        EMAIndicator ema5 = new EMAIndicator(closePrice, 5);
        EMAIndicator ema10 = new EMAIndicator(closePrice, 10);
        Num ema5Num = ema5.getValue(lastIndex);
        Num ema10Num = ema10.getValue(lastIndex);
        tech.setEma3(ema3.getValue(lastIndex).bigDecimalValue());
        tech.setEma5(ema5Num.bigDecimalValue());
        tech.setEma10(ema10Num.bigDecimalValue());
        tech.setPriceUp(isPriceUp(barList, tech.getEma5()) ? YES : NO);

        // 金叉: 当前 MA5 > MA10; 前一刻 MA5 <= MA10
        if (ema5Num.isGreaterThan(ema10Num) && ema5.getValue(lastIndex - 1).isLessThanOrEqual(ema10.getValue(lastIndex - 1))) {      // 金叉
            tech.setEmaGolden(GOLDEN_CROSS);
        }
        if (ema5Num.isLessThan(ema10Num) && ema5.getValue(lastIndex - 1).isGreaterThanOrEqual(ema10.getValue(lastIndex - 1))) {      // 死叉
            tech.setEmaGolden(DEATH_CROSS);
        }

        // 2. MACD（指数平滑异同移动平均）（趋势+动能） 短线参数(5, 13, 1)   零轴确定长短周期动量方向    -- 隔夜条件：MACD红柱、DIF > DEA。
        MACDIndicator dif = new MACDIndicator(closePrice, 5, 13);       // DIF快线
        EMAIndicator dea = new EMAIndicator(dif, 1);                    // 信号线 DEA慢线
        Num difNum = dif.getValue(lastIndex);
        Num deaNum = dea.getValue(lastIndex);
        Num histNum = difNum.minus(deaNum);                                     // 柱状图 (Histogram) = MACD线 - 信号线
        Num prevHist = dif.getValue(lastIndex - 1).minus(dea.getValue(lastIndex));
        tech.setMacdDif(difNum.bigDecimalValue());
        tech.setMacdDea(deaNum.bigDecimalValue());
        tech.setMacdBar(histNum.bigDecimalValue());
        if (difNum.isGreaterThan(deaNum)) {         // 金叉
            tech.setMacdGolden(GOLDEN_CROSS);
            if (histNum.isPositive() && histNum.isGreaterThan(prevHist)) {      // 金叉且红柱放大
                tech.setMacdGolden(GOLDEN_CROSS_RED);
            }
        } else if (difNum.isLessThan(deaNum)) {     // 死叉
            tech.setMacdGolden(DEATH_CROSS);
            if (histNum.isNegative() && histNum.isLessThan(prevHist)) {         // 死叉且绿柱放大
                tech.setMacdGolden(DEATH_CROSS_GREEN);
            }
        }

        // 3. BOLL（布林带）短线参数：10  衡量价格相对于波动的边界位置   -- 隔夜条件：价格在中轨之上，可持仓过夜，若跌破中轨则需离场
        StandardDeviationIndicator stdDev = new StandardDeviationIndicator(closePrice, 10);
        BollingerBandsMiddleIndicator middleBB = new BollingerBandsMiddleIndicator(new SMAIndicator(closePrice, 10));
        BollingerBandsUpperIndicator upperBB = new BollingerBandsUpperIndicator(middleBB, stdDev, DecimalNum.valueOf(2));
        BollingerBandsLowerIndicator lowerBB = new BollingerBandsLowerIndicator(middleBB, stdDev, DecimalNum.valueOf(2));
        Num mid = middleBB.getValue(lastIndex);
        Num upper = upperBB.getValue(lastIndex);
        Num lower = lowerBB.getValue(lastIndex);
        tech.setBollMid(mid.bigDecimalValue());
        tech.setBollUpper(upper.bigDecimalValue());
        tech.setBollLower(lower.bigDecimalValue());

        Num currBand = upper.minus(lower);
        Num bandWidthPct = currBand.dividedBy(mid).multipliedBy(DecimalNum.valueOf(100));   // 带宽指标（Bandwidth）是量化“开口”的核心公式，数值越大代表开口越宽，波动越剧烈, 用于不同价格水平间的比较：
        if (bandWidthPct.isGreaterThanOrEqual(DecimalNum.valueOf(5))) {                     // 扩大超过5%才视为有效，避免微小平移干扰。
            // 开口状态: 指上轨与下轨之间的宽度（即带宽），带宽 = 上轨值 - 下轨值，当前带宽大于其移动平均 → 开口扩张；小于其移动平均 → 开口收窄。
            Num avgBand = DecimalNum.valueOf(0);
            for (int i = lastIndex; i >= lastIndex - 4; i--) {
                avgBand = avgBand.plus(upperBB.getValue(i).minus(lowerBB.getValue(i)));
            }
            avgBand = avgBand.dividedBy(DecimalNum.valueOf(5));
            if (currBand.isGreaterThan(avgBand)) {              // 开口: 当带宽指标增大，超过前5期平均带宽
                if (tech.getPrice().compareTo(mid.bigDecimalValue()) > 0 && mid.isGreaterThan(middleBB.getValue(lastIndex - 1))) {
                    tech.setBollExpandStatus(EXPAND_UP);        // 开口扩大向上：价格位于中轨上方，或中轨向上倾斜, 开口扩大向上：上涨趋势加速
                } else {
                    tech.setBollExpandStatus(EXPAND_DOWN);      // 开口扩大向下：价格位于中轨下方，或中轨向下倾斜, 开口扩大向下：下跌趋势加速
                }
            } else {                                            // 收口: 当带宽指标降至极低水平（通常认为带宽10%）时，意味着市场进入极度萎缩的盘整期
                if (tech.getPrice().compareTo(mid.bigDecimalValue()) > 0 && mid.isGreaterThan(middleBB.getValue(lastIndex - 1))) {
                    tech.setBollExpandStatus(SHRINK);
                }
            }
        }

        // 4. RSI（相对强弱指标） 超短线最灵：6    衡量市场强弱与超买超卖
        RSIIndicator rsi6 = new RSIIndicator(closePrice, 6);
        Num rsi6Num = rsi6.getValue(lastIndex);
        tech.setRsi6(rsi6Num.bigDecimalValue());


        // 5. KDJ（随机指标）短线参数：5 2 2   对短线拐点极其灵敏    -- 隔夜条件：J 在 50~80 之间最稳；J>90 不隔夜。
        // 周期：计算RSV（未成熟随机值）的周期，分时越小，N越小（如 1 分钟取 5）； RSV = (当前价 - N周期最低价) / (N周期最高价 - N周期最低价) × 100；
        // K 值平滑：K 线是 RSV 的 M1 日移动平均，分时固定取 2/3；             K = 2/3×前一日K值 + 1/3×当日RSV（初始 K=50）；
        // D 值平滑：D 线是 K 线的 M2 日移动平均，分时固定取 2/3；             D = 2/3×前一日D值 + 1/3×当日K值（初始 D=50）；
        // J 值：公式固定为 J = 3*K - 2*D（无参数）。                        J = 3×K - 2×D（J 值范围通常 ±100，超 80 = 超买，低于 20 = 超卖）
        StochasticOscillatorKIndicator stoch = new StochasticOscillatorKIndicator(series, 5);
        SMAIndicator k = new SMAIndicator(stoch, 2);        // K = SMA(RSV, kPeriod)
        SMAIndicator d = new SMAIndicator(k, 2);            // D = SMA(K, dPeriod)
        Num kNum = k.getValue(lastIndex);
        Num dNum = d.getValue(lastIndex);
        Num jNum = kNum.multipliedBy(DecimalNum.valueOf(3)).minus(dNum.multipliedBy(DecimalNum.valueOf(2)));    // J = 3*K - 2*D
        tech.setKdjK(kNum.bigDecimalValue());
        tech.setKdjD(dNum.bigDecimalValue());
        tech.setKdjJ(jNum.bigDecimalValue());
        // 低位金叉（20 以下）： 当 K、D 线在 20 以下的超卖区形成金叉，代表价格超跌后的动能反转，此时买入信号最为准确。
        if (kNum.isGreaterThan(dNum) && k.getValue(lastIndex - 1).isLessThanOrEqual(d.getValue(lastIndex - 1))) {
            tech.setKdjGolden(GOLDEN_CROSS);
            if (kNum.isLessThanOrEqual(DecimalNum.valueOf(20)) && dNum.isLessThanOrEqual(DecimalNum.valueOf(20))) {
                tech.setKdjGolden(GOLDEN_CROSS_RED);
            }
        } else if (kNum.isLessThan(dNum) && k.getValue(lastIndex - 1).isGreaterThanOrEqual(d.getValue(lastIndex - 1))) {
            tech.setKdjGolden(DEATH_CROSS);
            if (kNum.isGreaterThanOrEqual(DecimalNum.valueOf(80)) && dNum.isGreaterThanOrEqual(DecimalNum.valueOf(80))) {
                tech.setKdjGolden(DEATH_CROSS_GREEN);
            }
        }

        // 6. VMACD（成交量MACD）  短线参数：5,13,1   量平滑异同平均，量化资金动能    -- 隔夜条件：VMACD 红柱 → 量价配合
        MACDIndicator vDif = new MACDIndicator(volume, 5, 13);
        EMAIndicator vDea = new EMAIndicator(vDif, 1);
        Num vDifNum = vDif.getValue(lastIndex);
        Num vDeaNum = vDea.getValue(lastIndex);
        Num vHistNum = vDifNum.minus(vDeaNum);                             // 柱状图 (Histogram) = MACD线 - 信号线
        tech.setVmacdDif(vDifNum.bigDecimalValue());
        tech.setVmacdDea(vDeaNum.bigDecimalValue());
        tech.setVmacdBar(vHistNum.bigDecimalValue());

        Num vPrevHist = vDif.getValue(lastIndex - 1).minus(vDea.getValue(lastIndex));
        if (vDifNum.isGreaterThan(vDeaNum)) {
            tech.setVmacdGolden(GOLDEN_CROSS);
            if (vDif.getValue(lastIndex - 1).isNegative() && vDifNum.isPositiveOrZero() && vHistNum.isPositive() && vHistNum.isGreaterThan(vPrevHist)) {     // 零轴红柱放大
                tech.setVmacdGolden(GOLDEN_CROSS_RED);
            }
        } else if (vDifNum.isLessThan(deaNum)) {
            tech.setVmacdGolden(DEATH_CROSS);
            if (vDif.getValue(lastIndex - 1).isPositive() && vDifNum.isNegativeOrZero() && vHistNum.isNegative() && vHistNum.isLessThan(vPrevHist)) {       // 零轴绿柱放大
                tech.setVmacdGolden(DEATH_CROSS_GREEN);
            }
        }


        // 7. OBVMA 能量潮均线确认资金流入流出     -- 隔夜条件：OBV > OBV_MA5
        OnBalanceVolumeIndicator obv = new OnBalanceVolumeIndicator(series);
        SMAIndicator obvMa5 = new SMAIndicator(obv, 5);
        Num obvNum = obv.getValue(lastIndex);
        Num obvMa5Num = obvMa5.getValue(lastIndex);
        tech.setObv(obvNum.longValue());
        tech.setObvMa5(obvMa5Num.longValue());

        if (obvNum.isGreaterThan(obvMa5Num) && obv.getValue(lastIndex - 1).isLessThanOrEqual(obvMa5.getValue(lastIndex - 1))) {
            tech.setObvGolden(GOLDEN_CROSS);
        } else if (obvNum.isLessThan(obvMa5Num) && obv.getValue(lastIndex - 1).isGreaterThanOrEqual(obvMa5.getValue(lastIndex - 1))) {
            tech.setObvGolden(DEATH_CROSS);
        }

        // 8. WR（威廉指标）极短线参数：6   用于1分钟或5分钟线，适合捕捉极速脉冲行情，预判趋势衰减      -- 隔夜条件：WR < 20 超买 → 不隔夜; WR > 80 超卖 → 可低吸隔夜; WR从超卖区回升时配合OBV放量可加仓。
        WilliamsRIndicator wr = new WilliamsRIndicator(series, 6);
        Num wrNum = wr.getValue(lastIndex);
        tech.setWr6(wrNum.bigDecimalValue());

        return tech;
    }


    /**
     * 成交量放大（当前分钟成交量/前5分钟均量）> 1.2
     */
    private BigDecimal calcVolumeAvgRising(List<StockKlineMinute> barList) {
        int lastIdx = barList.size() - 1;
        long sum = 0;
        for (int i = lastIdx - 4; i <= lastIdx; i++) {
            sum += barList.get(i).getVolume();
        }
        BigDecimal avgVol5 = BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(5), SCALE4, ROUND_MODE);
        return BigDecimal.valueOf(barList.get(lastIdx).getVolume()).divide(avgVol5, SCALE4, ROUND_MODE);
    }

    /**
     * 价格上升（当前价格>前3分钟均价 且 当前价格站上EMA5）
     */
    private boolean isPriceUp(List<StockKlineMinute> barList, BigDecimal ema5) {
        int lastIdx = barList.size() - 1;
        BigDecimal sum = ZERO;
        for (int i = lastIdx - 2; i <= lastIdx; i++) {
            sum = sum.add(barList.get(i).getPrice());
        }
        BigDecimal avgPrice3 = sum.divide(BigDecimal.valueOf(3), SCALE4, ROUND_MODE);
        BigDecimal price = barList.get(lastIdx).getPrice();
        return price.compareTo(avgPrice3) > 0 && price.compareTo(ema5) > 0;
    }


    public void judgeMinuteResonance(StockTechMinute tech) {
        int buyScore = 0;
        int sellScore = 0;
        List<String> buyReasons = new ArrayList<>();
        List<String> sellReasons = new ArrayList<>();

        // ---------------------- 分时共振买入信号判定（10条，超短线核心） ----------------------
        // 一. 趋势类指标（权重40%）
        // 1. EMA 多头排列 10%
        if (tech.getPrice().compareTo(tech.getEma5()) > 0 && tech.getEma5().compareTo(tech.getEma10()) > 0) {
            buyScore++;
            buyReasons.add("EMA多头排列,短期强势(价格>EMA5>EMA10);");
        }
        // 2. EMA金叉
        if (tech.getEmaGolden() == GOLDEN_CROSS) {
            buyScore++;
            buyReasons.add("EMA金叉;");
        }

        // 3. MACD 零轴上金叉 20%
        if (tech.getMacdDif().compareTo(ZERO) >= 0 && tech.getMacdGolden() == GOLDEN_CROSS) {
            buyScore++;
            buyReasons.add("MACD零轴上金叉;");
        } else if (tech.getMacdDif().compareTo(ZERO) >= 0 && tech.getMacdGolden() == GOLDEN_CROSS_RED) {
            buyScore += 2;
            buyReasons.add("MACD零轴上金叉且红柱放大(动能强);");
        }

        // 4. BOLL 突破下轨支撑 在短线分时系统中，开口的突然放大（尤其是 5 分钟 BOLL）通常被视为短线爆发的买入时机。
        if (tech.getPrice().compareTo(tech.getBollLower()) < 0) {
            buyScore += 2;
            buyReasons.add("价格突破BOLL下轨(买入信号);");
        } else if (tech.getPrice().compareTo(tech.getBollLower()) == 0) {
            buyScore += 1;
            buyReasons.add("价格触及BOLL下轨(买入信号);");
        }
        // 5. BOll 开口扩大向上：价格位于中轨上方，或中轨向上倾斜, 开口扩大向上：上涨趋势加速
        if (tech.getBollExpandStatus() == EXPAND_UP) {
            buyScore += 1;
            buyReasons.add("开口扩大向上且价格位于中轨上方(上涨趋势);");
        }

        // 二 动能类指标（权重30%）
        // 6. RSI 衡量市场强弱与超买超卖 -- 隔夜条件：RSI6 在 50~70 之间最强；>80 不隔夜。
        if (tech.getRsi6().compareTo(BigDecimal.valueOf(30)) <= 0) {     // 向上反转信号
            buyScore += 2;
            buyReasons.add("RSI超卖(≤30);");
        }
        // 7.  KDJ 对短线拐点极其灵敏 低位金叉（K<20）：代表价格超跌后的动能反转，此时买入信号最为准确。
        if (tech.getKdjGolden() == GOLDEN_CROSS_RED) {
            buyScore += 2;
            buyReasons.add("(KDJ低位金叉，强烈买入信号(K≤20);");
        } else if (tech.getKdjGolden() == GOLDEN_CROSS) {
            buyScore += 2;
            buyReasons.add("(KDJ金叉;");
        }
        // 8. KDJ超卖区（机会显现） 精准买卖点（J值比K/D更准）
        if (tech.getKdjJ().compareTo(BigDecimal.valueOf(10)) <= 0) {
            buyScore += 2;
            buyReasons.add("(KDJ严重超卖，买入信号(J≤10);");
        } else if (tech.getKdjJ().compareTo(BigDecimal.valueOf(20)) <= 0) {
            buyScore++;
            buyReasons.add("(KDJ超卖，买入信号(J≤20);");
        }


        // 三 量价类指标（权重20%）
        // 9. VMACD（成交量MACD） -- 隔夜条件：VMACD 红柱 → 量价配合
        if (tech.getVmacdGolden() == GOLDEN_CROSS_RED) {
            buyScore += 3;
            buyReasons.add("VMACD零轴金叉且红柱放大(放量);");
        } else if (tech.getVmacdGolden() == GOLDEN_CROSS) {
            buyScore++;
            buyReasons.add("VMACD金叉(放量)");
        }
        // 10. OBVMA 能量潮均线   -- 隔夜条件：OBV > OBV_MA5
        if (tech.getObvGolden() == GOLDEN_CROSS) {
            buyScore++;
            buyReasons.add("OBV金叉 资金流入(买入信号)");
        }

        // 四 风险控制（权重10%）
        // 11. WR 指标在 -80 以下（超卖区）盘整后，上穿 -80 回到常规区时。等待信号确认,股价重新站上分时均价线时，才是安全的低吸时点。
        if (tech.getWr6().compareTo(BigDecimal.valueOf(-80)) < 0) {
            buyScore += 1;
            buyReasons.add("WR超卖区，买入机会(<-80);");
        }
        // 12. 成交量上升 大于1.2倍
        if (tech.getVolumeRising().compareTo(BigDecimal.valueOf(1.2)) >= 0) {
            buyScore += 1;
            buyReasons.add("成交量上升(>1.2倍);");
        }

        // 13. 股价上升


        // ---------------------- 分时共振卖出信号判定（超短线规则） ----------------------
        // 一 趋势类指标（权重40%）
        // 1. EMA空头排列 10%
        if (tech.getPrice().compareTo(tech.getEma5()) < 0 && tech.getEma5().compareTo(tech.getEma10()) <= 0) {
            sellScore++;
            sellReasons.add("EMA空头排列,短期弱势(价格<EMA5<EMA10);");
        }
        // 2. EMA死叉
        if (tech.getEmaGolden() == DEATH_CROSS) {
            sellScore++;
            sellReasons.add("EMA死叉;");
        }

        // 3. MACD 零轴下死叉 20%
        if (tech.getMacdDif().compareTo(ZERO) < 0 && tech.getMacdGolden() == DEATH_CROSS) {
            sellScore++;
            sellReasons.add("MACD零轴下死叉;");
        } else if (tech.getMacdDif().compareTo(ZERO) < 0 && tech.getMacdGolden() == DEATH_CROSS_GREEN) {
            sellScore += 2;
            sellReasons.add("MACD零轴下死叉且绿柱放大(动能弱);");
        }

        // 4. BOLL 突破上轨压力 短线止盈离场点
        if (tech.getPrice().compareTo(tech.getBollUpper()) > 0) {
            sellScore += 2;
            sellReasons.add("价格突破BOLL上轨(卖出信号);");
        } else if (tech.getPrice().compareTo(tech.getBollUpper()) == 0) {
            sellScore += 1;
            sellReasons.add("价格触及BOLL上轨(卖出信号);");
        }
        // 5. 开口扩大向下：价格位于中轨下方，或中轨向下倾斜, 开口扩大向下：下跌趋势加速
        if (tech.getBollExpandStatus() == EXPAND_UP) {
            buyScore += 1;
            buyReasons.add("开口扩大向下且价格位于中轨下方(下跌趋势);");
        }

        // 二 动能类指标（权重30%）
        // 6. RSI -- 隔夜条件：RSI6 在 50~70 之间最强；>80 不隔夜。
        if (tech.getRsi6().compareTo(BigDecimal.valueOf(80)) >= 0) {        // 向下反转信号
            sellScore += 2;
            sellReasons.add("RSI超买(≥80);");
        }
        // 7. KDJ高位死叉
        if (tech.getKdjGolden() == DEATH_CROSS_GREEN) {
            sellScore += 3;
            sellReasons.add("(KDJ高位死叉，强烈卖出信号(K≥80);");
        } else if (tech.getKdjGolden() == DEATH_CROSS) {
            sellScore++;
            sellReasons.add("(KDJ死叉;");
        }
        // 8. KDJ超买区（风险积聚）
        if (tech.getKdjJ().compareTo(BigDecimal.valueOf(90)) >= 0) {
            sellScore += 2;
            sellReasons.add("(KDJ严重超买，卖出信号(J≥90);");
        } else if (tech.getKdjJ().compareTo(BigDecimal.valueOf(80)) >= 0) {
            sellScore++;
            sellReasons.add("(KDJ超买，卖出信号(J≥80);");
        }


        // 三 量价类指标（权重20%）
        // 9. VMACD（成交量MACD）
        if (tech.getVmacdGolden() == DEATH_CROSS_GREEN) {
            sellScore += 3;
            sellReasons.add("VMACD零轴死叉且绿柱放大(缩量);");
        } else if (tech.getVmacdGolden() == DEATH_CROSS) {
            sellScore++;
            sellReasons.add("VMACD死叉(缩量);");
        }
        // 10. OBVMA 能量潮均线   -- 隔夜条件：OBV > OBV_MA5
        if (tech.getObvGolden() == DEATH_CROSS) {
            sellScore++;
            sellReasons.add("OBV死叉 资金流出(卖出信号)");
        }
        // 11. WR 指标在 -20 以上（超买区）盘整后，下穿 -20 回到常规区时。
        if (tech.getWr6().compareTo(BigDecimal.valueOf(-20)) > 0) {
            sellScore += 1;
            sellReasons.add("WR超买区，卖出信号(>-20);");
        }
        // 12. 成交量缩量 小于0.8倍
        if (tech.getVolumeRising().compareTo(BigDecimal.valueOf(0.8)) <= 0) {
            sellScore += 1;
            sellReasons.add("成交量缩量(<0.8倍);");
        }

    }





}
