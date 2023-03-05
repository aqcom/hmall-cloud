package cn.itcast.user.controller;
import cn.itcast.hmall.pojo.user.User;
import cn.itcast.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id")Long id){
        return userService.getUser(id);
    }
    @RequestMapping("/user/pay/{money}/{id}")
    public void  pay(@PathVariable("money")Long money,@PathVariable("id")Long id){
        userService.pay(money,id);
    }
}
