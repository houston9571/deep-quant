package com.optimus.mysql.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.optimus.ext.CountLotsWriter;
import com.optimus.ext.CountUtilWriter;
import com.optimus.ext.DivideBy100Reader;
import com.optimus.ext.PercentageWriter;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.optimus.constant.Constants.ID;

/**
 * 股票日线技术指标表（衍生）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("stock_tech_daily")
public class StockTechDaily extends BaseEntity {


    @TableId(value = ID, type = IdType.AUTO)
    private Integer id;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 股票名称
     */
    private String stockName;


    @JSONField(format = "yyyy-MM-dd")
    private LocalDate tradeDate;

    /**
     * 5周期均线
     */
    private BigDecimal ma5;
    /**
     * 10周期均线
     */
    private BigDecimal ma10;
    /**
     * 20周期均线
     */
    private BigDecimal ma20;

    private BigDecimal ma60;
    /**
     * MACD-DIF
     */
    private BigDecimal macdDif;
    /**
     * MACD-DEA
     */
    private BigDecimal macdDea;
    /**
     * MACD-BAR
     */
    private BigDecimal macdBar;
    /**
     * 6周期RSI
     */
    private BigDecimal rsi6;
    /**
     * 14周期RSI
     */
    private BigDecimal rsi14;
    /**
     * KDJ-K值
     */
    private BigDecimal kdjK;
    /**
     * KDJ-D值
     */
    private BigDecimal kdjD;
    /**
     * KDJ-J值
     */
    private BigDecimal kdjJ;
    /**
     * BOLL中轨
     */
    private BigDecimal bollMid;
    /**
     * BOLL上轨
     */
    private BigDecimal bollUpper;
    /**
     * BOLL下轨
     */
    private BigDecimal bollLower;

    /**
     * 布林带状态：1=收口,2=开口,3=正常
     */
    private Integer bollStatus;
    /**
     * 平均真实波幅（ATR）
     */
    private BigDecimal atr;
    /**
     * 今日ATR/昨日ATR（波动率放大倍数
     */
    private BigDecimal atrRatio;
    /**
     * 能量潮（OBV）
     */
    private Long obv;

    /**
     * CCI顺势指标(14日)
     */
    private BigDecimal cci;

    /**
     * 10日威廉指标
     */
    private BigDecimal wr10;
    /**
     * 资金流量指标（MFI）(14日)
     */
    private BigDecimal mfi;

    /**
     * 量能MACD-DIF（EMA12-EMA26）
     */
    private BigDecimal vmacdDif;
    /**
     * 量能MACD-DEA（EMA(DIF,9)）
     */
    private BigDecimal vmacdDea;
    /**
     * 量能BAR（2*(DIF-DEA)）
     */
    private BigDecimal vmacdBar;
    /**
     * OBV20日均线 OBV增强
     */
    private Long obvMa20;

    // 筹码相关（日线简化版，精准版需逐笔数据）
    /**
     * 60日平均成本（成交量加权）
     */
    private BigDecimal avgCost;
    /**
     * 筹码集中度（%，值越小越集中）
     */
    private BigDecimal costConcentration;


    // 顶底背离相关
    /**
     * 背离类型：0=无背离,1=MACD顶背离,2=MACD底背离,3=RSI顶背离,4=RSI底背离,5=KDJ顶背离,6=KDJ底背离,7=CCI顶背离,8=CCI底背离
     */
    private Integer divergenceType;
    /**
     * 背离强度：0~100（值越大背离越明显）
     */
    private BigDecimal divergenceStrength;

    // 多指标共振相关
    /**
     * 共振信号：0=无信号,1=短线买入,2=短线卖出,3=趋势走强,4=趋势走弱
     */
    private Integer resonanceSignal;
    /**
     * 共振评分：0~100（值越高信号越可靠）
     */
    private BigDecimal resonanceScore;
}
