package com.optimus.rest;

import com.optimus.base.Result;
import com.optimus.mysql.entity.OrgDept;
import com.optimus.mysql.entity.OrgPartner;
import com.optimus.mysql.vo.DragonStockList;
import com.optimus.service.DragonStockService;
import com.optimus.service.OrgDeptService;
import com.optimus.service.OrgPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;
import static com.optimus.enums.ErrorCode.DATA_NOT_EXIST;

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
    @DeleteMapping("partner/dept/{code}/{deptCode}")
    public Result<Void> deletePartnerDept(@PathVariable String code, @PathVariable String deptCode) {
        return orgPartnerService.deletePartnerDept(code, deptCode);
    }


    /**
     * 游资未匹配的营业部列表
     */
    @GetMapping("partner/dept/nomatch/{code}")
    public Result<List<OrgDept>> queryNomatchPartnerDeptList(@PathVariable String code) {
        return orgDeptService.queryNomatchPartnerDeptList(code);
    }

    /**
     * 增加游资的席位
     */
    @PutMapping("partner/dept/{code}/{deptCode}")
    public Result<Void> addPartnerDept(@PathVariable String code, @PathVariable String deptCode) {
        return orgPartnerService.addPartnerDept(code, deptCode);
    }
}
