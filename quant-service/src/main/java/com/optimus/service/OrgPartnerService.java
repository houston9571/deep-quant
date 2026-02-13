package com.optimus.service;

import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.OrgDept;
import com.optimus.mysql.entity.OrgPartner;

import java.util.List;
import java.util.Set;

public interface OrgPartnerService extends MybatisBaseService<OrgPartner> {


    List<OrgPartner> queryOrgPartnerList();

    int deletePartnerDept(String code, String deptCode);

    int addPartnerDept(String code, String deptCode);



}
