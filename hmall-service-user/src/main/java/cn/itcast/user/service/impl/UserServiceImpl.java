package cn.itcast.user.service.impl;

import cn.itcast.hmall.pojo.user.User;
import cn.itcast.user.mapper.UserMapper;
import cn.itcast.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED ,readOnly = false)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User getUser(Long id) {
        if(id!=null){
            return   this.getById(id);
        }
        throw  new RuntimeException("查询错误");
    }
      //扣钱
    @Override
    public void pay(Long money, Long id) {
        if(money==null||id==null){
            throw  new RuntimeException("扣钱出问题");
        }
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id",id).setSql("balance=balance-"+money);
        this.update(userUpdateWrapper);
    }
}
