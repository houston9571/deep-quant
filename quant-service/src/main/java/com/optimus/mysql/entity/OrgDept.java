package com.optimus.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("org_dept")
public class OrgDept extends BaseEntity {


    /**
     * 营业厅代码
     */
    @TableId(value = "code", type = IdType.INPUT)
    private String code;

    /**
     * 营业厅名称
     */
    private String name;

    private String codeOld;

    private String remark;

}
