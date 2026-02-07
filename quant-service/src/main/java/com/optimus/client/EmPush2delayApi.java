package com.optimus.client;

import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@BaseRequest(baseURL = "http://push2delay.eastmoney.com", headers = {"User-Agent:Mozilla/5.0...", "Host:push2delay.eastmoney.com", "Referer:http://push2delay.eastmoney.com"})
public interface EmPush2delayApi {



    /**
     * 分页获取股票当日交易行情
     * http://push2delay.eastmoney.com/api/qt/stock/get?secid=1.600986&ut=fa5fd1943c7b386f172d6893dbfba10b&invt=2&fields=f80,f43,f44,f45,f46,f47,f48,f49,f50,f51,f52,f57,f58,f60,f116,f117,f161,f162,f163,f164,f167,f168,f169,f170,f171,f178
     */
    @Get(url = "/api/qt/clist/get?_={ts}&fields={fields}&pn={pn}&pz={pz}&np=1&invt=2&fs=m:0+t:6+f:!2,m:0+t:80+f:!2,m:1+t:2+f:!2,m:1+t:23+f:!2,m:0+t:81+s:262144+f:!2&fid=f12&po=0&dect=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=4363375817489466|0|1|0|web")
    JSONObject getStockTradeList(@Var("fields") String fields, @Var("ts") long ts, @Var("pn") int pn, @Var("pz") int pz);



    /**
     * 获取股票实时交易行情
     * http://push2delay.eastmoney.com/api/qt/stock/get?secid=1.600986&ut=fa5fd1943c7b386f172d6893dbfba10b&invt=2&fields=f80,f43,f44,f45,f46,f47,f48,f49,f50,f51,f52,f57,f58,f60,f116,f117,f161,f162,f163,f164,f167,f168,f169,f170,f171,f178
     */
    @Get(url = "/api/qt/stock/get?secid={marketCode}.{code}&ut=fa5fd1943c7b386f172d6893dbfba10b&invt=2&fields={fields}")
    JSONObject getStockTradeRealtime(@Var("code") String code, @Var("marketCode") String marketCode, @Var("fields") String fields);


    /**
     * 获取实时资金流向 klt=1 K线类型
     * http://push2delay.eastmoney.com/api/qt/stock/fflow/kline/get?klt=101&secid=1.600986&lmt=0&fields1=f1,f2,f3,f7&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65
     * 日期        主力净流入  小单净流入   中单净流入   大单净流入   超大单净流入
     * "klines": ["2026-01-07,-34158323.0,-1068838.0,35227152.0,-2848341.0,-31309982.0"]
     */
    @Get(url = "/api/qt/stock/fflow/kline/get?klt={klt}&secid={marketCode}.{code}&lmt=0&fields1=f1,f2,f3,f7&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65")
    JSONObject getStockFundsFlow(@Var("code") String code, @Var("marketCode") String marketCode, @Var("klt") int klt);
}
