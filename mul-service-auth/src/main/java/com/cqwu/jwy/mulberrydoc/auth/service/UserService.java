package com.cqwu.jwy.mulberrydoc.auth.service;

import com.cqwu.jwy.mulberrydoc.auth.constant.AuthError;
import com.cqwu.jwy.mulberrydoc.auth.dao.UserDao;
import com.cqwu.jwy.mulberrydoc.auth.pojo.User;
import com.cqwu.jwy.mulberrydoc.auth.util.IdUtil;
import com.cqwu.jwy.mulberrydoc.auth.util.SecretUtil;
import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.common.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 用户服务类
 */
@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    private SecretUtil secretUtil;
    @Autowired
    private IdUtil idUtil;

    /**
     * 重复出现的用户名
     */
    private final Set<String> duplicateUsername = new HashSet<>();

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 用户实例
     * @throws Exception 异常
     */
    public User createUser(User user) throws Exception {
        // 判断用户名是否在 缓存 中重复
        if (duplicateUsername.contains(user.getUsername())) {
            LOG.warn("用户名: {} 已在缓存中出现", user.getUsername());
            throw new WebException(AuthError.USERNAME_CONFLICT);
        }
        // 判断用户名是否在 数据库 中重复
        if (Objects.nonNull(userDao.queryUserByUsername(user.getUsername()))) {
            LOG.warn("用户名: {} 已在数据库中出现", user.getUsername());
            duplicateUsername.add(user.getUsername());
            throw new WebException(AuthError.USERNAME_CONFLICT);
        }
        duplicateUsername.add(user.getUsername());
        user.setId(idUtil.nextUserId());
        user.setCreatedAt(DateUtil.nowDatetime());
        user.setUpdatedAt(user.getCreatedAt());
        user.setPassword(secretUtil.generatePassword(user.getPassword(), user.getUsername()));
        userDao.insert(user);
        return user;
    }

    /**
     * 通过 用户ID 查询用户
     *
     * @param id 用户ID
     * @return 结果
     */
    public User queryUserById(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return userDao.queryUserById(id);
    }

    public void updateAvatar(String id, String avatar) {
        userDao.updateAvatar(id, avatar, DateUtil.nowDatetime());
    }
}
