package com.optimus.client;

import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Var;
import org.springframework.stereotype.Component;

@Component
@BaseRequest(baseURL = "http://push2.eastmoney.com", headers = {"User-Agent:Mozilla/5.0...", "Host:push2.eastmoney.com", "Referer:http://push2.eastmoney.com"})
public interface EmPush2Api {


}
