package com.optimus.components;

import com.optimus.mysql.entity.StockDaily;
import com.optimus.mysql.entity.StockTechDaily;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.optimus.constant.Constants.HUNDRED;
import static com.optimus.constant.Constants.ROUND_MODE;
import static java.math.BigDecimal.ZERO;

/**
 * A股技术指标计算工具类
 * 核心规则：基于前复权收盘价计算，精度保留4位小数，符合A股行情软件标准
 */
public class StockTechIndicatorCalculator {

    // 默认精度：4位小数
    private static final int SCALE = 4;

    // 背离类型常量
    public static final int DIVERGENCE_NONE = 0;
    public static final int DIVERGENCE_MACd_TOP = 1;
    public static final int DIVERGENCE_MACd_BOTTOM = 2;
    public static final int DIVERGENCE_RSI_TOP = 3;
    public static final int DIVERGENCE_RSI_BOTTOM = 4;
    public static final int DIVERGENCE_KDJ_TOP = 5;
    public static final int DIVERGENCE_KDJ_BOTTOM = 6;
    public static final int DIVERGENCE_CCI_TOP = 7;
    public static final int DIVERGENCE_CCI_BOTTOM = 8;

    // 共振信号常量
    public static final int RESONANCE_NONE = 0;
    public static final int RESONANCE_BUY = 1;
    public static final int RESONANCE_SELL = 2;
    public static final int RESONANCE_TREND_UP = 3;
    public static final int RESONANCE_TREND_DOWN = 4;

    /**
     * ====================== 1. 移动平均线（MA）计算 ======================
     * 计算指定周期的移动平均线（MA）
     *
     * @param barList 按时间升序排列的日线数据
     * @param period  周期（5/10/20/60）
     * @return 每个日期对应的MA值
     */
    public static List<BigDecimal> calculateMA(List<StockDaily> barList, int period) {
        List<BigDecimal> maList = new ArrayList<>();
        if (barList.size() < period) {
            // 数据量不足，填充null
            barList.forEach(bar -> maList.add(null));
            return maList;
        }

        // 前period-1个数据MA为null
        for (int i = 0; i < period - 1; i++) {
            maList.add(null);
        }

        // 从第period个数据开始计算MA
        for (int i = period - 1; i < barList.size(); i++) {
            BigDecimal sum = ZERO;
            // 累加最近period个收盘价
            for (int j = i - period + 1; j <= i; j++) {
                sum = sum.add(barList.get(j).getClose());
            }
            BigDecimal ma = sum.divide(BigDecimal.valueOf(period), SCALE, ROUND_MODE);
            maList.add(ma);
        }
        return maList;
    }

    /**
     * ====================== 2. MACD指标计算 ======================
     * 计算MACD（DIF=EMA12-EMA26，DEA=EMA(DIF,9)，BAR=2*(DIF-DEA)）
     *
     * @param barList 按时间升序排列的日线数据
     * @return 包含DIF/DEA/BAR的三维数组（[0]=DIF, [1]=DEA, [2]=BAR）
     */
    public static List<BigDecimal[]> calculateMACD(List<StockDaily> barList) {
        List<BigDecimal[]> macdList = new ArrayList<>();
        int minSize = 26; // MACD最小数据量（EMA26）
        if (barList.size() < minSize) {
            barList.forEach(bar -> macdList.add(new BigDecimal[]{null, null, null}));
            return macdList;
        }

        // 步骤1：计算EMA12和EMA26
        List<BigDecimal> ema12List = calculateEMA(barList, 12);
        List<BigDecimal> ema26List = calculateEMA(barList, 26);

        // 步骤2：计算DIF（EMA12 - EMA26）
        List<BigDecimal> difList = new ArrayList<>();
        for (int i = 0; i < barList.size(); i++) {
            if (ema12List.get(i) == null || ema26List.get(i) == null) {
                difList.add(null);
            } else {
                difList.add(ema12List.get(i).subtract(ema26List.get(i)).setScale(SCALE, ROUND_MODE));
            }
        }

        // 步骤3：计算DEA（EMA(DIF,9)）
        List<BigDecimal> deaList = calculateEMAByValueList(difList, 9);

        // 步骤4：计算BAR（2*(DIF-DEA)）
        for (int i = 0; i < barList.size(); i++) {
            BigDecimal dif = difList.get(i);
            BigDecimal dea = deaList.get(i);
            if (dif == null || dea == null) {
                macdList.add(new BigDecimal[]{null, null, null});
            } else {
                BigDecimal bar = dif.subtract(dea).multiply(BigDecimal.valueOf(2)).setScale(SCALE, ROUND_MODE);
                macdList.add(new BigDecimal[]{dif, dea, bar});
            }
        }
        return macdList;
    }

    /**
     * 计算指数移动平均线（EMA）
     * 公式：EMA(N) = 当日收盘价*2/(N+1) + 昨日EMA*(N-1)/(N+1)
     */
    private static List<BigDecimal> calculateEMA(List<StockDaily> barList, int period) {
        List<BigDecimal> emaList = new ArrayList<>();
        if (barList.size() < period) {
            barList.forEach(bar -> emaList.add(null));
            return emaList;
        }

        // 前period-1个数据EMA为null
        for (int i = 0; i < period - 1; i++) {
            emaList.add(null);
        }

        // 第period个数据的EMA初始值=MA(period)
        BigDecimal maInit = ZERO;
        for (int i = 0; i < period; i++) {
            maInit = maInit.add(barList.get(i).getClose());
        }
        BigDecimal emaInit = maInit.divide(BigDecimal.valueOf(period), SCALE, ROUND_MODE);
        emaList.add(emaInit);

        // 后续数据的EMA
        BigDecimal factor1 = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(period + 1), SCALE, ROUND_MODE);
        BigDecimal factor2 = BigDecimal.valueOf(period - 1).divide(BigDecimal.valueOf(period + 1), SCALE, ROUND_MODE);
        for (int i = period; i < barList.size(); i++) {
            BigDecimal ema = barList.get(i).getClose().multiply(factor1)
                    .add(emaList.get(i - 1).multiply(factor2))
                    .setScale(SCALE, ROUND_MODE);
            emaList.add(ema);
        }
        return emaList;
    }

    /**
     * 基于数值列表计算EMA（用于MACD的DEA计算）
     */
    private static List<BigDecimal> calculateEMAByValueList(List<BigDecimal> valueList, int period) {
        List<BigDecimal> emaList = new ArrayList<>();
        // 过滤null值，找到第一个非null的索引
        int firstValidIdx = -1;
        for (int i = 0; i < valueList.size(); i++) {
            if (valueList.get(i) != null) {
                firstValidIdx = i;
                break;
            }
            emaList.add(null);
        }

        if (firstValidIdx == -1) {
            return emaList;
        }

        // 初始值=第一个有效值
        emaList.add(valueList.get(firstValidIdx));
        BigDecimal factor1 = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(period + 1), SCALE, ROUND_MODE);
        BigDecimal factor2 = BigDecimal.valueOf(period - 1).divide(BigDecimal.valueOf(period + 1), SCALE, ROUND_MODE);

        for (int i = firstValidIdx + 1; i < valueList.size(); i++) {
            if (valueList.get(i) == null) {
                emaList.add(null);
                continue;
            }
            BigDecimal ema = valueList.get(i).multiply(factor1)
                    .add(emaList.get(i - 1).multiply(factor2))
                    .setScale(SCALE, ROUND_MODE);
            emaList.add(ema);
        }
        return emaList;
    }

    /**
     * ====================== 3. RSI指标计算 ======================
     * 计算相对强弱指数（RSI）
     * 公式：RSI(N) = 近N日上涨均值 / (上涨均值+下跌均值) * 100
     *
     * @param barList 日线数据
     * @param period  周期（6/14）
     * @return RSI值列表
     */
    public static List<BigDecimal> calculateRSI(List<StockDaily> barList, int period) {
        List<BigDecimal> rsiList = new ArrayList<>();
        if (barList.size() < period + 1) { // 需要period个涨跌幅数据
            barList.forEach(bar -> rsiList.add(null));
            return rsiList;
        }

        // 前period个数据RSI为null
        for (int i = 0; i < period; i++) {
            rsiList.add(null);
        }

        // 计算每日涨跌幅
        List<BigDecimal> changeList = new ArrayList<>();
        for (int i = 1; i < barList.size(); i++) {
            BigDecimal change = barList.get(i).getClose().subtract(barList.get(i - 1).getClose()).setScale(SCALE, ROUND_MODE);
            changeList.add(change);
        }

        // 滑动窗口计算RSI
        for (int i = period; i < barList.size(); i++) {
            BigDecimal upSum = ZERO; // 上涨总和
            BigDecimal downSum = ZERO; // 下跌总和（取绝对值）
            // 取最近period个涨跌幅
            for (int j = i - period; j < i; j++) {
                BigDecimal change = changeList.get(j);
                if (change.compareTo(ZERO) > 0) {
                    upSum = upSum.add(change);
                } else if (change.compareTo(ZERO) < 0) {
                    downSum = downSum.add(change.abs());
                }
            }

            // 避免除以0
            if (upSum.add(downSum).compareTo(ZERO) == 0) {
                rsiList.add(BigDecimal.valueOf(50).setScale(SCALE, ROUND_MODE));
                continue;
            }

            BigDecimal rsi = upSum.divide(upSum.add(downSum), SCALE, ROUND_MODE)
                    .multiply(HUNDRED)
                    .setScale(SCALE, ROUND_MODE);
            rsiList.add(rsi);
        }
        return rsiList;
    }

    /**
     * ====================== 4. KDJ指标计算 ======================
     * 计算KDJ指标（随机指标）
     * 公式：
     * RSV = (当日收盘价 - 近9日最低价) / (近9日最高价 - 近9日最低价) * 100
     * K = 2/3*昨日K + 1/3*当日RSV
     * D = 2/3*昨日D + 1/3*当日K
     * J = 3*K - 2*D
     *
     * @param barList 日线数据
     * @return 包含K/D/J的三维数组
     */
    public static List<BigDecimal[]> calculateKDJ(List<StockDaily> barList) {
        List<BigDecimal[]> kdjList = new ArrayList<>();
        int period = 9;
        if (barList.size() < period) {
            barList.forEach(bar -> kdjList.add(new BigDecimal[]{null, null, null}));
            return kdjList;
        }

        // 前period-1个数据为null
        for (int i = 0; i < period - 1; i++) {
            kdjList.add(new BigDecimal[]{null, null, null});
        }

        // 初始化K/D（默认50）
        BigDecimal prevK = BigDecimal.valueOf(50);
        BigDecimal prevD = BigDecimal.valueOf(50);

        for (int i = period - 1; i < barList.size(); i++) {
            // 取近9日的最高价和最低价
            BigDecimal high = barList.get(i).getHigh();
            BigDecimal low = barList.get(i).getLow();
            for (int j = i - period + 1; j < i; j++) {
                high = high.max(barList.get(j).getHigh());
                low = low.min(barList.get(j).getLow());
            }

            // 计算RSV
            BigDecimal rsv;
            if (high.compareTo(low) == 0) {
                rsv = BigDecimal.valueOf(50);
            } else {
                rsv = barList.get(i).getClose().subtract(low)
                        .divide(high.subtract(low), SCALE, ROUND_MODE)
                        .multiply(HUNDRED)
                        .setScale(SCALE, ROUND_MODE);
            }

            // 计算K/D/J
            BigDecimal k = prevK.multiply(BigDecimal.valueOf(2))
                    .add(rsv)
                    .divide(BigDecimal.valueOf(3), SCALE, ROUND_MODE);
            BigDecimal d = prevD.multiply(BigDecimal.valueOf(2))
                    .add(k)
                    .divide(BigDecimal.valueOf(3), SCALE, ROUND_MODE);
            BigDecimal j = k.multiply(BigDecimal.valueOf(3))
                    .subtract(d.multiply(BigDecimal.valueOf(2)))
                    .setScale(SCALE, ROUND_MODE);

            kdjList.add(new BigDecimal[]{k, d, j});
            // 更新前值
            prevK = k;
            prevD = d;
        }
        return kdjList;
    }

    /**
     * ====================== 5. 布林带（BOLL）计算 ======================
     * 计算布林带（中轨=MA20，上轨=MA20+2*标准差，下轨=MA20-2*标准差）
     *
     * @param barList 日线数据
     * @return 包含中轨/上轨/下轨的三维数组
     */
    public static List<BigDecimal[]> calculateBOLL(List<StockDaily> barList) {
        List<BigDecimal[]> bollList = new ArrayList<>();
        int period = 20;
        if (barList.size() < period) {
            barList.forEach(bar -> bollList.add(new BigDecimal[]{null, null, null}));
            return bollList;
        }

        // 计算MA20
        List<BigDecimal> ma20List = calculateMA(barList, period);

        for (int i = 0; i < barList.size(); i++) {
            if (ma20List.get(i) == null) {
                bollList.add(new BigDecimal[]{null, null, null});
                continue;
            }

            // 计算近20日收盘价的标准差
            BigDecimal sum = ZERO;
            for (int j = i - period + 1; j <= i; j++) {
                BigDecimal diff = barList.get(j).getClose().subtract(ma20List.get(i));
                sum = sum.add(diff.multiply(diff));
            }
            BigDecimal std = BigDecimal.valueOf(Math.sqrt(sum.divide(BigDecimal.valueOf(period), SCALE, ROUND_MODE).doubleValue()))
                    .setScale(SCALE, ROUND_MODE);

            // 计算上轨/下轨
            BigDecimal mid = ma20List.get(i);
            BigDecimal upper = mid.add(std.multiply(BigDecimal.valueOf(2))).setScale(SCALE, ROUND_MODE);
            BigDecimal lower = mid.subtract(std.multiply(BigDecimal.valueOf(2))).setScale(SCALE, ROUND_MODE);

            bollList.add(new BigDecimal[]{mid, upper, lower});
        }
        return bollList;
    }

    /**
     * ====================== 6. ATR（平均真实波幅）计算 ======================
     * 计算平均真实波幅（ATR）
     * 真实波幅TR = max(当日高-当日低, |当日高-昨日收|, |当日低-昨日收|)
     * ATR = MA(TR, 14)
     *
     * @param barList 日线数据
     * @return ATR值列表
     */
    public static List<BigDecimal> calculateATR(List<StockDaily> barList) {
        List<BigDecimal> atrList = new ArrayList<>();
        int period = 14;
        if (barList.size() < period + 1) {
            barList.forEach(bar -> atrList.add(null));
            return atrList;
        }

        // 计算每日真实波幅TR
        List<BigDecimal> trList = new ArrayList<>();
        trList.add(null); // 第一天无TR
        for (int i = 1; i < barList.size(); i++) {
            StockDaily curr = barList.get(i);
            StockDaily prev = barList.get(i - 1);

            // 当日高-当日低
            BigDecimal tr1 = curr.getHigh().subtract(curr.getLow());
            // |当日高-昨日收|
            BigDecimal tr2 = curr.getHigh().subtract(prev.getClose()).abs();
            // |当日低-昨日收|
            BigDecimal tr3 = curr.getLow().subtract(prev.getClose()).abs();

            // 取最大值作为TR
            BigDecimal tr = tr1.max(tr2).max(tr3).setScale(SCALE, ROUND_MODE);
            trList.add(tr);
        }

        // 前period个数据ATR为null
        for (int i = 0; i < period; i++) {
            atrList.add(null);
        }

        // 计算ATR（MA(TR,14)）
        for (int i = period; i < barList.size(); i++) {
            BigDecimal sum = ZERO;
            for (int j = i - period + 1; j <= i; j++) {
                if (trList.get(j) != null) {
                    sum = sum.add(trList.get(j));
                }
            }
            BigDecimal atr = sum.divide(BigDecimal.valueOf(period), SCALE, ROUND_MODE);
            atrList.add(atr);
        }
        return atrList;
    }

    /**
     * ====================== 7. OBV（能量潮）计算 ======================
     * 计算能量潮（OBV）
     * 规则：当日收盘价>昨日→OBV=昨日OBV+当日成交量；反之则减；相等则不变
     *
     * @param barList 日线数据
     * @return OBV值列表
     */
    public static List<Long> calculateOBV(List<StockDaily> barList) {
        List<Long> obvList = new ArrayList<>();
        if (barList.isEmpty()) {
            return obvList;
        }

        // 第一天OBV=当日成交量
        obvList.add(barList.get(0).getVolume());

        for (int i = 1; i < barList.size(); i++) {
            StockDaily curr = barList.get(i);
            StockDaily prev = barList.get(i - 1);
            long prevObv = obvList.get(i - 1);
            long currVol = curr.getVolume();

            if (curr.getClose().compareTo(prev.getClose()) > 0) {
                obvList.add(prevObv + currVol);
            } else if (curr.getClose().compareTo(prev.getClose()) < 0) {
                obvList.add(prevObv - currVol);
            } else {
                obvList.add(prevObv);
            }
        }
        return obvList;
    }

    /**
     * ====================== 8. WR（威廉指标）计算 ======================
     * 80 → 超卖 → 短线低吸信号
     * <20 → 超买 → 短线止盈信号
     * ================================================================
     * 计算威廉指标 WR
     * 公式：WR(N) = (N日最高收盘价 - 当日收盘价) / (N日最高收盘价 - N日最低收盘价) * 100
     *
     * @param barList 日线数据
     * @param period  周期（常用10）
     * @return WR值列表 0~100
     */
    public static List<BigDecimal> calculateWR(List<StockDaily> barList, int period) {
        List<BigDecimal> wrList = new ArrayList<>();
        if (barList.size() < period) {
            barList.forEach(bar -> wrList.add(null));
            return wrList;
        }

        // 前 period-1 个为 null
        for (int i = 0; i < period - 1; i++) {
            wrList.add(null);
        }

        for (int i = period - 1; i < barList.size(); i++) {
            // 近 N 日最高价
            BigDecimal highest = barList.get(i).getHigh();
            // 近 N 日最低价
            BigDecimal lowest = barList.get(i).getLow();

            for (int j = i - period + 1; j <= i; j++) {
                highest = highest.max(barList.get(j).getHigh());
                lowest = lowest.min(barList.get(j).getLow());
            }

            BigDecimal close = barList.get(i).getClose();

            // 分母为0（高低价相同）
            if (highest.subtract(lowest).compareTo(ZERO) == 0) {
                wrList.add(BigDecimal.valueOf(50).setScale(SCALE, ROUND_MODE));
                continue;
            }

            BigDecimal wr = highest.subtract(close)
                    .divide(highest.subtract(lowest), SCALE, ROUND_MODE)
                    .multiply(HUNDRED)
                    .setScale(SCALE, ROUND_MODE);

            wrList.add(wr);
        }
        return wrList;
    }

    /**
     * ====================== 9. CCI（顺势指标）计算 ======================
     * > 100 → 进入强势拉升区
     * < -100 → 进入超跌区
     * ==================================================================
     * 计算顺势指标 CCI
     * 公式：
     * Typical Price = (high + low + close) / 3
     * MA = MA(Typical Price, N)
     * MD = 平均绝对偏差
     * CCI = (TP - MA) / (0.015 * MD)
     *
     * @param barList 日线数据
     * @param period  周期（常用14）
     * @return CCI值列表
     */
    public static List<BigDecimal> calculateCCI(List<StockDaily> barList, int period) {
        List<BigDecimal> cciList = new ArrayList<>();
        if (barList.size() < period) {
            barList.forEach(bar -> cciList.add(null));
            return cciList;
        }

        // 计算每日典型价格 TP
        List<BigDecimal> tpList = new ArrayList<>();
        for (StockDaily bar : barList) {
            BigDecimal tp = bar.getHigh()
                    .add(bar.getLow())
                    .add(bar.getClose())
                    .divide(BigDecimal.valueOf(3), SCALE, ROUND_MODE);
            tpList.add(tp);
        }

        // 前 period-1 个为 null
        for (int i = 0; i < period - 1; i++) {
            cciList.add(null);
        }

        for (int i = period - 1; i < barList.size(); i++) {
            // 计算 MA(TP)
            BigDecimal sumMA = ZERO;
            for (int j = i - period + 1; j <= i; j++) {
                sumMA = sumMA.add(tpList.get(j));
            }
            BigDecimal ma = sumMA.divide(BigDecimal.valueOf(period), SCALE, ROUND_MODE);

            // 计算平均绝对偏差 MD
            BigDecimal sumMD = ZERO;
            for (int j = i - period + 1; j <= i; j++) {
                BigDecimal diff = tpList.get(j).subtract(ma).abs();
                sumMD = sumMD.add(diff);
            }
            BigDecimal md = sumMD.divide(BigDecimal.valueOf(period), SCALE, ROUND_MODE);

            // 分母为0
            if (md.compareTo(ZERO) == 0) {
                cciList.add(ZERO.setScale(SCALE, ROUND_MODE));
                continue;
            }

            BigDecimal cci = tpList.get(i).subtract(ma).divide(md.multiply(new BigDecimal("0.015")), SCALE, ROUND_MODE);

            cciList.add(cci);
        }
        return cciList;
    }

    /**
     * ====================== 10. MFI（资金流量指标）计算 ======================
     * 比 RSI 更灵敏，叠加成交量
     * >80 超买，<20 超卖
     * 顶背离 / 底背离准确率极高
     * ======================================================================
     * <p>
     * 计算资金流量指标 MFI
     * 公式：
     * Typical Price = (high+low+close)/3
     * Money Flow = TP * volume
     * 正资金流量：今日TP > 昨日TP
     * 负资金流量：今日TP < 昨日TP
     * 资金比率 = 正资金流量和 / 负资金流量和
     * MFI = 100 - (100 / (1 + 资金比率))
     *
     * @param barList 日线数据
     * @param period  周期（常用14）
     * @return MFI值列表 0~100
     */
    public static List<BigDecimal> calculateMFI(List<StockDaily> barList, int period) {
        List<BigDecimal> mfiList = new ArrayList<>();
        if (barList.size() < period + 1) {
            barList.forEach(bar -> mfiList.add(null));
            return mfiList;
        }

        // 1. 计算典型价格 TP
        List<BigDecimal> tpList = new ArrayList<>();
        for (StockDaily bar : barList) {
            BigDecimal tp = bar.getHigh()
                    .add(bar.getLow())
                    .add(bar.getClose())
                    .divide(BigDecimal.valueOf(3), SCALE, ROUND_MODE);
            tpList.add(tp);
        }

        // 2. 计算资金流量
        List<BigDecimal> moneyFlowList = new ArrayList<>();
        for (int i = 0; i < barList.size(); i++) {
            BigDecimal mf = tpList.get(i).multiply(new BigDecimal(barList.get(i).getVolume()));
            moneyFlowList.add(mf);
        }

        // 前 period 个为 null
        for (int i = 0; i < period; i++) {
            mfiList.add(null);
        }

        // 3. 滑动窗口计算 MFI
        for (int i = period; i < barList.size(); i++) {
            BigDecimal positiveFlow = ZERO;
            BigDecimal negativeFlow = ZERO;

            for (int j = i - period + 1; j <= i; j++) {
                if (j < 1) continue;

                BigDecimal currTp = tpList.get(j);
                BigDecimal prevTp = tpList.get(j - 1);
                BigDecimal mf = moneyFlowList.get(j);

                if (currTp.compareTo(prevTp) > 0) {
                    positiveFlow = positiveFlow.add(mf);
                } else if (currTp.compareTo(prevTp) < 0) {
                    negativeFlow = negativeFlow.add(mf);
                }
            }

            // 无负资金流
            if (negativeFlow.compareTo(ZERO) == 0) {
                mfiList.add(HUNDRED.setScale(SCALE, ROUND_MODE));
                continue;
            }

            BigDecimal moneyRatio = positiveFlow.divide(negativeFlow, SCALE, ROUND_MODE);
            BigDecimal mfi = HUNDRED
                    .subtract(HUNDRED.divide(BigDecimal.ONE.add(moneyRatio), SCALE, ROUND_MODE))
                    .setScale(SCALE, ROUND_MODE);

            mfiList.add(mfi);
        }
        return mfiList;
    }


    /**
     * ====================== 11. VMACD（量能MACD）计算 ======================
     * 1. V_DIF 上穿 V_DEA → 量能放大，资金进场（比价格 MACD 提前预判趋势）；
     * 2. 价格 MACD 红柱但 VMACD 绿柱 → 量价背离，谨防回调
     * =====================================================================
     * <p>
     * 计算量能MACD（以成交量代替收盘价计算MACD）
     * 公式：V_DIF=EMA(成交量,12)-EMA(成交量,26)，V_DEA=EMA(V_DIF,9)，V_BAR=2*(V_DIF-V_DEA)
     *
     * @param barList 日线数据
     * @return 包含V_DIF/V_DEA/V_BAR的三维数组
     */
    public static List<BigDecimal[]> calculateVMACD(List<StockDaily> barList) {
        List<BigDecimal[]> vmacdList = new ArrayList<>();
        int minSize = 26; // 最小数据量（EMA26）
        if (barList.size() < minSize) {
            barList.forEach(bar -> vmacdList.add(new BigDecimal[]{null, null, null}));
            return vmacdList;
        }

        // 步骤1：提取成交量并转为BigDecimal
        List<BigDecimal> volList = new ArrayList<>();
        for (StockDaily bar : barList) {
            volList.add(new BigDecimal(bar.getVolume()).setScale(SCALE, ROUND_MODE));
        }

        // 步骤2：计算成交量的EMA12和EMA26
        List<BigDecimal> volEma12 = calculateEMAByValueList(volList, 12);
        List<BigDecimal> volEma26 = calculateEMAByValueList(volList, 26);

        // 步骤3：计算V_DIF
        List<BigDecimal> vDifList = new ArrayList<>();
        for (int i = 0; i < barList.size(); i++) {
            if (volEma12.get(i) == null || volEma26.get(i) == null) {
                vDifList.add(null);
            } else {
                vDifList.add(volEma12.get(i).subtract(volEma26.get(i)).setScale(SCALE, ROUND_MODE));
            }
        }

        // 步骤4：计算V_DEA
        List<BigDecimal> vDeaList = calculateEMAByValueList(vDifList, 9);

        // 步骤5：计算V_BAR
        for (int i = 0; i < barList.size(); i++) {
            BigDecimal vDif = vDifList.get(i);
            BigDecimal vDea = vDeaList.get(i);
            if (vDif == null || vDea == null) {
                vmacdList.add(new BigDecimal[]{null, null, null});
            } else {
                BigDecimal vBar = vDif.subtract(vDea).multiply(BigDecimal.valueOf(2)).setScale(SCALE, ROUND_MODE);
                vmacdList.add(new BigDecimal[]{vDif, vDea, vBar});
            }
        }
        return vmacdList;
    }

    /**
     * ====================== 12. OBV均线（OBV_MA20）计算 ======================
     * 1. OBV 突破 OBV_MA20 → 资金趋势向上，短线看涨；
     * 2. OBV 在 MA20 上方持续走高 → 资金锁仓，趋势延续
     * =======================================================================
     * <p>
     * 计算OBV均线（常用20日）
     * 公式：OBV_MA(N) = MA(OBV, N)
     *
     * @param obvList 已计算的OBV列表（来自calculateOBV方法）
     * @param period  周期（常用20）
     * @return OBV均线列表
     */
    public static List<Long> calculateOBVMA(List<Long> obvList, int period) {
        List<Long> obvMaList = new ArrayList<>();
        if (obvList.size() < period) {
            obvList.forEach(obv -> obvMaList.add(null));
            return obvMaList;
        }

        // 前period-1个为null
        for (int i = 0; i < period - 1; i++) {
            obvMaList.add(null);
        }

        // 滑动窗口计算MA
        for (int i = period - 1; i < obvList.size(); i++) {
            long sum = 0;
            for (int j = i - period + 1; j <= i; j++) {
                sum += obvList.get(j);
            }
            long obvMa = sum / period; // 成交量为整数，MA取整
            obvMaList.add(obvMa);
        }
        return obvMaList;
    }

    /**
     * ====================== 13. 筹码相关指标（日线简化版） ======================
     * 1. 当前股价远高于平均成本 → 获利盘多，易回调；
     * 2. 当前股价接近平均成本 → 支撑 / 压力位，变盘节点
     * ========================================================================
     * <p>
     * 计算60日平均成本（成交量加权）
     * 公式：AVG_COST = Σ(TP * Volume) / ΣVolume （TP=(high+low+close)/3）
     *
     * @param barList 日线数据
     * @param period  周期（常用60）
     * @return 平均成本列表
     */
    public static List<BigDecimal> calculateAvgCost(List<StockDaily> barList, int period) {
        List<BigDecimal> avgCostList = new ArrayList<>();
        if (barList.size() < period) {
            barList.forEach(bar -> avgCostList.add(null));
            return avgCostList;
        }

        // 前period-1个为null
        for (int i = 0; i < period - 1; i++) {
            avgCostList.add(null);
        }

        for (int i = period - 1; i < barList.size(); i++) {
            BigDecimal sumTpVol = ZERO; // TP*成交量 总和
            long sumVol = 0; // 成交量总和

            for (int j = i - period + 1; j <= i; j++) {
                StockDaily bar = barList.get(j);
                // 典型价格TP
                BigDecimal tp = bar.getHigh().add(bar.getLow()).add(bar.getClose()).divide(BigDecimal.valueOf(3), SCALE, ROUND_MODE);
                // TP*成交量
                BigDecimal tpVol = tp.multiply(new BigDecimal(bar.getVolume()));
                sumTpVol = sumTpVol.add(tpVol);
                sumVol += bar.getVolume();
            }

            if (sumVol == 0) {
                avgCostList.add(null);
                continue;
            }

            // 平均成本 = 总金额 / 总成交量
            BigDecimal avgCost = sumTpVol.divide(new BigDecimal(sumVol), SCALE, ROUND_MODE);
            avgCostList.add(avgCost);
        }
        return avgCostList;
    }

    /**
     * ====================== 14. 筹码集中度（日线简化版） ======================
     * 1. <10% → 筹码高度集中，主力控盘，易拉升；
     * 2. >30% → 筹码分散，震荡为主；
     * 3. 集中度持续下降 → 主力吸筹
     * ======================================================================
     * <p>
     * 计算筹码集中度（简化版，精准版需逐笔数据）
     * 公式：集中度 = (近60日90%成本价 - 近60日10%成本价) / 平均成本 * 100%
     * 简化版：90%成本价=近60日最高价，10%成本价=近60日最低价
     *
     * @param barList     日线数据
     * @param avgCostList 已计算的平均成本列表
     * @param period      周期（常用60）
     * @return 筹码集中度列表（%，值越小筹码越集中）
     */
    public static List<BigDecimal> calculateCostConcentration(List<StockDaily> barList, List<BigDecimal> avgCostList, int period) {
        List<BigDecimal> concentrationList = new ArrayList<>();
        if (barList.size() < period || avgCostList.size() != barList.size()) {
            barList.forEach(bar -> concentrationList.add(null));
            return concentrationList;
        }

        // 前period-1个为null
        for (int i = 0; i < period - 1; i++) {
            concentrationList.add(null);
        }

        for (int i = period - 1; i < barList.size(); i++) {
            if (avgCostList.get(i) == null || avgCostList.get(i).compareTo(ZERO) == 0) {
                concentrationList.add(null);
                continue;
            }

            // 近60日最高价（90%成本价简化）、最低价（10%成本价简化）
            BigDecimal high60 = barList.get(i).getHigh();
            BigDecimal low60 = barList.get(i).getLow();
            for (int j = i - period + 1; j <= i; j++) {
                high60 = high60.max(barList.get(j).getHigh());
                low60 = low60.min(barList.get(j).getLow());
            }

            // 计算集中度
            BigDecimal concentration = high60.subtract(low60)
                    .divide(avgCostList.get(i), SCALE, ROUND_MODE)
                    .multiply(HUNDRED)
                    .setScale(SCALE, ROUND_MODE);
            concentrationList.add(concentration);
        }
        return concentrationList;
    }


    /**
     * ====================== 15. 顶底背离核心判断方法 ======================
     * 回溯周期      短线用 10 天，中线用 20 天
     * 背离强度阈值	10	阈值越高，背离信号越可靠（过滤假背离）
     * 背离优先级	MACD>RSI>KDJ>CCI	可根据策略调整（如短线优先 RSI）
     * <p>
     * 批量判断单只股票的所有背离类型（按优先级：MACD>RSI>KDJ>CCI）
     *
     * @param barList   日线数据
     * @param macdList  MACD指标列表（DIF/DEA/BAR）
     * @param rsi14List RSI14列表
     * @param kdjList   KDJ列表（K/D/J）
     * @param cciList   CCI列表
     * @return 二维数组：[divergenceType, divergenceStrength]
     */
    public static List<Object[]> batchJudgeDivergence(List<StockDaily> barList,
                                                      List<BigDecimal[]> macdList,
                                                      List<BigDecimal> rsi14List,
                                                      List<BigDecimal[]> kdjList,
                                                      List<BigDecimal> cciList) {
        List<Object[]> divergenceResult = new ArrayList<>();
        int lookBackPeriod = 10; // 回溯周期（可配置）
        int minSize = Math.max(lookBackPeriod, 20); // 最小数据量
        BigDecimal lookBackStrength = BigDecimal.valueOf(10); // 背离强度阈值	10	阈值越高，背离信号越可靠（过滤假背离）

        // 提取收盘价列表
        List<BigDecimal> closeList = new ArrayList<>();
        for (StockDaily bar : barList) {
            closeList.add(bar.getClose());
        }

        // 提取各指标核心值
        List<BigDecimal> macdDifList = new ArrayList<>();
        List<BigDecimal> kdjJList = new ArrayList<>();
        for (int i = 0; i < barList.size(); i++) {
            macdDifList.add(macdList.get(i) != null ? macdList.get(i)[0] : null);
            kdjJList.add(kdjList.get(i) != null ? kdjList.get(i)[2] : null);
        }

        // 前minSize个数据无背离
        for (int i = 0; i < minSize; i++) {
            divergenceResult.add(new Object[]{DIVERGENCE_NONE, ZERO});
        }

        // 批量判断背离（按优先级取最强的背离类型）
        for (int i = minSize; i < barList.size(); i++) {
            // 1. 判断MACD背离（优先级最高）
            Object[] macdTop = judgeDivergence(closeList, macdDifList, i, lookBackPeriod, DIVERGENCE_MACd_TOP, DIVERGENCE_NONE);
            Object[] macdBottom = judgeDivergence(closeList, macdDifList, i, lookBackPeriod, DIVERGENCE_NONE, DIVERGENCE_MACd_BOTTOM);

            if ((int) macdTop[0] != DIVERGENCE_NONE && ((BigDecimal) macdTop[1]).compareTo(lookBackStrength) > 0) {
                divergenceResult.add(macdTop);
                continue;
            }
            if ((int) macdBottom[0] != DIVERGENCE_NONE && ((BigDecimal) macdBottom[1]).compareTo(BigDecimal.valueOf(10)) > 0) {
                divergenceResult.add(macdBottom);
                continue;
            }

            // 2. 判断RSI背离
            Object[] rsiTop = judgeDivergence(closeList, rsi14List, i, lookBackPeriod, DIVERGENCE_RSI_TOP, DIVERGENCE_NONE);
            Object[] rsiBottom = judgeDivergence(closeList, rsi14List, i, lookBackPeriod, DIVERGENCE_NONE, DIVERGENCE_RSI_BOTTOM);
            if ((int) rsiTop[0] != DIVERGENCE_NONE && ((BigDecimal) rsiTop[1]).compareTo(lookBackStrength) > 0) {
                divergenceResult.add(rsiTop);
                continue;
            }
            if ((int) rsiBottom[0] != DIVERGENCE_NONE && ((BigDecimal) rsiBottom[1]).compareTo(lookBackStrength) > 0) {
                divergenceResult.add(rsiBottom);
                continue;
            }

            // 3. 判断KDJ背离
            Object[] kdjTop = judgeDivergence(closeList, kdjJList, i, lookBackPeriod, DIVERGENCE_KDJ_TOP, DIVERGENCE_NONE);
            Object[] kdjBottom = judgeDivergence(closeList, kdjJList, i, lookBackPeriod, DIVERGENCE_NONE, DIVERGENCE_KDJ_BOTTOM);
            if ((int) kdjTop[0] != DIVERGENCE_NONE && ((BigDecimal) kdjTop[1]).compareTo(lookBackStrength) > 0) {
                divergenceResult.add(kdjTop);
                continue;
            }
            if ((int) kdjBottom[0] != DIVERGENCE_NONE && ((BigDecimal) kdjBottom[1]).compareTo(lookBackStrength) > 0) {
                divergenceResult.add(kdjBottom);
                continue;
            }

            // 4. 判断CCI背离
            Object[] cciTop = judgeDivergence(closeList, cciList, i, lookBackPeriod, DIVERGENCE_CCI_TOP, DIVERGENCE_NONE);
            Object[] cciBottom = judgeDivergence(closeList, cciList, i, lookBackPeriod, DIVERGENCE_NONE, DIVERGENCE_CCI_BOTTOM);
            if ((int) cciTop[0] != DIVERGENCE_NONE && ((BigDecimal) cciTop[1]).compareTo(lookBackStrength) > 0) {
                divergenceResult.add(cciTop);
                continue;
            }
            if ((int) cciBottom[0] != DIVERGENCE_NONE && ((BigDecimal) cciBottom[1]).compareTo(lookBackStrength) > 0) {
                divergenceResult.add(cciBottom);
                continue;
            }

            // 无背离
            divergenceResult.add(new Object[]{DIVERGENCE_NONE, ZERO});
        }

        return divergenceResult;
    }


    /**
     * 通用顶底背离判断（适配所有指标）
     *
     * @param priceList      价格列表（收盘价）
     * @param indicatorList  指标值列表（MACD DIF/RSI/KDJ J/CCI）
     * @param startIdx       起始索引（需至少2个高点/低点）
     * @param lookBackPeriod 回溯周期（常用10）
     * @return 数组：[背离类型, 背离强度]
     */
    private static Object[] judgeDivergence(List<BigDecimal> priceList, List<BigDecimal> indicatorList,
                                            int startIdx, int lookBackPeriod, int topType, int bottomType) {
        // 数据不足直接返回无背离
        if (startIdx < lookBackPeriod || indicatorList.size() <= startIdx || priceList.size() <= startIdx) {
            return new Object[]{DIVERGENCE_NONE, ZERO};
        }

        // 1. 找价格的两个高点/低点
        // 近期极值（当前往前lookBackPeriod/2）
        int recentStart = startIdx - lookBackPeriod / 2;
        BigDecimal recentPriceExtreme = priceList.get(startIdx);
        int recentExtremeIdx = startIdx;
        for (int i = recentStart; i <= startIdx; i++) {
            if (priceList.get(i) == null) continue;
            // 顶背离：找近期最高价；底背离：找近期最低价
            if (topType != DIVERGENCE_NONE) {
                if (priceList.get(i).compareTo(recentPriceExtreme) > 0) {
                    recentPriceExtreme = priceList.get(i);
                    recentExtremeIdx = i;
                }
            } else {
                if (priceList.get(i).compareTo(recentPriceExtreme) < 0) {
                    recentPriceExtreme = priceList.get(i);
                    recentExtremeIdx = i;
                }
            }
        }

        // 前期极值（lookBackPeriod前的区间）
        int preStart = startIdx - lookBackPeriod;
        int preEnd = startIdx - lookBackPeriod / 2;
        BigDecimal prePriceExtreme = priceList.get(preStart);
        int preExtremeIdx = preStart;
        for (int i = preStart; i <= preEnd; i++) {
            if (priceList.get(i) == null) continue;
            if (topType != DIVERGENCE_NONE) {
                if (priceList.get(i).compareTo(prePriceExtreme) > 0) {
                    prePriceExtreme = priceList.get(i);
                    preExtremeIdx = i;
                }
            } else {
                if (priceList.get(i).compareTo(prePriceExtreme) < 0) {
                    prePriceExtreme = priceList.get(i);
                    preExtremeIdx = i;
                }
            }
        }

        // 2. 找对应位置的指标极值
        BigDecimal recentIndicator = indicatorList.get(recentExtremeIdx);
        BigDecimal preIndicator = indicatorList.get(preExtremeIdx);
        if (recentIndicator == null || preIndicator == null) {
            return new Object[]{DIVERGENCE_NONE, ZERO};
        }

        // 3. 判断背离
        int divergenceType = DIVERGENCE_NONE;
        BigDecimal strength = ZERO;
        // 顶背离：价格新高但指标未新高
        if (topType != DIVERGENCE_NONE) {
            if (recentPriceExtreme.compareTo(prePriceExtreme) > 0 && recentIndicator.compareTo(preIndicator) < 0) {
                divergenceType = topType;
                // 计算强度：(价格差值/前期价格) + (指标差值/前期指标) * 100
                BigDecimal priceDiffRatio = recentPriceExtreme.subtract(prePriceExtreme).divide(prePriceExtreme, SCALE, ROUND_MODE);
                BigDecimal indicatorDiffRatio = preIndicator.subtract(recentIndicator).divide(preIndicator, SCALE, ROUND_MODE);
                strength = priceDiffRatio.add(indicatorDiffRatio).multiply(HUNDRED).setScale(SCALE, ROUND_MODE);
            }
        } else {
            // 底背离：价格新低但指标未新低
            if (recentPriceExtreme.compareTo(prePriceExtreme) < 0 && recentIndicator.compareTo(preIndicator) > 0) {
                divergenceType = bottomType;
                BigDecimal priceDiffRatio = prePriceExtreme.subtract(recentPriceExtreme).divide(prePriceExtreme, SCALE, ROUND_MODE);
                BigDecimal indicatorDiffRatio = recentIndicator.subtract(preIndicator).divide(preIndicator, SCALE, ROUND_MODE);
                strength = priceDiffRatio.add(indicatorDiffRatio).multiply(HUNDRED).setScale(SCALE, ROUND_MODE);
            }
        }

        // 强度限制在0~100
        if (strength.compareTo(ZERO) < 0) strength = ZERO;
        if (strength.compareTo(HUNDRED) > 0) strength = HUNDRED;

        return new Object[]{divergenceType, strength};
    }


    /**
     * ====================== 16. 多指标共振筛选 ======================
     * 批量筛选全量股票的共振信号
     *
     * @param stockCode 股票代码
     * @param barList   日线数据
     * @param techList  技术指标列表
     */
    public static void batchJudgeResonance(String stockCode, List<StockDaily> barList, List<StockTechDaily> techList) {
        for (int i = 0; i < techList.size(); i++) {
            Object[] resonance = judgeResonance(barList, techList, i);
            techList.get(i).setResonanceSignal((int) resonance[0]);
            techList.get(i).setResonanceScore((BigDecimal) resonance[1]);
        }
    }


    /**
     * 短线多指标共振筛选（可配置规则）
     *
     * @param barList  日线数据
     * @param techList 技术指标列表（StockTechDaily）
     * @param idx      当前索引
     * @return 数组：[resonanceSignal, resonanceScore]
     */
    // ====================== 15. 多指标共振筛选（完整版：含WR/MFI/BOLL/ATR/筹码） ======================

    /**
     * 短线多指标共振筛选（全指标版）
     *
     * @param barList  日线数据
     * @param techList 技术指标列表（StockTechDaily）
     * @param idx      当前索引
     * @return 数组：[resonanceSignal, resonanceScore]
     */
    public static Object[] judgeResonance(List<StockDaily> barList, List<StockTechDaily> techList, int idx) {
        // 数据不足直接返回无信号
        if (idx < 20) {
            return new Object[]{RESONANCE_NONE, ZERO};
        }

        StockTechDaily currTech = techList.get(idx);
        StockTechDaily prevTech = techList.get(idx - 1);
        StockDaily currBar = barList.get(idx);

        int signal = RESONANCE_NONE;
        BigDecimal score = ZERO;
        int totalBuyRule = 12;   // 买入总规则数
        int buyMatch = 0;        // 买入匹配数
        int totalSellRule = 10;  // 卖出总规则数
        int sellMatch = 0;       // 卖出匹配数

        // ======================== 【买入共振规则：12条】 ========================
        // 1. MA5上穿MA10（均线金叉）
        if (currTech.getMa5() != null && currTech.getMa10() != null && prevTech.getMa5() != null && prevTech.getMa10() != null
                && currTech.getMa5().compareTo(currTech.getMa10()) > 0 && prevTech.getMa5().compareTo(prevTech.getMa10()) <= 0) buyMatch++;

        // 2. MACD金叉（DIF上穿DEA）
        if (currTech.getMacdDif() != null && currTech.getMacdDea() != null && prevTech.getMacdDif() != null && prevTech.getMacdDea() != null
                && currTech.getMacdDif().compareTo(currTech.getMacdDea()) > 0 && prevTech.getMacdDif().compareTo(prevTech.getMacdDea()) <= 0) buyMatch++;

        // 3. RSI14：30~70 健康区间且向上
        if (currTech.getRsi14() != null && prevTech.getRsi14() != null
                && currTech.getRsi14().compareTo(new BigDecimal(30)) > 0
                && currTech.getRsi14().compareTo(new BigDecimal(70)) < 0
                && currTech.getRsi14().compareTo(prevTech.getRsi14()) > 0) buyMatch++;

        // 4. KDJ金叉 + J<80（未超买）
        if (currTech.getKdjK() != null && currTech.getKdjD() != null && currTech.getKdjJ() != null
                && prevTech.getKdjK() != null && prevTech.getKdjD() != null
                && currTech.getKdjK().compareTo(currTech.getKdjD()) > 0
                && prevTech.getKdjK().compareTo(prevTech.getKdjD()) <= 0
                && currTech.getKdjJ().compareTo(new BigDecimal(80)) < 0) buyMatch++;

        // 5. CCI > -100 或上穿100（进入强势）
        if (currTech.getCci() != null && prevTech.getCci() != null
                && (currTech.getCci().compareTo(new BigDecimal(100)) > 0 && prevTech.getCci().compareTo(new BigDecimal(100)) <= 0
                || currTech.getCci().compareTo(new BigDecimal(-100)) > 0)) buyMatch++;

        // 6. WR10 > 80（超卖区，低吸信号）
        if (currTech.getWr10() != null && currTech.getWr10().compareTo(new BigDecimal(80)) > 0) buyMatch++;

        // 7. MFI > 50 且向上（资金流入）
        if (currTech.getMfi() != null && prevTech.getMfi() != null
                && currTech.getMfi().compareTo(new BigDecimal(50)) > 0
                && currTech.getMfi().compareTo(prevTech.getMfi()) > 0) buyMatch++;

        // 8. BOLL：收盘价站上中轨（中轨支撑）
        if (currTech.getBollMid() != null && currBar.getClose().compareTo(currTech.getBollMid()) >= 0) buyMatch++;

        // 9. ATRRatio > 1.3（波动率放大，变盘启动）
        if (currTech.getAtrRatio() != null && currTech.getAtrRatio().compareTo(new BigDecimal(1.3)) > 0) buyMatch++;

        // 10. 筹码集中度 < 15%（高度集中，主力控盘）
        if (currTech.getCostConcentration() != null && currTech.getCostConcentration().compareTo(new BigDecimal(15)) < 0) buyMatch++;

        // 11. VMACD金叉（量能同步放大）
        if (currTech.getVmacdDif() != null && currTech.getVmacdDea() != null && prevTech.getVmacdDif() != null && prevTech.getVmacdDea() != null
                && currTech.getVmacdDif().compareTo(currTech.getVmacdDea()) > 0) buyMatch++;

        // 12. OBV > OBV_MA20（资金趋势向上）
        if (currTech.getObv() != null && currTech.getObvMa20() != null && currTech.getObv() > currTech.getObvMa20()) buyMatch++;

        // ======================== 【卖出共振规则：10条】 ========================
        // 1. MA5下穿MA10（均线死叉）
        if (currTech.getMa5() != null && currTech.getMa10() != null && prevTech.getMa5() != null && prevTech.getMa10() != null
                && currTech.getMa5().compareTo(currTech.getMa10()) < 0 && prevTech.getMa5().compareTo(prevTech.getMa10()) >= 0) sellMatch++;

        // 2. MACD死叉
        if (currTech.getMacdDif() != null && currTech.getMacdDea() != null && prevTech.getMacdDif() != null && prevTech.getMacdDea() != null
                && currTech.getMacdDif().compareTo(currTech.getMacdDea()) < 0) sellMatch++;

        // 3. RSI>80 或 KDJ J>90（超买）
        if ((currTech.getRsi14() != null && currTech.getRsi14().compareTo(new BigDecimal(80)) > 0)
                || (currTech.getKdjJ() != null && currTech.getKdjJ().compareTo(new BigDecimal(90)) > 0)) sellMatch++;

        // 4. 任意顶背离
        if (currTech.getDivergenceType() != null && currTech.getDivergenceType() % 2 == 1) sellMatch++;

        // 5. WR10 < 20（超买区，见顶信号）
        if (currTech.getWr10() != null && currTech.getWr10().compareTo(new BigDecimal(20)) < 0) sellMatch++;

        // 6. MFI > 80（资金超买）
        if (currTech.getMfi() != null && currTech.getMfi().compareTo(new BigDecimal(80)) > 0) sellMatch++;

        // 7. BOLL：跌破上轨/中轨（上轨压力）
        if (currTech.getBollUpper() != null && currBar.getClose().compareTo(currTech.getBollUpper()) < 0) sellMatch++;

        // 8. 筹码集中度 > 30%（筹码分散，抛压大）
        if (currTech.getCostConcentration() != null && currTech.getCostConcentration().compareTo(new BigDecimal(30)) > 0) sellMatch++;

        // 9. ATRRatio < 0.9（波动率萎缩，变盘下跌）
        if (currTech.getAtrRatio() != null && currTech.getAtrRatio().compareTo(new BigDecimal("0.9")) < 0) sellMatch++;

        // 10. OBV 跌破 OBV_MA20（资金出逃）
        if (currTech.getObv() != null && currTech.getObvMa20() != null && currTech.getObv() < currTech.getObvMa20()) sellMatch++;

        // ======================== 信号判定 ========================
        // 强买入：≥7条匹配
        if (buyMatch >= 7) {
            signal = RESONANCE_BUY;
            score = new BigDecimal(buyMatch * 100 / totalBuyRule);
        }
        // 强卖出：≥5条匹配
        else if (sellMatch >= 5) {
            signal = RESONANCE_SELL;
            score = new BigDecimal(sellMatch * 100 / totalSellRule);
        }
        // 趋势走强
        else if (buyMatch >= 4 && buyMatch < 7) {
            signal = RESONANCE_TREND_UP;
            score = new BigDecimal(buyMatch * 100 / totalBuyRule);
        }
        // 趋势走弱
        else if (sellMatch >= 3 && sellMatch < 5) {
            signal = RESONANCE_TREND_DOWN;
            score = new BigDecimal(sellMatch * 100 / totalSellRule);
        }

        return new Object[]{signal, score};
    }


    // ====================== 15. 策略回测（验证信号胜率） ======================

    /**
     * 回测共振信号的实战效果
     *
     * @param stockCode 股票代码
     * @param barList   日线数据
     * @param techList  带共振信号的指标数据
     * @return 回测结果：[买入信号数, 盈利信号数, 胜率, 平均盈亏比]
     */
    public static BigDecimal[] backTestResonanceSignal(String stockCode, List<StockDaily> barList, List<StockTechDaily> techList) {
        int buySignalCount = 0;    // 买入信号总数
        int profitCount = 0;       // 盈利的买入信号数
        BigDecimal totalProfit = ZERO; // 总盈亏
        BigDecimal totalLoss = ZERO;   // 总亏损

        for (int i = 20; i < techList.size() - 3; i++) { // 信号后看3天收益
            StockTechDaily tech = techList.get(i);
            if (tech.getResonanceSignal() != RESONANCE_BUY || tech.getResonanceScore().compareTo(new BigDecimal(60)) < 0) {
                continue;
            }

            buySignalCount++;
            // 买入价：信号日收盘价
            BigDecimal buyPrice = barList.get(i).getClose();
            // 卖出价：3天后收盘价（超短线）
            BigDecimal sellPrice = barList.get(i + 3).getClose();
            // 盈亏比（扣除手续费0.1%）
            BigDecimal profitRatio = sellPrice.subtract(buyPrice).divide(buyPrice, SCALE, ROUND_MODE).subtract(new BigDecimal("0.001"));

            if (profitRatio.compareTo(ZERO) > 0) {
                profitCount++;
                totalProfit = totalProfit.add(profitRatio);
            } else {
                totalLoss = totalLoss.add(profitRatio.abs());
            }
        }

        // 计算胜率
        BigDecimal winRate = buySignalCount == 0 ? ZERO : new BigDecimal(profitCount).divide(new BigDecimal(buySignalCount), SCALE, ROUND_MODE).multiply(new BigDecimal(100));

        // 计算平均盈亏比
        BigDecimal avgProfitRatio = profitCount == 0 ? ZERO : totalProfit.divide(new BigDecimal(profitCount), SCALE, ROUND_MODE);
        BigDecimal avgLossRatio = (buySignalCount - profitCount) == 0 ? ZERO : totalLoss.divide(new BigDecimal(buySignalCount - profitCount), SCALE, ROUND_MODE);
        BigDecimal profitLossRatio = avgLossRatio.compareTo(ZERO) == 0 ? ZERO : avgProfitRatio.divide(avgLossRatio, SCALE, ROUND_MODE);

        return new BigDecimal[]{new BigDecimal(buySignalCount), new BigDecimal(profitCount), winRate, profitLossRatio};
    }


}