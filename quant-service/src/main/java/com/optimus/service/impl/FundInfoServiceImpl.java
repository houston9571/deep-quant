package com.optimus.service.impl;

import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.FundInfo;
import com.optimus.mysql.mapper.FundInfoMapper;
import com.optimus.service.FundInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundInfoServiceImpl extends MybatisBaseServiceImpl<FundInfoMapper, FundInfo> implements FundInfoService {


    private final FundInfoMapper fundInfoMapper;


}
