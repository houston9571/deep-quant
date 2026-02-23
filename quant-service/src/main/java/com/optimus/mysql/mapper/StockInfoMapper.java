package com.optimus.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.optimus.mysql.entity.StockInfo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StockInfoMapper extends BaseMapper<StockInfo> {

    @Select("SELECT DISTINCT stock_code FROM stock_kline_daily WHERE stock_code NOT IN (SELECT stock_code FROM stock_info)")
    List<StockInfo> queryStockInfoNotIn();

}
