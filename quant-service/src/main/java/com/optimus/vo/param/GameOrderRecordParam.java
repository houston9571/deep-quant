package com.optimus.vo.param;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "GameOrderRecordParam", description = "订单修改记录参数信息")
public class GameOrderRecordParam {

    @ApiModelProperty("多个Id")
    private List<Long> orderIds;

    @ApiModelProperty("状态：-1 异常 0新建 1成功 2失败 3处理中")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("walletType: 1单一钱包 2转账钱包")
    private Integer walletType;
}