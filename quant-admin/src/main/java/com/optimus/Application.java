package com.optimus;

import com.dtflys.forest.springboot.annotation.ForestScan;
import com.google.common.collect.Maps;
import com.optimus.filter.CorsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication(exclude = {
//        WebMvcAutoConfiguration.class,          // 如果不用WebMVC
        DataSourceAutoConfiguration.class,      // 如果不用数据库
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        RedisAutoConfiguration.class,           // 如果不用Redis
        KafkaAutoConfiguration.class,           // 如果不用Kafka
        SecurityAutoConfiguration.class         // 如果不用Spring Security
})
@ForestScan(basePackages = {"com.optimus.client"})
@EnableAsync
@EnableScheduling
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Locale.setDefault(Locale.CHINA);

        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        Environment env = ctx.getEnvironment();
        String str = String.format("################   Spring Boot Application: %s-%s %s #################",
                env.getProperty("spring.application.name"), env.getProperty("spring.profiles.active"), env.getProperty("server.port"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            sb.append("#");
        }
        logger.info(sb.toString());
        logger.info(str);
        logger.info(sb.toString());
//        Properties p = System.getProperties();
//        SortedMap<String, String> m = Maps.newTreeMap();
//        p.keySet().forEach(e -> m.put(String.valueOf(e), p.getProperty(String.valueOf(e))));
//        m.keySet().forEach(k -> logger.info("{}={}", k, m.get(k)));
    }


    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration(CorsFilter corsFilter) {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(corsFilter);
        registration.addUrlPatterns("*");
        registration.setName("CorsFilter");
        registration.setOrder(1);
        return registration;
    }

//    @Bean
//    public FilterRegistrationBean<GlobalApiFilter> siteGlobalApiFilterRegistration(GlobalApiFilter globalApiFilter) {
//        FilterRegistrationBean<GlobalApiFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(globalApiFilter);
//        registration.addUrlPatterns(API_PREFIX + "/*");
//        registration.setName("GlobalApiFilter");
//        registration.setOrder(2);
//        return registration;
//    }
//
//    @Bean
//    public FilterRegistrationBean<GlobalThirdFilter> siteGlobalThirdFilterRegistration(GlobalThirdFilter globalThirdFilter) {
//        FilterRegistrationBean<GlobalThirdFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(globalThirdFilter);
//        registration.addUrlPatterns(THIRD_PREFIX + "/*");
//        registration.setName("GlobalThirdFilter");
//        registration.setOrder(3);
//        return registration;
//    }
/*

    @Bean
    public ForestJsonConverter forestFastjsonConverter() {
        ForestFastjsonConverter converter = new ForestFastjsonConverter();
        // 设置日期格式
        converter.setDateFormat("yyyy-MM-dd HH:mm:ss");
        // 设置序列化特性
//        converter.setSerializerFeature(SerializerFeature.IgnoreErrorGetter);
        return converter;
    }
*/

}
