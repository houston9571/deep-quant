package com.optimus.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.optimus.mysql.entity.StockDragon;
import com.optimus.mysql.entity.StockDragonDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface StockDragonDetailMapper extends BaseMapper<StockDragonDetail> {


    @Select("SELECT a.code AS partnerCode,a.name AS partnerName,c.* " +
            "FROM org_main_partner a LEFT JOIN org_partner_dept b ON a.`code`=b.partner_code LEFT JOIN stock_dragon_detail c on b.dept_code=c.dept_code " +
            "WHERE c.trade_date=(SELECT MAX(c.trade_date) FROM stock_dragon_detail) ")
    List<StockDragonDetail> queryDragonDetail();

}
