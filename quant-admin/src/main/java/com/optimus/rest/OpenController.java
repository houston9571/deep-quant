package com.optimus.rest;


import com.alibaba.fastjson2.JSONObject;
import com.optimus.base.Result;
import com.optimus.service.StockKlineDailyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
public class OpenController extends BaseController {


    private final HttpServletRequest request;

    private final StockKlineDailyService stockKlineDailyService;


    @GetMapping("getFirstRequest2Data/{code}")
    public Result<JSONObject> getFirstRequest2Data(@PathVariable String code) {
        return Result.success();
    }


}
