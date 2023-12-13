package com.refengSGL.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.refengSGL.entity.User;
import com.refengSGL.entity.request.LoginBo;
import com.refengSGL.entity.request.RegisterBo;
import com.refengSGL.entity.request.UserPageBo;
import com.refengSGL.entity.request.UserVo;
import com.refengSGL.entity.response.UserPageDto;
import com.refengSGL.exception.CodeException;
import com.refengSGL.mapper.UserMapper;
import com.refengSGL.service.UserService;
import com.refengSGL.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.refengSGL.constant.UserConstant.IS_LOGIN;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwmIMGWJH5htnuiIN9rh25V5XFtqpTa5gygImbV8cEv+DvO+sCV+xOBWzsHNBfT+AaggfFLyTfONm6eMLdo1Xmdp+mG9ZBw5/g3hbUJgjpRtiULlKvxnZzM4pcn2Q/uTRyNn/9DF+ZvSYIc6ynG8cHV4WwfkUjpA2lbMP/8lZz8wIDAQAB";

    private final String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALCYgwZYkfmG2e6Ig32uHblXlcW2qlNrmDKAiZtXxwS/4O876wJX7E4FbOwc0F9P4BqCB8UvJN842bp4wt2jVeZ2n6Yb1kHDn+DeFtQmCOlG2JQuUq/GdnMzilyfZD+5NHI2f/0MX5m9JghzrKcbxwdXhbB+RSOkDaVsw//yVnPzAgMBAAECgYBliat8kJyOLp1L664/K0fn85YoMllI0cqW7xUv/o5uCq2YoIkFtewnCDOSpxagVtryIuW42NyIhLWb5CeXLH0MqLIxkaNteLJ8TizyqFotJ3e+Kg4YipZtJt6NAOoUPvUJhRGLiuFoEAfpGsFVk/eGR0O5jl7az4gO7fGdL8rkgQJBAPfRvdUgWyWm9nIzzM8Y7cGAzKPtLG9bZKbAUyOnScxy3/ACkOCdSknNV/qQgKiNAfhFw1kuYNbnRpz5tWsSJSECQQC2bODDdpN5mWO5o5HlHDZ6ncjAUre3Cifop9MqI3S+EyIcWSPbIACpP2+vE3qrY7Ecfq5Q7sBrw16YIe+JpOKTAkEAz+/vhvsFP8yekSihu0vBg1HdY9bIKA/ZnolVuV7O73ucJzkf8zhHczGXlqjVK5hVhMqUpSnjG68ncuObs+GfwQJBAI1foWw5Z57T/dHu7nDUxW+O+fX55MAoUbde4CoG4kEviL7ZYg0+JuUC8WWHvgg2sRo5HpRAAsVA2f0iR4WvT5sCQQCEKfVy7KMoK9XYrF47rfxyFDQQcof7HtD44aaAJTajcfH8S4KQUw8rUHIRFaAmWEv7rKP/aIXAKKjywJrs2N6h";

    @Override
    public User userLogin(LoginBo loginBo) {
        // 提取用户名和密码
        String username = loginBo.getUsername();
        String password = loginBo.getPassword();

        // 1、查看是否存在匹配用户名
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(user)) {
            return null;
        }
        // 2、验证密码
        String originalPassword = SaSecureUtil.rsaDecryptByPrivate(privateKey, user.getPassword());
        log.info(originalPassword);
        if (!originalPassword.equals(password)) {
            return null;
        }
        // 3、确认用户是否被禁用
        if (!user.getStatus().equals(0)) {
            return null;
        }

        StpUtil.login(user.getId());
        StpUtil.getSession().set("userId",user.getId());
        log.info("userID:{}",user.getId());

        user.setIsLogin(IS_LOGIN);
        baseMapper.updateById(user);
        return user;
    }

    @Override
    public int userRegister(RegisterBo registerBo) {
        String username = registerBo.getUsername();
        log.info("username:{}",username);
        // 首先进行对用户名的校验，数据库中不应该出现相同的用户名
        identifyDuplicateUserName(username);
        String password = registerBo.getPassword();
        String email = registerBo.getEmail();
        String phone = registerBo.getPhone();
        String encryptedPassword = SaSecureUtil.rsaEncryptByPublic(publicKey, password);
        log.info("encryptedPassword:{}",encryptedPassword);
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptedPassword);
        user.setEmail(email);
        user.setPhone(phone);

        int count = baseMapper.insert(user);
        return count;
    }

    @Override
    public User getUserInfoById(Long id) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, id);
        User user = baseMapper.selectOne(queryWrapper);
        return user;
    }

    @Override
    public UserVo getCurrentUserInfo() {
        // 根据userId查询用户信息
        Integer userId = TokenUtils.getCurrentUser().getId();

        User user = baseMapper.selectById(userId);
        return user.getUserVo();
    }

    @Override
    public boolean updateByUserId(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();
        String phone = user.getPhone();
        Integer id = user.getId();

        if (StringUtils.isNotBlank(username)) {
            // 数据库中不应该出现相同的用户名
            identifyDuplicateUserName(username);
        }
        if (StringUtils.isNotBlank(password)) {
            // 需要对密码进行加密存储
            String encryptedPassword = SaSecureUtil.rsaEncryptByPublic(publicKey, password);
            user.setPassword(encryptedPassword);
        }
        int count = baseMapper.updateById(user);
        log.info("count:{}",count);
        return count > 0;
    }

    private void identifyDuplicateUserName(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User one = baseMapper.selectOne(queryWrapper);

        if (!Objects.isNull(one)) {
            log.info("userId",one.getId());
            if (TokenUtils.getCurrentUser().getId().equals(one.getId())){
                return;
            }
            log.info("Duplicate user names exist in the database");
            throw new CodeException("用户名已存在");
        }
    }

    @Override
    public Page<UserPageDto> getUserPage(UserPageBo userPageBo) {
        Integer pageNum = userPageBo.getPageNum();
        Integer pageSize = userPageBo.getPageSize();
        String userName = userPageBo.getUserName();

        Page<User> pageInfo = new Page<>(pageNum, pageSize);

        // 分页条件查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.likeRight(User::getUsername, userName);
        }
        baseMapper.selectPage(pageInfo, queryWrapper);

        // 封装页面数据
        List<User> pageInfoRecords = pageInfo.getRecords();
        List<UserPageDto> userPageDtoList = pageInfoRecords
                .stream()
                .map(this::getUserInfoPage)
                .collect(Collectors.toList());
        Page<UserPageDto> userPageDtoPage = new Page<>(pageNum, pageSize, pageInfo.getTotal());
        userPageDtoPage.setRecords(userPageDtoList);
        return userPageDtoPage;
    }

    @Override
    public boolean updateStatus(Integer id) {
        if(TokenUtils.getCurrentUser().getId()==1){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getId, id);
            User user = baseMapper.selectOne(queryWrapper);
            if (Objects.isNull(user)) {
            log.info("目标用户不存在");
            throw new CodeException("目标用户不存在");
        }
            user.setStatus(user.getStatus() == 1 ? 0 : 1);
            baseMapper.updateById(user);
            return true;
        }
            return false;
    }

    private UserPageDto getUserInfoPage(User user) {
        UserPageDto userPageDto = new UserPageDto();
        userPageDto.setId(user.getId());
        userPageDto.setUsername(user.getUsername());
        userPageDto.setEmail(user.getEmail());
        userPageDto.setPassword(user.getPassword());
        userPageDto.setPhone(user.getPhone());
        userPageDto.setStatus(user.getStatus());
        return userPageDto;
    }

}



