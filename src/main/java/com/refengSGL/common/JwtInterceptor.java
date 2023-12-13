package com.refengSGL.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.refengSGL.entity.User;
import com.refengSGL.exception.CodeException;
import com.refengSGL.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())){
            log.info("[JwtInterceptor-preHandle] OPTIONS 请求放行");
            return true;
        }

        if (StringUtils.isBlank(token)) {
            throw new CodeException("token为空");
        }
        // 获取token中的uid
        String uid;
        User user;
        try {
            uid = JWT.decode(token).getAudience().get(0);
            // 根据uid查询数据库用户实体
            user = userService.getById(Integer.parseInt(uid));
        } catch (Exception e) {
            String errorMessage = "token验证失败，请重新登录";
            log.error(errorMessage + ", token=" + token, e);
            throw new CodeException(errorMessage);
        }
        if (Objects.isNull(user)) {
            throw new CodeException("用户不存在，请重新登录");
        }
        try {
            // 加签验证token
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
            verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new CodeException("token验证失败，请重新登录");
        }
        return true;
    }
}
