package com.optimus.vo.dto;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description 下注流水
 */
@Data
public class GameBetSportDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty("注单号")
    protected String id;

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

    @ApiModelProperty("币种")
    private String currency;

    @ApiModelProperty("投注金额")
    private BigDecimal bet;

    @ApiModelProperty("有效投注额")
    private BigDecimal validBet;

    @ApiModelProperty("玩家实际输赢")
    private BigDecimal win;

    @ApiModelProperty("提前结算 0否 1是")
    private Integer beforehand;

    @ApiModelProperty("订单状态")
    private Integer orderStatus;

    @ApiModelProperty("订单状态名称")
    private String orderStatusName;

    @ApiModelProperty("关次类型")
    private String seriesType;

    @ApiModelProperty("投注类型")
    private String betType;

    @ApiModelProperty("数据版本号")
    private Integer version;

    @JSONField(serialize = false)
    @ApiModelProperty("赛事ID")
    private String matchId;

    @ApiModelProperty("赛事信息")
    private JSONArray matchJson;

    @ApiModelProperty("结算时间")
    private LocalDateTime settlementTime;

    @JSONField(name = "betTime")
    @ApiModelProperty("投注时间")
    private LocalDateTime createTime;


}
