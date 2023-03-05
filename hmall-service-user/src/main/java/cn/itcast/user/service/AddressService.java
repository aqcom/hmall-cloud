package cn.itcast.user.service;

import cn.itcast.hmall.pojo.user.Address;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AddressService extends IService<Address> {
    List<Address> getByUid();

    Address getAddressById(Long id);
}
