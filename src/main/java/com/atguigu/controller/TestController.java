package com.atguigu.controller;

import com.atguigu.entity.Users;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: TestController
 * @author: mafangnian
 * @date: 2022/7/26 14:24
 * @Blog: null
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("hello")
    public String hello() {
        return "hello security";
    }

    @GetMapping("index")
    public String index() {
        return "hello index";
    }


    @GetMapping("update")
    //@Secured({"ROLE_sale","manager"}) //用户具有"ROLE_sale"角色,或"manager"的权限其中之一才能访问
    //@PreAuthorize("hasAnyAuthority('admins')") //用户具备admins权限才能访问
    @PostAuthorize("hasAnyAuthority('menu:system')")
    //访问web页面,执行完update方法之后，再进行校验 控制台会打印update.... 但是没有menu:system权限，无法访问
    public String update() {
        System.out.println("update.....");
        return "hello update";
    }


    @GetMapping("getAll")
    @PreAuthorize("hasAnyAuthority('admins')")
    @PostFilter("filterObject.username == 'admin1'")
    public List<Users> getAllUser() {
        ArrayList<Users> list = new ArrayList<>();
        list.add(new Users(12, "admin1", "1111"));
        list.add(new Users(22, "admin2", "888"));
        System.out.println(list);
        return list; //return的是admin1的信息
    }


}


