package com.optimus.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
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
    private String stockName;

    private LocalDate tradeDate;
    private LocalTime tradeTime;
    private BigDecimal price;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal open;
    private BigDecimal close;

    private BigDecimal ema3;
    private BigDecimal ema5;
    private BigDecimal ema10;
    private Short emaGolden;

    private BigDecimal macdDif;
    private BigDecimal macdDea;
    private BigDecimal macdBar;
    private Short macdGolden;

    private BigDecimal rsi3;
    private BigDecimal rsi6;
    private BigDecimal rsi9;

    private BigDecimal kdjK;
    private BigDecimal kdjD;
    private BigDecimal kdjJ;
    private Short kdjGolden;

    private BigDecimal wr6;

    private BigDecimal bollMid;
    private BigDecimal bollUpper;
    private BigDecimal bollLower;
    private BigDecimal bollBandWidthPct;
    private Integer bollExpandStatus;

    private BigDecimal vmacdDif;
    private BigDecimal vmacdDea;
    private BigDecimal vmacdBar;
    private Short vmacdGolden;

    private Long obv;
    private Long obvMa5;
    private Short obvGolden;

    private Integer resonanceSignal;
    private BigDecimal resonanceScore;


}
