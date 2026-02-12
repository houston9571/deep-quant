package com.optimus.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.optimus.mysql.entity.BoardDelay;
import com.optimus.mysql.entity.StockDelay;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface BoardDelayMapper extends BaseMapper<BoardDelay> {

    @Select("SELECT DISTINCT trade_date FROM board_delay  ORDER BY trade_date DESC LIMIT #{days}")
    List<BoardDelay> queryBoardTradeDate(int days);


    @Select("SELECT * FROM board_delay WHERE trade_date=#{tradeDate} ORDER BY change_rate DESC LIMIT #{top}")
    List<BoardDelay> queryBoardTop(@Param("tradeDate") LocalDate tradeDate, @Param("top") int top);
}
