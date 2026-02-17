package com.optimus.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.optimus.client.EastMoneyDragonApi;
import com.optimus.enums.DateFormatEnum;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.BoardDelay;
import com.optimus.mysql.entity.OrgDept;
import com.optimus.mysql.entity.DragonStockDetail;
import com.optimus.mysql.mapper.DragonStockDetailMapper;
import com.optimus.mysql.vo.DragonDeptDto;
import com.optimus.service.OrgDeptService;
import com.optimus.service.DragonStockDetailService;
import com.optimus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.optimus.constant.Constants.*;
import static java.math.RoundingMode.HALF_UP;

@Slf4j
@Service
@RequiredArgsConstructor
public class DragonStockDetailServiceImpl extends MybatisBaseServiceImpl<DragonStockDetailMapper, DragonStockDetail> implements DragonStockDetailService {

    private final DragonStockDetailMapper dragonStockDetailMapper;

    private final OrgDeptService orgDeptService;

    private final EastMoneyDragonApi eastMoneyDragonApi;


    /**
     * 查询龙虎榜游资席位买入详情，合并为按游资的竖型列表，第一行表头游资
     */
    public List<List<DragonStockDetail>> queryDragonStockDetailWithPartner(String tradeDate) {
        List<DragonStockDetail> list = dragonStockDetailMapper.queryDragonStockDetailWithPartner(tradeDate);
        Map<String, List<DragonStockDetail>> map = Maps.newLinkedHashMap();
        Map<String, DragonStockDetail> totalNet = Maps.newLinkedHashMap();
        Map<String, List<DragonStockDetail>> partners = Maps.newLinkedHashMap();
        for (DragonStockDetail detail : list) {
            if (map.containsKey(detail.getPartnerCode())) {
                map.get(detail.getPartnerCode()).add(detail);
                totalNet.get(detail.getPartnerCode()).setNetBuyAmount(totalNet.get(detail.getPartnerCode()).getNetBuyAmount() + detail.getNetBuyAmount());
            } else {
                List<DragonStockDetail> details = Lists.newArrayList();
                details.add(detail);
                map.put(detail.getPartnerCode(), details);
                totalNet.put(detail.getPartnerCode(), DragonStockDetail.builder().partnerCode(detail.getPartnerCode()).partnerName(detail.getPartnerName()).netBuyAmount(detail.getNetBuyAmount()).totalNetBuyRatio(detail.getTotalNetBuyRatio()).build());
            }
            DragonStockDetail np = DragonStockDetail.builder().partnerCode(detail.getPartnerCode()).partnerName(detail.getPartnerName()).netBuyAmount(detail.getNetBuyAmount()).totalNetBuyRatio(detail.getTotalNetBuyRatio()).build();
            if (partners.containsKey(detail.getCode())) {
                partners.get(detail.getCode()).add(np);
            } else {
                partners.put(detail.getCode(), new ArrayList<DragonStockDetail>() {{
                    add(np);
                }});
            }

        }

        List<String> qt = new ArrayList<String>() {{
            add("量化基金");
            add("量化打板");
            add("量化抢筹");
            add("T王");
        }};

        List<List<DragonStockDetail>> grid = Lists.newArrayList();
        ArrayList<DragonStockDetail> totalNets = new ArrayList<>(totalNet.values());
        totalNets.sort(Comparator.comparingLong(DragonStockDetail::getNetBuyAmount).reversed());
        grid.add(totalNets);
        for (DragonStockDetail d : totalNets) {
            if (!qt.contains(d.getPartnerName())) {
                List<DragonStockDetail> data = map.get(d.getPartnerCode());
                for (DragonStockDetail dd : data) {
                    dd.setPartners(partners.get(dd.getCode()));
                }
                grid.add(data);
            }
        }
        totalNets.removeIf(d -> qt.contains(d.getPartnerName()));
        return grid;
    }


    /**
     * 龙虎榜个股买卖详情
     */
    public int syncDragonStockDetail(LocalDate date, String code, String name) {
        Map<String, DragonStockDetail> map = Maps.newHashMap();
        String tradeDate = DateUtils.format(date, DateFormatEnum.DATE);
        try {
            JSONObject buy = eastMoneyDragonApi.syncDragonStockListBuy(tradeDate, code).getJSONObject(LABEL_RESULT);
            JSONObject sell = eastMoneyDragonApi.syncDragonStockListSell(tradeDate, code).getJSONObject(LABEL_RESULT);
            JSONArray data = new JSONArray();
            if (ObjectUtil.isNotNull(buy) && ObjectUtil.isNotNull(sell) && buy.containsKey(LABEL_DATA) && sell.containsKey(LABEL_DATA)) {
                data = buy.getJSONArray(LABEL_DATA);
                if (!CollectionUtils.isEmpty(data)) {
                    data.fluentAddAll(sell.getJSONArray(LABEL_DATA));
                    for (int i = 0; i < data.size(); i++) {
                        try {
                            DragonStockDetail d = JSONObject.parseObject(data.getString(i), DragonStockDetail.class);
                            // 自然人、其他自然人、机构投资者、中小投资者、深股通投资者等，这些code都是0
                            if (!StrUtil.equals(d.getDeptCode(), "0")) {
                                d.setName(name);
                                if (!map.containsKey(d.getDeptCode()) || map.get(d.getDeptCode()).getTradeId() < d.getTradeId()) {
                                    d.setTotalNetBuyRatio(BigDecimal.valueOf(d.getNetBuyAmount()).divide(BigDecimal.valueOf(d.getAccumAmount()), new MathContext(4, HALF_UP)).multiply(HUNDRED));
                                    map.put(d.getDeptCode(), d);
                                }
                            }
                        } catch (Exception e) {
                            log.error(">>>>>getDragonStockDetail JSONObject.parseObject error. {} {}", data.getString(i), e.getMessage());
                        }
                    }
                }
            }
            log.info(">>>>>getDragonStockDetail: {} {} {} total:{} save:{}", date, code, name, data.size(), map.size());
        } catch (Exception e) {
            log.error(">>>>>getDragonStockDetail request json error. {}", e.getMessage());
        }
        try {
            if (!CollectionUtils.isEmpty(map)) {
                ArrayList<DragonStockDetail> list = new ArrayList<>(map.values());
                list.sort(Comparator.comparingLong(DragonStockDetail::getNetBuyAmount).reversed());
                saveOrUpdateBatch(list, new String[]{"code", "trade_date", "dept_code"});
            }
        } catch (Exception e) {
            log.error(">>>>>getDragonStockDetail saveBatch error. {}", e.getMessage());
        }
        return map.size();
    }

}
