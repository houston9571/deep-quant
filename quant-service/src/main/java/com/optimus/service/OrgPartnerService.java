package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.OrgDept;
import com.optimus.mysql.entity.OrgPartner;

import java.util.List;
import java.util.Set;

public interface OrgPartnerService extends MybatisBaseService<OrgPartner> {


    Result<List<OrgPartner>> queryOrgPartnerList();

    Result<Void> deletePartnerDept(String code, String deptCode);

    Result<Void> addPartnerDept(String code, String deptCode);



}
