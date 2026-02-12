package com.optimus.rest;

import com.optimus.base.PageResult;
import com.optimus.base.Result;
import com.optimus.mysql.entity.BoardDelay;
import com.optimus.mysql.entity.StockDragon;
import com.optimus.mysql.entity.StockDragonDetail;
import com.optimus.service.StockDragonDetailService;
import com.optimus.service.StockDragonService;
import com.optimus.thread.Threads;
import com.optimus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
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

    private final StockDragonService stockDragonService;

    private final StockDragonDetailService stockDragonDetailService;


    /**
     * 查询当天龙虎榜列表，按游资分类
     */
    @GetMapping("list")
    public Result<List<StockDragonDetail>> queryDragonList() {
        return Result.success(stockDragonDetailService.queryDragonDetail());
    }
}
