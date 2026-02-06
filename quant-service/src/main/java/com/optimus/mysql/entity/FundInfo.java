package com.optimus.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("fund_info")
public class FundInfo extends BaseEntity {

    @TableId(value = "code", type = IdType.INPUT)
    private String code;

    private String name;

    private String type;

    private String pyjc;

    private  String pyqc;


}
