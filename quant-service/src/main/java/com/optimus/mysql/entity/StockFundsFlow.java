package com.optimus.mysql.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.optimus.constant.Constants;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("stock_funds_flow")
public class StockFundsFlow extends BaseEntity {


    @TableId(value = Constants.ID, type = IdType.AUTO)
    private Integer id;

    private String code;

    private String name;

    @TableField("transaction_date")
    @JSONField(format="yyyy-MM-dd", ordinal = 90)
    private LocalDate transactionDate;

    /**
     * 主力净流入 = 超大单净流入 + 大单净流入
     * 散户净流入 = 中单净流入 + 小单净流入
     */
    private String mainNetInflow;

    /**
     * 超大单净流入	Super Large Net Inflow	> 100万元	机构、顶级大户
     */
    private String superLargeNetInflow;

    /**
     * 大单净流入	Large Net Inflow	20万 - 100万元	大户、部分机构
     */
    private String largeNetInflow;

    /**
     * 中单净流入	Medium Net Inflow	4万 - 20万元	中户
     */
    private String mediumNetInflow;

    /**
     * 小单净流入	Small Net Inflow	< 4万元	散户
     */
    private String smallNetInflow;


}
