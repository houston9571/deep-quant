package com.optimus.mysql.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.optimus.ext.CountLotsWriter;
import com.optimus.ext.CountUtilWriter;
import com.optimus.ext.DivideBy100Reader;
import com.optimus.ext.PercentageWriter;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.optimus.constant.Constants.ID;

/**
 * 股票行情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("stock_trade_realtime")
public class StockTradeRealTime extends BaseEntity {


    @TableId(value = ID, type = IdType.AUTO)
    private Integer id;

    /**
     * 股票代码
     */
    @JSONField(ordinal = 2, alternateNames = "f57")
    private String code;

    /**
     * 股票名称
     */
    @JSONField(ordinal = 3, alternateNames = "f58")
    private String name;


    @TableField("transaction_date")
    @JSONField(format="yyyy-MM-dd", ordinal = 90)
    private LocalDate transactionDate;

    /**
     * f116 总市值/100 %
     */
    @JSONField(alternateNames = "f116",   serializeUsing = CountUtilWriter.class)
    private Integer marketValue;

    /**
     * f117 流通市值/100 %
     */
    @JSONField(alternateNames = "f117",   serializeUsing = CountUtilWriter.class)
    private Integer floatMarketValue;

    /**
     * f162 市盈(动)/100 % 未来12个月预期盈利 前瞻性
     */
    @JSONField(alternateNames = "f162", deserializeUsing = DivideBy100Reader.class )
    private BigDecimal pef;

    /**
     * f163 市盈(静)/100 % 上一个完整财年盈利 最"静态"
     */
    @JSONField(alternateNames = "f163", deserializeUsing = DivideBy100Reader.class )
    private BigDecimal pet;

    /**
     * f164 市盈(TTM)/100 % 过去12个月滚动盈利	更及时
     */
    @JSONField(alternateNames = "f164", deserializeUsing = DivideBy100Reader.class )
    private BigDecimal pettm;

    /**
     * f167 市净PB/100 %
     */
    @JSONField(alternateNames = "f167", deserializeUsing = DivideBy100Reader.class )
    private BigDecimal pb;

    /**
     * f168 换手率/100 %
     */
    @JSONField(alternateNames = "f168", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal turnoverRate;

    /**
     * f169 涨跌额/100
     */
    @JSONField(alternateNames = "f169", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal changeAmount;

    /**
     * f170 涨跌幅/100 %
     */
    @JSONField(alternateNames = "f170", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal changePercent;

    /**
     * f171 振幅/100 %
     */
    @JSONField(alternateNames = "f171", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal changeRange;

    /**
     * f43 最新价/100
     */
    @JSONField(alternateNames = "f43", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal latestPrice;

    /**
     * f44 最高价/100
     */
    @JSONField(alternateNames = "f44", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal highestPrice;

    /**
     * f45 最低价/100
     */
    @JSONField(alternateNames = "f45", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal lowestPrice;

    /**
     * f46 今开/100
     */
    @JSONField(alternateNames = "f46", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal openPrice;

    /**
     * f60 昨收/100
     */
    @JSONField(alternateNames = "f60", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal closePrice;

    /**
     * f47 成交量（手）
     */
    @JSONField(alternateNames = "f47", serializeUsing = CountLotsWriter.class)
    private Integer volume;

    /**
     * f48 成交额
     */
    @JSONField(alternateNames = "f48", serializeUsing = CountUtilWriter.class)
    private Integer turnover;

    /**
     * f49 外盘（主动性买盘,买方主动"扫货"，按卖方报价成交）
     */
    @JSONField(alternateNames = "f49", serializeUsing = CountUtilWriter.class)
    private Integer buyVolume;

    /**
     * f161 内盘（主动性卖盘,卖方主动"砸盘"，按买方报价成交）
     */
    @JSONField(alternateNames = "f161", serializeUsing = CountUtilWriter.class)
    private Integer sellVolume;

    /**
     * f50 量比/100
     */
    @JSONField(alternateNames = "f50", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal volumeRatio;

    /**
     * f51 涨停价/100
     */
    @JSONField(alternateNames = "f51", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal limitUp;

    /**
     * f52 跌停价/100
     */
    @JSONField(alternateNames = "f52", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal limitDown;




}
