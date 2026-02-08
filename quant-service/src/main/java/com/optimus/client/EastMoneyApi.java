package com.optimus.client;

import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@BaseRequest(headers = {"User-Agent:Mozilla/5.0..."})
public interface EastMoneyApi {

    String PUSH2DELAY_URL = "http://push2delay.eastmoney.com";
    String PUSH2DELAY_HOST = "push2delay.eastmoney.com";

    String DATACENTER_URL = "https://datacenter.eastmoney.com";
    String DATACENTER_HOST = "datacenter.eastmoney.com";

    String DATACENTER_WEB_URL = "https://datacenter-web.eastmoney.com";
    String DATACENTER_WEB_HOST = "datacenter-web.eastmoney.com";



    /**
     * 分页获取股票当日交易行情
     *
     */
    @Get(url = PUSH2DELAY_URL + "/api/qt/clist/get?_={ts}&fields={fields}&pn={pageNum}&pz={pageSize}" +
            "&np=1&invt=2&fid=f12&po=0&dect=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=4363375817489466|0|1|0|web" +
            "&fs=m:0+t:6+f:!2,m:0+t:80+f:!2,m:1+t:2+f:!2,m:1+t:23+f:!2,m:0+t:81+s:262144+f:!2",
            headers = {"Host:" + PUSH2DELAY_HOST, "Referer:" + PUSH2DELAY_HOST}
    )
    JSONObject getStockTradeList(@Var("fields") String fields, @Var("ts") long ts, @Var("pageNum") int pageNum, @Var("pageSize") int pageSize);

    /**
     * 获取股票实时交易行情
     * http://push2delay.eastmoney.com/api/qt/stock/get?secid=1.600986&ut=fa5fd1943c7b386f172d6893dbfba10b&invt=2&fields=f80,f43,f44,f45,f46,f47,f48,f49,f50,f51,f52,f57,f58,f60,f116,f117,f161,f162,f163,f164,f167,f168,f169,f170,f171,f178
     */
    @Get(url = PUSH2DELAY_URL + "/api/qt/stock/get?secid={marketCode}.{code}&ut=fa5fd1943c7b386f172d6893dbfba10b&invt=2&fields={fields}",
            headers = {"Host:" + PUSH2DELAY_HOST, "Referer:" + PUSH2DELAY_HOST}
    )
    JSONObject getStockTradeRealtime(@Var("code") String code, @Var("marketCode") String marketCode, @Var("fields") String fields);

    /**
     * 获取实时资金流向 klt=1 K线类型
     * http://push2delay.eastmoney.com/api/qt/stock/fflow/kline/get?klt=1&secid=1.600986&lmt=0&fields1=f1,f2,f3,f7&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65
     */
    @Get(url = PUSH2DELAY_URL + "/api/qt/stock/fflow/kline/get?klt={klt}&secid={marketCode}.{code}&lmt=0" +
            "&fields1=f1,f2,f3,f7&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65",
            headers = {"Host:" + PUSH2DELAY_HOST, "Referer:" + PUSH2DELAY_HOST}
    )
    JSONObject getStockFundsFlow(@Var("code") String code, @Var("marketCode") String marketCode, @Var("klt") int klt);

    /**
     * 所属概念
     * https://datacenter.eastmoney.com/securities/api/data/get?type=RPT_F10_CORETHEME_BOARDTYPE&sty=ALL&filter=(SECUCODE="600986.SH")&p=1&ps=&sr=1&st=BOARD_RANK&source=HSF10&client=PC&v=04715492084049595
     */
    @Get(url = DATACENTER_URL + "/securities/api/data/get?type=RPT_F10_CORETHEME_BOARDTYPE&sty=ALL" +
            "&filter=(SECUCODE=\"{code}.{market}\")&p=1&ps=&sr=1&st=BOARD_RANK&source=HSF10&client=PC&v=04715492084049595",
            headers = {"Host:" + DATACENTER_HOST, "Referer:" + DATACENTER_HOST}
    )
    JSONObject getBoards(@Var("code") String code, @Var("market") String market);

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
     *
     * https://datacenter-web.eastmoney.com/api/data/v1/get?reportName=RPT_BILLBOARD_DAILYDETAILSSELL&filter=(TRADE_DATE='2026-02-03')(SECURITY_CODE="000547")&sortColumns=SELL&sortTypes=-1&pageNumber=1&pageSize=50&columns=ALL&source=WEB&client=WEB&_=1770486329447
     */
    @Get(url = DATACENTER_WEB_URL + "/api/data/v1/get?reportName=RPT_BILLBOARD_DAILYDETAILSSELL&filter=(TRADE_DATE='{tradeDate}')(SECURITY_CODE=\"{code}\")&sortColumns=SELL&sortTypes=-1&pageNumber=1&pageSize=50&columns=ALL&source=WEB&client=WEB&_=1770486329447")
    JSONObject getStockDragonListSell(@Var("tradeDate") String tradeDate, @Var("code") String code);

    /**
     *
     * https://datacenter-web.eastmoney.com/api/data/v1/get?reportName=RPT_BILLBOARD_DAILYDETAILSBUY&filter=(TRADE_DATE='2026-02-03')(SECURITY_CODE="000547")&sortTypes=-1&sortColumns=BUY&pageNumber=1&pageSize=50&columns=ALL&source=WEB&client=WEB&_=1770542839476
     */
    @Get(url = DATACENTER_WEB_URL + "/api/data/v1/get?reportName=RPT_BILLBOARD_DAILYDETAILSBUY&filter=(TRADE_DATE='{tradeDate}')(SECURITY_CODE=\"{code}\")&sortTypes=-1&sortColumns=BUY&pageNumber=1&pageSize=50&columns=ALL&source=WEB&client=WEB&_=1770542839476")
    JSONObject getStockDragonListBuy(@Var("tradeDate") String tradeDate, @Var("code") String code);

}
