package com.optimus.rest;

import com.optimus.base.PageResult;
import com.optimus.base.Result;
import com.optimus.mysql.entity.BoardDelay;
import com.optimus.service.BoardDelayService;
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
@RequestMapping(value = "board", produces = APPLICATION_JSON)
public class BoardRest {

    private final BoardDelayService boardDelayService;



    /**
     * 查询每日排名前10的板块，竖型列表，第一行是表头信息
     */
    @GetMapping("{days}/{top}")
    public Result<List<BoardDelay>> boardList(@PathVariable int days, @PathVariable int top) {
        List<List<BoardDelay>> grid = boardDelayService.queryBoardTradeList(days, top);
        return PageResult.success(grid);
    }


}
