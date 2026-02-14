package com.optimus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.OrgDept;
import com.optimus.mysql.mapper.OrgDeptMapper;
import com.optimus.service.OrgDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrgDeptServiceImpl extends MybatisBaseServiceImpl<OrgDeptMapper, OrgDept> implements OrgDeptService {

    private final OrgDeptMapper orgDeptMapper;

    /**
     *
     */
    public int saveBatch(Set<OrgDept> orgDeptSet) {
        int count = 0;
        for (OrgDept o : orgDeptSet) {
            if (!exist(new LambdaQueryWrapper<OrgDept>().eq(OrgDept::getCode, o.getCode()))) {
                save(o);
                count++;
            }
        }
        log.info("saveBatch OrgSalesDept total:{} save:{}", orgDeptSet.size(), count);
        return count;
    }

    public List<OrgDept> queryNomatchPartnerDeptList(String code) {
        return orgDeptMapper.queryNomatchPartnerDeptList(code);
    }

}
