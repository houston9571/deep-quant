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

@Slf4j
@Service
@RequiredArgsConstructor
public class Ta4jMinuteIndicatorCalculator {

    public StockTechMinute convertToTimeSeries(List<StockKlineMinute> barList) {
        int size = barList.size();
        StockKlineMinute curr = barList.get(size - 1);
        StockTechMinute techMinute = new StockTechMinute();
        techMinute.setStockCode(curr.getStockCode());
        techMinute.setStockName(curr.getStockName());
        techMinute.setTradeDate(curr.getTradeDate());
        techMinute.setTradeTime(curr.getTradeTime());
        techMinute.setPrice(curr.getPrice());
        techMinute.setHigh(curr.getHigh());
        techMinute.setLow(curr.getLow());
        techMinute.setOpen(curr.getOpen());
        techMinute.setClose(curr.getClose());

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

        // 1. EMA（指数移动平均） 3 5 10        -- 隔夜条件：价格站上 EMA5/EMA10 → 隔夜安全；跌破 EMA10 → 不隔夜。
        EMAIndicator ema3 = new EMAIndicator(closePrice, 3);
        EMAIndicator ema5 = new EMAIndicator(closePrice, 5);
        EMAIndicator ema10 = new EMAIndicator(closePrice, 10);
        Num ema5Num = ema5.getValue(lastIndex);
        Num ema10Num = ema10.getValue(lastIndex);
        techMinute.setEma3(ema3.getValue(lastIndex).bigDecimalValue());
        techMinute.setEma5(ema5Num.bigDecimalValue());
        techMinute.setEma10(ema10Num.bigDecimalValue());
        if (techMinute.getPrice().compareTo(techMinute.getEma5()) > 0) {
            buyScore++;
            buyReasons.add("价格>EMA5(短期强势);");
        } else {
            sellScore--;
            sellReasons.add("价格<EMA5(短期弱势);");

        }
        // 金叉: 分时MA3上穿MA5（核心短线趋势）;  当日EMA(5) > 前一日EMA(5);  当日EMA(10) < 前一日EMA(10);   EMA(5)上穿EMA(10)（即短期均线上穿长期均线）。
        if (ema5Num.isGreaterThan(ema10Num) && ema5.getValue(lastIndex - 1).isLessThanOrEqual(ema10.getValue(lastIndex - 1))) {
            techMinute.setEmaGolden(GOLDEN_CROSS);
            buyScore += 2;
            buyReasons.add("EMA金叉;");
        }
        if (ema5Num.isLessThan(ema10Num) && ema5.getValue(lastIndex - 1).isGreaterThanOrEqual(ema10.getValue(lastIndex - 1))) {
            techMinute.setEmaGolden(DEATH_CROSS);
            sellScore += 2;
            sellReasons.add("EMA死叉;");
        }

        // 2. MACD（指数平滑异同移动平均）（趋势 + 动能） 短线参数(5, 13, 1)       -- 隔夜条件：MACD红柱、DIF > DEA。
        MACDIndicator dif = new MACDIndicator(closePrice, 5, 13);  // DIF快线
        EMAIndicator dea = new EMAIndicator(dif, 1);    // 信号线 DEA慢线
        Num difNum = dif.getValue(lastIndex);
        Num deaNum = dea.getValue(lastIndex);
        Num histNum = difNum.minus(deaNum);                                  // 柱状图 (Histogram) = MACD线 - 信号线
        Num prevHist = dif.getValue(lastIndex - 1).minus(dea.getValue(lastIndex));
        techMinute.setMacdDif(difNum.bigDecimalValue());
        techMinute.setMacdDea(deaNum.bigDecimalValue());
        techMinute.setMacdBar(histNum.bigDecimalValue());
        if (difNum.isGreaterThan(deaNum)) {
            techMinute.setMacdGolden(GOLDEN_CROSS);
            if (histNum.isPositive() && histNum.isGreaterThan(prevHist)) {
                buyScore += 2;
                buyReasons.add("MACD金叉且红柱放大(动能强);");
            } else {
                buyScore += 1;
                buyReasons.add("MACD金叉;");
            }
        } else if (difNum.isLessThan(deaNum)) {
            techMinute.setMacdGolden(DEATH_CROSS);
            sellScore += 2;
            sellReasons.add("MACD死叉或绿柱(动能弱);");
        }

        // 3. RSI（相对强弱指标） 6(超短线最灵） -- 隔夜条件：RSI6 在 50~70 之间最强；>80 不隔夜。
        RSIIndicator rsi6 = new RSIIndicator(closePrice, 6);
        Num rsi6Num = rsi6.getValue(lastIndex);
        techMinute.setRsi6(rsi6Num.bigDecimalValue());
        if (rsi6Num.isLessThanOrEqual(DecimalNum.valueOf(30))) {                // 向上反转信号
            buyScore += 2;
            buyReasons.add("RSI超卖(≤30);");
        } else if (rsi6Num.isGreaterThanOrEqual(DecimalNum.valueOf(80))) {      // 向下反转信号
            sellScore += 2;
            sellReasons.add("RSI超买(≥80);");
        } else if (rsi6Num.isGreaterThan(DecimalNum.valueOf(50))) {             // RSI以50为中界限，大于50视为多头行情，小于50视为空头行情
            buyScore++;
            buyReasons.add("RSI强势区(>50);");
        } else {
            sellScore++;
            sellReasons.add("RSI弱势区(<50);");
        }

        // 4. KDJ（随机指标）5 2 2     -- 隔夜条件：J 在 50~80 之间最稳；J>90 不隔夜。
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
        techMinute.setKdjK(kNum.bigDecimalValue());
        techMinute.setKdjD(dNum.bigDecimalValue());
        techMinute.setKdjJ(jNum.bigDecimalValue());

        if (kNum.isGreaterThan(dNum) && k.getValue(lastIndex - 1).isLessThanOrEqual(d.getValue(lastIndex - 1))) {
            techMinute.setKdjGolden(GOLDEN_CROSS);
            if (kNum.isLessThanOrEqual(DecimalNum.valueOf(20)) && dNum.isLessThanOrEqual(DecimalNum.valueOf(20))) {            // 低位金叉（20 以下）： 当 K、D 线在 20 以下的超卖区形成金叉，代表价格超跌后的动能反转，此时买入信号最为准确。
                buyScore += 3;
                buyReasons.add("(KDJ低位金叉，强烈买入信号(K≤20);");
            } else {
                buyScore++;
                buyReasons.add("(KDJ金叉;");
            }
        } else if (kNum.isLessThan(dNum) && k.getValue(lastIndex - 1).isGreaterThanOrEqual(d.getValue(lastIndex - 1))) {
            techMinute.setKdjGolden(DEATH_CROSS);
            if (kNum.isGreaterThanOrEqual(DecimalNum.valueOf(80)) && dNum.isGreaterThanOrEqual(DecimalNum.valueOf(80))) {
                sellScore += 3;
                sellReasons.add("(KDJ高位死叉，强烈卖出信号(K≥80);");
            } else {
                sellScore++;
                sellReasons.add("(KDJ死叉;");
            }
        }
        if (jNum.isLessThanOrEqual(DecimalNum.valueOf(10))) {
            buyScore += 2;
            buyReasons.add("(KDJ严重超卖，买入信号(J≤10);");
        } else if (jNum.isLessThanOrEqual(DecimalNum.valueOf(20))) {    // 超卖区（机会显现）
            buyScore++;
            buyReasons.add("(KDJ超卖，买入信号(J≤20);");
        }

        if (jNum.isGreaterThanOrEqual(DecimalNum.valueOf(90))) {
            sellScore += 2;
            sellReasons.add("(KDJ严重超买，卖出信号(J≥90);");

        } else if (jNum.isGreaterThanOrEqual(DecimalNum.valueOf(80))) {     // 超买区（风险积聚）
            sellScore++;
            sellReasons.add("(KDJ超买，卖出信号(J≥80);");
        }


        // 5. WR（威廉指标）极短线参数：6 用于1分钟或5分钟线，适合捕捉极速脉冲行情       -- 隔夜条件：WR < 20 超买 → 不隔夜; WR > 80 超卖 → 可低吸隔夜; WR从超卖区回升时配合OBV放量可加仓。
        WilliamsRIndicator wr = new WilliamsRIndicator(series, 6);
        Num wrNum = wr.getValue(lastIndex);
        techMinute.setWr6(wrNum.bigDecimalValue());

        if (wrNum.isLessThan(DecimalNum.valueOf(-80))) {    // 买入点： WR 指标在 -80 以下（超卖区）盘整后，上穿 -80 回到常规区时。等待信号确认,股价重新站上分时均价线时，才是安全的低吸时点。
            buyScore += 1;
            buyReasons.add("WR超卖，买入机会(<-80);");
        } else if (wrNum.isGreaterThan(DecimalNum.valueOf(-20))) {  // 卖出点： WR 指标在 -20 以上（超买区）盘整后，下穿 -20 回到常规区时。
            sellScore += 1;
            sellReasons.add("WR超买，卖出信号(>-20);");
        }

        // 6. BOLL（布林带）10  -- 隔夜条件：价格在中轨之上，可持仓过夜，若跌破中轨则需离场
        StandardDeviationIndicator stdDev = new StandardDeviationIndicator(closePrice, 10);
        BollingerBandsMiddleIndicator middleBB = new BollingerBandsMiddleIndicator(new SMAIndicator(closePrice, 10));
        BollingerBandsUpperIndicator upperBB = new BollingerBandsUpperIndicator(middleBB, stdDev, DecimalNum.valueOf(2));
        BollingerBandsLowerIndicator lowerBB = new BollingerBandsLowerIndicator(middleBB, stdDev, DecimalNum.valueOf(2));
        Num mid = middleBB.getValue(lastIndex);
        Num upper = upperBB.getValue(lastIndex);
        Num lower = lowerBB.getValue(lastIndex);

        // 带宽指标（Bandwidth）是量化“开口”的核心公式，其数值越大代表开口越宽，波动越剧烈, 用于不同价格水平间的比较：
        Num bandWidthPct = upper.minus(lower).dividedBy(mid).multipliedBy(DecimalNum.valueOf(100));

        // 开口状态: 指上轨与下轨之间的宽度（即带宽），带宽 = 上轨值 - 下轨值，当前带宽大于其移动平均 → 开口扩张；小于其移动平均 → 开口收窄。
        // 在短线分时系统中，开口的突然放大（尤其是 5 分钟 BOLL）通常被视为短线爆发的买入时机。
        BigDecimal avgBand = BigDecimal.ZERO;
        for (int i = lastIndex; i >= lastIndex - 4; i--) {
            avgBand = avgBand.add(upperBB.getValue(i).minus(lowerBB.getValue(i)).bigDecimalValue());
        }
        avgBand = avgBand.divide(BigDecimal.valueOf(5), ROUND_MODE);
        BigDecimal currBand = upper.minus(lower).bigDecimalValue();
        boolean isExpanding = currBand.compareTo(avgBand) > 0;
        boolean isNarrowing = currBand.compareTo(avgBand) < 0;

        techMinute.setBollMid(mid.bigDecimalValue());
        techMinute.setBollUpper(upper.bigDecimalValue());
        techMinute.setBollLower(lower.bigDecimalValue());
        techMinute.setBollBandWidthPct(bandWidthPct.bigDecimalValue());
        techMinute.setBollExpandStatus(isExpanding ? 1 : (isNarrowing ? 2 : 0));

        if (techMinute.getPrice().compareTo(techMinute.getBollLower()) <= 0) {
            buyScore += 2;
            buyReasons.add("价格突破BOLL下轨(反弹概率大);");
        } else if (techMinute.getPrice().compareTo(techMinute.getBollUpper()) >= 0) {
            sellScore += 2;
            sellReasons.add("价格突破BOLL上轨 (回调概率大)");
        } else if (techMinute.getPrice().compareTo(techMinute.getBollMid()) > 0) {
            {
                buyScore++;
                buyReasons.add("价格站上分时BOLL中轨");
            }
        }
        // 开口: 当带宽指标迅速增大，且股价放量突破上轨时，形成“开口喇叭”形态，代表单边上涨行情的启动。

        // 收口: 当带宽指标降至极低水平（通常认为带宽 < 0.10 或 10%）时，意味着市场进入极度萎缩的盘整期，往往是新一轮大行情爆发的前兆

        // 7. VMACD（成交量MACD） 对成交量计算MACD(5,13,1)     -- 隔夜条件：VMACD 红柱 → 量价配合
        MACDIndicator vDif = new MACDIndicator(volume, 5, 13);
        EMAIndicator vDea = new EMAIndicator(vDif, 1);
        Num vDifNum = vDif.getValue(lastIndex);
        Num vDeaNum = vDea.getValue(lastIndex);
        Num vHistNum = vDifNum.minus(vDeaNum);                             // 柱状图 (Histogram) = MACD线 - 信号线
        techMinute.setVmacdDif(vDifNum.bigDecimalValue());
        techMinute.setVmacdDea(vDeaNum.bigDecimalValue());
        techMinute.setVmacdBar(vHistNum.bigDecimalValue());
        // 量增价升逻辑
        if (vDifNum.isGreaterThan(vDeaNum)) {
            buyScore++;
            buyReasons.add("VMACD 金叉 (放量)");
        }

        if (vDifNum.isGreaterThan(vDeaNum)) {
            techMinute.setVmacdGolden(GOLDEN_CROSS);
            Num vPrevHist = vDif.getValue(lastIndex - 1).minus(vDea.getValue(lastIndex));
            if (vDifNum.isNegativeOrZero()) { // 零轴之下（DIF < 0）
                if (vHistNum.isNegativeOrZero() && vHistNum.isGreaterThan(vPrevHist)) {  // 柱状图由绿转红
                    buyScore += 2;
                    buyReasons.add("VMACD低位金叉且由绿转红(反弹信号);");    // 低位金叉（量能处于收缩期）： 当 DIF上穿过 DEA，且柱状图由绿转红，视为量能衰竭后的反弹信号，预示成交量即将回升。
                } else {
                    buyScore += 1;
                    buyReasons.add("VMACD低位金叉(反弹信号);");
                }
            } else {
                buyScore += 1;
                buyReasons.add("VMACD高位金叉(加速放量);");     // 高位金叉（量能处于扩张期）： 往往意味着第二波放量拉升，可靠性极高，常对应主升浪的启动。
            }
        } else if (vDifNum.isLessThan(deaNum)) {
            techMinute.setVmacdGolden(DEATH_CROSS);
            if (vDifNum.isPositive()) { // 零轴之下（DIF < 0）
                sellScore += 2;
                sellReasons.add("VMACD高位死叉(顶部放量);");
            } else {
                sellScore += 1;
                sellReasons.add("VMACD低位死叉(阴跌无量);");
            }
        }


        // 8. OBV（能量潮） OBVM5         -- 隔夜条件：OBV > OBV_MA5
        OnBalanceVolumeIndicator obv = new OnBalanceVolumeIndicator(series);
        SMAIndicator obvMa5 = new SMAIndicator(obv, 5);
        Num obvNum = obv.getValue(lastIndex);
        Num obvMa5Num = obvMa5.getValue(lastIndex);
        techMinute.setObv(obvNum.longValue());
        techMinute.setObvMa5(obvMa5Num.longValue());

        if (obvNum.isGreaterThan(obvMa5Num) && obv.getValue(lastIndex - 1).isLessThanOrEqual(obvMa5.getValue(lastIndex - 1))) {
            techMinute.setObvGolden(GOLDEN_CROSS);
            buyScore++;
            buyReasons.add("OBV金叉 资金流入(买入信号)");
        } else if (obvNum.isLessThan(obvMa5Num) && obv.getValue(lastIndex - 1).isGreaterThanOrEqual(obvMa5.getValue(lastIndex - 1))) {
            techMinute.setObvGolden(DEATH_CROSS);
            sellScore++;
            sellReasons.add("OBV死叉 资金流出(卖出信号)");
        }



        return techMinute;
    }

}
