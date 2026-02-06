package com.optimus.mysql;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.base.CaseFormat;
import com.optimus.base.PageInfo;
import com.optimus.base.Result;
import com.optimus.mysql.entity.BaseEntity;
import com.optimus.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StopWatch;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.optimus.enums.ErrorCode.DATA_NOT_EXIST;
import static com.optimus.mysql.entity.BaseEntity.CREATE_TIME;
import static com.optimus.mysql.entity.BaseEntity.UPDATE_TIME;

@Slf4j
public class MybatisBaseServiceImpl<M extends BaseMapper<P>, P extends BaseEntity> implements MybatisBaseService<P>, InitializingBean {

    @Autowired
    protected SqlSession sqlSession;

    protected M baseMapper;


    /**
     * 子类要先注入mapper对象
     */
    @Override
    public void afterPropertiesSet() {
        Type[] types = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        Class m = (Class) types[0];
//        Class p = (Class) types[1];
        baseMapper = (M) sqlSession.getMapper(m);
    }


    @Override
    public Result<Void> save(P entity) {
        return Result.isSuccess(baseMapper.insert(entity), DATA_NOT_EXIST);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> saveBatch(List<P> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Result.success(0);
        }
        int size = list.size();
        AtomicInteger at = new AtomicInteger();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (P p : list) {
            baseMapper.insert(p);
        }
        stopWatch.stop();
        log.info(">>>>> saveBatch size:{} time:{}", size, DateUtils.formatDateTime(stopWatch.getTotalTimeMillis()));
        return Result.success(size);
    }


    @Override
    public Result<Void> updateById(P entity) {
        return Result.isSuccess(baseMapper.updateById(entity), DATA_NOT_EXIST);
    }

    @Override
    public Result<Void> update(P entity, Wrapper<P> queryWrapper) {
        return Result.isSuccess(baseMapper.update(entity, queryWrapper), DATA_NOT_EXIST);
    }

    @Override
    public Result<Void> deleteById(Serializable id) {
        return Result.isSuccess(baseMapper.deleteById(id), DATA_NOT_EXIST);
    }

    @Override
    public Result<Void> delete(Wrapper<P> queryWrapper) {
        return Result.isSuccess(baseMapper.delete(queryWrapper), DATA_NOT_EXIST);
    }

    @Override
    public Result<Integer> deleteBatch(Collection<? extends Serializable> ids) {
        int l = baseMapper.deleteBatchIds(ids);
        return l > 0 ? Result.success(l) : Result.fail(DATA_NOT_EXIST);
    }


    @Override
    public Long count(P entity) {
        return baseMapper.selectCount(wrapperQueryParams(entity, null));
    }

    @Override
    public boolean exist(Wrapper<P> queryWrapper) {
        return baseMapper.selectCount(queryWrapper) > 0;
    }


    @Override
    public P findOne(P entity) {
        return findOne(entity, null);
    }

    @Override
    public P findById(Serializable id) {
        if (null == id) {
            return null;
        }
        return baseMapper.selectById(id);
    }

    @Override
    public P findOne(P entity, QueryWrapper<P> queryWrapper) {
        List<P> list = baseMapper.selectList(wrapperQueryParams(entity, queryWrapper));
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<P> findAll() {
        return baseMapper.selectList(null);
    }


    @Override
    public List<P> queryList(P entity) {
        return queryList(entity, null);
    }

    @Override
    public List<P> queryList(P entity, QueryWrapper<P> queryWrapper) {
        return baseMapper.selectList(wrapperQueryParams(entity, queryWrapper));
    }

    @Override
    public List<P> queryPage(PageInfo<P> pageInfo) {
        return queryPage(pageInfo, null);
    }

    @Override
    public List<P> queryPage(PageInfo<P> pageInfo, QueryWrapper<P> queryWrapper) {
        pageInfo.startPage();
        return baseMapper.selectList(wrapperQueryParams(pageInfo.getData(), queryWrapper));
    }


    public QueryWrapper<P> wrapperQueryParams(P entity, QueryWrapper<P> query) {
        if (ObjectUtil.isEmpty(query)) {
            query = new QueryWrapper<>();
        }
        if (ObjectUtil.isNotEmpty(entity)) {
            Class<?> cls = entity.getClass();
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                TableField annotation = field.getAnnotation(TableField.class);
                if (ObjectUtil.isEmpty(annotation) || !annotation.exist()) {
                    continue;
                }
                try {
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), cls);
                    Object value = ReflectionUtils.invokeMethod(pd.getReadMethod(), entity);
                    if (ObjectUtil.isEmpty(value) || StrUtil.isBlank(value.toString())) {
                        continue;
                    }
                    query.eq(StrUtil.isNotBlank(annotation.value()) ? annotation.value() : CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()), value);
                } catch (IntrospectionException e) {
                    log.error("无法解析字符串方法名 {} ", field.getName());
                }
            }
            if (StrUtil.isAllNotBlank(entity.getCreateTimeStart(), entity.getCreateTimeEnd())) {
                query.between(CREATE_TIME, entity.getCreateTimeStart(), entity.getCreateTimeEnd());
            }
            if (StrUtil.isAllNotBlank(entity.getUpdateTimeStart(), entity.getUpdateTimeEnd())) {
                query.between(UPDATE_TIME, entity.getUpdateTimeStart(), entity.getUpdateTimeEnd());
            }
            if (StrUtil.isNotBlank(entity.getOrderColumn())) {
                String[] cols = entity.getOrderColumn().split(COMMA);
                String[] asc = StrUtil.isNotBlank(entity.getIsAsc()) ? entity.getIsAsc().split(COMMA) : new String[0];
                for (int i = 0; i < cols.length; i++) {
                    query.orderBy(true, "1".equals(i < asc.length ? asc[i] : "1"), CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, cols[i]));
                }
            } else {
                query.orderBy(true, false, UPDATE_TIME);
            }
        }
        return query;
    }


}
