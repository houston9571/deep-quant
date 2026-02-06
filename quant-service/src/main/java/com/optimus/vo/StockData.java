package com.optimus.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class StockData {

    @JSONField(name = "f43", label = "")
    private Long latestPrice;      // 最新价 * 100
    @JSONField(name = "f44")
    private Long highestPrice;     // 最高价 * 100
    @JSONField(name = "f45")
    private Long lowestPrice;      // 最低价 * 100
    @JSONField(name = "f46")
    private Long openPrice;        // 今开 * 100
    @JSONField(name = "f47")
    private Long volume;           // 成交量（手）
    @JSONField(name = "f48")
    private Long turnover;         // 成交额 * 10000
    @JSONField(name = "f50")
    private Long volumeRatio;      // 量比 * 100
    @JSONField(name = "f51")
    private Long limitUp;          // 涨停价 * 100
    @JSONField(name = "f52")
    private Long limitDown;        // 跌停价 * 100
    @JSONField(name = "f57")
    private String symbol;         // 代码
    @JSONField(name = "f58")
    private String name;           // 名称
    @JSONField(name = "f168")
    private Long turnoverRate;     // 换手率 * 100
    @JSONField(name = "f169")
    private Long changePercent;    // 涨跌幅 * 100
    @JSONField(name = "f170")
    private Long changeAmount;     // 涨跌额 * 100
}
