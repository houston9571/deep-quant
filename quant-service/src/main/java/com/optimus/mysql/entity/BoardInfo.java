package com.optimus.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 板块基本资料
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("board_info")
public class BoardInfo extends BaseEntity {

    @TableId(value = "code", type = IdType.INPUT)
    private String code;

    @TableField("name")
    private String name;

    private String type;

    private String level;



}
