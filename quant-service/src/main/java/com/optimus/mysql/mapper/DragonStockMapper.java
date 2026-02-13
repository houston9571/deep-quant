package com.optimus.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.optimus.mysql.entity.DragonStock;
import com.optimus.mysql.vo.DragonStockList;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DragonStockMapper extends BaseMapper<DragonStock> {


    @Select("SELECT a.code AS partnerCode, a.name AS partnerName, c.trade_date, c.dept_code, dept.name AS dept_name, " +
            "c.net_buy_amount AS deptNetBuyAmount, c.buy_amount AS deptBuyAmount, c.total_buy_ratio AS deptTotalBuyRatio, " +
            "c.sell_amount AS deptSellAmount, c.total_sell_ratio AS deptTotalSellRatio, d.*, e.* " +
            "FROM org_partner a LEFT JOIN org_partner_dept b ON a.`code`=b.partner_code LEFT JOIN org_dept dept ON b.dept_code=dept.`code` " +
            "LEFT JOIN dragon_stock_detail c on b.dept_code=c.dept_code " +
            "LEFT JOIN dragon_stock d on c.code=d.code AND c.trade_date=d.trade_date " +
            "LEFT JOIN stock_delay e on c.code=e.code AND c.trade_date=e.trade_date " +
            "WHERE c.trade_date=#{tradeDate}")
    List<DragonStockList> queryDragonPartnerList(@Param("tradeDate") String tradeDate);
}
