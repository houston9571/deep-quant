package com.optimus.rest;

import com.optimus.base.Result;
import com.optimus.mysql.entity.OrgPartner;
import com.optimus.mysql.vo.DragonStockList;
import com.optimus.service.DragonStockService;
import com.optimus.service.OrgPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "dragon", produces = APPLICATION_JSON)
public class DragonListRest {

    private final DragonStockService dragonStockService;



    /**
     * 查询当天龙虎榜列表，按游资分类
     */
    @GetMapping("partner/{tradeDate}")
    public Result<List<DragonStockList>> queryDragonPartnerList(@PathVariable String tradeDate) {
        return Result.success(dragonStockService.queryDragonPartnerList(tradeDate));
    }



}
