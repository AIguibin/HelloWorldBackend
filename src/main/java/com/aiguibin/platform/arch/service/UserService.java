package com.aiguibin.platform.arch.service;

import com.aiguibin.platform.arch.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 用户服务接口.
 * 提供用户相关的各种服务，包括用户查询、验证等.
 */
public interface UserService {

    /**
     * 根据用户编号获取用户信息.
     * @param userNum 用户编号.
     * @return 用户实体对象.
     */
    User getUserByUserNum(String userNum);

    /**
     * 根据用户名获取用户信息.
     * @param userName 用户名.
     * @return 用户实体对象.
     */
    User getUserByUserName(String userName);

    /**
     * 分页查询用户列表.
     * @param page 页码.
     * @param size 每页大小.
     * @return 分页后的用户列表.
     */
    Page<User> getUserList(int page, int size);

    /**
     * 验证用户是否存在.
     * @param userNum 用户编号.
     * @return 是否存在.
     */
    boolean isUserExists(String userNum);

    /**
     * 获取用户角色列表.
     * @param userNum 用户编号.
     * @return 角色列表.
     */
    List<String> getUserRoles(String userNum);

    /**
     * 验证用户密码.
     * @param userNum 用户编号.
     * @param password 密码.
     * @return 是否验证通过.
     */
    boolean verifyPassword(String userNum, String password);

    /**
     * 获取用户权限列表.
     * @param userNum 用户编号.
     * @return 权限列表.
     */
    List<String> getUserPermissions(String userNum);

    /**
     * 检查用户是否拥有指定角色.
     * @param userNum 用户编号.
     * @param roleCode 角色代码.
     * @return 是否拥有该角色.
     */
    boolean hasRole(String userNum, String roleCode);

    /**
     * 检查用户是否拥有指定权限.
     * @param userNum 用户编号.
     * @param permCode 权限代码.
     * @return 是否拥有该权限.
     */
    boolean hasPermission(String userNum, String permCode);

    /**
     * 根据用户名查找用户.
     * @param username 用户名.
     * @return 用户实体对象.
     */
    User findByUsername(String username);

    /**
     * 匹配密码.
     * @param password 明文密码.
     * @param encryptedPassword 加密密码.
     * @return 是否匹配.
     */
    boolean matchesPassword(String password, String encryptedPassword);

    /**
     * 根据角色代码获取用户编号列表.
     * @param roleCode 角色代码.
     * @return 用户编号列表.
     */
    List<String> getUserNumsByRoleCode(String roleCode);

    /**
     * 根据部门代码获取用户编号列表.
     * @param deptCode 部门代码.
     * @return 用户编号列表.
     */
    List<String> getUserNumsByDeptCode(String deptCode);

    /**
     * 根据职位代码获取用户编号列表.
     * @param positionCode 职位代码.
     * @return 用户编号列表.
     */
    List<String> getUserNumsByPositionCode(String positionCode);

    /**
     * 验证用户是否有效.
     * @param userNum 用户编号.
     * @return 是否有效.
     */
    boolean isValidUser(String userNum);

    /**
     * 根据用户编号获取用户姓名.
     * @param userNum 用户编号.
     * @return 用户姓名.
     */
    String getUserNameByNum(String userNum);
}
