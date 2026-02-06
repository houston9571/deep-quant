package com.optimus.vo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@ApiModel(value = "SystemIncome", description = "平台营收信息")
public class SystemIncomeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("日期")
    private String reportDate;

    @ApiModelProperty("币种")
    private String currency;

    @ApiModelProperty("平台营收")
    private BigDecimal income;

    @ApiModelProperty("商户充值金额")
    private BigDecimal amountAddMerchant;

    @ApiModelProperty("厂商充值金额")
    private BigDecimal amountAddPlatform;

    @ApiModelProperty("商户结算金额")
    private BigDecimal amountSettleMerchant;

    @ApiModelProperty("厂商结算金额")
    private BigDecimal amountSettlePlatform;
}
