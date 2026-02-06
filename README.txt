

### https://github.com/axiaoxin-com/investool/blob/v1.3.9/datacenter/eastmoney/company_profile.go


emh5.eastmoney.com          H5获取A股市场实时行情、历史数据、资金流向、财务指标等金融数据
push2.eastmoney.com         实时数据推送 http://push2delay.eastmoney.com
push2his.eastmoney.com      历史数据 K线数据（日K、周K、月K） 分时数据 复权数据
datacenter.eastmoney.com    数据中心
quote.eastmoney.com         实时行情 股票、基金、债券、期货实时行情 指数行情 港股、美股行情


根据股票查询购买的基金 机构 股东
query_fund_by_stock.go


### 概念页面
https://quote.eastmoney.com/bk/90.BK1134.html


### 返回范围内的交易数据 klt=1 K线类型
https://push2his.eastmoney.com/api/qt/stock/kline/get?klt=101&fqt=1&secid=1.600986&beg=20260201&end=20260207&fields1=f1,f2,f3,f4,f5,f6&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61
// 日期,开盘,收盘,最高,最低,成交量,成交额,振幅,涨跌幅,涨跌额,换手率
"klines":["2026-02-06 09:31,14.60,13.97,14.78,13.97,366811,527498432.00,5.22,-9.99,-1.55,2.47"]

### 每分钟的交易数据 klt=1 K线类型
https://push2.eastmoney.com/api/qt/stock/trends2/get?fields1=f1,f2,f3,f4,f5,f6,f7&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64,f65&klt=1&secid=1.600986
"trends":["2026-01-07 15:00,88.63,88.63,88.63,88.63,2638,23380594.00,88.102,1936,0.00,437302,3852698702.00"]






