package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.OrgDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface OrgDeptService extends MybatisBaseService<OrgDept> {


    int saveBatch(Set<OrgDept> orgDeptSet);

    Result<List<OrgDept>> queryNomatchPartnerDeptList(@Param("code") String code);


}
