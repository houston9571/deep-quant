package com.optimus.mysql;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.optimus.base.PageInfo;
import com.optimus.base.Result;
import com.optimus.exception.ServiceException;
import com.optimus.mysql.entity.BaseEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static com.optimus.enums.ErrorCode.INTERFACE_UNSUPPORTED;

public interface MybatisBaseService<P extends BaseEntity> {

    Result<Void> save(P entity);

    Result<Integer> saveBatch(List<P> list);

    Result<Integer> saveBatch(List<P> list, int batchSize);


    Result<Void> updateById(P entity);

    Result<Void> update(P entity, Wrapper<P> queryWrapper);


    Result<Void> deleteById(Serializable id);

    Result<Void> delete(Wrapper<P> queryWrapper) ;

    Result<Integer> deleteBatch(Collection<? extends Serializable> ids);


    Long count(P entity);

    boolean exist(Wrapper<P> queryWrapper);


    P findById(Serializable id);

    default P findByIdCache(Serializable id) {
        throw new ServiceException(INTERFACE_UNSUPPORTED, "findByIdCache");
    }

    P findOne(P entity);

    P findOne(P entity, QueryWrapper<P> query);

    List<P> findAll();


    List<P> queryList(P entity);

    List<P> queryList(P entity, QueryWrapper<P> queryWrapper);

    List<P> queryPage(PageInfo<P> pageInfo);

    List<P> queryPage(PageInfo<P> pageInfo, QueryWrapper<P> queryWrapper);
}
