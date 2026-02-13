package com.optimus.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.springframework.data.annotation.Transient;

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

    private String style;

    private String level;

    private String fundSize;

    private String remark;

    @Transient
    private String deptCode;

    @Transient
    private String deptName;
}
