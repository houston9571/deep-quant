package com.optimus.filter;

import cn.hutool.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.*;


@Slf4j
@Component
public class CorsFilter implements Filter {

//    String[] script = {"bash", ".", "redirect", "url", "referer", "http", "systemd", "shell", "cd+", "rm+", "wget", "curl", "chmod", "sh+", "<script", "alert(", "iframe", "grant", "drop"};

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

//        String url = request.getRequestURI().toLowerCase();
//        String query = request.getQueryString();
//        query = StringUtils.isNotEmpty(query) ? query.toLowerCase() : "";
//        if (StringUtils.containsAny(url, script) || StringUtils.containsAny(query, script)) {
//            response.setStatus(HttpStatus.HTTP_OK);
//            response.setHeader("Content-Type", "application/json;charset=UTF-8");
//            PrintWriter writer = response.getWriter();
//            writer.print(JSONObject.toJSONString(Result.fail(ATTACK_FILTER)));
//            writer.flush();
//            writer.close();
//            log.error("攻击过滤: url:{} param:{}",url,query);
//            return;
//        }
        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(ORIGIN));
        response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, DELETE, PUT, OPTIONS");
        response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, CONTENT_TYPE + "," + ACCEPT_LANGUAGE);
        response.setHeader(ACCESS_CONTROL_MAX_AGE, "3600");
        response.setHeader(CACHE_CONTROL, "no-cache, no-store, must-revalidate");

        if (RequestMethod.OPTIONS.name().equals(request.getMethod())) {
            response.setStatus(HttpStatus.HTTP_OK);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
