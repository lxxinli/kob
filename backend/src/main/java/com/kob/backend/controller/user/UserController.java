package com.kob.backend.controller.user;

import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    // !!!如果想要映射所有请求就@Mapping,否则就@类型名+请求,such as @GetMapping()
    @GetMapping("all/")
    public List<User> getAll() {
        return userMapper.selectList(null);
    }

    @GetMapping("{userId}/")
    public User getuser(@PathVariable int userId) {
        return userMapper.selectById(userId);
    }

    @GetMapping("add/{userId}/{username}/{password}/")
    public String addUser(@PathVariable int userId,
                          @PathVariable String username,
                          @PathVariable String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String EncodedPassword = passwordEncoder.encode(password);
        User user = new User(userId, username, EncodedPassword);
        userMapper.insert(user);
        return "Add User Successfully!";
    }

    @GetMapping("delete/{userId}/")
    public String deleteUser(@PathVariable int userId) {
        userMapper.deleteById(userId);
        return "Delete User Successfully!";
    }


}
