package com.optimus.rest;

import com.optimus.base.PageResult;
import com.optimus.base.Result;
import com.optimus.mysql.entity.BoardDelay;
import com.optimus.mysql.entity.DragonStockDetail;
import com.optimus.mysql.entity.OrgPartner;
import com.optimus.mysql.vo.DragonStockList;
import com.optimus.service.DragonStockDetailService;
import com.optimus.service.DragonStockService;
import com.optimus.service.OrgPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "dragon", produces = APPLICATION_JSON)
public class DragonListRest {

    private final DragonStockService dragonStockService;

    private final DragonStockDetailService dragonStockDetailService;

    /**
     * 按游资席位查询最近龙虎榜，竖型列表，第一行表头日期
     */
    @GetMapping("partner/{tradeDate}")
    public Result<List<List<DragonStockDetail>>> boardList(@PathVariable String tradeDate) {
        return Result.success(dragonStockDetailService.queryDragonStockDetailWithPartner(tradeDate));
    }


    /**
     * 查询当天龙虎榜列表，按游资分类
     */
    @GetMapping("stock/{tradeDate}")
    public Result<List<DragonStockList>> queryDragonStockList(@PathVariable String tradeDate) {
        return Result.success(dragonStockService.queryDragonStockList(tradeDate));
    }



    /**
     * 查询股票龙虎榜详细信息
     */
    @GetMapping("detail/stock/{code}")
    public Result<List<DragonStockList>> queryDragonStockDetail(@PathVariable String code) {
        return Result.success(dragonStockService.queryDragonStockDetail(code));
    }




}
