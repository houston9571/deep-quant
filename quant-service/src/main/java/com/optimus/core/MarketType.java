package com.optimus.core;

import com.optimus.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class MarketType {

    private static String MARKET_SH = "SH", MARKET_SZ = "SZ", MARKET_CODE_SH = "1", MARKET_CODE_SZ = "0";

    /**
     * 第1位标识证券大类，第2位标识该大类下的衍生证券
     * 新股申购：以730打头。配股代码：沪市以700打头，深市以080打头。
     */
    private static Map<String, String> marketMap = new HashMap<String, String>() {{
        put("600", MARKET_SH);  // 沪市A股
        put("601", MARKET_SH);  // 沪市A股
        put("603", MARKET_SH);  // 沪市A股
        put("605", MARKET_SH);  // 沪市A股
        put("688", MARKET_SH);  // 科创板
        put("900", MARKET_SH);  // 沪市B股
        put("000", MARKET_SZ);  // 深市A股
        put("001", MARKET_SZ);  // 深市A股
        put("002", MARKET_SZ);  // 深交所中小板A股
        put("003", MARKET_SZ);  // 深交所中小板A股
        put("200", MARKET_SZ);  // 深市B股
        put("300", MARKET_SZ);  // 创业板
    }};

    private static Map<String, String> marketCodeMap = new HashMap<String, String>() {{
        put("600", MARKET_CODE_SH);  // 沪市A股
        put("601", MARKET_CODE_SH);  // 沪市A股
        put("603", MARKET_CODE_SH);  // 沪市A股
        put("605", MARKET_CODE_SH);  // 沪市A股
        put("688", MARKET_CODE_SH);  // 科创板
        put("900", MARKET_CODE_SH);  // 沪市B股
        put("000", MARKET_CODE_SZ);  // 深市A股
        put("001", MARKET_CODE_SZ);  // 深市A股
        put("002", MARKET_CODE_SZ);  // 深交所中小板A股
        put("003", MARKET_CODE_SZ);  // 深交所中小板A股
        put("200", MARKET_CODE_SZ);  // 深市B股
        put("300", MARKET_CODE_SZ);  // 创业板
    }};

    public static String getMarket(String scode) {
        if (scode.length() == 5) {
            return "HK";
        }
        String s = marketMap.get(scode.substring(0, 3));
        return StringUtil.isNotEmpty(s) ? s : (Integer.parseInt(scode.substring(0, 1)) < 6) ? MARKET_SZ : MARKET_SH;
    }

    public static String getMarketCode(String scode) {
        if (scode.length() == 5) {
            return "116";
        }
        String s = marketCodeMap.get(marketMap.get(scode.substring(0, 3)));
        return StringUtil.isNotEmpty(s) ? s : (Integer.parseInt(scode.substring(0, 1)) < 6) ? MARKET_CODE_SZ : MARKET_CODE_SH;
    }


    public static Map<String, String> codeMap(String scode) {
        return new HashMap<String, String>() {{
            put("scode", scode);
            put("market", getMarket(scode));
            put("marketCode", getMarketCode(scode));
        }};
    }
}
