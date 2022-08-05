package com.atguigu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * @ClassName: SecurityConfig
 * @author: mafangnian
 * @date: 2022/7/26 17:14
 * @Blog: null
 */
@Configuration
public class SecurityConfigTest extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    //注入数据源
    @Autowired
    private DataSource dataSource;

    //配置对象
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        //使用jdbc模板操作数据库生成的token
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        // 赋值数据源
        jdbcTokenRepository.setDataSource(dataSource);
        // 自动创建表,第一次执行会创建，以后要执行就要删除掉！
//        jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }

    @Bean
    PasswordEncoder password() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //设置自己写的实现类并设置密码编码
        auth.userDetailsService(userDetailsService).passwordEncoder(password());
    }

    //使用HttpSecurity参数,来选择自定义的登录页面设置
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //用户注销
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/test/hello").permitAll();
        //设置403没有权限访问的页面
        http.exceptionHandling().accessDeniedPage("/unauth.html");
        //自定义自己编写的登录页面
        http.formLogin()
                .loginPage("/login.html") //登录页面设置
                .loginProcessingUrl("/user/login")  //登录访问路径
                .defaultSuccessUrl("/success.html").permitAll()  //登录成功之后跳转的路径
                .and().authorizeRequests()
                .antMatchers("/", "/test/hello", "/user/login").permitAll()  //设置哪些路径可以直接访问
                //.antMatchers("/test/index").hasAuthority("admins")//设置权限为admins才问/test/index
                //设置拥有权限manager,admins其中一个就能问/test/index
                //.antMatchers("/test/index").hasAnyAuthority("admins","manager")
                .antMatchers("/test/index").hasRole("sale")//用户具备sale角色才能访问
                //用户具备"admins","role"两个角色其中一个就可以访问
//                .antMatchers("/test/index").hasAnyRole("admins","role")
                .anyRequest().authenticated()
                .and().rememberMe().tokenRepository(persistentTokenRepository())//设置记住我
                .tokenValiditySeconds(60)//设置token存活时间 单位秒
                .userDetailsService(userDetailsService)//设置服务类
                .and().csrf().disable(); //关闭csrf防护
    }
}