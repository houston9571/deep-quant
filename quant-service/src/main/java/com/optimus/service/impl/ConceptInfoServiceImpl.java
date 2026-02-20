package com.optimus.service.impl;

import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.ConceptInfo;
import com.optimus.mysql.mapper.ConceptInfoMapper;
import com.optimus.service.ConceptInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConceptInfoServiceImpl extends MybatisBaseServiceImpl<ConceptInfoMapper, ConceptInfo> implements ConceptInfoService {

    private final ConceptInfoMapper conceptInfoMapper;


}
