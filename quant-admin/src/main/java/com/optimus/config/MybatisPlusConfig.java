package com.optimus.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.baomidou.mybatisplus.annotation.DbType.MYSQL;

/**
 * MyBatis-plus 配置
 */
@Configuration
@MapperScan("com.optimus.mysql.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(MYSQL));
        return interceptor;
    }

//    @Bean
//    public String i18nInterceptor(SqlSessionFactory sqlSessionFactory, I18nService i18nService) {
//        sqlSessionFactory.getConfiguration().addInterceptor(new I18nInterceptor(i18nService));
//        return "interceptor";
//    }


}