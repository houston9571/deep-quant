package com.optimus.rest;


import com.alibaba.fastjson2.JSONObject;
import com.optimus.base.Result;
import com.optimus.service.StockTradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
public class OpenController extends BaseController {


    private final HttpServletRequest request;

    private final StockTradeService stockQuoteService;







    @GetMapping("getFirstRequest2Data/{code}")
    public Result<JSONObject> getFirstRequest2Data(@PathVariable String code) {
        return stockQuoteService.getFirstRequest2Data(code);
    }


}
