package com.optimus.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.optimus.mysql.entity.OrgDept;
import com.optimus.mysql.entity.OrgPartner;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface OrgPartnerMapper extends BaseMapper<OrgPartner> {

    @Select("SELECT a.*, JSON_ARRAYAGG(JSON_OBJECT('deptCode', c.code, 'deptName', c.name)) AS deptList " +
            "FROM org_partner a  LEFT JOIN org_partner_dept b ON a.`code`=b.partner_code LEFT JOIN org_dept c ON b.dept_code=c.`code` " +
            "GROUP BY a.code")
    List<OrgPartner> queryOrgPartnerList();


    @Delete("DELETE FROM org_partner_dept WHERE partner_code=#{code} AND dept_code=#{deptCode} ")
    int deletePartnerDept(@Param("code") String code, @Param("deptCode") String deptCode);

    @Select("SELECT COUNT(1) FROM org_partner_dept WHERE partner_code= #{code} AND dept_code= #{deptCode} ")
    int countPartnerDept(@Param("code") String code, @Param("deptCode") String deptCode);

    @Insert("INSERT INTO org_partner_dept(partner_code, dept_code) VALUES (#{code}, #{deptCode} )")
    int addPartnerDept(@Param("code") String code, @Param("deptCode") String deptCode);
}
