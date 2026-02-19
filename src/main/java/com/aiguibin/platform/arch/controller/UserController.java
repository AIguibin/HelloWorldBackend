package com.aiguibin.platform.arch.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.platform.arch.dto.ChangePasswordRO;
import com.aiguibin.platform.arch.dto.UserCreateRO;
import com.aiguibin.platform.arch.dto.UserUpdateRO;
import com.aiguibin.platform.arch.entity.User;
import com.aiguibin.platform.arch.mapper.UserMapper;
import com.aiguibin.platform.arch.dto.ResultVO;
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
    public ResultVO<Page<User>> list(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String usernumb,
                                        @RequestParam(required = false) String username) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        if (usernumb != null && !usernumb.isEmpty()) {
            qw.eq(User::getUserNum, usernumb);
        }
        if (username != null && !username.isEmpty()) {
            qw.like(User::getUserName, username);
        }
        qw.orderByDesc(User::getCreatedTime);
        Page<User> pageData = userMapper.selectPage(new Page<>(page, size), qw);
        return ResultVO.success(pageData);
    }

    @GetMapping("/{id}")
    public ResultVO<User> detail(@PathVariable Long id) {
        return ResultVO.success(userMapper.selectById(id));
    }

    @PostMapping
    public ResultVO<Long> create(@RequestBody @Validated UserCreateRO req) {
        // 唯一约束前置校验
        User exists = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserNum, req.getUsernumb()));
        if (exists != null) {
            return ResultVO.error("用户编号已存在");
        }
        User u = new User();
        u.setUserNum(req.getUsernumb());
        u.setUserName(req.getUsername());
        // 初始密码强制为 666666
        u.setPassword(passwordEncoder.encode("666666"));
        u.setStatus(1); // 启用状态
        try {
            userMapper.insert(u);
        } catch (DuplicateKeyException e) {
            return ResultVO.error("唯一约束冲突：用户编号已存在");
        }
        return ResultVO.success(u.getId());
    }

    @PutMapping("/{id}")
    public ResultVO<Boolean> update(@PathVariable Long id, @RequestBody @Validated UserUpdateRO req) {
        if (!id.equals(req.getId())) {
            return ResultVO.error("路径ID与请求体ID不一致");
        }
        // 检查用户编号是否被其他人占用
        User other = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserNum, req.getUsernumb()));
        if (other != null && !other.getId().equals(id)) {
            return ResultVO.error("用户编号已被占用");
        }
        User u = userMapper.selectById(id);
        if (u == null) {
            return ResultVO.error("用户不存在");
        }
        u.setUserNum(req.getUsernumb());
        u.setUserName(req.getUsername());
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            u.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        int rows = userMapper.updateById(u);
        return ResultVO.success(rows > 0);
    }

    @DeleteMapping("/{id}")
    public ResultVO<Boolean> delete(@PathVariable Long id) {
        int rows = userMapper.deleteById(id);
        return ResultVO.success(rows > 0);
    }

    @PostMapping("/change-password")
    public ResultVO<?> changePassword(@RequestBody @Validated ChangePasswordRO req, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        // 从operator中解析出用户名，格式为 "userNum|userName"
        String[] operatorParts = operator.split("\\|");
        String username = operatorParts.length == 2 ? operatorParts[1] : operator;
        User u = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserName, username));
        if (u == null) {
            return ResultVO.error("用户不存在或未登录");
        }
        // 验证当前密码
        if (!passwordEncoder.matches(req.getCurrentPassword(), u.getPassword())) {
            return ResultVO.error("当前密码不正确");
        }
        // 新密码一致性
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            return ResultVO.error("新密码与确认密码不一致");
        }
        // 强度检测：至少6位，同时包含字母与数字
        String np = req.getNewPassword();
        if (!np.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) {
            return ResultVO.error("新密码需至少6位，且包含字母和数字");
        }
        // 更新为新密码
        u.setPassword(passwordEncoder.encode(np));
        int rows = userMapper.updateById(u);
        return rows > 0 ? ResultVO.success("密码修改成功") : ResultVO.error("密码修改失败");
    }
}