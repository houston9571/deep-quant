package com.optimus.service.impl;

import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.OrgPartner;
import com.optimus.mysql.mapper.OrgPartnerMapper;
import com.optimus.service.OrgPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrgPartnerServiceImpl extends MybatisBaseServiceImpl<OrgPartnerMapper, OrgPartner> implements OrgPartnerService {

    private final OrgPartnerMapper orgPartnerMapper;

    public List<OrgPartner> queryOrgPartnerList() {
        return orgPartnerMapper.queryOrgPartnerList();
    }

    public int deletePartnerDept(String code,  String deptCode) {
        return orgPartnerMapper.deletePartnerDept(code,deptCode);
    }

    public int addPartnerDept(String code,  String deptCode) {
        return orgPartnerMapper.addPartnerDept(code,deptCode);
    }




}
