package com.optimus.mysql.entity;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("org_partner")
public class OrgPartner extends BaseEntity {


    @TableId(value = "code", type = IdType.INPUT)
    private String code;

    private String name;


    /**
     * 一年上榜次数
     */
    private Integer boardCount;

    /**
     * 一年成交额
     */
    private Integer tradeAmount;

    /**
     * 最优持仓期
     */
    private Integer holdingPeriod;

    /**
     * 上涨概率
     */
    private BigDecimal rising;

    private String style;

    private String level;

    private String fundSize;

    private String remark;

    @TableField(exist = false)
    private String deptList;

    @TableField(exist = false)
    private JSONArray deptArray;

    public JSONArray getDeptArray(){
        return JSONArray.parseArray(deptList);
    }
}
