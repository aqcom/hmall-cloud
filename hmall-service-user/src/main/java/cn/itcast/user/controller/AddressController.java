package cn.itcast.user.controller;

import cn.itcast.hmall.pojo.user.Address;
import cn.itcast.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;
    @GetMapping("/uid")
    public List<Address> getByUid(){
        return addressService.getByUid();
    }
    //根据addressId查询Address
    @GetMapping("/{addressId}")
    public Address getAddressID(@PathVariable("addressId")Long id){
        return addressService.getAddressById(id);
    }
}
