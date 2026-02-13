package com.optimus.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.optimus.constant.Constants;
import lombok.*;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("board_stock")
public class BoardStock extends BaseEntity {

    @TableId(value = Constants.ID, type = IdType.AUTO)
    private Integer id;

    private String code;

    private String bcode;

}
