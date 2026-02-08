package com.optimus.rest;

import com.optimus.base.Result;
import com.optimus.mysql.entity.StockDragon;
import com.optimus.service.StockDragonDetailService;
import com.optimus.service.StockDragonService;
import com.optimus.thread.Threads;
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
     * 个股龙虎榜列表
     */
    @GetMapping("stock/{date}")
    public Result<Integer> getStockDragonList(@PathVariable String date) {
        Result<List<StockDragon>> result = stockDragonService.getStockDragonList(date);
        if (result.hasData()) {
            List<StockDragon> list = result.getData();
            Threads.asyncExecute(() -> {
                int count = 0;
                for (StockDragon d : list) {
                    count+=stockDragonDetailService.getStockDragonDetail(d.getTradeDate(), d.getCode(), d.getName());
                }
                log.info(">>>>>getStockDragonDetail: {} total_save_size:{}", date, count);
            });
        }
        return Result.success(result.getData().size());
    }


}
