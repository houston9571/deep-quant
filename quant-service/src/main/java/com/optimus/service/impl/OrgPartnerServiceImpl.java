package com.optimus.service.impl;

import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.OrgPartner;
import com.optimus.mysql.mapper.OrgPartnerMapper;
import com.optimus.service.OrgPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.optimus.enums.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrgPartnerServiceImpl extends MybatisBaseServiceImpl<OrgPartnerMapper, OrgPartner> implements OrgPartnerService {

    private final OrgPartnerMapper orgPartnerMapper;

    public Result<List<OrgPartner>> queryOrgPartnerList() {
        List<OrgPartner> list = orgPartnerMapper.queryOrgPartnerList();



        return Result.success(orgPartnerMapper.queryOrgPartnerList());
    }

    public Result<Void> deletePartnerDept(String code, String deptCode) {
        return Result.isSuccess(orgPartnerMapper.deletePartnerDept(code, deptCode), DATA_NOT_EXIST);
    }

    public Result<Void> addPartnerDept(String code, String deptCode) {
        if (orgPartnerMapper.countPartnerDept(code, deptCode) == 0) {
            return Result.isSuccess(orgPartnerMapper.addPartnerDept(code, deptCode), DB_ERROR);
        }
        return Result.fail(DATE_DUPLICATE);
    }


}
