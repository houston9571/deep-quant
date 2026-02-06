package com.optimus.vo.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *  下注流水
 */
@Data
public class GameBetDTO  implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("注单号")
    protected String id;

    @ApiModelProperty("主注单号")
    private String mainBillNo;

    @ApiModelProperty("玩家Id")
    private Integer playerId;

    @ApiModelProperty("用户Id")
    private String uid;

    @ApiModelProperty("游戏厂商")
    private String platform;

    @ApiModelProperty("钱包类型")
    private Integer walletType;

    @ApiModelProperty("游戏分类: FISH, CHESS, E_GAME, LIVE, SPORT, SPORT_VIRTUAL, E_SPORT, LOTTERY, -1")
    private String category;

    @ApiModelProperty("游戏分类Code")
    private String typeCode;

    @ApiModelProperty("币种")
    private String currency;

    @ApiModelProperty("游戏编码")
    private String gameCode;

    @ApiModelProperty("游戏名称")
    private String gameName;

    @ApiModelProperty("游戏局号")
    private String tableId;

    @ApiModelProperty("订单状态: 1 结算中  3 已退款  5 已结算")
    private Integer orderStatus;

    @ApiModelProperty("结算时玩家余额")
    private BigDecimal balance;

    @ApiModelProperty("投注金额")
    private BigDecimal bet;

    @ApiModelProperty("有效投注额")
    private BigDecimal validBet;

    @ApiModelProperty("玩家实际输赢")
    private BigDecimal win;

    @ApiModelProperty("结算时间")
    private LocalDateTime settlementTime;

    @JSONField(name = "betTime")
    @ApiModelProperty("投注时间")
    private LocalDateTime createTime;


}
