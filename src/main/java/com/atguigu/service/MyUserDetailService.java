package com.atguigu.service;

import com.atguigu.entity.Users;
import com.atguigu.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: MyUserDetailService
 * @author: mafangnian
 * @date: 2022/7/26 18:24
 * @Blog: null
 */
@Service("UserDetailsService")
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //调用userMapper,根据用户名查询数据库
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        //where username = ?
        wrapper.eq("username",username);
        Users users = userMapper.selectOne(wrapper);
        //判断数据库理有没有这个用户
        if (users == null){ //没有这个用户，认证失败
            throw new UsernameNotFoundException("用户名不存在！");
        }
        //借助AuthorityUtils职责工具类将返回的用户设置角色或者权限
        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("admins,ROLE_sale");
        //从数据库查询的结果来返回user对象,得到对应的用户名和密码
        return new User(users.getUsername(),new BCryptPasswordEncoder().encode(users.getPassword()),auths);
    }
}
