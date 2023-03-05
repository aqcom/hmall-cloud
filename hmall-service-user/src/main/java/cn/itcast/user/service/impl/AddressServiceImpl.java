package cn.itcast.user.service.impl;

import cn.itcast.hmall.dto.common.ThreadLocalUtil;
import cn.itcast.hmall.pojo.user.Address;
import cn.itcast.user.mapper.AddressMapper;
import cn.itcast.user.service.AddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional(propagation = Propagation.SUPPORTS ,readOnly = true)
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    //根据用户ID查询地址列表
    @Override
    public List<Address> getByUid() {
        Long userId = ThreadLocalUtil.getUserId();
        QueryWrapper<Address> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("user_id",userId);
        return this.list(addressQueryWrapper);
    }
   //根据addressId查询Address
    @Override
    public Address getAddressById(Long id) {
        if (id==null){
            return null;
        }
        return this.getById(id) ;
    }
}
