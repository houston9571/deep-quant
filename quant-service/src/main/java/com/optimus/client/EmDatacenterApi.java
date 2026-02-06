package com.optimus.client;

import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.annotation.*;
import org.springframework.stereotype.Component;


@Component
@BaseRequest(baseURL = "https://datacenter.eastmoney.com", headers = {"User-Agent:Mozilla/5.0...", "Host:datacenter.eastmoney.com", "Referer:http://datacenter.eastmoney.com"})
public interface EmDatacenterApi {

    /**
     * 所属概念
     * https://datacenter.eastmoney.com/securities/api/data/get?type=RPT_F10_CORETHEME_BOARDTYPE&sty=ALL&filter=(SECUCODE="600986.SH")&p=1&ps=&sr=1&st=BOARD_RANK&source=HSF10&client=PC&v=04715492084049595
     */
    @Get(url = "/securities/api/data/get?type=RPT_F10_CORETHEME_BOARDTYPE&sty=ALL&filter=(SECUCODE=\"{code}.{market}\")&p=1&ps=&sr=1&st=BOARD_RANK&source=HSF10&client=PC&v=04715492084049595")
    JSONObject getBoards(@Var("code") String code, @Var("market") String market);

}
