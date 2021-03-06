package com.youlai.auth.service;

import com.youlai.admin.api.dto.UserDTO;
import com.youlai.admin.api.service.AdminUserService;
import com.youlai.auth.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


/**
 * 自定义用户认证和授权
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String clientId = request.getParameter("client_id");
        UserDTO user = adminUserService.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名或者密码错误");
        }
        user.setClientId(clientId);
        UserInfo userInfo = new UserInfo(user);
        if (!userInfo.isEnabled()) {
            throw new DisabledException("该账户已被禁用!");
        } else if (!userInfo.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定!");
        } else if (!userInfo.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        } else if (!userInfo.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("该账户的登录凭证已过期，请重新登录!");
        }
        return userInfo;
    }

}
