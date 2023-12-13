package com.refengSGL.utils;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.refengSGL.entity.User;
import com.refengSGL.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Component
public class TokenUtils {

    private static UserService staticUserService;

    @Resource
    private UserService userService;

    @PostConstruct
    public void setUserService() {
        staticUserService = userService;
    }

    /**
     * 生成token
     */
    public static String createToken(String uid, String sign) {
        return JWT.create()
                // 设置uid为载荷
                .withAudience(uid)
                // 设置过期时间为2小时
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2))
                // 设置验签算法
                .sign(Algorithm.HMAC256(sign));
    }

    /**
     * 获取当前的用户信息
     * TODO 使用该方法替换实现类中硬编码的uid
     */
    public static User getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("Authorization");
            log.info("token:{}",token);
            if (StringUtils.isNotBlank(token)) {
                String uid = JWT.decode(token).getAudience().get(0);
                log.info("uid:{}",uid);
                return staticUserService.getById(Integer.valueOf(uid));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
