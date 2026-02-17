package com.optimus.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.optimus.mysql.entity.BoardDelay;
import com.optimus.mysql.entity.DragonStock;
import com.optimus.mysql.vo.DragonStockList;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DragonStockMapper extends BaseMapper<DragonStock> {



    @Select("SELECT a.code AS partnerCode, a.name AS partnerName, c.trade_date, c.dept_code, dept.name AS dept_name, " +
            "c.net_buy_amount AS partnerNetBuyAmount, c.buy_amount AS partnerBuyAmount, c.total_buy_ratio AS partnerTotalBuyRatio, " +
            "c.sell_amount AS partnerSellAmount, c.total_sell_ratio AS partnerTotalSellRatio, d.*, e.* " +
            "FROM org_partner a LEFT JOIN org_partner_dept b ON a.`code`=b.partner_code LEFT JOIN org_dept dept ON b.dept_code=dept.`code` " +
            "LEFT JOIN dragon_stock_detail c on b.dept_code=c.dept_code " +
            "LEFT JOIN dragon_stock d on c.code=d.code AND c.trade_date=d.trade_date " +
            "LEFT JOIN stock_delay e on c.code=e.code AND c.trade_date=e.trade_date " +
            "WHERE c.trade_date=#{tradeDate}")
    List<DragonStockList> queryDragonStockList(@Param("tradeDate") String tradeDate);

    @Select("SELECT y.*, d.*, e.* " +
            "FROM ( " +
            " SELECT partner_code, partner_name, `code`, `name`,trade_date, SUM(net_buy_amount) partnerNetBuyAmount, SUM(total_net_buy_ratio) partnerNetBuyRatio, SUM(buy_amount) partnerBuyAmount, SUM(sell_amount) partnerSellAmount " +
            " FROM ( " +
            "  SELECT a.`code` AS partner_code, a.name AS partner_name, c.`code`, c.`name`, c.trade_date, c.net_buy_amount, c.total_net_buy_ratio, c.buy_amount, c.sell_amount " +
            "  FROM org_partner a LEFT JOIN org_partner_dept b ON a.`code`=b.partner_code LEFT JOIN dragon_stock_detail c ON b.dept_code=c.dept_code " +
            "  WHERE c.code= #{code} AND c.trade_date>'2026-02-10'" +
            "  ) x GROUP BY partner_code, partner_name, `code`, `name`, trade_date " +
            " ORDER BY trade_date DESC " +
            ") y " +
            "LEFT JOIN dragon_stock d on y.code=d.code AND y.trade_date=d.trade_date " +
            "LEFT JOIN stock_delay e on y.code=e.code AND y.trade_date=e.trade_date " +
            "LIMIT 30")
    List<DragonStockList> queryDragonStockDetail(@Param("code") String tradeDate);




}
