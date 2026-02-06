package com.optimus.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.optimus.mysql.entity.BoardInfo;
import com.optimus.mysql.entity.StockInfo;
import org.apache.ibatis.annotations.Insert;

public interface BoardInfoMapper extends BaseMapper<BoardInfo> {

    @Insert("INSERT INTO board_info (code, newCode, name, type, level) VALUES (#{code}, #{newCode}, #{name}, #{type}, #{level}) " +
            "ON DUPLICATE KEY UPDATE email = VALUES(email),  age = VALUES(age), update_time = NOW()")
    int saveOrUpdateById(BoardInfo boardInfo);
}
