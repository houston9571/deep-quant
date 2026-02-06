package com.optimus.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.optimus.base.Result;
import com.optimus.client.EmPush2delayApi;
import com.optimus.client.EastMoneyH5Api;
import com.optimus.core.StockCodeUtils;
import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.FundInfo;
import com.optimus.mysql.mapper.FundInfoMapper;
import com.optimus.service.FundInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.optimus.core.StockConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundInfoServiceImpl extends MybatisBaseServiceImpl<FundInfoMapper, FundInfo> implements FundInfoService {

    @Autowired
    EmPush2delayApi eastMoneyApi;
    @Autowired
    EastMoneyH5Api eastMoneyH5Api;

    private final FundInfoMapper fundInfoMapper;


}
