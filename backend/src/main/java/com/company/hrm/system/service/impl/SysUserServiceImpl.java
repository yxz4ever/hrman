package com.company.hrm.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.common.exception.BusinessException;
import com.company.hrm.common.security.JwtTokenUtil;
import com.company.hrm.system.entity.SysUser;
import com.company.hrm.system.mapper.SysUserMapper;
import com.company.hrm.system.security.LoginUser;
import com.company.hrm.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public SysUser findByUsername(String username) {
        return baseMapper.findByUsername(username);
    }

    @Override
    public String login(String username, String password) {
        // 认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // 设置认证信息到Security上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取登录用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 生成Token
        String token = jwtTokenUtil.generateToken(loginUser.getUserId(), loginUser.getUsername());

        return token;
    }
}
