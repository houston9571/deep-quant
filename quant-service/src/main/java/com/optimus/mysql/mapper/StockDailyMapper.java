package com.optimus.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.optimus.mysql.entity.StockDaily;
import org.apache.ibatis.annotations.Select;

public interface StockDailyMapper extends BaseMapper<StockDaily> {

    /**
     * 筛选当日强力共振买入信号（评分≥60）
     */
    @Select("SELECT t.stock_code, t.trade_date, b.close, t.resonance_score,t.ma5, t.macd_dif, t.rsi14, t.kdj_j, t.cci, t.wr10, t.mfi, t.cost_concentration" +
            "FROM stock_tech_daily t " +
            "LEFT JOIN stock_bar_daily b ON t.stock_code = b.stock_code AND t.trade_date = b.trade_date" +
            "WHERE " +
            "    t.trade_date = CURDATE()  -- 当日" +
            "    AND t.resonance_signal = 1  -- 强力买入" +
            "    AND t.resonance_score >= 60  -- 评分≥60" +
            "    AND b.volume > 500000  -- 成交量>5万手（过滤小票）" +
            "    AND t.cost_concentration < 15  -- 筹码高度集中" +
            "ORDER BY t.resonance_score DESC" +
            "LIMIT 20;  -- 选前20只")
    void ddd();
}
