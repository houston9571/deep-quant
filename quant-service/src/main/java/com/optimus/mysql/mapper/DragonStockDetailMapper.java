package com.optimus.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.optimus.mysql.entity.DragonStockDetail;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DragonStockDetailMapper extends BaseMapper<DragonStockDetail> {


//    @Select("SELECT a.`code` AS partner_code, a.name AS partner_name, c.* " +
//            "FROM org_partner a LEFT JOIN org_partner_dept b ON a.`code`=b.partner_code LEFT JOIN dragon_stock_detail c  ON b.dept_code=c.dept_code " +
//            "WHERE  c.trade_date = #{tradeDate} " +
//            "ORDER BY c.net_buy_amount DESC ")

    @Select("SELECT partner_code, partner_name, `code`, `name`, trade_date, close_price, change_rate, " +
            "SUM(net_buy_amount) net_buy_amount, SUM(total_net_buy_ratio) total_net_buy_ratio, SUM(buy_amount) buy_amount, SUM(sell_amount) sell_amount " +
            "FROM ( " +
            " SELECT a.`code` AS partner_code, a.name AS partner_name, c.`code`, c.`name`, c.trade_date, c.close_price, c.change_rate, " +
            " c.net_buy_amount, c.total_net_buy_ratio, c.buy_amount, c.sell_amount " +
            " FROM org_partner a LEFT JOIN org_partner_dept b ON a.`code`=b.partner_code LEFT JOIN dragon_stock_detail c ON b.dept_code=c.dept_code " +
            " WHERE c.trade_date = #{tradeDate} " +
            ") x GROUP BY partner_code, partner_name, `code`, `name`, trade_date, close_price, change_rate " +
            "ORDER BY net_buy_amount DESC ")
    List<DragonStockDetail> queryDragonStockDetailWithPartner(String tradeDate);

}
