package com.optimus.rest;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
import com.dtflys.forest.Forest;
import com.optimus.BaseTest;
import com.optimus.utils.StringUtil;
import org.junit.Test;

public class PlayerTest extends BaseTest {


    private final String url = "http://127.0.0.1:8082/api/player";

    @Test
    public void enterGame() {
        long tm = System.currentTimeMillis();

        Forest.get(url + "/enterGame?agent=101&param=s9r6ETyTIbHKbc7wKhTcCqyv4HemX%2BpivwL9asnReptnWigF2ZBZ719O2dnRZqP6&sign=41a63123409aa51e4ac37db90ece30b5&timestamp=1697622637719")
                .addQuery("agent", 101)
                .addQuery("timestamp", tm)
                .addQuery("sign")
        ;
    }

    @Test
    public void test() {
        JSONObject json = XML.toJSONObject("<transactionID>025214112211222124922</transactionID><transactionID>025214112211221824904</transactionID>");
        String s = json.getStr("transactionID");
        System.out.println(s.startsWith("["));
    }

    @Test
    public void tt(){
        String json = "\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?><Data><Record><sessionToken>b340bf7a08784188947dcb8e08d5bc35</sessionToken><currency>CNY</currency><netAmount>770.0000</netAmount><validBetAmount>770.0000</validBetAmount><playname>MM81000428170</playname><agentCode>MM8</agentCode><settletime>11/25/2024 01:36:02</settletime><billNo>241125013273713</billNo><transactionID>025314112501354993596</transactionID><transactionID>025314112501354893592</transactionID><transactionID>025314112501354693584</transactionID><gametype>BAC</gametype><gameCode>GN07324B2503A</gameCode><transactionType>WIN</transactionType><transactionCode>BCP</transactionCode><ticketStatus>Win</ticketStatus><gameResult>P;S7;DK:B;HJ;HQ;C3</gameResult><finish>true</finish></Record></Data>";
        JSONObject j =  XML.toJSONObject(json).getJSONObject("Data").getJSONObject("Record");
        System.out.println(j.toStringPretty());
        String s = j.getStr("transactionID");
        JSONArray array = new JSONArray();
        if (StringUtil.startsWith(s, "[")) {
            System.out.println("-------");
            array = j.getJSONArray("transactionID");
        } else {
            array.add(s);
        }
        for (int i = 0; i < array.size(); i++) {
            String transactionId = array.getStr(i);
            System.out.println(transactionId);
        }

    }
}
