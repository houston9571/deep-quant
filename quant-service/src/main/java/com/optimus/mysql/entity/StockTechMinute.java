package com.optimus.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.optimus.constant.Constants.ID;

/**
 * 股票分钟线行情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("stock_tech_minute")
public class StockTechMinute extends BaseEntity {

    @TableId(value = ID, type = IdType.AUTO)
    private Integer id;

    private String stockCode;
    private LocalDate tradeDate;
    private LocalTime tradeTime;

    private BigDecimal ma3;
    private BigDecimal ma5;
    private BigDecimal ma10;

    private BigDecimal macdDif;
    private BigDecimal macdDea;
    private BigDecimal macdBar;

    private BigDecimal rsi3;
    private BigDecimal rsi9;

    private BigDecimal kdjK;
    private BigDecimal kdjD;
    private BigDecimal kdjJ;

    private BigDecimal wr6;

    private BigDecimal bollMid;
    private BigDecimal bollUpper;
    private BigDecimal bollLower;

    private Integer bollStatus;

    private BigDecimal vmacdDif;
    private BigDecimal vmacdDea;
    private Long obv;
    private Long obvMa5;

    private Integer resonanceSignal;
    private BigDecimal resonanceScore;


}
