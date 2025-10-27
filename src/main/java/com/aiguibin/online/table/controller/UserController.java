package com.aiguibin.online.table.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.online.table.dto.ChangePasswordRequest;
import com.aiguibin.online.table.dto.UserCreateRequest;
import com.aiguibin.online.table.dto.UserUpdateRequest;
import com.aiguibin.online.table.entity.User;
import com.aiguibin.online.table.mapper.UserMapper;
import com.aiguibin.online.table.model.ApiResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Resource
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ApiResponse<Page<User>> list(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String usernumb,
                                        @RequestParam(required = false) String username) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        if (usernumb != null && !usernumb.isEmpty()) {
            qw.eq(User::getUsernumb, usernumb);
        }
        if (username != null && !username.isEmpty()) {
            qw.like(User::getUsername, username);
        }
        qw.orderByDesc(User::getCreateTime);
        Page<User> pageData = userMapper.selectPage(new Page<>(page, size), qw);
        return ApiResponse.success(pageData);
    }

    @GetMapping("/{id}")
    public ApiResponse<User> detail(@PathVariable Long id) {
        return ApiResponse.success(userMapper.selectById(id));
    }

    @PostMapping
    public ApiResponse<Long> create(@RequestBody @Validated UserCreateRequest req) {
        // 唯一约束前置校验
        User exists = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsernumb, req.getUsernumb()));
        if (exists != null) {
            return ApiResponse.error("用户编号已存在");
        }
        User u = new User();
        u.setUsernumb(req.getUsernumb());
        u.setUsername(req.getUsername());
        // 初始密码强制为 666666
        u.setPassword(passwordEncoder.encode("666666"));
        try {
            userMapper.insert(u);
        } catch (DuplicateKeyException e) {
            return ApiResponse.error("唯一约束冲突：用户编号已存在");
        }
        return ApiResponse.success(u.getId());
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody @Validated UserUpdateRequest req) {
        if (!id.equals(req.getId())) {
            return ApiResponse.error("路径ID与请求体ID不一致");
        }
        // 检查用户编号是否被其他人占用
        User other = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsernumb, req.getUsernumb()));
        if (other != null && !other.getId().equals(id)) {
            return ApiResponse.error("用户编号已被占用");
        }
        User u = userMapper.selectById(id);
        if (u == null) {
            return ApiResponse.error("用户不存在");
        }
        u.setUsernumb(req.getUsernumb());
        u.setUsername(req.getUsername());
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            u.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        int rows = userMapper.updateById(u);
        return ApiResponse.success(rows > 0);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        int rows = userMapper.deleteById(id);
        return ApiResponse.success(rows > 0);
    }

    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@RequestBody @Validated ChangePasswordRequest req, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        User u = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, operator));
        if (u == null) {
            return ApiResponse.error("用户不存在或未登录");
        }
        // 验证当前密码
        if (!passwordEncoder.matches(req.getCurrentPassword(), u.getPassword())) {
            return ApiResponse.error("当前密码不正确");
        }
        // 新密码一致性
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            return ApiResponse.error("新密码与确认密码不一致");
        }
        // 强度检测：至少6位，同时包含字母与数字
        String np = req.getNewPassword();
        if (!np.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) {
            return ApiResponse.error("新密码需至少6位，且包含字母和数字");
        }
        // 更新为新密码
        u.setPassword(passwordEncoder.encode(np));
        int rows = userMapper.updateById(u);
        return rows > 0 ? ApiResponse.success("密码修改成功") : ApiResponse.error("密码修改失败");
    }
}