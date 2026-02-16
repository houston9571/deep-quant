package com.optimus.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.optimus.mysql.entity.DragonStockDetail;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DragonStockDetailMapper extends BaseMapper<DragonStockDetail> {


    @Select("SELECT a.`code` AS partner_code, a.name AS partner_name, c.* " +
            "FROM org_partner a LEFT JOIN org_partner_dept b ON a.`code`=b.partner_code LEFT JOIN dragon_stock_detail c  ON b.dept_code=c.dept_code " +
            "WHERE  c.trade_date = #{tradeDate} " +
            "ORDER BY c.net_buy_amount DESC ")
    List<DragonStockDetail> queryDragonStockDetailWithPartner(String tradeDate);

}
