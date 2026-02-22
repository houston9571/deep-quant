package com.optimus.mysql.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.optimus.ext.CountUtilWriter;
import com.optimus.ext.PercentageWriter;
import com.optimus.mysql.entity.StockKlineDaily;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 龙虎榜页面
 */
@Data
public class DragonDetailStockKline extends StockKlineDaily {


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
    private Long partnerNetBuyAmount;

    @JSONField( serializeUsing = PercentageWriter.class)
    private BigDecimal partnerNetBuyRatio;

    /**
     * 买入金额
     */
    @JSONField(serializeUsing = CountUtilWriter.class)
    private Long partnerBuyAmount;

    /**
     * 买入金额占总成交比例
     */
    @JSONField( serializeUsing = PercentageWriter.class)
    private BigDecimal partnerBuyRatio;

    /**
     * 卖出金额
     */
    @JSONField(  serializeUsing = CountUtilWriter.class)
    private Long partnerSellAmount;

    /**
     * 卖出金额占总成交比例
     */
    @JSONField(  serializeUsing = PercentageWriter.class)
    private BigDecimal partnerSellRatio;

    private List<DragonDetailStockKline> partners;

}
