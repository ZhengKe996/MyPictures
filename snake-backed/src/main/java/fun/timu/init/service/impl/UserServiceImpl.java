package fun.timu.init.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.timu.init.common.ErrorCode;
import fun.timu.init.constant.CommonConstant;
import fun.timu.init.exception.BusinessException;
import fun.timu.init.mapper.UserMapper;
import fun.timu.init.model.dto.user.UserQueryRequest;
import fun.timu.init.model.entity.User;
import fun.timu.init.model.enums.UserRoleEnum;
import fun.timu.init.model.vo.LoginUserVO;
import fun.timu.init.model.vo.UserVO;
import fun.timu.init.service.UserService;
import fun.timu.init.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static fun.timu.init.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author zhengke
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-01-21 10:42:08
 */

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    public static final String SALT = "Init";// TODO 盐值，混淆密码
    private Lock lock = new ReentrantLock(); // 锁

    /**
     * 用户注册函数
     *
     * @param userAccount   用户账号，要求长度至少为4
     * @param userPassword  用户密码，要求长度至少为8
     * @param checkPassword 用于确认的密码，必须与userPassword相同
     * @return 注册成功后用户的ID
     * @throws BusinessException 当参数不合法或注册过程中出现错误时抛出
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验参数是否为空或长度是否符合要求
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 检查两次输入的密码是否一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 使用锁确保用户注册的原子性，避免并发问题
        lock.lock();
        try {
            // 检查数据库中是否已存在相同账号的用户
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }

            // 2. 对用户密码进行加密处理
            String encryptPassword = getEncryptPassword(userPassword);

            // 3. 创建User对象并插入到数据库
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserName("普通用户" + UUID.randomUUID());
            user.setUserRole(UserRoleEnum.USER.getValue());
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            // 返回新注册用户ID
            return user.getId();
        } finally {
            // 释放锁，确保后续其他线程可以执行注册操作
            lock.unlock();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        try {
            // 2. 加密
            String encryptPassword = getEncryptPassword(userPassword);

            // 查询用户是否存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            queryWrapper.eq("userPassword", encryptPassword);
            User user = this.baseMapper.selectOne(queryWrapper);

            // 用户不存在
            if (user == null) {
                log.warn("user login failed, userAccount: {}", userAccount);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
            }

            // 3. 记录用户的登录态（Redis）
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
            return this.getLoginUserVO(user);
        } catch (Exception e) {
            log.error("Error during user login process", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统内部错误");
        }
    }


    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 1. 先判断是否已登录
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "Session is null");
        }

        Object userObj = session.getAttribute(USER_LOGIN_STATE);
        if (userObj == null || !(userObj instanceof User)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "User object is null or not an instance of User");
        }

        User currentUser = (User) userObj;
        if (currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "User ID is null");
        }

        // 2. 查询数据库获取的User对象
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        return currentUser;
    }


    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 1. 先判断是否已登录
        HttpSession session = request.getSession(false); // 获取会话，允许返回 null
        if (session == null) return null;

        Object userObj = session.getAttribute(USER_LOGIN_STATE);
        if (!(userObj instanceof User)) return null;

        User currentUser = (User) userObj;

        // 2. 检查用户 ID 是否为空
        if (currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户信息不完整");
        }

        // 3. 从数据库查询（追求性能的话，直接走缓存，不查数据库）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        return currentUser;
    }


    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        HttpSession session = request.getSession(false); // 避免不必要的会话创建
        if (session == null) {
            return false; // 如果没有会话，直接返回false
        }

        Object userObj = session.getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            return false; // 如果用户对象为空，直接返回false
        }

        if (!(userObj instanceof User)) {
            return false; // 如果用户对象不是User类型，直接返回false
        }

        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 获取会话时不创建新会话
        if (session == null || session.getAttribute(USER_LOGIN_STATE) == null) {
            log.warn("User not logged in: {}", request.getRemoteAddr());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录状态
        session.removeAttribute(USER_LOGIN_STATE);
        log.info("User logged out successfully: {}", request.getRemoteAddr());
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) return null;
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) return null;

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) return new ArrayList<>();
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");

        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    /**
     * 对用户密码进行加密处理
     * <p>
     * 本方法使用MD5算法对用户密码进行不可逆加密处理加密过程包括：
     * 1. 将用户密码与预定义的盐值（SALT）拼接，以增加密码的安全性
     * 2. 使用DigestUtils工具类中的md5DigestAsHex方法对拼接后的字符串进行MD5加密
     * 该方法将字符串转换为字节数组，然后执行MD5加密，并返回加密后的十六进制字符串表示
     *
     * @param userPassword 明文用户密码，即用户输入的原始、未加密的密码
     * @return 返回加密后的密码字符串，作为用户密码的安全存储形式
     */
    public String getEncryptPassword(String userPassword) {
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }
}




