package com.optimus.rest;

import com.optimus.base.Result;
import com.optimus.mysql.entity.OrgDept;
import com.optimus.mysql.entity.OrgPartner;
import com.optimus.service.OrgDeptService;
import com.optimus.service.OrgPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "org", produces = APPLICATION_JSON)
public class OrgRest {

    private final OrgPartnerService orgPartnerService;

    private final OrgDeptService orgDeptService;



    /**
     * 游资列表
     */
    @GetMapping("partner")
    public Result<List<OrgPartner>> queryDragonPartnerList() {
        return orgPartnerService.queryOrgPartnerList();
    }

    /**
     * 删除游资的席位
     */
    @DeleteMapping("partner/dept/{partnerCode}/{deptCode}")
    public Result<Void> deletePartnerDept(@PathVariable String partnerCode, @PathVariable String deptCode) {
        return orgPartnerService.deletePartnerDept(partnerCode, deptCode);
    }


    /**
     * 游资未匹配的营业部列表
     */
    @GetMapping("partner/dept/nomatch/{partnerCode}")
    public Result<List<OrgDept>> queryNomatchPartnerDeptList(@PathVariable String partnerCode) {
        return orgDeptService.queryNomatchPartnerDeptList(partnerCode);
    }

    /**
     * 增加游资的席位
     */
    @PutMapping("partner/dept/{partnerCode}/{deptCode}")
    public Result<Void> addPartnerDept(@PathVariable String partnerCode, @PathVariable String deptCode) {
        return orgPartnerService.addPartnerDept(partnerCode, deptCode);
    }
}
