package com.optimus.client;

import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Var;
import org.springframework.stereotype.Component;

import static cn.hutool.core.text.StrPool.COLON;
import static com.dtflys.forest.http.ForestHeader.HOST;

@Component
@BaseRequest(headers = {"User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36"})
public interface EastMoneyDragonApi {

    String PUSH2DELAY_URL = "http://push2delay.eastmoney.com";
    String PUSH2DELAY_HOST = HOST + COLON +"push2delay.eastmoney.com";

    String DATACENTER_URL = "https://datacenter.eastmoney.com";
    String DATACENTER_HOST = HOST + COLON +"datacenter.eastmoney.com";

    String DATACENTER_WEB_URL = "https://datacenter-web.eastmoney.com";
    String DATACENTER_WEB_HOST = HOST + COLON +"datacenter-web.eastmoney.com";




    /**
     * 个股龙虎榜列表
     * 查询多日数据  filter=(TRADE_DATE<='2026-02-06')(TRADE_DATE>='2026-02-03')
     * 排序  sortColumns=SECURITY_CODE,TRADE_DATE&sortTypes=1,-1
     * https://datacenter-web.eastmoney.com/api/data/v1/get?reportName=RPT_DAILYBILLBOARD_DETAILSNEW&filter=(TRADE_DATE='2026-02-03')&sortColumns=SECURITY_CODE,TRADE_DATE&sortTypes=1,-1&pageSize=100&pageNumber=1&columns=TRADE_DATE,SECURITY_CODE,SECUCODE,SECURITY_NAME_ABBR,EXPLAIN,CLOSE_PRICE,CHANGE_RATE,BILLBOARD_NET_AMT,BILLBOARD_BUY_AMT,BILLBOARD_SELL_AMT,BILLBOARD_DEAL_AMT,ACCUM_AMOUNT,DEAL_NET_RATIO,DEAL_AMOUNT_RATIO,TURNOVERRATE,FREE_MARKET_CAP,EXPLANATION,D1_CLOSE_ADJCHRATE,D2_CLOSE_ADJCHRATE,D5_CLOSE_ADJCHRATE,D10_CLOSE_ADJCHRATE,SECURITY_TYPE_CODE&source=WEB&client=WEB
     */
    @Get(url = DATACENTER_WEB_URL + "/api/data/v1/get?reportName=RPT_DAILYBILLBOARD_DETAILSNEW&filter=(TRADE_DATE='{tradeDate}')&pageNumber={pageNum}&pageSize={pageSize}&source=WEB&client=WEB" +
            "&columns=TRADE_DATE,SECURITY_CODE,SECUCODE,SECURITY_NAME_ABBR,EXPLAIN,CLOSE_PRICE,CHANGE_RATE,BILLBOARD_NET_AMT,BILLBOARD_BUY_AMT,BILLBOARD_SELL_AMT,BILLBOARD_DEAL_AMT,ACCUM_AMOUNT,DEAL_NET_RATIO,DEAL_AMOUNT_RATIO,TURNOVERRATE,FREE_MARKET_CAP,EXPLANATION,D1_CLOSE_ADJCHRATE,D2_CLOSE_ADJCHRATE,D5_CLOSE_ADJCHRATE,D10_CLOSE_ADJCHRATE,SECURITY_TYPE_CODE"
    )
    JSONObject getStockDragonList(@Var("tradeDate") String tradeDate, @Var("pageNum") int pageNum, @Var("pageSize") int pageSize);

    /**
     * https://datacenter-web.eastmoney.com/api/data/v1/get?reportName=RPT_BILLBOARD_DAILYDETAILSSELL&filter=(TRADE_DATE='2026-02-03')(SECURITY_CODE="000547")&sortColumns=SELL&sortTypes=-1&pageNumber=1&pageSize=50&columns=ALL&source=WEB&client=WEB&_=1770486329447
     */
    @Get(url = DATACENTER_WEB_URL + "/api/data/v1/get?reportName=RPT_BILLBOARD_DAILYDETAILSSELL&filter=(TRADE_DATE='{tradeDate}')(SECURITY_CODE=\"{code}\")&sortColumns=SELL&sortTypes=-1&pageNumber=1&pageSize=50&columns=ALL&source=WEB&client=WEB&_=1770486329447")
    JSONObject getStockDragonListSell(@Var("tradeDate") String tradeDate, @Var("code") String code);

    /**
     * https://datacenter-web.eastmoney.com/api/data/v1/get?reportName=RPT_BILLBOARD_DAILYDETAILSBUY&filter=(TRADE_DATE='2026-02-03')(SECURITY_CODE="000547")&sortTypes=-1&sortColumns=BUY&pageNumber=1&pageSize=50&columns=ALL&source=WEB&client=WEB&_=1770542839476
     */
    @Get(url = DATACENTER_WEB_URL + "/api/data/v1/get?reportName=RPT_BILLBOARD_DAILYDETAILSBUY&filter=(TRADE_DATE='{tradeDate}')(SECURITY_CODE=\"{code}\")&sortTypes=-1&sortColumns=BUY&pageNumber=1&pageSize=50&columns=ALL&source=WEB&client=WEB&_=1770542839476")
    JSONObject getStockDragonListBuy(@Var("tradeDate") String tradeDate, @Var("code") String code);


    /**
     * 实时访问主要指数 fs指定指数代码
     * https://push2.eastmoney.com/api/qt/clist/get?_=1770608220902&fs=i:1.000001,i:0.399001,i:0.399006,i:1.000300,i:100.HSI,i:100.UDI,i:100.DJIA,i:100.SPX,i:100.NDX,i:100.MCX,i:100.FCHI,i:100.GDAXI&fields=f2,f4,f3,f12,f13,f14&np=1&invt=2&pn=1&pz=100&po=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=4363375817489466|0|1|0|web
     */
    JSONObject getStockMarketIndex(@Var("tradeDate") String tradeDate, @Var("code") String code);


}
