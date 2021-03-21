package com.wangwren.springcloud.config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 配置全局Cors跨域请求
 */
@Component
public class SimpleCorsFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCorsFilter.class);
    private final static String DEFAULT_ORIGIN = "*";

    private static String maxAge = "0";

    private static String allowCredentials = "true";

    private static String allowMethod = "POST,GET,OPTIONSZ,PUT,DELETE";

    private static String[] allowOrigins = {"http://*","https://*"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (StringUtils.isBlank(maxAge)) {
            maxAge = "0";
        }
        if (StringUtils.isBlank(allowCredentials)) {
            allowCredentials = "true";
        }
        if (StringUtils.isBlank(allowMethod)) {
            allowMethod = "POST,GET,OPTIONS";
        }
        logger.debug("corsFilter-init: maxAge='{}',allowCredentials='{}',allowMethod='{}',allowOrigins='{}' ", //
                maxAge, allowCredentials, allowMethod, allowOrigins);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse rsp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) rsp;
        try {
            final String origin = request.getHeader("Origin");

            if (origin == null) {
                return;  // access
            }

            if (allowOrigins == null || allowOrigins.length == 0) { // default allow

                response.setHeader("Access-Control-Allow-Origin", DEFAULT_ORIGIN);
                response.setHeader("Access-Control-Allow-Methods", allowMethod);
                response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept,X-DEV-ID,X-WeshareAuth-Token");
                response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
                response.setHeader("Access-Control-Max-Age", maxAge);

            } else {
                boolean bAllow = false;

                for (String tmp : allowOrigins) {
                    if (StringUtils.isBlank(tmp)) {
                        continue;
                    }
                    if (equalsAddress(origin, tmp)) {
                        bAllow = true;
                        break;
                    }
                }

                if (bAllow) {
                    if (request.getMethod().equals("OPTIONS") &&
                            !StringUtils.isEmpty(request.getHeader("Access-Control-Request-Method"))) {

                        response.setHeader("Access-Control-Allow-Origin",DEFAULT_ORIGIN);
                        response.setHeader("Access-Control-Allow-Methods", allowMethod);
                        response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept,X-DEV-ID,X-WeshareAuth-Token,authorization");
                        response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
                        response.setHeader("Access-Control-Max-Age", maxAge);

                    } else {

                        response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept,X-DEV-ID,X-WeshareAuth-Token");
                        response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
                        response.setHeader("Access-Control-Allow-Origin", origin);
                    }

                } else {
                    String uri = request.getServletPath().concat("_").concat(origin);
                    logger.debug("SimpleCorsFilter: DO NOT ALLOW: origin: {}, uri: {}", origin, uri);
                }

            }
        } finally {
            chain.doFilter(request, response);
        }
    }

    public static boolean equalsAddress(String address, String regex) {
        regex = regex.replace(".", "\\.");
        regex = regex.replace("*", "(.*)");
        Pattern pattern = Pattern.compile("^" + regex + "$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(address);
        return matcher.find();
    }

}


