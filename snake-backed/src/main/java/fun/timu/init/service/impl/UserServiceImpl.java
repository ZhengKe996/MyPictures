package fun.timu.init.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
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

    /**
     * 用户登录方法
     *
     * @param userAccount  用户账号，用于识别用户身份
     * @param userPassword 用户密码，用于验证用户身份
     * @param request      HTTP请求对象，用于获取和设置请求信息
     * @return 登录成功的用户信息视图对象
     * @throws BusinessException 当参数验证失败、用户不存在或系统发生内部错误时抛出
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验参数的有效性
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
            // 2. 加密用户密码以进行安全验证
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


    /**
     * 获取当前登录的用户信息
     * <p>
     * 此方法通过HTTP请求获取当前登录用户的信息它首先检查会话是否已创建，
     * 然后从会话中提取用户信息如果用户未登录或会话信息不正确，则抛出异常
     *
     * @param request HTTP请求对象，用于获取会话信息
     * @return 当前登录的User对象如果未登录或会话信息无效，则抛出异常
     * @throws BusinessException 当用户未登录、用户对象为空、用户ID为空或用户信息在数据库中不存在时
     */
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


    /**
     * 重写获取登录用户方法，允许返回null
     * 此方法用于根据请求获取当前登录用户信息，如果用户未登录或会话信息异常，将返回null
     * 主要步骤包括：判断是否已登录、检查用户对象类型、验证用户ID，并从数据库查询用户信息
     *
     * @param request HTTP请求对象，用于获取会话信息
     * @return User对象，表示当前登录用户，如果未登录或出现异常则返回null
     */
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


    /**
     * 判断当前请求的用户是否为管理员
     *
     * @param request HTTP请求对象，用于获取当前会话
     * @return 如果当前用户是管理员，则返回true；否则返回false
     */
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

    /**
     * 判断用户是否为管理员角色
     *
     * @param user 待判断的用户对象
     * @return 如果用户不为空且用户角色为管理员，则返回true；否则返回false
     */
    @Override
    public boolean isAdmin(User user) {
        // 检查用户是否不为空且用户的角色值等于管理员角色值
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户退出登录功能
     * 该方法通过HTTP请求来注销用户登录状态
     *
     * @param request HTTP请求对象，用于获取当前会话和用户信息
     * @return 总是返回true，表示退出登录操作成功
     * @throws BusinessException 如果用户在未登录状态下尝试退出登录，抛出操作错误异常
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 尝试获取当前会话，如果不存则不创建新会话
        HttpSession session = request.getSession(false);
        // 检查会话是否存在，以及用户登录状态是否存在
        if (session == null || session.getAttribute(USER_LOGIN_STATE) == null) {
            // 如果用户未登录，记录警告日志并抛出异常
            log.warn("User not logged in: {}", request.getRemoteAddr());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除用户的登录状态，实现退出登录
        session.removeAttribute(USER_LOGIN_STATE);
        // 记录用户成功退出登录的信息
        log.info("User logged out successfully: {}", request.getRemoteAddr());
        // 返回true表示退出登录操作成功
        return true;
    }

    /**
     * 根据用户对象获取登录用户视图对象
     * <p>
     * 此方法的作用是将用户对象转换为登录用户视图对象，用于在不同层次之间传递用户信息
     *
     * @param user 用户对象，包含用户的基本信息
     * @return 登录用户视图对象，如果输入的用户对象为null，则返回null
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        // 检查输入的用户对象是否为null，如果为null则直接返回null
        if (user == null) return null;

        // 创建一个新的登录用户视图对象实例
        LoginUserVO loginUserVO = new LoginUserVO();

        // 使用Spring框架的BeanUtils工具类，将用户对象的属性值复制到登录用户视图对象中
        // 这样做可以简化对象属性之间的复制操作，提高代码的可读性和维护性
        BeanUtils.copyProperties(user, loginUserVO);

        // 返回填充了用户信息的登录用户视图对象
        return loginUserVO;
    }

    /**
     * 根据User对象获取UserVO对象
     * 此方法用于将User实体类对象转换为UserVO视图对象，便于在不同层次之间传递数据
     *
     * @param user User实体类对象，包含用户相关信息如果传入的User对象为null，则返回null
     * @return UserVO视图对象，包含与User实体类相同的信息如果输入为null，则返回null
     */
    @Override
    public UserVO getUserVO(User user) {
        // 检查输入的User对象是否为null，如果为null则直接返回null
        if (user == null) return null;

        // 创建一个新的UserVO对象
        UserVO userVO = new UserVO();
        // 使用Spring框架的BeanUtils工具类，将User对象的属性值复制到UserVO对象中
        BeanUtils.copyProperties(user, userVO);
        // 返回填充好的UserVO对象
        return userVO;
    }

    /**
     * 根据用户列表获取用户视图对象列表
     * 此方法主要用于将一系列用户对象转换为一系列用户视图对象，便于在不同层次之间传递数据
     *
     * @param userList 用户列表，不应为null
     * @return 用户视图对象列表如果输入列表为空，则返回空列表
     */
    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        // 检查输入列表是否为空，如果为空则直接返回一个新的空列表，避免后续操作中的空指针异常
        if (CollUtil.isEmpty(userList)) return new ArrayList<>();

        // 使用流处理将每个用户对象映射到用户视图对象，并收集结果到一个新的列表中
        // 这里利用了Java 8的Stream API来简化集合的处理，提高代码的可读性和效率
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 生成用户查询包装器
     * 根据用户查询请求中的条件，创建一个QueryWrapper对象，用于后续的数据库查询操作
     *
     * @param userQueryRequest 用户查询请求对象，包含查询条件和排序信息
     * @return QueryWrapper<User> 用户查询的QueryWrapper对象
     * @throws BusinessException 如果请求参数为空，则抛出业务异常
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
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




