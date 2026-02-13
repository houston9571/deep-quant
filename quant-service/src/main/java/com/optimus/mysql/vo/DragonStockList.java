package com.optimus.mysql.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.optimus.ext.CountUtilWriter;
import com.optimus.ext.PercentageWriter;
import com.optimus.ext.StringToDateReader;
import com.optimus.mysql.entity.BaseEntity;
import com.optimus.mysql.entity.StockDelay;
import lombok.*;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.optimus.constant.Constants.HUNDRED;
import static com.optimus.constant.Constants.ID;

/**
 * 龙虎榜页面
 */
@Data
public class DragonStockList extends StockDelay {


    /**************************
     ******** 龙虎榜 *********
     **************************/
    /**
     * 龙虎榜 净买额
     */
    @JSONField( serializeUsing = CountUtilWriter.class)
    private Long netBuyAmount;

    /**
     * 龙虎榜 净买额/市场总成交额
     */
    @JSONField( serializeUsing = PercentageWriter.class)
    private BigDecimal netBuyAmountRatio;

    /**
     * 龙虎榜 买入额
     */
    @JSONField( serializeUsing = CountUtilWriter.class)
    private Long buyAmount;

    @JSONField( serializeUsing = PercentageWriter.class)
    private BigDecimal buyAmountRatio;

    /**
     * 龙虎榜 卖出额
     */
    @JSONField( serializeUsing = CountUtilWriter.class)
    private Long sellAmount;

    @JSONField( serializeUsing = PercentageWriter.class)
    private BigDecimal sellAmountRatio;

    /**
     * 龙虎榜 成交额
     */
    @JSONField( serializeUsing = CountUtilWriter.class)
    private Long dealAmount;


    /**
     * 龙虎榜 成交额/市场总成交额
     */
    @JSONField(  serializeUsing = PercentageWriter.class)
    private BigDecimal dealAmountRatio;

    /**
     * 解读
     */
    private String explains;

    /**
     * 上榜原因
     */
    private String explanation;


    /**************************
     ******** 机构买入 *********
     **************************/

    private String partnerCode;

    private String partnerName;

    private String deptCode;

    private String deptName;

    /**
     * 净买入
     */
    @JSONField( serializeUsing = CountUtilWriter.class)
    private Long deptNetBuyAmount;

    @JSONField( serializeUsing = PercentageWriter.class)
    private BigDecimal deptTotalNetBuyRatio;
    /**
     * 买入金额
     */
    @JSONField(serializeUsing = CountUtilWriter.class)
    private Long deptBuyAmount;

    /**
     * 买入金额占总成交比例
     */
    @JSONField( serializeUsing = PercentageWriter.class)
    private BigDecimal deptTotalBuyRatio;

    /**
     * 卖出金额
     */
    @JSONField(  serializeUsing = CountUtilWriter.class)
    private Long deptSellAmount;

    /**
     * 卖出金额占总成交比例
     */
    @JSONField(  serializeUsing = PercentageWriter.class)
    private BigDecimal deptTotalSellRatio;

}
