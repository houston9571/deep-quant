package com.optimus.rest;

import com.optimus.base.Result;
import com.optimus.mysql.entity.FundInfo;
import com.optimus.service.FundInfoService;
import com.optimus.sprider.SpriderTemplateParser;
import com.optimus.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;
import static com.optimus.enums.ErrorCode.DATA_NOT_EXIST;


@Slf4j
@RestController
@RequestMapping(value = "fund", produces = APPLICATION_JSON)
public class FundInfoRest {

    @Autowired
    SpriderTemplateParser spiderTemplateParser;

    @Autowired
    FundInfoService fundInfoService;

//    @Autowired
//    FundInvestService fundInvestService;
//
//    @Autowired
//    FundOverveiwService fundOverveiwService;
//
//    @Autowired
//    FundValueHistoryService fundValueHistoryService;
//
//    @Autowired
//    FundManagerService fundManagerService;
//
//    @Autowired
//    StockActionRest stockActionRest;


    @GetMapping("codeList")
    public Result<Integer> fundCode() {
        String url = "http://fund.eastmoney.com/js/fundcode_search.js";
        String js = spiderTemplateParser.getPage(url);
        if (StringUtil.isNotEmpty(js)) {
            js = js.substring(js.indexOf("[[") + 2, js.length() - 3);
            js = js.replaceAll("\"", "");
            String[] founds = js.split("],\\[");
            if (ArrayUtils.isNotEmpty(founds)) {
                String[] fc;
                List<FundInfo> fundInfoList = Lists.newArrayList();
                for (String found : founds) {
                    try {
                        fc = found.split(",");
                        fundInfoList.add(FundInfo.builder().code(fc[0]).pyjc(fc[1]).name(fc[2]).type(fc[3]).pyqc(fc.length >= 5 ? fc[4] : "").build());
                    } catch (Exception e) {
                        log.error("解析基金列表错误：{}", found);
                    }
                }
                return Result.success(fundInfoService.saveBatch(fundInfoList));
            }
        }
        return Result.fail(DATA_NOT_EXIST);
    }
/*

    @GetMapping("overview/{fcodeStr}")
    public JSONResult overview(@PathVariable String fcodeStr) {
        String[] fcodeArray = fcodeStr.split(",");
        for (String fcode : fcodeArray) {
            List<Map<String, String>[]> factors = spiderTemplateParser.parserAsMap("F01-overview.json", createMap(fcode));
            if (CollectionUtils.isEmpty(factors)) {
                return JSONResult.failed("未获取到页面信息");
            }
            Map<String, String> fundBasic = factors.get(0)[0]; //fundBasic
            Map<String, String> fundManager = factors.get(1)[0]; //fundManager
            Map<String, String> fundPartReport = factors.get(2)[0]; // fundPartReport
            Map<String, String> fundInvestSetup = factors.get(3)[0]; //fundInvestSetup
            Map<String, String>[] fundInvestItem = factors.get(4); //基金投资项目
            if (fundBasic != null) {
                String js = spiderTemplateParser.getPage("http://fund.eastmoney.com/pingzhongdata/" + fcode + ".js");
                Pattern pp = Pattern.compile("Data_currentFundManager(.*?)Data_buySedemption");
                Matcher m = pp.matcher(js);
                if (m.find()) {
                    try {
                        String s = m.group(1);
                        s = s.substring(s.indexOf("["), s.lastIndexOf("]") + 1);
                        // 多个基金经理
                        JSONArray array = JSON.parseArray(s);
                        for (int i = array.size() - 1; i >= 0; i--) {
                            JSONObject json = array.getJSONObject(i);
                            JSONObject power = json.getJSONObject("power");
                            JSONArray data = power.getJSONArray("data");
                            fundManager.put("mid", json.getString("id"));
                            fundManager.put("name", json.getString("name"));
                            fundManager.put("star", json.getString("star"));
                            fundManager.put("rzsj", json.getString("workTime"));
                            fundManager.put("glgm", json.getString("fundSize"));
                            fundManager.put("jyz", data.getString(0));
                            fundManager.put("syl", data.getString(1));
                            fundManager.put("kfx", data.getString(2));
                            fundManager.put("wdx", data.getString(3));
                            fundManager.put("zsnl", data.getString(4));
                            fundManager.put("avr", NumberUtils.isNumeric(power.getString("avr")) ? power.getString("avr") : null);
                            fundManager.put("pjrq", power.getString("jzrq"));
                            fundManagerService.saveMap(fundManager);
                        }
                    } catch (Exception e) {
                        log.error("fundManagerService.saveMap --> {} ", e.getLocalizedMessage());
                    }
                }
                String reportDate = fundPartReport.get("reportDate");
                pp = Pattern.compile("(\\d{4}-\\d{1,2}-\\d{1,2})");
                m = pp.matcher(reportDate);
                if (m.find()) { // 场外基金会有季度报告，场内基金是十大持有人
                    fundBasic.putAll(fundPartReport);
                    fundBasic.put("tradeOn", "场外");
                } else
                    fundBasic.put("tradeOn", "场内");
                fundBasic.putAll(fundManager);
                fundBasic.putAll(fundInvestSetup);  //资产配置中的报告期肯定会有
                fundOverveiwService.saveMap(fundBasic);
                reportDate = fundInvestSetup.get("reportDate");
                if (ArrayUtils.isNotEmpty(fundInvestItem))
                    fundInvestService.batchSave(fcode, reportDate, fundInvestItem);
                valueHistory(fcode);
            }

            // 添加基金后，如果基金持股的股票不在股票表中，查询后获取股票信息
            JSONResult result = fundOverveiwService.queryNotInStock();
            if (result.isSuccess()) {
                JSONArray array = result.getData();
                for (int i = 0; i < array.size(); i++) {
                    stockActionRest.overview(array.getJSONObject(i).getString("scode"));
                }
            }
        }
        return JSONResult.success("");
    }


    @GetMapping("valueHistory/{fcode}")
    public JSONResult valueHistory(@PathVariable String fcode) {
        List<Map<String, String>[]> factors = spiderTemplateParser.parserAsMap("F02-valueHistory.json", createMap(fcode));
        if (CollectionUtils.isEmpty(factors)) {
            return JSONResult.failed("未获取到页面信息");
        }
        try {
            Map<String, String> valueHistory = factors.get(0)[0];
            Map<String, String> fundIncrease = factors.get(1)[0];
            Map<String, String> fundFormance = factors.get(2)[0];
            if (valueHistory != null) {
                valueHistory.put("fcode", fcode);
                valueHistory.putAll(fundIncrease);
                valueHistory.putAll(fundFormance);
                String js = spiderTemplateParser.getPage("http://fund.eastmoney.com/pingzhongdata/" + fcode + ".js");
                Pattern pp = Pattern.compile("Data_performanceEvaluation = (.*?);");
                Matcher m = pp.matcher(js);
                if (m.find()) {
                    JSONObject json = JSONObject.parseObject(m.group(1));
                    JSONArray data = json.getJSONArray("data");
                    valueHistory.put("avr", NumberUtils.isNumeric(json.getString("avr")) ? json.getString("avr") : null);
                    valueHistory.put("xznl", data.getString(0));
                    valueHistory.put("syl", data.getString(1));
                    valueHistory.put("kfx", data.getString(2));
                    valueHistory.put("wdx", data.getString(3));
                    valueHistory.put("zsnl", data.getString(4));
                }
                js = spiderTemplateParser.getPage("http://fund.eastmoney.com/pingzhongdata/" + fcode + ".js");
                pp = Pattern.compile("Data_currentFundManager(.*?)Data_buySedemption");
                m = pp.matcher(js);
                if (m.find()) {
                    String s = m.group(1);
                    s = s.substring(s.indexOf("[") + 1, s.lastIndexOf("]"));
                    JSONArray array = JSON.parseArray(JsonPath.read(s, "$.profit.series[0].data").toString());
                    valueHistory.put("tlpj", array.getJSONObject(1).getString("y"));
                    valueHistory.put("hs300", array.getJSONObject(2).getString("y"));
                }
                fundValueHistoryService.saveMap(valueHistory);
            }

        } catch (Exception e) {
            log.error("valueHistory --> {} ", e.getLocalizedMessage());
        }
        return JSONResult.success("");
    }

    */
/**
 * 执行基金概况表中已有的基金历史净值
 *//*

    @GetMapping("valueHistoryTask")
    public JSONResult valueHistoryTask() {
        JSONResult result = fundOverveiwService.allFcode();
        if (result.isSuccess()) {
            JSONArray array = result.getData();
            for (int i = 0; i < array.size(); i++) {
                valueHistory(array.getJSONObject(i).getString("fcode"));
            }
        }
        return JSONResult.success("");
    }

    */

    /**
     * 执行基金概况表中已有的基金持股等
     *//*

    @GetMapping("overveiwTask")
    public JSONResult overveiwTask() {
        JSONResult result = fundOverveiwService.allFcode();
        if (result.isSuccess()) {
            JSONArray array = result.getData();
            for (int i = 0; i < array.size(); i++) {
                overview(array.getJSONObject(i).getString("fcode"));
            }
        }
        return JSONResult.success("");
    }
*/
    private Map<String, String> createMap(String fcode) {
        return new HashMap<String, String>() {{
            put("fcode", fcode);
        }};
    }
}
